
package speedeatsof.logica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author juanR
 */
public class Espacios {
    private int id_espacios;
    private int num;
    private int capacidad;
    private String Tipo;
    private boolean disponible = true;

    public Espacios() {
    }

    public Espacios(int id_espacios,int num, int capacidad, String Tipo) {
        this.num = num;
        this.capacidad = capacidad;
        this.Tipo = Tipo;
        this.id_espacios = id_espacios;
    }

    public int getId_espacios() {
        return id_espacios;
    }

    public void setId_espacios(int id_espacios) {
        this.id_espacios = id_espacios;
    }
    
    
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public void Guardar(){
        String sql = "insert into espacios(num, capacidad, Tipo, disponible) values (" 
            + this.num + ", " 
            + this.capacidad + ", '" 
            + this.Tipo + "', " 
            + (this.disponible ? 1 : 0) + ")";

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejar el error adecuadamente
        }
    }
    public void Actualizar() {
    String sql = "UPDATE espacios SET "
            + "num = " + this.num + ", "
            + "capacidad = " + this.capacidad + ", "
            + "tipo = '" + this.Tipo + "', "
            + "disponible = " + (this.disponible ? 1 : 0) + " "
            + "WHERE id_espacio = " + this.id_espacios;

    try {
        Conexion c = new Conexion();
        c.ejecutar(sql);
        c.cerrarConexion();
    } catch (SQLException e) {
        // Manejo de excepciones
        e.printStackTrace();
    }
}


    public ArrayList<Espacios> consultarEspacios() throws SQLException {
        String sql="select * from espacios";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Espacios> espacios = new ArrayList<>();
        while(rs.next()){
            Espacios e = new Espacios();
            e.setId_espacios(rs.getInt("id_espacio"));
            e.setNum(rs.getInt("num"));
            e.setCapacidad(rs.getInt("capacidad"));
            e.setTipo(rs.getString("tipo"));
            e.setDisponible(rs.getBoolean("disponible"));
            espacios.add(e);
            
            
        }
        return espacios;
    }
    public ArrayList<Espacios> consultarEspaciosxTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM espacios WHERE tipo='" + tipo + "'";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Espacios> espacios = new ArrayList<>();
        while(rs.next()){
            Espacios e = new Espacios();
            e.setId_espacios(rs.getInt("id_espacio"));
            e.setNum(rs.getInt("num"));
            e.setCapacidad(rs.getInt("capacidad"));
            e.setTipo(rs.getString("tipo"));
            e.setDisponible(rs.getBoolean("disponible"));
            espacios.add(e);
            
            
        }
        return espacios;
    }
    public void EliminarEspacios(int id_espacio){
        String sql = "DELETE FROM espacios WHERE id_espacio = " + id_espacio;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql); // Ejecuta la consulta
            c.cerrarConexion(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        }
    }
    public void CambiarEstado(int id_espacio) {
        String sql = "UPDATE espacios SET disponible='false' WHERE id_espacio=" + id_espacio;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de error
        }
    }   
}
