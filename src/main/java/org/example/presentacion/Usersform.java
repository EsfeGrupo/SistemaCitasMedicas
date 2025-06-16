package org.example.presentacion;

import org.example.dominio.User;
import org.example.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class Usersform extends JDialog{
    private JPanel mainPanel;
    private JPanel pnlTituloOperaciones;
    private JPanel pnlOperaciones;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCrear;
    private JTextField txtBuscar;
    private JPanel pnlTitulo;
    private JTable table1;

    private DefaultTableModel tableModel;

    public Usersform(JFrame parent) {
        super(parent, "Gestión de Usuarios", true); // Modal, bloquea el padre

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // Definir columnas y modelo
        String[] columnas = {"Nombre", "Email", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0);
        table1.setModel(tableModel);

        cargarUsuariosDesdeBD();

        btnCrear.addActionListener(e -> {
            Crearusuario crearForm = new Crearusuario(this);
            crearForm.setVisible(true);
        });



        btnEditar.addActionListener(e -> {
            int fila = table1.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un usuario para editar");
                return;
            }

            String nombre = tableModel.getValueAt(fila, 0).toString();
            String email = tableModel.getValueAt(fila, 1).toString();
            String estado = tableModel.getValueAt(fila, 2).toString();

            editarusuario editarForm = new editarusuario(this);
            editarForm.setDatos(nombre, email, estado);
            editarForm.setVisible(true);
        });

        btnEliminar.addActionListener(e -> {
            int fila = table1.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un usuario para eliminar");
                return;
            }

            String email = tableModel.getValueAt(fila, 1).toString(); // El email identifica al usuario

            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro que desea eliminar este usuario?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    UserDAO dao = new UserDAO();
                    boolean eliminado = dao.eliminarPorEmail(email);
                    if (eliminado) {
                        tableModel.removeRow(fila);
                        JOptionPane.showMessageDialog(null, "Usuario eliminado de la base de datos.");
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo eliminar el usuario.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al eliminar usuario: " + ex.getMessage());
                }
            }
        });
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = txtBuscar.getText();
                filtrarUsuarios(texto);
            }
        });



    }

    private void cargarUsuariosDesdeBD() {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> usuarios = userDAO.obtenerTodos(); // Asume que este método existe y retorna todos los usuarios

            for (User u : usuarios) {
                String estadoStr = (u.getStatus() == 1) ? "Activo" : "Inactivo";
                Object[] fila = {u.getName(), u.getEmail(), estadoStr};
                tableModel.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filtrarUsuarios(String texto) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table1.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
    public void refrescarTabla() {
        tableModel.setRowCount(0); // Limpia la tabla
        cargarUsuariosDesdeBD();  // Vuelve a cargar los datos desde la BD
    }

}
