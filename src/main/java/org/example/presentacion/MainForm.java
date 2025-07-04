package org.example.presentacion;

import javax.swing.*;

public class MainForm extends JFrame{

    private JPanel MainPanel;

    public MainForm(){
        setTitle("Sistema en java de Citas medicas de escritorio"); // Establece el título de la ventana principal (JFrame).
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura la operación por defecto al cerrar la ventana para que la aplicación se termine.
        setLocationRelativeTo(null); // Centra la ventana principal en la pantalla.
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa la ventana principal en estado maximizado, ocupando toda la pantalla.
        createMenu(); // Llama al método 'createMenu()' para crear y agregar la barra de menú a la ventana principal.
    }
    private void createMenu() {
    // Barra de menú
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    JMenuItem itemPaciente = new JMenuItem("Pacientes");
    menuBar.add(itemPaciente);
    itemPaciente.addActionListener(e -> {
        PacienteForm paciente = new PacienteForm(this);
        paciente.setVisible(true);
    });
    JMenuItem itemCita = new JMenuItem("Citas");
    menuBar.add(itemCita);
    itemCita.addActionListener(e -> {
        CitasForm citas = new CitasForm(this);
        citas.setVisible(true);
    });
        JMenuItem itemPagar = new JMenuItem("Pago de cita");
        menuBar.add(itemPagar);
        itemPagar.addActionListener(e -> {
            Pagosform pago = new Pagosform(this);
            pago.setVisible(true);
        });
    JMenuItem itemMedico = new JMenuItem("Médicos");
    menuBar.add(itemMedico);
    itemMedico.addActionListener(e -> {
        DoctorForm medico = new DoctorForm(this);
        medico.setVisible(true);
    });


    JMenuItem itemUsuarios = new JMenuItem("Usuarios");
    menuBar.add(itemUsuarios);
        itemUsuarios.addActionListener(e -> {
            Usersform usuariosForm = new Usersform(this); // 'this' es MainForm
            usuariosForm.setVisible(true); // Ya es un JDialog modal, bloqueará la principal
        });


    /*JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
    menuBar.add(itemChangeUser);
    itemChangeUser.addActionListener(e -> {
        LoginForm loginForm = new LoginForm(this);
        loginForm.setVisible(true);
    });*/

    JMenuItem itemSalir = new JMenuItem("Salir");
    menuBar.add(itemSalir);
    itemSalir.addActionListener(e -> System.exit(0));
}
}
