package org.example.presentacion;

import javax.swing.*;
import java.text.SimpleDateFormat; // Para formatear la fecha
import java.util.Date; // Para obtener la fecha actual

public class Pagos extends JFrame {
    private JLabel LPresentacion;
    private JPanel JContenido;
    private JButton pagarButton;
    private JComboBox comboBox1;
    private JLabel lblFecha;

    private  JLabel lblFechaActual;

    public  Pagos(){
        // --- Lógica para mostrar la fecha actual en el JLabel ---
        if (lblFechaActual != null) { // Asegúrate de que el JLabel exista
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Formato para la fecha y hora
            Date fechaActual = new Date(); // Obtiene la fecha y hora actual del sistema
            String fechaFormateada = sdf.format(fechaActual); // Formatea la fecha a una cadena

            lblFechaActual.setText("Fecha y Hora Actual: " + fechaFormateada); // Asigna el texto al JLabel
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Pagos().notifyAll();
            }
        });
}
}
