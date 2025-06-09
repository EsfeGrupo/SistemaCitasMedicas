package org.example.presentacion;

import javax.swing.*;
import java.awt.*;

public class DoctorForm extends JDialog {
    private JPanel mainPanel;
    private JPanel pnlTituloOperaciones;
    private JPanel pnlOperaciones;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCrear;
    private JTextField txtBuscar;
    private JPanel pnlTitulo;
    private JTable tabDetalles;

    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.

    public DoctorForm(MainForm mainForm) {
        this.mainForm = mainForm; // Inicializa la referencia a la ventana principal.
        setContentPane(mainPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        setTitle("Doctores"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.
    }
}
