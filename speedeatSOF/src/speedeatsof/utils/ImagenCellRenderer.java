package speedeatsof.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImagenCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof byte[]) {
            byte[] imageBytes = (byte[]) value;
            try {
                // Convertir los bytes en una imagen
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

                // Redimensionar la imagen a un tamaño fijo de 100x75
                Image scaledImage = image.getScaledInstance(100, 75, Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(scaledImage);

                // Crear un JLabel con el ImageIcon redimensionado
                JLabel label = new JLabel(imageIcon);

                // Ajustar la altura de la fila para que se acomode a la nueva imagen
                table.setRowHeight(row, 75);  // Altura fija para la fila

                // Devolver el JLabel con la imagen redimensionada
                return label;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Si no es una imagen, devuelve el valor original (generalmente texto)
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

