package org.example.presentacion;

import org.example.persistencia.CitasDAO; // Importa la interfaz o clase citasDAO, que define las operaciones de acceso a datos para la entidad citas.

import javax.swing.*; // Importa el paquete Swing, que proporciona clases para crear interfaces gráficas de usuario.
import javax.swing.table.DefaultTableModel; // Importa la clase DefaultTableModel, utilizada para crear y manipular modelos de datos para JTable.

import org.example.dominio.Citas; // Importa la clase User, que representa la entidad de usuario en el dominio de la aplicación.
import org.example.utils.CUD; // Importa el enum  CUD (Create, Update, Delete).

import java.awt.event.KeyAdapter; // Importa la clase KeyAdapter, una clase adaptadora para recibir eventos de teclado.
import java.awt.event.KeyEvent; // Importa la clase KeyEvent, que representa un evento de teclado.
import java.util.ArrayList; // Importa la clase ArrayList, una implementación de la interfaz List que permite almacenar colecciones dinámicas de objetos.


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

    private CitasDAO citasDAO; // Instancia de citasDAO para realizar operaciones de base de datos de usuarios.
    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.

    public CitasForm(MainForm mainForm) {
        this.mainForm = mainForm; // Inicializa la referencia a la ventana principal.
        setContentPane(citasPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        setTitle("Citas"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.


        // Agrega un ActionListener al botón btnCreate.
        btnCrear.addActionListener(s -> {
            // Crea una nueva instancia de UserWriteForm para la creación de un nuevo usuario, pasando la MainForm, la constante CREATE de CUD y un nuevo objeto User vacío.
            CitasWriteForm CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.CREATE, new Citas());
            // Hace visible el formulario de escritura de usuario.
            CitasWriteForm.setVisible(true);
            // Limpia la tabla de usuarios creando y asignando un modelo de tabla vacío  para refrescar la lista después de la creación.
            DefaultTableModel emptyModel = new DefaultTableModel();
            tabDetalles.setModel(emptyModel);
        });

        // Agrega un ActionListener al botón btnUpdate.
        btnEditar.addActionListener(s -> {
            // Llama al método getUserFromTableRow para obtener el usuario seleccionado en la tabla.
            Citas citas = getCitasFromTableRow();
            // Verifica si se seleccionó un usuario en la tabla (getUserFromTableRow no devolvió null).
            if (citas != null) {
                // Crea una nueva instancia de UserWriteForm para la actualización del usuario seleccionado, pasando la MainForm, la constante UPDATE de CUD y el objeto User obtenido.
                CitasWriteForm CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.UPDATE, citas);
                // Hace visible el formulario de escritura de usuario.
                CitasWriteForm.setVisible(true);
                // Limpia la tabla de usuarios creando y asignando un modelo de tabla vacío para refrescar la lista después de la actualización.
                DefaultTableModel emptyModel = new DefaultTableModel();
                tabDetalles.setModel(emptyModel);
            }
        });

        // Agrega un ActionListener al botón btnEliminar.
        btnEliminar.addActionListener(s -> {
            // Llama al método getUserFromTableRow para obtener el usuario seleccionado en la tabla.
            Citas citas = getCitasFromTableRow();
            // Verifica si se seleccionó un usuario en la tabla (getUserFromTableRow no devolvió null).
            if (citas != null) {
                // Crea una nueva instancia de UserWriteForm para la eliminación del usuario seleccionado, pasando la MainForm, la constante DELETE de CUD y el objeto User obtenido.
                CitasWriteForm CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.DELETE, citas);
                // Hace visible el formulario de escritura de usuario.
                CitasWriteForm.setVisible(true);
                // Limpia la tabla de usuarios creando y asignando un modelo de tabla vacío  para refrescar la lista después de la eliminación.
                DefaultTableModel emptyModel = new DefaultTableModel();
                tabDetalles.setModel(emptyModel);
            }
        });
    }

    private Citas getCitasFromTableRow() {
        Citas citas = null; // Inicializa la variable user a null.
        try {
            // Obtiene el índice de la fila seleccionada en la tabla.
            int filaSelect = this.tabDetalles.getSelectedRow();
            int id = 0; // Inicializa la variable id a 0.

            // Verifica si se ha seleccionado alguna fila en la tabla.
            if (filaSelect != -1) {
                // Si se seleccionó una fila, obtiene el valor de la primera columna  ID de esa fila.
                id = (int) this.tabDetalles.getValueAt(filaSelect, 0);
            } else {
                // Si no se seleccionó ninguna fila, muestra un mensaje de advertencia al usuario.
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null; // Retorna null ya que no se puede obtener un usuario sin una fila seleccionada.
            }

            // Llama al método 'getById' del UserDAO para obtener el objeto User correspondiente al ID obtenido de la tabla.
            citas = citasDAO.getById(id);

            // Verifica si se encontró un usuario con el ID proporcionado.
            if (citas.getId() == 0) {
                // Si el ID del usuario devuelto es 0 (o alguna otra indicación de que no se encontró),
                // muestra un mensaje de advertencia al usuario.
                JOptionPane.showMessageDialog(null,
                        "No se encontró ningún usuario.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null; // Retorna null ya que no se encontró ningún usuario con ese ID.
            }

            // Si se encontró un usuario, lo retorna.
            return citas;
        } catch (Exception ex) {
            // Captura cualquier excepción que ocurra durante el proceso (por ejemplo, errores de base de datos).
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE); // Muestra un mensaje de error al usuario con la descripción de la excepción.
            return null; // Retorna null en caso de error.
        }
    }
}
