package org.example.presentacion;

import org.example.dominio.Doctor;
import org.example.persistencia.CitasDAO; // Importa la interfaz o clase citasDAO, que define las operaciones de acceso a datos para la entidad citas.

import javax.swing.*; // Importa el paquete Swing, que proporciona clases para crear interfaces gráficas de usuario.
import javax.swing.table.DefaultTableModel; // Importa la clase DefaultTableModel, utilizada para crear y manipular modelos de datos para JTable.

import org.example.dominio.Citas; // Importa la clase User, que representa la entidad de usuario en el dominio de la aplicación.
import org.example.utils.CUD; // Importa el enum  CUD (Create, Update, Delete).

import java.awt.event.KeyAdapter; // Importa la clase KeyAdapter, una clase adaptadora para recibir eventos de teclado.
import java.awt.event.KeyEvent; // Importa la clase KeyEvent, que representa un evento de teclado.
import java.sql.SQLException;
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

        txtBuscar.addKeyListener(new KeyAdapter() {
            // Sobrescribe el método keyReleased, que se llama cuando se suelta una tecla.
            @Override
            public void keyReleased(KeyEvent e) {
                // Verifica si el campo de texto txtNombre no está vacío.
                if (!txtBuscar.getText().trim().isEmpty()) {
                    // Llama al método search para buscar usuarios según el texto ingresado.
                    search(txtBuscar.getText());
                } else {
                    // Si el campo de texto está vacío, crea un modelo de tabla vacío y lo asigna a la tabla de usuarios para limpiarla.
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tabDetalles.setModel(emptyModel);
                }
            }
        });

        // Agrega un ActionListener al botón btnCreate.
        btnCrear.addActionListener(s -> {
            // Crea una nueva instancia de UserWriteForm para la creación de un nuevo usuario, pasando la MainForm, la constante CREATE de CUD y un nuevo objeto User vacío.
            CitasWriteForm CitasWriteForm = null;
            try {
                CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.CREATE, new Citas());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
                CitasWriteForm CitasWriteForm = null;
                try {
                    CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.UPDATE, citas);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
                CitasWriteForm CitasWriteForm = null;
                try {
                    CitasWriteForm = new CitasWriteForm(this.mainForm, CUD.DELETE, citas);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Hace visible el formulario de escritura de usuario.
                CitasWriteForm.setVisible(true);
                // Limpia la tabla de usuarios creando y asignando un modelo de tabla vacío  para refrescar la lista después de la eliminación.
                DefaultTableModel emptyModel = new DefaultTableModel();
                tabDetalles.setModel(emptyModel);
            }
        });
    }
    private void search(String query) {
        try {
            // Llama al método 'search' del UserDAO para buscar usuarios cuya información
            // coincida con la cadena de búsqueda 'query'. La implementación específica
            ArrayList<Citas> citas = citasDAO.search(query);
            // Llama al método 'createTable' para actualizar la tabla de usuarios
            // en la interfaz gráfica con los resultados de la búsqueda.
            createTable(citas);
        } catch (Exception ex) {
            // Captura cualquier excepción que ocurra durante el proceso de búsqueda
            // (por ejemplo, errores de base de datos).
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE); // Muestra un mensaje de error al usuario.
            return; // Sale del método 'search' después de mostrar el error.
        }
    }

    public void createTable(ArrayList<Citas> citas) {

        // Crea un nuevo modelo de tabla por defecto (DefaultTableModel).
        // Se sobrescribe el método isCellEditable para hacer que todas las celdas de la tabla no sean editables.
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Retorna false para indicar que ninguna celda debe ser editable.
            }
        };

        // Define las columnas de la tabla. Los nombres de las columnas corresponden
        // a los atributos que se mostrarán de cada objeto User.
        model.addColumn("Id");
        model.addColumn("Paciente");
        model.addColumn("Doctor");
        model.addColumn("Fecha y Hora");
        model.addColumn("Estado");

        // Establece el modelo de tabla creado como el modelo de datos para la
        // JTable 'tabDetalles' (la tabla que se muestra en la interfaz gráfica).
        this.tabDetalles.setModel(model);

        // Declara un array de objetos 'row' que se utilizará temporalmente para agregar filas.
        Object row[] = null;

        // Itera a través de la lista de objetos User proporcionada.
        for (int i = 0; i < citas.size(); i++) {
            // Obtiene el objeto User actual de la lista.
            Citas cita = citas.get(i);
            // Agrega una nueva fila vacía al modelo de la tabla.
            model.addRow(row);
            // Establece el valor del ID del usuario en la celda correspondiente de la fila actual (columna 0).
            model.setValueAt(cita.getId(), i, 0);
            // Establece el valor del nombre del usuario en la celda correspondiente de la fila actual (columna 1).
            model.setValueAt(cita.getPacienteId(), i, 1);
            // Establece el valor del email del usuario en la celda correspondiente de la fila actual (columna 2).
            model.setValueAt(cita.getDoctorId(), i, 2);
            model.setValueAt(cita.getFechaHora(), i, 3);
            // Establece el valor del estatus del usuario (probablemente obtenido a través de un método 'getStrEstatus()')
            // en la celda correspondiente de la fila actual (columna 3).
            model.setValueAt(cita.getEstado(), i, 4);
        }

        // Llama al método 'hideCol' para ocultar la columna con índice 0 (la columna del ID).
        // Esto es común cuando el ID es necesario internamente pero no se quiere mostrar al usuario.
        hideCol(0);
    }

    private void hideCol(int pColumna) {
        // Obtiene el modelo de columnas de la JTable y establece el ancho máximo de la columna especificada a 0.
        // Esto hace que la columna no sea visible en la vista de datos de la tabla.
        this.tabDetalles.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        // Establece el ancho mínimo de la columna especificada a 0.
        // Esto asegura que la columna no ocupe espacio incluso si el layout manager intenta ajustarla.
        this.tabDetalles.getColumnModel().getColumn(pColumna).setMinWidth(0);
        // Realiza las mismas operaciones para el encabezado de la tabla.
        // Esto asegura que el nombre de la columna también se oculte y no ocupe espacio en la parte superior de la tabla.
        this.tabDetalles.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tabDetalles.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
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
