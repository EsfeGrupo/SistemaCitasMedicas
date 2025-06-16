package org.example.presentacion;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import org.example.dominio.Citas;
import org.example.dominio.Pago;
import org.example.dominio.Paciente;
import org.example.persistencia.PacienteDAO;
import org.example.persistencia.CitasDAO;
import org.example.persistencia.PagoDAO;
import org.example.utils.CBOption;
import org.example.utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class PagoWriteForm extends JDialog {

    private JPanel prinPanel;
    private JLabel lblTitulo;
    private JLabel lblMonto;
    private JTextField txtMonto;
    private JButton btnGuardar;
    private JPanel panelFechaPago;
    private DatePicker dpFechaPago;
    private JButton btnCancelar;
    private JComboBox cbxcita;
    private JLabel lblCita;
    private JLabel lblFechaPago;

    private MainForm mainForm;
    private CUD cud;
    private Pago pago;
    private PagoDAO pagoDAO;
    private PacienteDAO pacienteDAO;

    public PagoWriteForm(MainForm mainForm, CUD cud, Pago pago) throws SQLException {
        this.mainForm = mainForm;
        this.cud = cud;
        this.pago = pago;
        this.pagoDAO = new PagoDAO();
        this.pacienteDAO = new PacienteDAO(); // ← Línea clave

        setContentPane(prinPanel);
        setModal(true);
        setTitle("Formulario de Pagos");
        setLocationRelativeTo(mainForm);

        initDatePicker();
        init();
        pack();

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarPago());
    }

    private void init() throws SQLException {
        initCboCita();

        switch (this.cud) {
            case CREATE:
                lblTitulo.setText("Nuevo Pago");
                btnGuardar.setText("Crear");
                break;
            case UPDATE:
                lblTitulo.setText("Modificar Pago");
                btnGuardar.setText("Guardar");
                break;
            case DELETE:
                lblTitulo.setText("Eliminar Pago");
                btnGuardar.setText("Eliminar");
                break;
        }

        setValuesControls(this.pago);
    }

    private void initCboCita() throws SQLException {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        List<Citas> citasList = new CitasDAO().search("");

        for (Citas c : citasList) {
            String nombrePaciente = "Paciente Desconocido";
            int pacienteIdInt = c.getPacienteId();

            if (pacienteIdInt != 0) {
                Paciente paciente = pacienteDAO.getById(pacienteIdInt);
                if (paciente != null && paciente.getNombre() != null) {
                    nombrePaciente = paciente.getNombre();
                }
            }

            model.addElement(new CBOption(nombrePaciente, c.getId()));
        }

        cbxcita.setModel(model);
    }


    private void initDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setLocale(new Locale("es", "ES"));
        dateSettings.setAllowKeyboardEditing(false);

        dpFechaPago = new DatePicker(dateSettings);

        if (panelFechaPago != null) {
            panelFechaPago.setLayout(new BorderLayout());
            panelFechaPago.add(dpFechaPago, BorderLayout.CENTER);
        }
    }

    private void setValuesControls(Pago pago) {
        txtMonto.setText(pago.getMonto() != 0.0 ? String.valueOf(pago.getMonto()) : "");

        if (pago.getFechaPago() != null && dpFechaPago != null) {
            dpFechaPago.setDate(pago.getFechaPago().toLocalDate());
        } else {
            dpFechaPago.setDate(null);
        }

        if (pago.getCitaId() != null && !pago.getCitaId().isEmpty()) {
            DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cbxcita.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                CBOption option = model.getElementAt(i);
                if (pago.getCitaId().equals(String.valueOf(option.getValue()))) {
                    cbxcita.setSelectedItem(option);
                    break;
                }
            }
        } else {
            cbxcita.setSelectedIndex(-1);
        }

        if (this.cud == CUD.DELETE) {
            txtMonto.setEnabled(false);
            dpFechaPago.setEnabled(false);
            cbxcita.setEnabled(false);
            btnGuardar.setText("Confirmar Eliminación");
        }
    }

    private boolean getValuesControls() {
        try {
            double monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            this.pago.setMonto((float) monto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (dpFechaPago.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La fecha de pago es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        this.pago.setFechaPago(LocalDateTime.of(dpFechaPago.getDate(), LocalTime.MIDNIGHT));

        CBOption citaOp = (CBOption) cbxcita.getSelectedItem();
        if (citaOp == null || (int) citaOp.getValue() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cita.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        this.pago.setCitaId(String.valueOf(citaOp.getValue()));

        if (this.cud != CUD.CREATE && this.pago.getId() == 0) {
            JOptionPane.showMessageDialog(this, "ID de pago no válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void guardarPago() {
        try {
            if (!getValuesControls()) return;

            boolean result = false;
            switch (this.cud) {
                case CREATE:
                    Pago createdPago = pagoDAO.create(this.pago);
                    result = createdPago != null && createdPago.getId() > 0;
                    break;
                case UPDATE:
                    result = pagoDAO.update(this.pago);
                    break;
                case DELETE:
                    result = pagoDAO.delete(this.pago);
                    break;
            }

            if (result) {
                JOptionPane.showMessageDialog(this, "Transacción exitosa", "Información", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se realizó ninguna acción", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }
}
