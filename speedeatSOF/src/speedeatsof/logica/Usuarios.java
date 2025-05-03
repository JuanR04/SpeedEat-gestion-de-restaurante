/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package speedeatsof.logica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Usuarios {
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String correo;
    private String contraseña;
    private boolean is_su=false;
    private String rol="Sin Asignar";

    public Usuarios() {
    }

    public Usuarios(int id_usuario,String nombre, String apellido, String documento, String telefono, String correo, String contraseña, boolean is_su, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.contraseña = contraseña;
        this.is_su = is_su;
        this.rol = rol;
        this.id_usuario= id_usuario;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public boolean isIs_su() {
        return is_su;
    }

    public void setIs_su(boolean is_su) {
        this.is_su = is_su;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
     public void guardar (){
        
       String sql = "insert into Usuarios(nombre, apellido, documento, telefono, correo, contraseña, is_su, rol) values ('" 
        + this.nombre + "', '" 
        + this.apellido + "', '" 
        + this.documento + "', '" 
        + this.telefono + "', '" 
        + this.correo + "', '" 
        + this.contraseña + "', " 
        + (this.is_su ? 1 : 0) + ", '" 
        + this.rol + "')";
        try{
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        }catch(Exception e){
            
        }
    }
    public void Actualizar() {
        String sql = "UPDATE Usuarios SET "
                + "nombre = '" + this.nombre + "', "
                + "apellido = '" + this.apellido + "', "
                + "documento = '" + this.documento + "', "
                + "telefono = '" + this.telefono + "', "
                + "correo = '" + this.correo + "', "
                + "contraseña = '" + this.contraseña + "', "
                + "is_su = " + (this.is_su ? 1 : 0) + ", "
                + "rol = '" + this.rol + "' "
                + "WHERE id_usuario = " + this.id_usuario;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql);
            c.cerrarConexion();
        } catch (Exception e) {
            // Manejo de excepciones
        }
    }
    
    

    public Usuarios login(String correo, String contraseña) {
        Conexion conexion = new Conexion();
        Connection con = conexion.con;
        String sql = "SELECT * FROM Usuarios WHERE correo = ? AND contraseña = ?";
        
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, correo);
            stm.setString(2, contraseña);
            ResultSet rs = stm.executeQuery();

            // Verifica si hay un resultado y crea el objeto Usuarios
            if (rs.next()) {
                Usuarios usuario = new Usuarios(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("documento"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("contraseña"),
                    rs.getBoolean("is_su"),
                    rs.getString("rol")
                );
                rs.close();
                conexion.cerrarConexion();
                return usuario;
            } else {
                rs.close();
                conexion.cerrarConexion();
                System.out.println("Usuario o contraseña incorrectos.");
                return null; // Usuario no encontrado
            }
        } catch (SQLException e) {
            System.out.println("Error en el login: " + e.getMessage());
            return null;
        }
    }
    public ArrayList<Usuarios> consultarUsuarios() throws SQLException{
        String sql="select * from usuarios";
        Conexion c = new Conexion();
        ResultSet rs = c.ejecutarConsulta(sql);
        ArrayList<Usuarios> usuarios = new ArrayList<>();
        while(rs.next()){
            Usuarios u = new Usuarios();
            u.setId_usuario(rs.getInt("id_usuario"));
            u.setNombre(rs.getString("nombre"));
            u.setApellido(rs.getString("apellido"));
            u.setDocumento(rs.getString("documento"));
            u.setTelefono(rs.getString("telefono"));
            u.setCorreo(rs.getString("correo"));
            u.setContraseña(rs.getString("contraseña"));
            u.setIs_su(rs.getBoolean("is_su"));
            u.setRol(rs.getString("rol"));
            usuarios.add(u);
            
            
        }
        return usuarios;
    }
    public ArrayList<Usuarios> ConsultarxTipo(String rol) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE rol = '" + rol + "'";
        Conexion c = new Conexion();
        ArrayList<Usuarios> usuariosT = new ArrayList<>();

        try {
            // Ejecutar la consulta
            ResultSet rs = c.ejecutarConsulta(sql);

            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setDocumento(rs.getString("documento"));
                u.setTelefono(rs.getString("telefono"));
                u.setCorreo(rs.getString("correo"));
                u.setContraseña(rs.getString("contraseña"));
                u.setIs_su(rs.getBoolean("is_su"));
                u.setRol(rs.getString("rol"));
                usuariosT.add(u);
            }

            rs.close(); // Cerrar ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.cerrarConexion(); // Asegurarse de cerrar la conexión
        }

        return usuariosT;
    }
    public Usuarios ConsultarxDocumento(String documento) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE documento = '" + documento + "'";
        Conexion c = new Conexion();
        Usuarios usuario = null;

        try {
            // Ejecutar la consulta
            ResultSet rs = c.ejecutarConsulta(sql);

            if (rs.next()) {
                usuario = new Usuarios();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setDocumento(rs.getString("documento"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContraseña(rs.getString("contraseña"));
                usuario.setIs_su(rs.getBoolean("is_su"));
                usuario.setRol(rs.getString("rol"));
            }

            rs.close(); // Cerrar ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.cerrarConexion(); // Asegurarse de cerrar la conexión
        }

        return usuario; // Retorna el objeto encontrado o null si no existe
    }
    public void eliminarUsuario(int id_usuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = " + id_usuario;

        try {
            Conexion c = new Conexion();
            c.ejecutar(sql); // Ejecuta la consulta
            c.cerrarConexion(); // Cierra la conexión
        } catch (Exception e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        }
    }

        



    
    
}
