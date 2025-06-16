package org.example;

import org.example.presentacion.MainForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Utiliza el hilo de despacho de eventos (Event Dispatch Thread - EDT) para asegurar
            // que todas las operaciones relacionadas con la interfaz gráfica de usuario (Swing)
            // se realicen de forma segura y sin bloqueos.
            MainForm mainForm  = new MainForm(); // Crea una nueva instancia del formulario principal de la aplicación.
            mainForm.setVisible(true); // Hace vidsadasdassible el formulario principal. Inicialmente podría estar vacío o tener una interfaz de carga.
          });
    }
}