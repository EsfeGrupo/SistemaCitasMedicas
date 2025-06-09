package org.example.presentacion;

import javax.swing.*;
import java.awt.*;

public class PacienteForm extends JDialog{
    private JPanel mainPanel;
    private JPanel pnlTituloOperaciones;
    private JPanel pnlTitulo;
    private JPanel pnlOperaciones;
    private JButton btnEliminar;
    private JButton btnEditar;
    private JButton btnCrear;
    private JTable tabDetalles;
    private JTextField txtBuscar;


    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.


    public PacienteForm(MainForm mainForm) {
        this.mainForm = mainForm; // Inicializa la referencia a la ventana principal.
        setContentPane(mainPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        setTitle("Paciente"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.

        Component frame = null;
        btnCrear.addActionListener(e -> {
            // Lógica para crear un nuevo paciente
            JOptionPane.showMessageDialog(frame, "Crear Paciente");
        });

        btnEditar.addActionListener(e -> {
            // Lógica para editar un paciente seleccionado
            JOptionPane.showMessageDialog(frame, "Editar Paciente");
        });

        btnEliminar.addActionListener(e -> {
            // Lógica para eliminar un paciente seleccionado
            JOptionPane.showMessageDialog(frame, "Eliminar Paciente");
        });
    }
}
