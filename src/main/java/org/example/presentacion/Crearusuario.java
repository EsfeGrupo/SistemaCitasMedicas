package org.example.presentacion;

import org.example.dominio.User;
import org.example.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;


public class Crearusuario extends JDialog {
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JPanel panelPrincipal;

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    public Crearusuario(Window parent) {
        super(parent, "Crear Usuario", ModalityType.APPLICATION_MODAL);
        // resto del constructor
        setSize(400, 250);
        setLocationRelativeTo(parent);
        // etc.



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

        // Contraseña
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
                String hashedPassword = hashPassword(password);


                if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                    return;
                }

                try {
                    UserDAO dao = new UserDAO();
                    dao.insertar(new User(nombre, email, password)); // Asegúrate de que este constructor exista
                    JOptionPane.showMessageDialog(null, "Usuario creado exitosamente.");
                    dispose(); // Cierra la ventana
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al crear usuario: " + ex.getMessage());
                }
            }
        });

        btnCancelar.addActionListener(e -> dispose());



    }



}
