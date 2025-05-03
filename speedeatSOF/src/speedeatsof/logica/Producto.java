package speedeatsof.logica;

import java.sql.*;
import java.util.ArrayList;

public class Producto {
    private int id_producto;
    private String nombre;
    private String tipo;
    private int cantidad;
    private int precio;
    private boolean estado = true;
    private byte[] imagen;

    public Producto() {
    }

    public Producto(int id_producto, String nombre, String tipo, int cantidad, int precio, byte[] imagen) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters y Setters
    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    // Método para guardar el producto (sin imagen)
    public void Guardar() {
        String sql = "INSERT INTO productos (nombre, tipo, cantidad_disponible, precio, estado) VALUES ('" 
                + this.nombre + "', '" 
                + this.tipo + "', " 
                + this.cantidad + ", " 
                + this.precio + ", " 
                + (this.estado ? 1 : 0) + ")";

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }
    }

    // Método para actualizar el producto
    public void Actualizar() {
        String sql = "UPDATE productos SET "
                + "nombre = '" + this.nombre + "', "
                + "tipo = '" + this.tipo + "', "
                + "cantidad_disponible = " + this.cantidad + ", "
                + "precio = " + this.precio + ", "
                + "estado = " + this.estado + " "
                + "WHERE id_producto = " + this.id_producto;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }
    }

    // Método para eliminar un producto
    public void eliminarProducto(int id_producto) {
        String sql = "DELETE FROM productos WHERE id_producto = " + id_producto;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
        }
    }
    public ArrayList<Producto> consultarProductos() throws SQLException {
        String sql = "SELECT * FROM productos";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Producto> productos = new ArrayList<>();

        while (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setTipo(rs.getString("tipo"));
            p.setCantidad(rs.getInt("cantidad_disponible"));
            p.setPrecio(rs.getInt("precio"));
            p.setEstado(rs.getBoolean("estado"));
            productos.add(p);
        }
        return productos;
    }

    // Método para consultar productos (sin imagen)
    public ArrayList<Producto> consultarProductosxTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM productos WHERE tipo='" + tipo + "'";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Producto> productos = new ArrayList<>();

        while (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setTipo(rs.getString("tipo"));
            p.setCantidad(rs.getInt("cantidad_disponible"));
            p.setPrecio(rs.getInt("precio"));
            p.setEstado(rs.getBoolean("estado"));
            productos.add(p);
        }
        return productos;
    }

    // Método para agregar un producto con imagen
    public void guardarConImagen() {
        String sql = "INSERT INTO productos (nombre, tipo, cantidad_disponible, precio, estado, imagen) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = new Conexion().con;
             PreparedStatement stm = con.prepareStatement(sql)) {
            
            stm.setString(1, this.nombre);
            stm.setString(2, this.tipo);
            stm.setInt(3, this.cantidad);
            stm.setInt(4, this.precio);
            stm.setBoolean(5, this.estado);
            if (this.imagen != null) {
                stm.setBytes(6, this.imagen); // Establecer la imagen como un arreglo de bytes
            }
            stm.executeUpdate();
            System.out.println("Producto con imagen guardado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al guardar el producto con imagen: " + e.getMessage());
        }
    }

    // Método para obtener una imagen de un producto
    public byte[] obtenerImagen(int id_producto) {
        String sql = "SELECT imagen FROM productos WHERE id_producto = ?";
        byte[] imagen = null;

        try (Connection con = new Conexion().con;
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setInt(1, id_producto);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                imagen = rs.getBytes("imagen");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la imagen: " + e.getMessage());
        }

        return imagen;
    }
}

    
