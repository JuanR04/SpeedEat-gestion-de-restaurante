package speedeatsof.logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexion {
    private final String usuario = "root";
    private final String clave = "";
    private final String url = "jdbc:mysql://localhost:3306/speedeat";
    public Connection con;

    public Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(url, usuario, clave);
            System.out.println("Conexión establecida con éxito.");
        } catch (SQLException sqle) {
            System.out.println("Error de conexión: " + sqle);
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver no encontrado: " + ex);
        }
    }

    // Método para obtener la conexión
    public Connection getConnection() {
        return this.con; // Retorna la conexión
    }

    public void ejecutar(String sql) throws SQLException {
        try (PreparedStatement stm = this.con.prepareStatement(sql)) {
            stm.execute();
        } catch (SQLException ex) {
            System.out.println("Error en la ejecución de SQL: " + ex);
            throw ex; // Lanza la excepción para que pueda manejarse en otro lugar
        }
    }

    public ResultSet ejecutarConsulta(String sql) throws SQLException {
        PreparedStatement stm = this.con.prepareStatement(sql);
        return stm.executeQuery(); // El llamador debe cerrar el ResultSet
    }

    public void cerrarConexion() {
        try {
            if (this.con != null && !this.con.isClosed()) {
                this.con.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e);
        }
    }
}

