package org.example.presentacion;

import org.example.Main;
import org.example.dominio.User;
import org.example.persistencia.UserDAO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

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



    public LoginForm() {
        setTitle("Login");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cambiar color de fondo a celeste
        getContentPane().setBackground(new Color(173, 216, 230)); // celeste claro

        setLayout(new BorderLayout());

        // Título arriba centrado
        JLabel lblTitulo = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel con GridBagLayout para mejor control tamaño y posición
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false); // Para que el panel herede el fondo celeste
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiqueta Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Email:"), gbc);

        // Campo Email más pequeño y proporcional
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtEmail = new JTextField(15);
        panelCampos.add(txtEmail, gbc);

        // Etiqueta Contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Contraseña:"), gbc);

        // Campo Contraseña más pequeño y proporcional
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPassword = new JPasswordField(15);
        panelCampos.add(txtPassword, gbc);

        add(panelCampos, BorderLayout.CENTER);

        // Panel para botón con fondo transparente
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        btnLogin = new JButton("Iniciar sesión");
        panelBoton.add(btnLogin);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(panelBoton, BorderLayout.SOUTH);

        // Acción de login
        btnLogin.addActionListener(e -> realizarLogin());
    }
    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            String hashedPassword = hashPassword(password); // Aquí aplicas el hash
            User usuario = dao.login(email, hashedPassword);


            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido, " + usuario.getName());

                MainForm MainForm = new MainForm(); // o null si no quieres padre
                MainForm.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }

}

