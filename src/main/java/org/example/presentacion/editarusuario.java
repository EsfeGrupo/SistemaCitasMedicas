package org.example.presentacion;

import org.example.dominio.User;
import org.example.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class editarusuario extends JDialog {
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JPanel panelPrincipal;

    // Aquí guardamos el email original para identificar el usuario a editar
    private String emailOriginal;

    public editarusuario(Window parent) {
        super(parent, "Editar Usuario", ModalityType.APPLICATION_MODAL);
        // inicializa componentes y layout
        setSize(400, 250);
        setLocationRelativeTo(parent);

        panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        panelPrincipal.add(txtNombre, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelPrincipal.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panelPrincipal.add(txtEmail, gbc);

        // Contraseña (puedes dejar en blanco para no cambiarla)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelPrincipal.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        panelPrincipal.add(txtPassword, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelPrincipal.add(panelBotones, gbc);

        add(panelPrincipal);

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                String email = txtEmail.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (nombre.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nombre y email son obligatorios.");
                    return;
                }

                try {
                    UserDAO dao = new UserDAO();

                    User userActualizado;
                    if (password.isEmpty()) {
                        // Si no se pone contraseña, no la cambiamos (debes tener un método para esto)
                        userActualizado = new User(nombre, email, null); // O manejar según tu clase User
                    } else {
                        userActualizado = new User(nombre, email, password);
                    }

                    // Actualiza usando email original para identificar
                    boolean exito = dao.actualizar(emailOriginal, userActualizado);

                    if (exito) {
                        JOptionPane.showMessageDialog(null, "Usuario actualizado exitosamente.");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar usuario.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    // Este método precarga los datos para editar
    public void setDatos(String nombre, String email, String estado) {
        txtNombre.setText(nombre);
        txtEmail.setText(email);
        // Para el estado, si quieres mostrar algo, puedes añadir un label o checkbox
        emailOriginal = email; // Guardamos el email original para la actualización
    }
}
