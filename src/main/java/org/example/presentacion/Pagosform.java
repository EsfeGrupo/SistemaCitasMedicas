package org.example.presentacion;

import org.example.persistencia.PagoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.example.dominio.Pago; // Importa la clase Pago
import org.example.utils.CUD;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Pagosform extends JDialog{
    private JLabel LPresentacion;
    private JPanel JContenido;
    private JPanel pnlSub;
    private JTable tblDetalle;
    private JButton btnCrear;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTextField txtBuscar;
    private JPanel prinPanel;
    private JTextField textField2;
    private JLabel lblFecha;

    private PagoDAO pagoDAO;
    private MainForm mainForm;

    private  JLabel lblFechaActual;

    public Pagosform(MainForm mainForm){
        this.mainForm = mainForm; // Asigna la instancia de MainForm recibida a la variable local.
        PagoDAO PagoDAO = new PagoDAO();
        setContentPane(prinPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, bloqueando la interacción con la ventana principal hasta que se cierre.
        setTitle("Buscar Pagos"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.


    // Agrega un listener de teclado al campo de texto txtBuscar para buscar pagos.
        txtBuscar.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            // Aquí podrías implementar una lógica de búsqueda más sofisticada
            // Por ejemplo, buscar por CitaId o por Monto.
            // Para simplificar, si el campo no está vacío, buscamos, de lo contrario limpiamos.
            if (!txtBuscar.getText().trim().isEmpty()) {
                search(txtBuscar.getText());
            } else {
                // Si el campo de texto está vacío, recarga todos los pagos o limpia la tabla.
                loadAllPagos(); // Podrías llamar a un método para cargar todos los pagos
                // o simplemente limpiar la tabla si no quieres mostrar nada sin búsqueda.
            }
        }
    });
        // Agrega un ActionListener al botón btnCrear.
        btnCrear.addActionListener(s -> {
            // Crea una nueva instancia de PagoWriteForm para la creación de un nuevo pago.
            // Se le pasa la MainForm, el modo CUD. CREATE y un nuevo objeto Pago vacío.
            PagoWriteForm pagoWriteForm = new PagoWriteForm(this.mainForm, CUD.CREATE, new Pago());
            pagoWriteForm.setVisible(true);
            // Después de cerrar el formulario de creación, recarga los datos para ver los cambios.
            loadAllPagos(); // Recarga todos los pagos para reflejar el nuevo registro.
        });
        // Agrega un ActionListener al botón btnEditar.
        btnEditar.addActionListener(s -> {
            // Llama al método getPagoFromTableRow para obtener el pago seleccionado en la tabla.
            Pago pago = getPagoFromTableRow();
            if (pago != null) {
                // Crea una nueva instancia de PagoWriteForm para la actualización del pago seleccionado.
                // Se le pasa la MainForm, el modo CUD. UPDATE y el objeto Pago obtenido.
                PagoWriteForm pagoWriteForm = new PagoWriteForm(this.mainForm, CUD.UPDATE, pago);
                pagoWriteForm.setVisible(true);
                // Después de cerrar el formulario de edición, recarga los datos.
                loadAllPagos(); // Recarga todos los pagos para reflejar los cambios.
            }
        });
        // Agrega un ActionListener al botón btnEliminar.
        btnEliminar.addActionListener(s -> {
            // Llama al método getPagoFromTableRow para obtener el pago seleccionado en la tabla.
            Pago pago = getPagoFromTableRow();
            if (pago != null) {
                // Crea una nueva instancia de PagoWriteForm para la eliminación del pago seleccionado.
                // Se le pasa la MainForm, el modo CUD. DELETE y el objeto Pago obtenido.
                PagoWriteForm pagoWriteForm = new PagoWriteForm(this.mainForm, CUD.DELETE, pago);
                pagoWriteForm.setVisible(true);
                // Después de cerrar el formulario de eliminación, recarga los datos.
                loadAllPagos(); // Recarga todos los pagos para reflejar la eliminación.
            }
        });
        // Cargar todos los pagos al inicio para que la tabla no esté vacía
        loadAllPagos();
    }

    public Pagosform(Runnable runnable) {

    }

    // Método para cargar y mostrar todos los pagos en la tabla
    private void loadAllPagos() {
        try {
            ArrayList<Pago> pagos = (ArrayList<Pago>) pagoDAO.getAll(); // Asumo que PagoDAO tiene un getAll()
            createTable(pagos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar pagos: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search(String text) {
        try {
            // Aquí debes decidir cómo buscar. Por ejemplo, si buscas por citaId o monto.
            // Para este ejemplo, haré una búsqueda simple que filtre los ya cargados,
            // pero lo ideal sería que tu PagoDAO tuviera un método 'search(String query)'
            // que filtre en la base de datos.
            ArrayList<Pago> allPagos = (ArrayList<Pago>) pagoDAO.getAll();
            ArrayList<Pago> filteredPagos = new ArrayList<>();
            for (Pago pago : allPagos) {
                // Ejemplo: buscar por CitaId o por Monto como string
                CharSequence query = null;
                if (String.valueOf(pago.getCitaId()).contains(query) ||
                        String.valueOf(pago.getMonto()).contains(query)) {
                    filteredPagos.add(pago);
                }
            }
            createTable(filteredPagos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error durante la búsqueda de pagos: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    public void createTable(ArrayList<Pago> pagos) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Define las columnas de la tabla para Pagos
        model.addColumn("Id");
        model.addColumn("ID Cita");
        model.addColumn("Monto");
        model.addColumn("Fecha de Pago");

        this.tblDetalle.setModel(model);

        Object row[] = null;

        for (int i = 0; i < pagos.size(); i++) {
            Pago pago = pagos.get(i);
            model.addRow(row);
            model.setValueAt(pago.getId(), i, 0);
            model.setValueAt(pago.getCitaId(), i, 1);
            model.setValueAt(pago.getMonto(), i, 2);
            // Formatea la fecha y hora si es necesario para una mejor visualización
            if (pago.getFechaPago() != null) {
                model.setValueAt(pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), i, 3);
            } else {
                model.setValueAt("", i, 3);
            }
        }
        hideCol(0); // Oculta la columna del ID
    }
    private void hideCol(int pColumna) {
        this.tblDetalle.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tblDetalle.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tblDetalle.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tblDetalle.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }
    // Método para obtener el objeto Pago seleccionado de la fila de la tabla.
    private Pago getPagoFromTableRow() {
        Pago pago = null;
        try {
            int filaSelect = this.tblDetalle.getSelectedRow();
            int id = 0;

            if (filaSelect != -1) {
                id = (int) this.tblDetalle.getValueAt(filaSelect, 0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Selecciona una fila de la tabla para esta operación.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            pago = pagoDAO.getById(id); // Asumo que PagoDAO tiene un getById(id)

            if (pago == null || pago.getId() == 0) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró el pago seleccionado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return pago;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error al obtener el pago: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime la pila de errores para depuración
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Pagosform(this).notifyAll();
            }
        });
}
}
