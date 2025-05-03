package speedeatsof.control;
import speedeatsof.utils.ImagenCellRenderer;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayOutputStream;
import speedeatsof.logica.Producto;
import speedeatsof.logica.Validaciones;
import speedeatsof.vista.Administrador.VentanaAdministrador;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public final class ControladorProducto implements ActionListener, ItemListener {
    private VentanaAdministrador a;
    private String rutaImagen = "";  // Variable para almacenar la ruta de la imagen seleccionada

    public ControladorProducto() {
        this.a = VentanaAdministrador.getInstancia();
        cargarTabla();
        iniciarEventos();
        HiloCargarTablaProducto hiloTP = new HiloCargarTablaProducto(this);
        hiloTP.start();
        
    }

    private void iniciarEventos() {
        a.getPp().getBtnAgregar().addActionListener(this);
        a.getPp().getBtnEditar().addActionListener(this);
        a.getPp().getBtnEliminar().addActionListener(this);
        a.getPp().getBtnSeleccionar().addActionListener(this);
        a.getPp().getBtnExaminar().addActionListener(this);  
        a.getPp().getCbxBuscar().addItemListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == a.getPp().getBtnAgregar()) {
            System.out.println("Agregar producto");
            AgregarProducto(rutaImagen);
        } else if (e.getSource() == a.getPp().getBtnEditar()) {
            System.out.println("Editar producto");
            EditarProducto();
            a.getPp().getBtnEditar().setEnabled(false);
            a.getPp().getBtnAgregar().setEnabled(true);
        } else if (e.getSource() == a.getPp().getBtnEliminar()) {
            System.out.println("Eliminar producto");
            EliminarProducto();
        } else if (e.getSource() == a.getPp().getBtnSeleccionar()) {
            System.out.println("Seleccionar producto");
            SeleccionarProducto();
        } else if (e.getSource() == a.getPp().getBtnExaminar()) {
            System.out.println("Seleccionar imagen");
            examinarImagen();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(a.getPp().getCbxBuscar())){
            String tipo= (String) a.getPp().getCbxBuscar().getSelectedItem();
            cargarTablaxConsulta(tipo);
            
        }
    }

   public void cargarTabla() {
        a.getPp().getDtm().setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
        Producto p = new Producto();
        try {
            ArrayList<Producto> productos = p.consultarProductos(); // Llama al método para obtener los productos
            for (Producto pro : productos) {
                // Obtener la imagen en formato byte[] usando el id del producto
                byte[] imagenBytes = p.obtenerImagen(pro.getId_producto());

                // Añade los datos de cada producto como una fila en el modelo de la tabla
                a.getPp().getDtm().addRow(new Object[]{
                    pro.getId_producto(),
                    pro.getNombre(),
                    pro.getTipo(),
                    pro.getCantidad(),
                    pro.getPrecio(),
                    pro.isEstado(),
                    imagenBytes  // Pasa la imagen en bytes a la tabla
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Establecer el renderizador de celdas para la columna de imágenes (columna 6)
        a.getPp().getTableProductos().getColumnModel().getColumn(6).setCellRenderer(new ImagenCellRenderer());
    }
    public void cargarTablaxConsulta(String tipo) {
        a.getPp().getDtm().setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
        Producto p = new Producto();
        try {
            ArrayList<Producto> productos = p.consultarProductosxTipo(tipo); // Llama al método para obtener los productos
            for (Producto pro : productos) {
                // Obtener la imagen en formato byte[] usando el id del producto
                byte[] imagenBytes = p.obtenerImagen(pro.getId_producto());

                // Añade los datos de cada producto como una fila en el modelo de la tabla
                a.getPp().getDtm().addRow(new Object[]{
                    pro.getId_producto(),
                    pro.getNombre(),
                    pro.getTipo(),
                    pro.getCantidad(),
                    pro.getPrecio(),
                    pro.isEstado(),
                    imagenBytes  // Pasa la imagen en bytes a la tabla
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos en la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Establecer el renderizador de celdas para la columna de imágenes (columna 6)
        a.getPp().getTableProductos().getColumnModel().getColumn(6).setCellRenderer(new ImagenCellRenderer());
    } 


   public void AgregarProducto(String rutaImagen) {
        Producto p = new Producto();
        Validaciones v = new Validaciones();
        String nombre = a.getPp().getTxtNombreP().getText();
        String tipo = String.valueOf(a.getPp().getCbxTipo().getSelectedItem());
        String cantidad = a.getPp().getTxtCantidad().getText();
        String precio = a.getPp().getTxtPrecio().getText();
        String estado = String.valueOf(a.getPp().getCbxEstado().getSelectedItem());
        String validar = v.validarDatosProducto(nombre, cantidad, precio);

        if (!validar.equals("Datos válidos.")) {
            JOptionPane.showMessageDialog(null, validar);
        } else {
            p.setNombre(nombre);
            p.setTipo(tipo);
            p.setCantidad(Integer.parseInt(cantidad));
            p.setPrecio(Integer.parseInt(precio));

            if (estado.equals("Activo")) {
                p.setEstado(true);
            } else {
                p.setEstado(false);
            }

            // Convertir la imagen a byte[] antes de guardar usando la ruta
            if (!rutaImagen.isEmpty()) {
                try {
                    File file = new File(rutaImagen);  // Usar la ruta para crear el archivo
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(ImageIO.read(file), "jpg", baos);  // Asumimos que la imagen es JPG, puedes ajustar para otros formatos
                    baos.flush();
                    byte[] imagenBytes = baos.toByteArray();  // Obtener el byte array de la imagen
                    p.setImagen(imagenBytes);  // Establecer la imagen en formato byte[]
                    baos.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al convertir la imagen: " + ex.getMessage());
                }
            }

            p.guardarConImagen();
            cargarTabla();
            JOptionPane.showMessageDialog(null, "Producto registrado correctamente...");
        }
    }




    public void examinarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagenes", "jpg", "jpeg", "png", "gif"));
        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            rutaImagen = archivo.getAbsolutePath();  // Obtener la ruta completa del archivo
            // Guardar la ruta de la imagen seleccionada
            JOptionPane.showMessageDialog(null, "Imagen seleccionada: " + rutaImagen);
           
        }
    }

    public void SeleccionarProducto() {
        a.getPp().getBtnEditar().setEnabled(true);
        a.getPp().getBtnAgregar().setEnabled(false);
        int fila = a.getPp().getTableProductos().getSelectedRow();
        a.getPp().getLabelid().setText(a.getPp().getDtm().getValueAt(fila, 0).toString());
        a.getPp().getTxtNombreP().setText(a.getPp().getDtm().getValueAt(fila, 1).toString());
        String tipo = String.valueOf(a.getPp().getDtm().getValueAt(fila, 2));
        switch (tipo) {
            case "Pizza" -> a.getPp().getCbxTipo().setSelectedIndex(1);
            case "Hamburguesa" -> a.getPp().getCbxTipo().setSelectedIndex(2);
            case "Perro Caliente" -> a.getPp().getCbxTipo().setSelectedIndex(3);
            case "Salchipapa" -> a.getPp().getCbxTipo().setSelectedIndex(4);
            case "Bebida" -> a.getPp().getCbxTipo().setSelectedIndex(5);
            default -> {
            }
        }
        a.getPp().getTxtCantidad().setText(a.getPp().getDtm().getValueAt(fila, 3).toString());
        a.getPp().getTxtPrecio().setText(a.getPp().getDtm().getValueAt(fila, 4).toString());
        String estado = a.getPp().getDtm().getValueAt(fila, 5).toString();
        if (estado.equals("true")) {
            a.getPp().getCbxEstado().setSelectedIndex(1);
        } else {
            a.getPp().getCbxEstado().setSelectedIndex(2);
        }
    }

    public void EditarProducto() {
        Producto p = new Producto();
        Validaciones v = new Validaciones();

        int id_producto = Integer.parseInt(a.getPp().getLabelid().getText());
        String nombre = a.getPp().getTxtNombreP().getText();
        String tipo = String.valueOf(a.getPp().getCbxTipo().getSelectedItem());
        String cantidad = a.getPp().getTxtCantidad().getText();
        String precio = a.getPp().getTxtPrecio().getText();
        String estado = String.valueOf(a.getPp().getCbxEstado().getSelectedItem());
        String validar = v.validarDatosProducto(nombre, cantidad, precio);

        if (!validar.equals("Datos válidos.")) {
            JOptionPane.showMessageDialog(null, validar);
        } else {
            p.setId_producto(id_producto);
            p.setNombre(nombre);
            p.setTipo(tipo);
            p.setCantidad(Integer.parseInt(cantidad));
            p.setPrecio(Integer.parseInt(precio));

            // Convertir la imagen a byte[] antes de actualizar
            if (!rutaImagen.isEmpty()) {
                try {
                    File file = new File(rutaImagen);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(ImageIO.read(file), "jpg", baos);  // Puedes cambiar el formato según sea necesario
                    baos.flush();
                    byte[] imagenBytes = baos.toByteArray();  // Obtener el byte array de la imagen
                    p.setImagen(imagenBytes);  // Establecer la imagen en formato byte[]
                    baos.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al convertir la imagen: " + ex.getMessage());
                }
            }

            if (estado.equals("Activo")) {
                p.setEstado(true);
            } else {
                p.setEstado(false);
            }

            p.Actualizar();
            cargarTabla();
            JOptionPane.showMessageDialog(null, "Producto actualizado correctamente...");
        }
    }


    public void EliminarProducto() {
        Producto p = new Producto();
        int fila = a.getPp().getTableProductos().getSelectedRow();
        int id_producto = Integer.parseInt(a.getPp().getDtm().getValueAt(fila, 0).toString());
        p.eliminarProducto(id_producto);
        cargarTabla();
        JOptionPane.showMessageDialog(null, "Producto eliminado correctamente...");
    }
}




