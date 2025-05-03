/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package speedeatsof.logica;

import java.util.regex.Pattern;

/**
 *
 * @author juanR
 */
public class Validaciones {

   
    
    public Validaciones() {
    }
 
    
    public String validarDatosUsuario(String nombre, String apellido, String documento, String telefono, String correo, String contraseña) {
        StringBuilder mensaje = new StringBuilder();

        // Validación de nombre (solo letras y espacios, no vacío)
        if (nombre == null || nombre.isEmpty() || !Pattern.matches("[a-zA-Z ]+", nombre)) {
            mensaje.append("El nombre no debe contener números y no puede estar vacío.\n");
        }

        // Validación de apellido (solo letras y espacios, no vacío)
        if (apellido == null || apellido.isEmpty() || !Pattern.matches("[a-zA-Z ]+", apellido)) {
            mensaje.append("El apellido no debe contener números y no puede estar vacío.\n");
        }

        // Validación de documento (solo números, no vacío)
        if (documento == null || !documento.matches("\\d+")) {
            mensaje.append("El documento debe contener solo números.\n");
        }

        // Validación de teléfono (solo números, no vacío)
        if (telefono == null || !telefono.matches("\\d+")) {
            mensaje.append("El teléfono debe contener solo números.\n");
        }

        // Validación de correo (debe contener '@' y un dominio básico)
        if (correo == null || !correo.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            mensaje.append("El correo debe ser válido y contener el carácter '@'.\n");
        }

        // Validación de contraseña (mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número)
        if (contraseña == null || contraseña.length() < 8 
                || !Pattern.compile("[A-Z]").matcher(contraseña).find() 
                || !Pattern.compile("[a-z]").matcher(contraseña).find() 
                || !Pattern.compile("[0-9]").matcher(contraseña).find()) {
            mensaje.append("La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula y un número.\n");
        }

        // Retornar resultado
        if (mensaje.length() == 0) {
            return "correcto";
        } else {
            return mensaje.toString();
        }
    }


    public String validarDatosProducto(String nombre, String cantidadStr, String precioStr) {
        // Validar nombre
        if (nombre == null || nombre.isEmpty()) {
            return "El campo nombre no puede estar vacío.";
        }

        // Validar cantidad
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                return "La cantidad debe ser un número mayor a 0.";
            }
        } catch (NumberFormatException e) {
            return "La cantidad debe ser un valor numérico.";
        }

        // Validar precio
        int precio;
        try {
            precio = Integer.parseInt(precioStr);
            if (precio <= 0) {
                return "El precio debe ser un número mayor a 0.";
            }
        } catch (NumberFormatException e) {
             return "El precio debe ser un valor numérico.";
        }

        return "Datos válidos.";
    }
    public String validarEspacios(int num, int capacidad) {
        // Validar el número
        if (num <= 0) {
            return "La cantidad debe ser un número mayor a 0.";
        }

        // Validar la capacidad (entre 2 y 6)
        if (capacidad < 2 || capacidad > 6) {
            return "La capacidad solo puede ser entre 2 y 6.";
        }

       

        // Si todos los datos son válidos
        return "Datos validos";
    }   
}

