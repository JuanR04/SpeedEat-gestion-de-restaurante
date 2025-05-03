-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 07-12-2024 a las 22:06:52
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `speedeat`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `espacios`
--

CREATE TABLE `espacios` (
  `id_espacio` int(11) NOT NULL,
  `num` int(11) NOT NULL,
  `capacidad` int(11) NOT NULL,
  `tipo` varchar(80) NOT NULL,
  `disponible` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

CREATE TABLE `pedido` (
  `id_pedido` int(11) NOT NULL,
  `mesero_id` int(11) NOT NULL,
  `mesa_id` int(11) DEFAULT NULL,
  `cant_personas` int(11) NOT NULL,
  `tiempo_estimado` time DEFAULT NULL,
  `estado` varchar(80) NOT NULL,
  `total` decimal(10,2) DEFAULT 0.00,
  `fecha_hora` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Disparadores `pedido`
--
DELIMITER $$
CREATE TRIGGER `ActualizarCapacidad` AFTER INSERT ON `pedido` FOR EACH ROW BEGIN
    DECLARE capacidad_actual INT;

    -- Obtener la capacidad actual de la mesa
    SELECT capacidad
    INTO capacidad_actual
    FROM espacios
    WHERE id_espacio = NEW.mesa_id;

    -- Actualizar la capacidad restando la cantidad de personas del nuevo pedido
    UPDATE espacios
    SET capacidad = capacidad_actual - NEW.cant_personas
    WHERE id_espacio = NEW.mesa_id;

    -- Verificar si la capacidad es 0 y actualizar la disponibilidad
    IF capacidad_actual - NEW.cant_personas <= 0 THEN
        UPDATE espacios
        SET disponible = FALSE
        WHERE id_espacio = NEW.mesa_id;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `DevolverCapacidad` AFTER UPDATE ON `pedido` FOR EACH ROW BEGIN
    -- Solo actuar si el estado cambia a "CANCELADO" o "PAGADO"
    IF (OLD.estado NOT IN ('CANCELADO', 'PAGADO')) AND (NEW.estado IN ('CANCELADO', 'PAGADO')) THEN
        -- Actualizar la capacidad sumando la cantidad de personas del pedido
        UPDATE espacios
        SET capacidad = capacidad + OLD.cant_personas
        WHERE id_espacio = OLD.mesa_id;

        -- Verificar si la capacidad es mayor a 0 y actualizar la disponibilidad
        UPDATE espacios
        SET disponible = TRUE
        WHERE id_espacio = OLD.mesa_id
          AND capacidad > 0;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `DevolverProducto` AFTER UPDATE ON `pedido` FOR EACH ROW BEGIN
    -- Solo actuar si el estado cambia a "CANCELADO"
    IF OLD.estado <> 'CANCELADO' AND NEW.estado = 'CANCELADO' THEN
        -- Devolver las cantidades de los productos asociados al pedido
        UPDATE productos p
        JOIN pedidoproducto pp ON pp.producto_id = p.id_producto
        SET p.cantidad_disponible = p.cantidad_disponible + pp.cantidad
        WHERE pp.pedido_id = OLD.id_pedido;

        -- Actualizar el estado del producto si hay stock disponible
        UPDATE productos
        SET estado = TRUE
        WHERE id_producto IN (
            SELECT pp.producto_id
            FROM pedidoproducto pp
            WHERE pp.pedido_id = OLD.id_pedido
        ) AND cantidad_disponible > 0;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidoproducto`
--

CREATE TABLE `pedidoproducto` (
  `id_pedidoproducto` bigint(20) UNSIGNED NOT NULL,
  `pedido_id` int(11) NOT NULL,
  `producto_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Disparadores `pedidoproducto`
--
DELIMITER $$
CREATE TRIGGER `ActulizarCantidad` AFTER INSERT ON `pedidoproducto` FOR EACH ROW BEGIN
    DECLARE cantidad_disponible_actual INT;

    -- Obtener la cantidad disponible actual del producto
    SELECT cantidad_disponible
    INTO cantidad_disponible_actual
    FROM productos
    WHERE id_producto = NEW.producto_id;

    -- Actualizar la cantidad disponible restando la cantidad del nuevo pedido
    UPDATE productos
    SET cantidad_disponible = cantidad_disponible_actual - NEW.cantidad
    WHERE id_producto = NEW.producto_id;

    -- Verificar si la cantidad disponible es 0 y actualizar el estado
    IF cantidad_disponible_actual - NEW.cantidad <= 0 THEN
        UPDATE productos
        SET estado = FALSE
        WHERE id_producto = NEW.producto_id;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo` varchar(100) NOT NULL,
  `cantidad_disponible` int(11) NOT NULL,
  `precio` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `imagen` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(80) NOT NULL,
  `apellido` varchar(80) NOT NULL,
  `documento` varchar(100) NOT NULL,
  `telefono` varchar(100) NOT NULL,
  `correo` varchar(200) NOT NULL,
  `contraseña` varchar(200) NOT NULL,
  `is_su` tinyint(1) NOT NULL DEFAULT 0,
  `rol` varchar(100) NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `documento`, `telefono`, `correo`, `contraseña`, `is_su`, `rol`) VALUES
(3, 'carlos', 'peres', '1109185263', '3052826870', 'carlos@gmail.com', 'Juan1234', 1, 'Administrador');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `espacios`
--
ALTER TABLE `espacios`
  ADD PRIMARY KEY (`id_espacio`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `pedido_ibfk_2` (`mesa_id`),
  ADD KEY `pedido_ibfk_1` (`mesero_id`) USING BTREE;

--
-- Indices de la tabla `pedidoproducto`
--
ALTER TABLE `pedidoproducto`
  ADD PRIMARY KEY (`id_pedidoproducto`),
  ADD KEY `pedido_id` (`pedido_id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id_producto`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `espacios`
--
ALTER TABLE `espacios`
  MODIFY `id_espacio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `pedidoproducto`
--
ALTER TABLE `pedidoproducto`
  MODIFY `id_pedidoproducto` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`mesero_id`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `pedido_ibfk_2` FOREIGN KEY (`mesa_id`) REFERENCES `espacios` (`id_espacio`);

--
-- Filtros para la tabla `pedidoproducto`
--
ALTER TABLE `pedidoproducto`
  ADD CONSTRAINT `fk_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
