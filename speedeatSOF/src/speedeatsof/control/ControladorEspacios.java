
package speedeatsof.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import speedeatsof.logica.Espacios;
import speedeatsof.logica.Validaciones;
import speedeatsof.vista.Administrador.ProductosPanel;
import speedeatsof.vista.Administrador.VentanaAdministrador;

/**
 *
 * @author juanR
 */
public class ControladorEspacios implements ActionListener,ItemListener{
    VentanaAdministrador a;
    ButtonGroup group = new ButtonGroup();
    public ControladorEspacios() {
        this.a = VentanaAdministrador.getInstancia();
        CargarTabla();
        IniciarEventos();

        // Configuración del grupo y botones
        JRadioButton radioBarra = a.getEp().getRadioBarra();
        JRadioButton radioMesa = a.getEp().getRadioMesa();

        group.add(radioBarra);
        group.add(radioMesa);

        // Configuración de ActionCommand (por si no están configurados previamente)
        radioBarra.setActionCommand("Barra");
        radioMesa.setActionCommand("Mesa");
    }
    
    private void IniciarEventos() {
        a.getEp().getBtnAgregar().addActionListener(this);
        a.getEp().getBtnEditar().addActionListener(this);
        a.getEp().getBtnEliminar().addActionListener(this);
        a.getEp().getBtnSeleccionar().addActionListener(this);
        a.getEp().getCbxBuscar().addItemListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource().equals(a.getEp().getBtnAgregar())){
            AgregarEspacio();
        }
        if (e.getSource().equals(a.getEp().getBtnSeleccionar())){
            SeleccionarEspacio();
        }
        if (e.getSource().equals(a.getEp().getBtnEditar())){
            EditarEspacios();
            a.getEp().getBtnEditar().setEnabled(false);
            a.getEp().getBtnAgregar().setEnabled(true);
            
        }
        if (e.getSource().equals(a.getEp().getBtnEliminar())){
            EliminarEspacios();
        }
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource().equals(a.getEp().getCbxBuscar())){
            String tipo = (String) a.getEp().getCbxBuscar().getSelectedItem();
            CargarTablaxTipo(tipo);
        }
    }
    

    private void CargarTabla() {
        a.getEp().getDtm().setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
        Espacios e = new Espacios();
        try {
            ArrayList<Espacios> espacios = e.consultarEspacios(); // Llama al método para obtener los productos
            for (Espacios es : espacios) {
                // Añade los datos de cada producto como una fila en el modelo de la tabla
                a.getEp().getDtm().addRow(new Object[]{
                    es.getId_espacios(),
                    es.getNum(),
                    es.getCapacidad(),
                    es.getTipo(),
                    es.isDisponible()  
                });
            }
        }catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ProductosPanel.class.getName())
                .log(Level.SEVERE, "Error al cargar los productos en la tabla", ex);
        }
    }
    private void CargarTablaxTipo(String tipo) {
        a.getEp().getDtm().setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
        Espacios e = new Espacios();
        try {
            ArrayList<Espacios> espacios = e.consultarEspaciosxTipo(tipo); // Llama al método para obtener los productos
            for (Espacios es : espacios) {
                // Añade los datos de cada producto como una fila en el modelo de la tabla
                a.getEp().getDtm().addRow(new Object[]{
                    es.getId_espacios(),
                    es.getNum(),
                    es.getCapacidad(),
                    es.getTipo(),
                    es.isDisponible()  
                });
            }
        }catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ProductosPanel.class.getName())
                .log(Level.SEVERE, "Error al cargar los productos en la tabla", ex);
        }
    }

    private void AgregarEspacio() {
        Espacios ep = new Espacios();
        Validaciones v = new Validaciones();
        String Seleccion = null;

        // Obtener valores de los campos de texto
        int num = Integer.parseInt(a.getEp().getTxtNumero().getText());
        int capacidad = Integer.parseInt(a.getEp().getTxtCapacidad().getText());

        // Verificar la selección del grupo de botones
        ButtonModel selectedModel = group.getSelection();
        if (selectedModel == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una opción de tipo.");
            return; // Salir del método si no hay selección
        } else {
            Seleccion = selectedModel.getActionCommand(); // Obtener el texto del botón seleccionado
        }

        // Validar los datos
        String validar = v.validarEspacios(num, capacidad);
        if (!"Datos validos".equals(validar)) {
            JOptionPane.showMessageDialog(null, validar);
        } else {
            ep.setNum(num);
            ep.setCapacidad(capacidad);
            ep.setTipo(Seleccion); // Asignar directamente el texto seleccionado
            ep.Guardar();
            CargarTabla();
            JOptionPane.showMessageDialog(null, "Espacio agregado correctamente.");
        }
    }
    public void SeleccionarEspacio() {
        a.getEp().getBtnEditar().setEnabled(true);
        a.getEp().getBtnAgregar().setEnabled(false);

        int fila = a.getEp().getTableEspacios().getSelectedRow();
        a.getEp().getLabelid().setText(a.getEp().getDtm().getValueAt(fila, 0).toString());
        a.getEp().getTxtNumero().setText(a.getEp().getDtm().getValueAt(fila, 1).toString());
        a.getEp().getTxtCapacidad().setText(a.getEp().getDtm().getValueAt(fila, 2).toString());

        String tipo = String.valueOf(a.getEp().getDtm().getValueAt(fila, 3));  // Obtienes el tipo
        switch (tipo) {
            case "Mesa" -> a.getEp().getRadioMesa().setSelected(true);
            case "Barra" -> a.getEp().getRadioBarra().setSelected(true);
            default -> {
                // Manejo por defecto, si es necesario
            }
        }
    }
    public void EditarEspacios() {
        Espacios e = new Espacios(); // Crear un objeto Espacio
        Validaciones v = new Validaciones(); // Para validaciones de entrada

        // Obtener los datos de la interfaz
        int id_espacio = Integer.parseInt(a.getEp().getLabelid().getText());
        int numero = Integer.parseInt(a.getEp().getTxtNumero().getText());
        int capacidad = Integer.parseInt(a.getEp().getTxtCapacidad().getText());
        String tipo = ""; 

        // Determinar el tipo de espacio dependiendo de cuál radioButton esté seleccionado
        if (a.getEp().getRadioMesa().isSelected()) {
            tipo = "Mesa";  // Si está seleccionada la opción Mesa
        } else if (a.getEp().getRadioBarra().isSelected()) {
            tipo = "Barra"; // Si está seleccionada la opción Barra
        }

        // Validar los datos
        String validar = v.validarEspacios(numero, capacidad);  // Crear el método de validación para espacios
        if (!validar.equals("Datos validos")){
            JOptionPane.showMessageDialog(null, validar);  // Muestra mensaje de error si no son válidos
        } else {
            // Setear los atributos del objeto espacio
            e.setId_espacios(id_espacio);
            e.setNum(numero);
            e.setCapacidad(capacidad);
            e.setTipo(tipo);
            e.Actualizar();  
            CargarTabla();

            // Mensaje de confirmación
            JOptionPane.showMessageDialog(null, "Espacio actualizado correctamente...");
        }
}

    private void EliminarEspacios() {
        Espacios ep = new Espacios();
        int fila = a.getEp().getTableEspacios().getSelectedRow();

        if (fila == -1) { // Verifica si hay una fila seleccionada
            JOptionPane.showMessageDialog(null, "Por favor, selecciona un espacio para eliminar.");
            return;
        }

        // Mostrar cuadro de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
            null, 
            "¿Estás segura que quieres eliminar el espacio?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) { // Si el usuario confirma
            try {
                int id = Integer.parseInt(a.getEp().getDtm().getValueAt(fila, 0).toString());
                ep.EliminarEspacios(id);
                JOptionPane.showMessageDialog(null, "Espacio eliminado correctamente.");
                CargarTabla(); // Actualiza la tabla
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el espacio: " + e.getMessage());
            }
        } else { // Si el usuario cancela
            JOptionPane.showMessageDialog(null, "Eliminación cancelada.");
        }
}

    

    





    
}
