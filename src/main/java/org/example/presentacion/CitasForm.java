package org.example.presentacion;

import javax.swing.*;
import java.awt.*;

public class CitasForm extends JDialog{
    private JPanel citasPanel;
    private JPanel pnlTituloOperaciones;
    private JPanel pnlOperaciones;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCrear;
    private JTextField txtBuscar;
    private JPanel pnlTitulo;
    private JTable tabDetalles;

    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.

    public CitasForm(MainForm mainForm) {
        this.mainForm = mainForm; // Inicializa la referencia a la ventana principal.
        setContentPane(citasPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        setTitle("Citas"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.


        Component frame = null;
        btnCrear.addActionListener(e -> {
            // Lógica para crear un nuevo paciente
            JOptionPane.showMessageDialog(frame, "Crear Cita");
        });

        btnEditar.addActionListener(e -> {
            // Lógica para editar un paciente seleccionado
            JOptionPane.showMessageDialog(frame, "Editar Cita");
        });

        btnEliminar.addActionListener(e -> {
            // Lógica para eliminar un paciente seleccionado
            JOptionPane.showMessageDialog(frame, "Eliminar Cita");
        });
    }

}
