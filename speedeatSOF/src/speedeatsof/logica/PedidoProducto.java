/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package speedeatsof.logica;
import java.sql.SQLException;
import java.sql.ResultSet;

public class PedidoProducto {

    // Atributos del modelo
    private int idPedidoProducto; // Opcional si usas un ID autogenerado
    private int pedidoId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;

    // Constructor
    
    public PedidoProducto() {
    }

    public PedidoProducto(int pedidoId, int productoId, int cantidad, double precioUnitario) {
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public int getIdPedidoProducto() {
        return idPedidoProducto;
    }

    public void setIdPedidoProducto(int idPedidoProducto) {
        this.idPedidoProducto = idPedidoProducto;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // Método para calcular el precio total
    public double getPrecioTotal() {
        return this.cantidad * this.precioUnitario;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "PedidoProducto{" +
               "idPedidoProducto=" + idPedidoProducto +
               ", pedidoId=" + pedidoId +
               ", productoId=" + productoId +
               ", cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario +
               ", precioTotal=" + getPrecioTotal() +
               '}';
    }
    public void Guardar() {
        // Crear la consulta SQL usando los atributos de la clase
        String sql = "INSERT INTO pedidoproducto (pedido_id, producto_id, cantidad, precio_unitario) VALUES (" 
                + this.pedidoId + ", " 
                + this.productoId + ", " 
                + this.cantidad + ", " 
                + this.precioUnitario + ")";

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql); // Ejecutar la consulta
            c.cerrarConexion(); // Cerrar la conexión
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }
    }
   public String CargarProductosMensaje(int pedidoId) {
        String sql = "SELECT p.nombre, pp.cantidad " +
                     "FROM productos p " +
                     "INNER JOIN pedidoproducto pp ON p.id_producto = pp.producto_id " +
                     "WHERE pp.pedido_id = " + pedidoId;

        StringBuilder mensaje = new StringBuilder();

        try {
            Conexion c = new Conexion();
            ResultSet rs = c.ejecutarConsulta(sql);  // Ejecutar la consulta y obtener el ResultSet

            while (rs.next()) {
                String nombreProducto = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad"); // Obtener la cantidad como entero

                // Formatear el mensaje con el nombre y la cantidad
                mensaje.append(nombreProducto)
                       .append(" (Cantidad: ").append(cantidad).append(")\n\n"); // Espacio entre productos
            }

            rs.close();  // Cerrar ResultSet
            c.cerrarConexion();  // Cerrar conexión
        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de error
        }

        return mensaje.toString().trim();  // Eliminar saltos de línea innecesarios al final
    }
}

