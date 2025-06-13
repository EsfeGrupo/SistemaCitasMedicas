package org.example.presentacion;

import org.example.persistencia.PacienteDAO;
import org.example.utils.CBOption;
import org.example.utils.CUD;

import javax.swing.*;
import org.example.dominio.Paciente;

import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;


public class PacienteWriteForm extends JDialog {
    private JPanel MainPanel;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;

    // AHORA TENEMOS DOS VARIABLES:
    // 1. Esta variable es el JPanel que arrastraste en el diseñador.
    //    El diseñador de IntelliJ lo enlaza automáticamente.
    private JPanel panelFechaNacimientoContenedor; // <--- RENOMBRA ESTA VARIABLE

    // 2. Esta variable es la instancia real del DatePicker que creas en el código.
    private DatePicker dpFechaNacimiento; // <--- MANTÉN ESTA VARIABLE ASÍ

    private JComboBox cboGenero;
    private JLabel lblTitulo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private PacienteDAO pacienteDAO;
    private MainForm mainForm;
    private CUD cud;
    private Paciente en;

    public PacienteWriteForm(MainForm mainForm, CUD cud, Paciente paciente) {
        this.cud = cud;
        this.en = paciente;
        this.mainForm = mainForm;
        pacienteDAO = new PacienteDAO();
        setContentPane(MainPanel);
        setModal(true);
        // NO LLAMES initDatePicker() AQUÍ DIRECTAMENTE.
        // El método $$$setupUI$$$ que genera IntelliJ debe ejecutar primero.
        // init() ya está bien porque se llama después de setContentPane(MainPanel);
        init();
        pack();
        setLocationRelativeTo(mainForm);

        btnCancelar.addActionListener(s -> this.dispose());
        btnGuardar.addActionListener(s -> Guardar());
    }

    private void init() {
        initcboGenero();
        initDatePicker(); // <-- Mantenemos la llamada aquí para que se ejecute después del setup del UI

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Paciente");
                btnGuardar.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Paciente");
                btnGuardar.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Paciente");
                btnGuardar.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void initcboGenero() {
        DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cboGenero.getModel();
        model.addElement(new CBOption("Masculino", (byte)1));
        model.addElement(new CBOption("Femenino", (byte)2));
        model.addElement(new CBOption("Otro", (byte)3));
    }

    // Método para inicializar y configurar LGoodDatePicker
    private void initDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setLocale(new Locale("es", "ES"));
        dateSettings.setAllowKeyboardEditing(false);

        dpFechaNacimiento = new DatePicker(dateSettings); // ¡Aquí creamos la instancia del DatePicker!

        // ESTA ES LA PARTE CLAVE:
        // Añadimos el DatePicker (dpFechaNacimiento) al JPanel contenedor (panelFechaNacimientoContenedor)
        // que ha sido enlazado automáticamente por el diseñador de UI.
        if (panelFechaNacimientoContenedor != null) {
            panelFechaNacimientoContenedor.setLayout(new BorderLayout()); // Asegura que el panel contenedor tenga un layout
            panelFechaNacimientoContenedor.add(dpFechaNacimiento, BorderLayout.CENTER); // Añade el DatePicker al panel
        } else {
            System.err.println("Error: panelFechaNacimientoContenedor no fue inicializado correctamente por el diseñador.");
        }
    }

    private void setValuesControls(Paciente paciente) {
        txtNombre.setText(paciente.getNombre());
        txtDireccion.setText(paciente.getDireccion());
        txtTelefono.setText(paciente.getTelefono());

        if (paciente.getFechaNacimiento() != null) {
            dpFechaNacimiento.setDate(paciente.getFechaNacimiento());
        } else {
            dpFechaNacimiento.setDate(null);
        }

        // Selección del género
        // Este bucle es más robusto si CBOption no sobrescribe equals/hashCode
        cboGenero.setSelectedItem(new CBOption(null, paciente.getGenero()));
        // Puedes borrar la línea de abajo si el bucle ya hace el trabajo
        // cboGenero.setSelectedItem(new CBOption(null, paciente.getGenero()));


        if (this.cud == CUD.DELETE) {
            txtNombre.setEditable(false);
            txtDireccion.setEditable(false);
            txtTelefono.setEditable(false);
            dpFechaNacimiento.setEnabled(false); // Deshabilita el DatePicker
            cboGenero.setEnabled(false);
        }
    }

    private boolean getValuesControls() {
        boolean res = false;
        CBOption selectedOption = (CBOption) cboGenero.getSelectedItem();
        byte status = selectedOption != null ? (byte) (selectedOption.getValue()) : (byte) 0;

        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo Nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return res;
        } else if (txtDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo Dirección es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return res;
        } else if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo Teléfono es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return res;
        }
        else if (dpFechaNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una fecha de nacimiento.", "Validación", JOptionPane.WARNING_MESSAGE);
            return res;
        }
        else if (status == (byte) 0) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un género.", "Validación", JOptionPane.WARNING_MESSAGE);
            return res;
        }
        else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Error: ID de paciente no válido para la operación.", "Validación", JOptionPane.ERROR_MESSAGE);
            return res;
        }

        res = true;

        this.en.setNombre(txtNombre.getText());
        this.en.setDireccion(txtDireccion.getText());
        this.en.setTelefono(txtTelefono.getText());
        this.en.setFechaNacimiento(dpFechaNacimiento.getDate());
        this.en.setGenero(status);

        return res;
    }

    private void Guardar() {
        try {
            boolean res = getValuesControls();

            if (res) {
                boolean r = false;

                switch (this.cud) {
                    case CREATE:
                        Paciente paciente = pacienteDAO.create(this.en);
                        if (paciente.getId() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = pacienteDAO.update(this.en);
                        break;
                    case DELETE:
                        r = pacienteDAO.delete(this.en);
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error inesperado: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}