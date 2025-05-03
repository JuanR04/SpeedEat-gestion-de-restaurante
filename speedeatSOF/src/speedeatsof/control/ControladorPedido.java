package speedeatsof.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import speedeatsof.utils.ImagenCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import speedeatsof.logica.Espacios;
import speedeatsof.logica.Pedido;
import speedeatsof.logica.PedidoProducto;
import speedeatsof.logica.Producto;
import speedeatsof.logica.Usuarios;
import speedeatsof.vista.Administrador.VentanaAdministrador;
import speedeatsof.vista.Mesero.VentanaMesero;

public final class ControladorPedido implements ActionListener, ItemListener, ListSelectionListener  {
    VentanaMesero m;
    VentanaAdministrador a;
    Usuarios mesero;
    private ArrayList<String[]> productosSeleccionados = new ArrayList<>();
    private static ControladorPedido instancia;

    public ControladorPedido() {
        System.out.println("Instanciando ControladorPedido...");
        this.m = VentanaMesero.getInstancia();
        this.mesero = mesero;
        this.a = VentanaAdministrador.getInstancia();
        InciarEventos();
        Configuraracion();
    }
    
    
    public static ControladorPedido getInstancia() {
        if (instancia == null) {
            instancia = new ControladorPedido();
        }
        return instancia;
    }

    private void InciarEventos() {
        // Botones
        m.getPe().getBtnAceptar().addActionListener(this);
        m.getPe().getBtnAgregarP().addActionListener(this);
        m.getPe().getBtnSeleccionar().addActionListener(this);
        m.getPo().getBtnEntregar().addActionListener(this);
        m.getPo().getBtnPagar().addActionListener(this);
        m.getPo().getBtnCancelar().addActionListener(this);
        m.getPo().getBtnprint().addActionListener(this);
        a.getIp().getBtnBuscar().addActionListener(this);
        a.getIp().getBtnRango().addActionListener(this);
        // ComboBox
        m.getPe().getCbxTipoP().addItemListener(this);
        m.getPe().getCbxTipoE().addItemListener(this);
        m.getPo().getCbxEstado().addItemListener(this);
        //seleccion
        m.getPo().getTablaPedidos().getSelectionModel().addListSelectionListener(this);
    }

   @Override
    public void actionPerformed(ActionEvent e) {
        Pedido p = new Pedido(); // Clase para manejar pedidos.

        // Verificar el origen del evento y ejecutar la acción correspondiente.
        if (e.getSource().equals(m.getPe().getBtnAgregarP())) {
            System.out.println("Botón 'Agregar Producto' presionado.");
            agregarProductoSeleccionado();
            return; // Salir para evitar ejecutar lógica innecesaria.
        }

        if (e.getSource().equals(m.getPe().getBtnAceptar())) {
            System.out.println("Botón 'Aceptar Pedido' presionado.");
            AceptarPedido();
            return;
        }

        if (e.getSource().equals(m.getPo().getBtnprint())) {
            System.out.println("Botón 'Imprimir Recibo' presionado.");
            imprimirRecibo();
            return;
        }

        if (e.getSource().equals(a.getIp().getBtnBuscar())) {
            System.out.println("Botón 'Buscar Informe' presionado.");
            LocalDate fecha = a.getIp().getJcalendar().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            CargarTablaInforme("PAGADO", fecha);
            GenerarInforme();
            return;
        }

        if (e.getSource().equals(a.getIp().getBtnRango())) {
            System.out.println("Botón 'Buscar por Rango' presionado.");
            LocalDate fecha1 = a.getIp().getJcalendar().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fecha2 = a.getIp().getJcalendar2().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            CargarTablaInformeRango("PAGADO", fecha1, fecha2);
            GenerarInforme();
            return;
        }

        // Verificar si una fila está seleccionada para los botones de cambio de estado.
        int fila = m.getPo().getTablaPedidos().getSelectedRow();
        if (fila == -1) {
            // Mostrar un mensaje si no se ha seleccionado una fila y la acción requiere una fila.
            if (e.getSource().equals(m.getPo().getBtnEntregar()) ||
                e.getSource().equals(m.getPo().getBtnPagar()) ||
                e.getSource().equals(m.getPo().getBtnCancelar())) {
                JOptionPane.showMessageDialog(null, "Por favor, selecciona un pedido.");
            }
            return; // Salir si no hay una fila seleccionada.
        }

        // Obtener el ID del pedido seleccionado.
        int id = (int) m.getPo().getDtm().getValueAt(fila, 0);

        // Manejar acciones que cambian el estado del pedido.
        if (e.getSource().equals(m.getPo().getBtnEntregar())) {
            System.out.println("Cambiando estado a 'ENTREGADO' para el pedido con ID: " + id);
            p.CambiarEstado("ENTREGADO", id);
            JOptionPane.showMessageDialog(null, "El pedudo ha sido entregado con exito, dirige a estado de entregado para seguir el flujo");
            return;
        }

        if (e.getSource().equals(m.getPo().getBtnPagar())) {
            System.out.println("Cambiando estado a 'PAGADO' para el pedido con ID: " + id);
            p.CambiarEstado("PAGADO", id);
            JOptionPane.showMessageDialog(null, "El pedudo ha sido Pagado con exito, dirige a estado de Pagado para seguir el flujo");
            return;
        }

        if (e.getSource().equals(m.getPo().getBtnCancelar())) {
            System.out.println("Cambiando estado a 'CANCELADO' para el pedido con ID: " + id);
            p.CambiarEstado("CANCELADO", id);
            JOptionPane.showMessageDialog(null, "El pedudo ha sido Cancelado con exito, dirige a estado de Cancelado para seguir el flujo");
            return;
        }
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(m.getPe().getCbxTipoP())) {
            String tipoSeleccionadoP = (String) m.getPe().getCbxTipoP().getSelectedItem();
            cargarTablaProducto(tipoSeleccionadoP);
        }
        if (e.getSource().equals(m.getPe().getCbxTipoE())) {
            String tipoSeleccionadoE = (String) m.getPe().getCbxTipoE().getSelectedItem();
            cargarTablaEspacios(tipoSeleccionadoE);
        }
        if (e.getSource().equals(m.getPo().getCbxEstado())) {
            String estadoSeleccionado = (String) m.getPo().getCbxEstado().getSelectedItem();
            cargarTablaPedido(estadoSeleccionado);
        }
    }
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Evitar que se procese si el evento aún está ajustando la selección
        if (!e.getValueIsAdjusting()) {
            int fila = m.getPo().getTablaPedidos().getSelectedRow();
            if (fila == -1) {
                // Si no hay fila seleccionada, deshabilitar botones
                m.getPo().getBtnEntregar().setEnabled(false);
                m.getPo().getBtnPagar().setEnabled(false);
                m.getPo().getBtnCancelar().setEnabled(false);
                return;
            }

            // Obtener el estado de la fila seleccionada
            String estado = m.getPo().getDtm().getValueAt(fila, 6).toString();

            // Habilitar o deshabilitar botones según el estado
            switch (estado) {
                case "EN PROCESO" -> {
                    m.getPo().getBtnEntregar().setEnabled(true);
                    m.getPo().getBtnPagar().setEnabled(false);
                    m.getPo().getBtnCancelar().setEnabled(true);
                }
                case "ENTREGADO" -> {
                    m.getPo().getBtnEntregar().setEnabled(false);
                    m.getPo().getBtnPagar().setEnabled(true);
                }
                case "PAGADO" -> {
                    m.getPo().getBtnEntregar().setEnabled(false);
                    m.getPo().getBtnPagar().setEnabled(false);
                    m.getPo().getBtnCancelar().setEnabled(false);
                    m.getPo().getBtnprint().setEnabled(true);
                }
                default -> {
                    m.getPo().getBtnEntregar().setEnabled(false);
                    m.getPo().getBtnPagar().setEnabled(false);
                    m.getPo().getBtnCancelar().setEnabled(false);
                }
            }
        }    
    }

    public void cargarTablaProducto(String tipo) {
        m.getPe().getDtm1().setRowCount(0);
        Pedido pe = new Pedido();
        Producto p = new Producto();
        try {
            ArrayList<Producto> productos = pe.ConsultaProducto(tipo);
            for (Producto pro : productos) {
                byte[] imagenBytes = p.obtenerImagen(pro.getId_producto());
                m.getPe().getDtm1().addRow(new Object[]{
                    pro.getId_producto(),
                    pro.getNombre(),
                    pro.getTipo(),
                    pro.getPrecio(),
                    imagenBytes
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        m.getPe().getTableProductos().getColumnModel().getColumn(4).setCellRenderer(new ImagenCellRenderer());
    }

    public void cargarTablaEspacios(String tipo) {
        m.getPe().getDtm2().setRowCount(0);
        Pedido pe = new Pedido();
        try {
            ArrayList<Espacios> espacios = pe.ConsultaEspacios(tipo);
            for (Espacios es : espacios) {
                m.getPe().getDtm2().addRow(new Object[]{
                    es.getId_espacios(),
                    es.getNum(),
                    es.getCapacidad(),
                    es.getTipo()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los espacios en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void cargarTablaPedido(String estado){
        m.getPo().getDtm().setRowCount(0);
        Pedido pe = new Pedido();
        PedidoProducto pp = new PedidoProducto();
        try{
            ArrayList<Pedido> pedidos = pe.ConsultarPedido(estado);
            for(Pedido pedido : pedidos){
                int mesa_id= pedido.getMesaId();
                int mesero_id = pedido.getMeseroId();
                int pedido_id = pedido.getId();
                m.getPo().getDtm().addRow(new Object[]{
                    pedido.getId(),
                    pedido.ConsultarMesero(mesero_id),
                    pedido.ConsultarEspacio(mesa_id),
                    pp.CargarProductosMensaje(pedido_id),
                    pedido.getCant_personas(),
                    pedido.getTiempoEstimado(),
                    pedido.getEstado(),
                    pedido.getTotal(),
                    pedido.getFechaHora()
                });
                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los espacios en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
            
        
        
    }

    public void agregarProductoSeleccionado() {
        int fila = m.getPe().getTableProductos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona un producto.");
            return;
        }
        int cantidad = (int) m.getPe().getSpinnerCantidad().getValue();
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a cero.");
            return;
        }
        int precio = (int) m.getPe().getDtm1().getValueAt(fila, 3);
        int precioTotal = cantidad * precio;
        String idProducto = m.getPe().getDtm1().getValueAt(fila, 0).toString();
        String[] producto = {idProducto, String.valueOf(cantidad), String.valueOf(precioTotal)};
        productosSeleccionados.add(producto);
        System.out.println("Producto agregado:");
        System.out.println("ID Producto: " + producto[0]);
        System.out.println("Cantidad: " + producto[1]);
        System.out.println("Precio: " + producto[2]);
        JOptionPane.showMessageDialog(null, "Producto seleccionado");
    }

    public void AceptarPedido() {
        Pedido pe = new Pedido();
        Espacios es = new Espacios();
        int fila = m.getPe().getTableEspacios().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Por favor seleccione una fila en la tabla de espacios.");
            return;
        }

        int id_mesero = m.getIdMesero();
        int id_mesa = (int) m.getPe().getDtm2().getValueAt(fila, 0);
        int cant_personas = (int) m.getPe().getSpinnerPersonas().getValue();
        JSpinner spinnerTiempo = m.getPe().getSpinnerTiempo();
        String estado = "EN PROCESO";

        // Validación de cantidad de personas
        if (cant_personas < 1) {
            JOptionPane.showMessageDialog(null, "La cantidad de personas debe ser mayor o igual a 1.");
            return;
        }

        // Validación de tiempo estimado
        Object spinnerValue = spinnerTiempo.getValue();
        if (!(spinnerValue instanceof java.util.Date)) {
            JOptionPane.showMessageDialog(null, "El tiempo estimado no es válido.");
            return;
        }
        LocalTime tiempo = ((java.util.Date) spinnerValue).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime();

        // Validación de productos seleccionados
        if (productosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un producto para el pedido.");
            return;
        }

        // Calcular el total del pedido
        double total = CalcularTotalPedido();
        LocalDateTime fecha = LocalDateTime.now();

        // Configurar el pedido
        pe.setMeseroId(id_mesero);
        pe.setMesaId(id_mesa);
        pe.setCant_personas(cant_personas);
        pe.setTiempoEstimado(tiempo);
        pe.setEstado(estado);
        pe.setTotal(total);
        pe.setFechaHora(fecha);

        // Guardar el pedido en la base de datos
        pe.GuardarPedido();
        int idPedido = pe.getId();

        // Guardar los productos del pedido
        PedidoProducto pp = new PedidoProducto();
        for (String[] producto : productosSeleccionados) {
            pp.setPedidoId(idPedido);
            pp.setProductoId(Integer.parseInt(producto[0]));
            pp.setCantidad(Integer.parseInt(producto[1]));
            pp.setPrecioUnitario(Double.parseDouble(producto[2]));
            pp.Guardar();
        }

        // Limpiar productos seleccionados
        productosSeleccionados.clear();

        // Cambiar el estado de la mesa si se seleccionó la opción de reservar
        if (m.getPe().getCheckReservar().isSelected()) {
            es.CambiarEstado(id_mesa);
        }

        JOptionPane.showMessageDialog(null, "Pedido realizado correctamente.");
    }


    public double CalcularTotalPedido() {
        double total = 0;
        for (String[] producto : productosSeleccionados) {
            total += Double.parseDouble(producto[2]);
        }
        return total;
    }
    public void Configuraracion(){
        JSpinner spinner = m.getPe().getSpinnerTiempo();

        // Crear un objeto Date para representar 00:30:00
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendario.set(java.util.Calendar.MINUTE, 30);
        calendario.set(java.util.Calendar.SECOND, 0);
        calendario.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Date tiempoPorDefecto = calendario.getTime();

        // Configurar el SpinnerDateModel con el tiempo por defecto 00:30:00
        SpinnerDateModel model = new SpinnerDateModel(tiempoPorDefecto, null, null, java.util.Calendar.MINUTE);
        spinner.setModel(model);

        // Establecer el formato de hora deseado (HH:mm:ss)
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm:ss");
        spinner.setEditor(editor);
    }
    public void imprimirRecibo() {
        // Obtener la fila seleccionada en la tabla
        int fila = m.getPo().getTablaPedidos().getSelectedRow();

        // Verificar si se seleccionó una fila
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona un pedido para imprimir el recibo.");
            return;
        }

        // Obtener los valores de la fila seleccionada
        String productos = m.getPo().getDtm().getValueAt(fila, 3).toString(); // Productos del pedido
        String mesero = m.getPo().getDtm().getValueAt(fila, 1).toString();    // Nombre del mesero
        String mesa = m.getPo().getDtm().getValueAt(fila, 2).toString();      // Número de mesa
        String fecha = m.getPo().getDtm().getValueAt(fila, 8).toString();     // Fecha del pedido
        String total = m.getPo().getDtm().getValueAt(fila, 7).toString();     // Total del pedido

        // Crear el mensaje del recibo
        String mensaje = "========== RECIBO ==========\n" +
                         "Fecha: " + fecha + "\n" +
                         "Mesero: " + mesero + "\n" +
                         "Mesa: " + mesa + "\n\n" +
                         "Productos:\n" + productos + "\n" +
                         "===========================\n" +
                         "Total: $" + total + "\n" +
                         "===========================";

        // Crear un JTextPane para permitir la alineación
        JTextPane textPane = m.getF().getTxtFactura(); // Asumiendo que ya tienes un JTextPane
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();

        // Configurar alineación al centro
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Establecer el texto en el JTextPane
        textPane.setText(mensaje);

        // Mostrar la ventana del recibo
        m.getF().setVisible(true);  // Asegurarse de que la ventana esté visible
    }
    public void CargarTablaInforme(String estado, LocalDate fechaSeleccionada) {
        a.getIp().getDtm().setRowCount(0); // Reinicia las filas de la tabla
        Pedido pe = new Pedido();
        PedidoProducto pp = new PedidoProducto();

        try {
            // Obtener los pedidos filtrados por estado y fecha
            ArrayList<Pedido> pedidos = pe.ConsultarPedidoFecha(estado, fechaSeleccionada);

            // Verificar si no se encontraron pedidos
            if (pedidos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontraron pedidos para la fecha seleccionada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            // Recorrer los pedidos y añadir filas a la tabla
            for (Pedido pedido : pedidos) {
                int mesa_id = pedido.getMesaId();
                int mesero_id = pedido.getMeseroId();
                int pedido_id = pedido.getId();
                a.getIp().getDtm().addRow(new Object[]{
                    pedido.getId(),
                    pedido.ConsultarMesero(mesero_id), // Método para obtener el nombre del mesero
                    pedido.ConsultarEspacio(mesa_id),  // Método para obtener el espacio de la mesa
                    pp.CargarProductosMensaje(pedido_id), // Método para obtener los productos del pedido
                    pedido.getCant_personas(),
                    pedido.getTiempoEstimado(),
                    pedido.getEstado(),
                    pedido.getTotal(),
                    pedido.getFechaHora()
                });
            }

        } catch (SQLException ex) {
            // Manejo de error si algo sale mal al consultar o cargar los pedidos
            JOptionPane.showMessageDialog(null, "Error al cargar los pedidos en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void CargarTablaInformeRango(String estado, LocalDate fecha1,LocalDate fecha2){
        a.getIp().getDtm().setRowCount(0); // Reinicia las filas de la tabla
        Pedido pe = new Pedido();
        PedidoProducto pp = new PedidoProducto();

        try {
            // Obtener los pedidos filtrados por estado y fecha
            ArrayList<Pedido> pedidos = pe.ConsultarPedidoRango(estado, fecha1, fecha2);

            // Verificar si no se encontraron pedidos
            if (pedidos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontraron pedidos para la fecha seleccionada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            // Recorrer los pedidos y añadir filas a la tabla
            for (Pedido pedido : pedidos) {
                int mesa_id = pedido.getMesaId();
                int mesero_id = pedido.getMeseroId();
                int pedido_id = pedido.getId();
                a.getIp().getDtm().addRow(new Object[]{
                    pedido.getId(),
                    pedido.ConsultarMesero(mesero_id), // Método para obtener el nombre del mesero
                    pedido.ConsultarEspacio(mesa_id),  // Método para obtener el espacio de la mesa
                    pp.CargarProductosMensaje(pedido_id), // Método para obtener los productos del pedido
                    pedido.getCant_personas(),
                    pedido.getTiempoEstimado(),
                    pedido.getEstado(),
                    pedido.getTotal(),
                    pedido.getFechaHora()
                });
            }

        } catch (SQLException ex) {
            // Manejo de error si algo sale mal al consultar o cargar los pedidos
            JOptionPane.showMessageDialog(null, "Error al cargar los pedidos en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }   
    }

    private void GenerarInforme() {
        // Verificar si hay filas en la tabla
        int rowCount = a.getIp().getTablaInformes().getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos en la tabla para generar el reporte.");
            return;
        }

        // Inicializar la variable para sumar los totales
        double totalVentas = 0;

        // Recorrer las filas de la tabla y sumar los valores de la columna "Total"
        for (int i = 0; i < rowCount; i++) {
            String totalString = a.getIp().getDtm().getValueAt(i, 7).toString(); // Columna de total
            totalVentas += Double.parseDouble(totalString);
        }

        // Crear el mensaje del reporte
        String mensaje = "============ REPORTE DE VENTAS ============\n" +
                         "Fecha del Reporte: " + LocalDate.now() + "\n" +
                         "------------------------------------------\n" +
                         "Número de Ventas: " + rowCount + "\n" +
                         "Total de Ventas: $" + String.format("%.2f", totalVentas) + "\n" +
                         "------------------------------------------\n" +
                         "Gracias por usar nuestro sistema.\n" +
                         "==========================================";

        // Crear un JTextArea para mostrar el reporte
        JTextArea textArea = new JTextArea(mensaje);
        textArea.setEditable(false); // Deshabilitar edición
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Fuente monoespaciada para alineación
        textArea.setBackground(Color.white); // Fondo blanco para buena visibilidad
        textArea.setForeground(Color.black); // Texto en negro para contraste

        // Crear un JScrollPane para el JTextArea
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        // Mostrar el JOptionPane con el JScrollPane
        JOptionPane.showMessageDialog(null, scrollPane, "Reporte de Ventas", JOptionPane.INFORMATION_MESSAGE);
    }


}



    



