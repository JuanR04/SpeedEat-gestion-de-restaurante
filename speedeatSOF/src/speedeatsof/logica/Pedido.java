
package speedeatsof.logica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDate;


public class Pedido {

    // Atributos del modelo
    private int id;                
    private int meseroId;          
    private int mesaId; 
    private int cant_personas;
    private LocalTime tiempoEstimado; 
    private String estado;         
    private double total;
    private LocalDateTime fechaHora; 

    // Constructor
    
    public Pedido() {
    }

    public Pedido(int meseroId, int mesaId,int cant_personas,LocalTime tiempoEstimado, String estado, double total, LocalDateTime fechaHora) {
        this.meseroId = meseroId;
        this.mesaId = mesaId;
        this.cant_personas = cant_personas;
        this.tiempoEstimado = tiempoEstimado;
        this.estado = estado;
        this.total = total;
        this.fechaHora = fechaHora;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeseroId() {
        return meseroId;
    }

    public void setMeseroId(int meseroId) {
        this.meseroId = meseroId;
    }

    public int getMesaId() {
        return mesaId;
    }

    public void setMesaId(int mesaId) {
        this.mesaId = mesaId;
    }

    public int getCant_personas() {
        return cant_personas;
    }

    public void setCant_personas(int cant_personas) {
        this.cant_personas = cant_personas;
    }
    

    public LocalTime getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(LocalTime tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Pedido{" +
               "id=" + id +
               ", meseroId=" + meseroId +
               ", mesaId=" + mesaId +
               ", tiempoEstimado='" + tiempoEstimado + '\'' +
               ", estado='" + estado + '\'' +
               ", total=" + total +
               ", fechaHora=" + fechaHora +
               '}';
    }
    
    //consultas sql
    public ArrayList<Producto> ConsultaProducto(String tipo) throws SQLException{
        String sql = "SELECT id_producto,nombre,tipo,precio FROM productos WHERE tipo = '" + tipo + "' AND estado = TRUE ";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Producto> productos = new ArrayList<>();

        while (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setTipo(rs.getString("tipo"));
            p.setPrecio(rs.getInt("precio"));
            productos.add(p);
        }
        return productos;
    }
    public ArrayList<Espacios> ConsultaEspacios(String tipo) throws SQLException {
        String sql = "SELECT id_espacio, num, capacidad, tipo FROM espacios WHERE tipo = '" + tipo + "' AND disponible = TRUE";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Espacios> espacios = new ArrayList<>();

        while (rs.next()) {
            Espacios e = new Espacios();
            e.setId_espacios(rs.getInt("id_espacio"));
            e.setNum(rs.getInt("num"));
            e.setCapacidad(rs.getInt("capacidad"));
            e.setTipo(rs.getString("tipo"));

            espacios.add(e);
        }
        return espacios;
    }


   public void GuardarPedido() {
        String sql = "INSERT INTO pedido (mesero_id, mesa_id, cant_personas, tiempo_estimado, estado, total, fecha_hora) VALUES (?, ?, ?, ?, ?, ?, ?) ";

        // Crear una instancia de Conexion
        Conexion c = null;
        java.sql.PreparedStatement stmt = null;

        try {
            c = new Conexion(); // Crear la conexión
            stmt = c.getConnection().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, this.meseroId);
            stmt.setInt(2, this.mesaId);
            stmt.setInt(3, this.cant_personas);
            stmt.setString(4, this.tiempoEstimado.toString());
            stmt.setString(5, this.estado);
            stmt.setDouble(6, this.total);
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(this.fechaHora));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close(); // Cerrar el PreparedStatement
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (c != null) {
                c.cerrarConexion(); // Llamar al método cerrarConexion para cerrar la conexión
            }
        }
    }
    public String ConsultarMesero(int meseroId) {
        String sql = "SELECT u.nombre, u.apellido " +
                      "FROM usuarios u " +
                      "INNER JOIN pedido p ON u.id_usuario = p.mesero_id " +
                      "WHERE p.mesero_id = " + meseroId;

         String nombreCompleto = "";

         try {
             Conexion c = new Conexion();
             ResultSet rs = c.ejecutarConsulta(sql); // Ejecutar la consulta

             if (rs.next()) {
                 String nombre = rs.getString("nombre");
                 String apellido = rs.getString("apellido");
                 nombreCompleto = nombre + " " + apellido;
             }

             c.cerrarConexion(); // Cerrar la conexión

         } catch (SQLException e) {
             e.printStackTrace(); // Manejo de error
         }

         return nombreCompleto;
    }
    public String ConsultarEspacio(int mesa_id) {
        String sql = "SELECT e.num " + // Solo seleccionamos el número de la mesa
                 "FROM espacios e " +
                 "INNER JOIN pedido p ON e.id_espacio = p.mesa_id " +
                 "WHERE p.mesa_id = " + mesa_id;

        String espacioInfo = "";

        try {
            Conexion c = new Conexion();
            ResultSet rs = c.ejecutarConsulta(sql); // Ejecutar la consulta

            if (rs.next()) {
                int num = rs.getInt("num"); // Obtenemos solo el número de la mesa
                espacioInfo = "Mesa " + num; // Formateamos el resultado
            } else {
                espacioInfo = "Mesa no encontrada."; // Manejo de caso donde no haya resultados
            }

            rs.close(); // Cerrar el ResultSet
            c.cerrarConexion(); // Cerrar la conexión
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
            espacioInfo = "Error al consultar la mesa.";
        }

        return espacioInfo;
    }
    public ArrayList<Pedido> ConsultarPedido(String estado) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE estado= '"+estado+"'";

        ArrayList<Pedido> pedidos = new ArrayList<>();

        try {
            Conexion c = new Conexion();
            ResultSet rs = c.ejecutarConsulta(sql); // Ejecutar la consulta

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id_pedido"));
                pedido.setMesaId(rs.getInt("mesa_id"));
                pedido.setMeseroId(rs.getInt("mesero_id"));
                pedido.setCant_personas(rs.getInt("cant_personas"));
                pedido.setTiempoEstimado(rs.getTime("tiempo_estimado").toLocalTime());
                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());

                pedidos.add(pedido);
            }

            c.cerrarConexion(); // Cerrar la conexión

        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }

        return pedidos;
    }
    public void CambiarEstado(String estado, int id){
        String sql = "UPDATE pedido SET estado='"+estado+"' WHERE id_pedido ='"+id+"'";
        
        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }
        
    }
    public ArrayList<Pedido> ConsultarPedidoFecha(String estado, LocalDate fechaSeleccionada) throws SQLException {
        // SQL con la función DATE() para comparar solo la fecha (sin la parte de la hora)
        String sql = "SELECT * FROM pedido WHERE estado = '" + estado + "' AND DATE(fecha_hora) = '" + fechaSeleccionada + "'";

        ArrayList<Pedido> pedidos = new ArrayList<>();

        try {
            Conexion c = new Conexion();
            ResultSet rs = c.ejecutarConsulta(sql); // Ejecutar la consulta con el método predefinido

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id_pedido"));
                pedido.setMesaId(rs.getInt("mesa_id"));
                pedido.setMeseroId(rs.getInt("mesero_id"));
                pedido.setCant_personas(rs.getInt("cant_personas"));
                pedido.setTiempoEstimado(rs.getTime("tiempo_estimado").toLocalTime());
                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());

                pedidos.add(pedido);
            }

            c.cerrarConexion(); // Cerrar la conexión

        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }

        return pedidos;
    }

    public ArrayList<Pedido> ConsultarPedidoRango(String estado, LocalDate fecha1, LocalDate fecha2) throws SQLException {
        // SQL para consultar los pedidos dentro de un rango de fechas y con un estado específico
        String sql = "SELECT * FROM pedido WHERE estado = '" + estado + "' AND DATE(fecha_hora) BETWEEN '" + fecha1 + "' AND '" + fecha2 + "'";

        ArrayList<Pedido> pedidos = new ArrayList<>();

        try {
            Conexion c = new Conexion();
            ResultSet rs = c.ejecutarConsulta(sql); // Ejecutar la consulta con el método predefinido

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id_pedido"));
                pedido.setMesaId(rs.getInt("mesa_id"));
                pedido.setMeseroId(rs.getInt("mesero_id"));
                pedido.setCant_personas(rs.getInt("cant_personas"));
                pedido.setTiempoEstimado(rs.getTime("tiempo_estimado").toLocalTime());
                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());

                pedidos.add(pedido);
            }

            c.cerrarConexion(); // Cerrar la conexión

        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }

        return pedidos;
    }

   




}


