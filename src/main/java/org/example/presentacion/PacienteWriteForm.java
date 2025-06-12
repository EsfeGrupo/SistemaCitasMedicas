package org.example.presentacion;

import javax.swing.*;

public class PacienteWriteForm {
    private final PacienteForm pacienteForm;
    private JPanel MainPanel;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtFecha;
    private JComboBox cboGenero;
    private JLabel lblTitulo;
    private JButton btnCrear;
    private JButton btnCancelar;


    public PacienteWriteForm(PacienteForm pacienteForm) {
        this.pacienteForm = pacienteForm; // Inicializa la referencia a la ventana principal.
        pacienteForm.setContentPane(MainPanel); // Establece el panel principal como el contenido de este diálogo.
        pacienteForm.setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        pacienteForm.setTitle("Paciente"); // Establece el título de la ventana del diálogo.
        pacienteForm.pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        pacienteForm.setLocationRelativeTo(pacienteForm); // Centra la ventana del diálogo relative a la ventana principal.
    }
}
