package org.example.presentacion;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import org.example.dominio.Citas; // Necesario para poblar el ComboBox de Citas
import org.example.dominio.Pago;
import org.example.dominio.Paciente;
import org.example.persistencia.PacienteDAO;
import org.example.persistencia.CitasDAO; // Necesario si vinculas Pago a Citas por ID en un ComboBox
import org.example.persistencia.PagoDAO;
import org.example.utils.CBOption;
import org.example.utils.CUD;


import javax.swing.*;
import java.awt.*;
import java.sql.SQLException; // Asegúrate de que esta importación esté presente
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

// ¡CORRECCIÓN CLAVE 1: Esta clase DEBE extender JDialog!
public class PagoWriteForm extends JDialog {

    // --- Componentes de la Interfaz de Usuario (UI) ---
    // Asegúrate de que estas variables estén declaradas aquí y enlazadas por tu diseñador de UI
    private JPanel prinPanel; // El panel principal que contiene todo el formulario
    private JLabel lblTitulo;
    private JLabel lblMonto;
    private JTextField txtMonto;
    private JButton btnGuardar;
    private JPanel panelFechaPago; // ¡Este es un JPanel, el CONTENEDOR del DatePicker!
    // ¡CORRECCIÓN CLAVE 2: Necesitas una instancia de DatePicker real!
    private DatePicker dpFechaPago; // La instancia del DatePicker

    private JButton btnCancelar;
    private JComboBox cbxcita; // ComboBox para seleccionar Cita
    private JLabel lblCita;
    private JLabel lblFechaPago; // Esta es una JLabel, no un DatePicker

    // --- Instancias de Datos y DAO ---
    private MainForm mainForm;
    private CUD cud;
    private Pago pago;
    private PagoDAO pagoDAO;
    private PacienteDAO pacienteDAO;

    // --- Constructor ---
    // El constructor debe declarar 'throws SQLException' si init() o initCboCita() lo hacen
    public PagoWriteForm(MainForm mainForm, CUD cud, Pago pago) throws SQLException {
        this.mainForm = mainForm;
        this.cud = cud;
        this.pago = pago;
        this.pagoDAO = new PagoDAO(); // Inicializa PagoDAO

        // --- Configuración de JDialog (estos requieren extender JDialog) ---
        setContentPane(prinPanel); // Establece el panel principal como contenido
        setModal(true); // Lo hace modal
        setTitle("Formulario de Pagos"); // Establece el título predeterminado
        setLocationRelativeTo(mainForm); // Centra el diálogo en relación con el formulario principal

        // --- Inicialización de Componentes y Lógica del Formulario ---
        // initDatePicker() DEBE ser llamado ANTES de init()
        initDatePicker(); // Inicializa el componente DatePicker y lo añade a panelFechaPago
        init(); // Llama al método init para configurar los componentes según CUD
        pack(); // Ajusta el tamaño del diálogo para que se ajusten sus componentes

        // --- Action Listeners ---
        btnCancelar.addActionListener(e -> dispose()); // Cierra el diálogo al cancelar
        btnGuardar.addActionListener(e -> guardarPago()); // Maneja guardar/actualizar/eliminar en el botón de guardar
    }

    // --- Método de Inicialización para la Lógica del Formulario ---
    private void init() throws SQLException { // Declara 'throws SQLException' porque 'initCboCita()' lo hace
        // Inicializa el ComboBox para Cita
        initCboCita();

        // Establece el título y el texto del botón según la operación CUD
        switch (this.cud) {
            case CREATE:
                lblTitulo.setText("Nuevo Pago"); // Establece el texto de lblTitulo para creación
                btnGuardar.setText("Crear");
                break;
            case UPDATE:
                lblTitulo.setText("Modificar Pago"); // Establece el texto de lblTitulo para actualización
                btnGuardar.setText("Guardar");
                break;
            case DELETE:
                lblTitulo.setText("Eliminar Pago"); // Establece el texto de lblTitulo para eliminación
                btnGuardar.setText("Eliminar");
                break;
        }

        // Rellena los controles con los datos de pago existentes si están disponibles
        setValuesControls(this.pago);
    }

    // --- Método para Inicializar el ComboBox de Citas ---
    private void initCboCita() throws SQLException { // Este método debe declarar 'throws SQLException'
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        try {
            List<Citas> citasList = new CitasDAO().search(""); // Obtiene todas las citas

            for (Citas c : citasList) {
                String nombrePaciente = "Paciente Desconocido"; // Valor por defecto
                int pacienteIdInt = 0; // Variable para almacenar el ID como int

                System.out.println("Procesando Cita ID: " + c.getId() + ", Paciente ID (String de Citas): " + c.getPacienteId());

                // INTENTA CONVERTIR EL pacienteId DE String A int
                try {
                    // Asegúrate de que c.getPacienteId() no sea null antes de intentar convertirlo
                    if (c.getPacienteId() != null && !c.getPacienteId().isEmpty()) {
                        pacienteIdInt = Integer.parseInt(c.getPacienteId());
                    }
                } catch (NumberFormatException e) {
                    // Si la conversión falla (porque no es un número), registra un error.
                    // Esta cita se manejará con el "Paciente Desconocido".
                    System.err.println("Advertencia: El pacienteId '" + c.getPacienteId() + "' en la cita " + c.getId() + " no es un número válido. " + e.getMessage());
                    // Puedes optar por 'continue;' aquí si prefieres saltar las citas con IDs inválidos.
                }
                System.out.println("Paciente ID (int convertido): " + pacienteIdInt);


                // Ahora usa pacienteIdInt para la comparación y para pasarlo a getById
                if (pacienteIdInt != 0) { // Verifica que el ID sea un número válido y no cero
                    Paciente paciente = pacienteDAO.getById(pacienteIdInt); // <-- ¡Aquí se pasa el int!
                    if (paciente != null && paciente.getNombre() != null) {
                        nombrePaciente = paciente.getNombre();
                    }
                }
                // Añade la opción al ComboBox: visiblemente el nombre del paciente, y el ID de la cita como valor
                // Nota: c.getId() es el ID de la Cita (int), no el pacienteId
                model.addElement(new CBOption(nombrePaciente, c.getId()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar citas o información de pacientes: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Error al cargar datos para el ComboBox de Citas: " + e.getMessage(), e);
        }
        cbxcita.setModel(model);
    }

    // --- Método para Inicializar el DatePicker ---
    private void initDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setLocale(new Locale("es", "ES")); // Establece la configuración regional para fechas en español
        dateSettings.setAllowKeyboardEditing(false); // Deshabilita la edición directa por teclado

        // ¡CORRECCIÓN CLAVE 3: Instancia el DatePicker en 'dpFechaPago'!
        dpFechaPago = new DatePicker(dateSettings);

        // Agrega el DatePicker a su panel contenedor (panelFechaPago)
        if (panelFechaPago != null) { // Asegura que el panelFechaPago esté inicializado por el diseñador
            panelFechaPago.setLayout(new BorderLayout()); // Asegura que el contenedor tenga un administrador de diseño
            // ¡CORRECCIÓN CLAVE 4: Añade 'dpFechaPago' (el DatePicker) a 'panelFechaPago' (el JPanel)!
            panelFechaPago.add(dpFechaPago, BorderLayout.CENTER);
        } else {
            System.err.println("Error: panelFechaPago no fue inicializado correctamente por el diseñador.");
            // Podrías lanzar una RuntimeException aquí si este panel es crucial
        }
    }


    // --- Método para Establecer Valores de Control desde el Objeto Pago ---
    private void setValuesControls(Pago pago) {
        // Establece el monto del pago
        if (pago.getMonto() != 0.0) { // Suponiendo que 0.0 es el valor predeterminado para un nuevo pago
            txtMonto.setText(String.valueOf(pago.getMonto()));
        } else {
            txtMonto.setText(""); // Limpia el campo si el monto es 0.0 o no está seteado
        }


        // ¡CORRECCIÓN CLAVE 5: Usa 'dpFechaPago' (el DatePicker) para establecer la fecha!
        if (pago.getFechaPago() != null && dpFechaPago != null) {
            dpFechaPago.setDate(pago.getFechaPago().toLocalDate());
        } else {
            dpFechaPago.setDate(null); // Limpia la fecha si no hay una asignada
        }

        // Selecciona la Cita ID correspondiente en el ComboBox
        if (pago.getCitaId() != null && !pago.getCitaId().isEmpty() && Integer.parseInt(pago.getCitaId()) != 0) {
            DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cbxcita.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                CBOption option = model.getElementAt(i);
                // Compara el ID de Cita (String) con el valor del CBOption (int, convertido a String)
                if (pago.getCitaId().equals(String.valueOf(option.getValue()))) {
                    cbxcita.setSelectedItem(option);
                    break;
                }
            }
        } else {
            cbxcita.setSelectedIndex(-1); // Deselecciona si no hay Cita ID
        }

        // Deshabilita los controles si la operación es ELIMINAR para evitar ediciones accidentales
        if (this.cud == CUD.DELETE) {
            txtMonto.setEnabled(false);
            // ¡CORRECCIÓN CLAVE 6: Deshabilita el DatePicker, no la JLabel!
            dpFechaPago.setEnabled(false);
            cbxcita.setEnabled(false);
            btnGuardar.setText("Confirmar Eliminación"); // Cambia el texto del botón para mayor claridad
        }
    }

    // --- Método para Obtener Valores de Control y Validar Entrada ---
    private boolean getValuesControls() {
        // Obtiene los valores de los controles y rellena el objeto Pago
        // Realiza una validación básica antes de continuar
        try {
            // Convertimos a double directamente, asumiendo que Pago.monto es double o float
            double monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser un número positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            this.pago.setMonto((float) monto); // Si Pago.monto es float, aquí ocurrirá una conversión implícita o error
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un valor numérico válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // ¡CORRECCIÓN CLAVE 7: Usa 'dpFechaPago' (el DatePicker) para obtener la fecha!
        if (dpFechaPago.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La fecha de pago es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Combina la fecha seleccionada con una hora predeterminada (medianoche)
        this.pago.setFechaPago(LocalDateTime.of(dpFechaPago.getDate(), LocalTime.MIDNIGHT));

        CBOption citaOp = (CBOption) cbxcita.getSelectedItem();
        if (citaOp == null || (int) citaOp.getValue() == 0) { // Suponiendo 0 para selección no válida/ninguna
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cita para el pago.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
// Convierte el int del CBOption a String antes de pasarlo a Pago.setCitaId()
        this.pago.setCitaId(String.valueOf(citaOp.getValue()));

        // Para operaciones de ACTUALIZACIÓN y ELIMINACIÓN, asegúrate de que el objeto Pago tenga un ID válido
        if (this.cud != CUD.CREATE && this.pago.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Error interno: ID de pago no válido para la operación de modificación/eliminación.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // --- Método para Manejar Guardar/Actualizar/Eliminar Pago ---
    private void guardarPago() {
        try {
            boolean valid = getValuesControls(); // Valida y rellena el objeto Pago

            if (valid) {
                boolean result = false;

                // Realiza la operación DAO según el modo CUD
                switch (this.cud) {
                    case CREATE:
                        Pago createdPago = pagoDAO.create(this.pago);
                        if (createdPago != null && createdPago.getId() > 0) result = true; // Verifica si la creación fue exitosa
                        break;
                    case UPDATE:
                        result = pagoDAO.update(this.pago);
                        break;
                    case DELETE:
                        result = pagoDAO.delete(this.pago);
                        break;
                }

                // Muestra retroalimentación al usuario
                if (result) {
                    JOptionPane.showMessageDialog(this, "Transacción exitosa", "Información", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cierra el diálogo al éxito
                } else {
                    JOptionPane.showMessageDialog(this, "No se logró realizar ninguna acción", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
            // Si 'valid' es falso, 'getValuesControls' ya mostró una advertencia, por lo que no es necesario otra aquí.

        } catch (Exception ex) {
            // Captura cualquier excepción inesperada durante el proceso
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime el seguimiento de la pila para fines de depuración
        }
    }

    // ¡CORRECCIÓN CLAVE 8: El método setVisible debe llamar a su superclase!
    @Override
    public  void setVisible(boolean b) { // Error en 'void void'
        super.setVisible(b);
    }
}