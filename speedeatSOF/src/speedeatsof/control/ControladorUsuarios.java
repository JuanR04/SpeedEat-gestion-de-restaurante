package speedeatsof.control;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import speedeatsof.logica.Usuarios;
import speedeatsof.logica.Validaciones;
import speedeatsof.vista.Administrador.UsuariosPanel;
import speedeatsof.vista.Administrador.VentanaAdministrador;
import speedeatsof.vista.Mesero.VentanaMesero;
import speedeatsof.vista.Principal.VentanaPrincipal;


public class ControladorUsuarios implements ActionListener, ItemListener{
    private VentanaPrincipal p;
    private VentanaAdministrador a;
    private VentanaMesero m;

    // Constructor que recibe las instancias de VentanaPrincipal y inicioSesion
    public ControladorUsuarios() {
        this.p = VentanaPrincipal.getInstancia();
        this.a = VentanaAdministrador.getInstancia();
        this.m = VentanaMesero.getInstancia();
        this.p.setVisible(true);
        this.p.setLocationRelativeTo(null);
        // Configurar los ActionListeners 
        this.p.getInicioPanel().getBtninicio().addActionListener(this);
        this.p.getBtnIncio().addActionListener(this);
        this.p.getBtnRegistro().addActionListener(this);
        this.p.getRegistroPanel().getBtnregistrar().addActionListener(this);
        // listener de UsuarioPanel
        this.a.getUp().getBtnAgregarUsuario().addActionListener(this);
        this.a.getUp().getBtnSeleccionar().addActionListener(this);
        this.a.getUp().getBtnEliminar().addActionListener(this);
        this.a.getUp().getBtnEditar().addActionListener(this);
        this.a.getUp().getCbxTipo().addItemListener(this);
        this.a.getUp().getBtnBuscar().addActionListener(this);
        //cargar tabla usuarios
        HiloCargarTablaUsuario hiloTU = new HiloCargarTablaUsuario(this);
        hiloTU.start();
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Registrar")){
            RegistrarUsuario();
        }
        if (e.getActionCommand().equals("Iniciar Sesión")){
            IniciarSesion();
        }
        if (e.getActionCommand().equals("Agregar Usuario")){
            AgregarUsuario();
        }
        if(e.getActionCommand().equals("Seleccionar")){
            SeleccionarUsuario();
            
        }
        if (e.getActionCommand().equals("Editar")){
            EditarUsuario();
            a.getUp().getBtnEditar().setEnabled(false);
            a.getUp().getBtnAgregarUsuario().setEnabled(true);
            
        }
        if (e.getActionCommand().equals("Buscar")){
           String numdocumento = a.getUp().getTxtDocBuscar().getText();
           if(numdocumento.isEmpty()){
               JOptionPane.showMessageDialog(null,"Ingresa el numero de documento que deseas Buscar");
               
           }else{
               try {
                   CargarUsuarioxDocumento(numdocumento);
               } catch (SQLException ex) {
                   Logger.getLogger(ControladorUsuarios.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        }
        if(e.getSource().equals(a.getUp().getBtnEliminar())){
            EliminarUsuario();
        }
            
            
            
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        JComboBox tipo = a.getUp().getCbxTipo();
        if (e.getSource().equals(tipo)){
            String Tipo = (String) tipo.getSelectedItem();
            try {
                CargarUsuarioxTipo(Tipo);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public void IniciarSesion(){
        Usuarios u = new Usuarios();
        String correo = p.getInicioPanel().getTxtcorreoi().getText();
        String contra = String.valueOf(p.getInicioPanel().getTxtcontrai().getPassword());

        Usuarios usuarioLogueado = u.login(correo, contra);
        if (usuarioLogueado != null) {
                switch (usuarioLogueado.getRol()) {
                    case "Administrador" -> {
                        a.habilitarOpcionesSuperUsuario(usuarioLogueado);
                        a.setUsuarioLogueado(usuarioLogueado);
                        a.setVisible(true);
                        a.setLocationRelativeTo(null);
                        p.dispose();
                    }
                    case "Mesero" -> {
                        m.setUsuarioLogueado(usuarioLogueado);
                        m.setVisible(true);
                        m.setLocationRelativeTo(null);
                        p.dispose();
                    }
                    default -> JOptionPane.showMessageDialog(null, "Rol no asignado, espere a que un super admin le asigne uno.");
                }
            } else {
                JOptionPane.showMessageDialog(p, "Correo o contraseña incorrectos.");
            }
    }
    public void RegistrarUsuario(){
        Usuarios u = new Usuarios();
        Validaciones v = new Validaciones();
            String nombre = p.getRegistroPanel().getTxtnombre().getText();
            String apellido = p.getRegistroPanel().getTxtapellido().getText();
            String documento = p.getRegistroPanel().getTxtdocumento().getText();
            String telefono = p.getRegistroPanel().getTxttelefono().getText();
            String correo = p.getRegistroPanel().getTxtcorreo().getText();
            String contra = String.valueOf(p.getRegistroPanel().getTxtpass().getPassword());
            String validar =v.validarDatosUsuario(nombre,apellido,documento,telefono,correo,contra);
            if (validar.equals("correcto")){
                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setDocumento(documento);
                u.setTelefono(telefono);
                u.setCorreo(correo);
                u.setContraseña(contra);
                u.guardar();
                JOptionPane.showMessageDialog(null, "Usuario registrado correctamente...");
            }else{
               JOptionPane.showMessageDialog(null, validar);
            
            }
    }
    //funcion administrador Crud
    public void AgregarUsuario(){
        Usuarios u = new Usuarios();
        Validaciones v = new Validaciones();
            String nombre= a.getUp().getTxtNombre().getText();
            String apellido=a.getUp().getTxtApellido().getText();
            String documento = a.getUp().getTxtDocumento().getText();
            String telefono = a.getUp().getTxtTelefono().getText();
            String correo = a.getUp().getTxtCorreo().getText();
            String contraseña = a.getUp().getTxtContraseña().getText();
            String rol = String.valueOf(a.getUp().getCbxRol().getSelectedItem());
            String su = String.valueOf(a.getUp().getCbxSu());
            String validar = v.validarDatosUsuario(nombre,apellido,documento,telefono,correo,contraseña);
            if (validar.equals("correcto")){
                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setDocumento(documento);
                u.setTelefono(telefono);
                u.setCorreo(correo);
                u.setContraseña(contraseña);
                u.setRol(rol);
                if(su.equals("Si")){
                    u.setIs_su(true);
                }else{
                    u.setIs_su(false);
                }
                u.guardar();
                cargarTabla();
                JOptionPane.showMessageDialog(null, "Usuario registrado correctamente...");
            }else{
               JOptionPane.showMessageDialog(null, validar);
            
            }
        
    }
    public void SeleccionarUsuario(){
        a.getUp().getBtnEditar().setEnabled(true);
        a.getUp().getBtnAgregarUsuario().setEnabled(false);
        int fila = a.getUp().getTablaUsuarios().getSelectedRow();
                a.getUp().getLabelId().setText(a.getUp().getDtm().getValueAt(fila, 0).toString());
                a.getUp().getTxtNombre().setText(a.getUp().getDtm().getValueAt(fila, 1).toString());
                a.getUp().getTxtApellido().setText(a.getUp().getDtm().getValueAt(fila, 2).toString());
                a.getUp().getTxtDocumento().setText(a.getUp().getDtm().getValueAt(fila, 3).toString());
                a.getUp().getTxtTelefono().setText(a.getUp().getDtm().getValueAt(fila, 4).toString());
                a.getUp().getTxtCorreo().setText(a.getUp().getDtm().getValueAt(fila, 5).toString());
                a.getUp().getTxtContraseña().setText(a.getUp().getDtm().getValueAt(fila, 6).toString());
                String is_su = a.getUp().getDtm().getValueAt(fila, 7).toString();
                if (is_su.equals("true")){
                     a.getUp().getCbxSu().setSelectedIndex(1);

                }else{
                     a.getUp().getCbxSu().setSelectedIndex(2);
                }
                String role = String.valueOf(a.getUp().getDtm().getValueAt(fila, 8));
                switch (role) {
                    case "user" -> a.getUp().getCbxRol().setSelectedIndex(1);
                    case "Mesero" -> a.getUp().getCbxRol().setSelectedIndex(2);
                    case "Administrador" -> a.getUp().getCbxRol().setSelectedIndex(3);
                    default -> {
                    }
                }
    }
    public void EliminarUsuario(){
        Usuarios u = new Usuarios();
        int fila = a.getUp().getTablaUsuarios().getSelectedRow();
        int id_usuario = (int) a.getUp().getDtm().getValueAt(fila, 0);
        u.eliminarUsuario(id_usuario);
        JOptionPane.showMessageDialog(null, "Usuario Eliminado correctamente");
        cargarTabla();
        
    }
    public void EditarUsuario(){
        Usuarios u = new Usuarios(); // Instancia de la clase Usuarios
        Validaciones v = new Validaciones();
    
        // Obtener los valores desde la interfaz gráfica
        int id = Integer.parseInt(a.getUp().getLabelId().getText());
        String nombre = a.getUp().getTxtNombre().getText();
        String apellido = a.getUp().getTxtApellido().getText();
        String documento = a.getUp().getTxtDocumento().getText();
        String telefono = a.getUp().getTxtTelefono().getText();
        String correo = a.getUp().getTxtCorreo().getText();
        String contraseña = a.getUp().getTxtContraseña().getText();
        String rol = a.getUp().getCbxRol().getSelectedItem().toString();
        String is_su = String.valueOf(a.getUp().getCbxSu().getSelectedItem()); // Ejemplo: 0 para true, 1 para false
        
        String validar = v.validarDatosUsuario(nombre, apellido, documento, telefono, correo, contraseña);
        if (validar.equals("correcto")){
            // Setear los valores en el objeto Usuarios
            u.setId_usuario(id);
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setDocumento(documento);
            u.setTelefono(telefono);
            u.setCorreo(correo);
            u.setContraseña(contraseña);
            u.setRol(rol);
            if (is_su.equals("Si")){
                u.setIs_su(true);
            }else{
                u.setIs_su(false);
            }
        

            // Llamar al método para actualizar en la base de datos
            JOptionPane.showMessageDialog(null, "Usuario editado Correctamente");
            u.Actualizar();

            // Recargar la tabla para reflejar los cambios
            cargarTabla();
        }
            
        
    }
    
    public void cargarTabla(){
        a.getUp().getDtm().setRowCount(0);
        Usuarios u = new Usuarios();
         try {
               ArrayList<Usuarios> usuarios = u.consultarUsuarios();
               for(Usuarios user : usuarios){
               a.getUp().getDtm().addRow(new Object[]{user.getId_usuario(),user.getNombre(),user.getApellido(),user.getDocumento(),user.getTelefono(),user.getCorreo(),user.getContraseña(),user.isIs_su(),user.getRol()});
               }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(UsuariosPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    }

    private void CargarUsuarioxTipo(String rolSeleccionado) throws SQLException {
        a.getUp().getDtm().setRowCount(0); // Limpiar tabla antes de cargar datos

        Usuarios u = new Usuarios();
        ArrayList<Usuarios> usuarios = u.ConsultarxTipo(rolSeleccionado); // Consultar usuarios por tipo

        for (Usuarios user : usuarios) {
            a.getUp().getDtm().addRow(new Object[]{
                user.getId_usuario(),
                user.getNombre(),
                user.getApellido(),
                user.getDocumento(),
                user.getTelefono(),
                user.getCorreo(),
                user.getContraseña(),
                user.isIs_su(),
                user.getRol()
            });
        }
    }
    private void CargarUsuarioxDocumento(String documento) throws SQLException {
        a.getUp().getDtm().setRowCount(0); // Limpiar la tabla antes de cargar datos

        Usuarios u = new Usuarios();
        Usuarios usuarioEncontrado = u.ConsultarxDocumento(documento);

        if (usuarioEncontrado != null) {
            a.getUp().getDtm().addRow(new Object[]{
                usuarioEncontrado.getId_usuario(),
                usuarioEncontrado.getNombre(),
                usuarioEncontrado.getApellido(),
                usuarioEncontrado.getDocumento(),
                usuarioEncontrado.getTelefono(),
                usuarioEncontrado.getCorreo(),
                usuarioEncontrado.getContraseña(),
                usuarioEncontrado.isIs_su(),
                usuarioEncontrado.getRol()
            });
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró ningún usuario con el documento: " + documento);
        }
    }



    
    
    
}




    

