package org.example.presentacion;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import org.example.dominio.Citas;
import org.example.dominio.Doctor;
import org.example.dominio.Paciente;
import org.example.persistencia.CitasDAO;
import org.example.persistencia.DoctorDAO;
import org.example.persistencia.PacienteDAO;
import org.example.utils.CBOption;
import org.example.utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class CitasWriteForm extends JDialog {
    private JPanel MainPanel;
    private JButton btnCrear;
    private JButton btnCancelar;
    private JComboBox cboEstado;
    private JComboBox cboDoctorId;
    private JComboBox cboPacienteId;
    private JPanel mainPanel;
    private JPanel panelFechaNacimientoContenedor;

    // 2. Esta variable es la instancia real del DatePicker que creas en el código.
    private DatePicker dpFechaNacimiento; // <--- MANTÉN ESTA VARIABLE ASÍ

    private MainForm mainForm;
    private CUD cud;
    private Citas citas;
    private CitasDAO citasDAO;

    public CitasWriteForm(MainForm mainForm, CUD cud, Citas citas) throws SQLException {
        this.mainForm = mainForm;
        this.cud = cud;
        this.citas = citas;
        this.citasDAO = new CitasDAO();

        setContentPane(MainPanel);
        setModal(true);
        setTitle("Formulario de Citas");
        setLocationRelativeTo(mainForm);
        initDatePicker(); // <-- AGREGA ESTA LÍNEA
        init();
        pack();


        btnCancelar.addActionListener(e -> dispose());
        btnCrear.addActionListener(e -> guardarCita());
    }

    private void init() throws SQLException {
        initCboEstado();
        initCboDoctor();
        initCboPaciente();

        switch (this.cud) {
            case CREATE:
                setTitle("Nueva Cita");
                btnCrear.setText("Crear");
                break;
            case UPDATE:
                setTitle("Modificar Cita");
                btnCrear.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Cita");
                btnCrear.setText("Eliminar");
                break;
        }

        setValuesControls(this.citas);
    }

    private void initCboEstado() {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        model.addElement(new CBOption("Pendiente", (byte) 1));
        model.addElement(new CBOption("Confirmada", (byte) 2));
        model.addElement(new CBOption("Cancelada", (byte) 3));
        cboEstado.setModel(model);
    }

    private void initCboDoctor() throws SQLException {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        List<Doctor> doctores = new DoctorDAO().search(""); // Assuming 0 gets all doctors
        for (Doctor d : doctores) {
            model.addElement(new CBOption(d.getNombre(), d.getId()));
        }
        cboDoctorId.setModel(model);
    }

    private void initCboPaciente() throws SQLException {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        List<Paciente> pacientes = new PacienteDAO().search(""); // Assuming empty string gets all patients
        for (Paciente p : pacientes) {
            model.addElement(new CBOption(p.getNombre(), p.getId()));
        }
        cboPacienteId.setModel(model);
    }
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
    private void setValuesControls(Citas cita) {
        if (cita.getFechaHora() != null && dpFechaNacimiento != null) {
            dpFechaNacimiento.setDate(cita.getFechaHora().toLocalDate());
        }

        cboEstado.setSelectedItem(new CBOption(null, cita.getEstado()));
        cboDoctorId.setSelectedItem(new CBOption(null, cita.getDoctorId()));
        cboPacienteId.setSelectedItem(new CBOption(null, cita.getPacienteId()));

        if (this.cud == CUD.DELETE) {
            dpFechaNacimiento.setEnabled(false); // Deshabilita el DatePicker
            cboEstado.setEnabled(false);
            cboDoctorId.setEnabled(false);
            cboPacienteId.setEnabled(false);
        }
    }

    private boolean getValuesControls() {
        boolean res = false;

        CBOption estadoOp = (CBOption) cboEstado.getSelectedItem();
        CBOption doctorOp = (CBOption) cboDoctorId.getSelectedItem();
        CBOption pacienteOp = (CBOption) cboPacienteId.getSelectedItem();

        byte estado = estadoOp != null ? (byte) estadoOp.getValue() : 0;
        int doctorId = doctorOp != null ? (int) doctorOp.getValue() : 0;
        int pacienteId = pacienteOp != null ? (int) pacienteOp.getValue() : 0;

        if (dpFechaNacimiento.getDate() == null || estado == 0 || doctorId == 0 || pacienteId == 0)
            return false;

        if (this.cud != CUD.CREATE && this.citas.getId() == 0)
            return false;

        this.citas.setFechaHora(dpFechaNacimiento.getDate().atStartOfDay());
        this.citas.setEstado(String.valueOf(estado));
        this.citas.setDoctorId(String.valueOf(doctorId));
        this.citas.setPacienteId(String.valueOf(pacienteId));

        return true;
    }

    private void guardarCita() {
        try {
            boolean valid = getValuesControls();

            if (valid) {
                boolean result = false;

                switch (this.cud) {
                    case CREATE:
                        Citas creada = citasDAO.create(this.citas);
                        if (creada.getId() > 0) result = true;
                        break;
                    case UPDATE:
                        result = citasDAO.update(this.citas);
                        break;
                    case DELETE:
                        result = citasDAO.delete(this.citas);
                        break;
                }

                if (result) {
                    JOptionPane.showMessageDialog(this, "Transacción exitosa", "Información", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se logró realizar ninguna acción", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }
}
