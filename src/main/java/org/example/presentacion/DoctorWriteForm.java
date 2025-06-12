package org.example.presentacion;

import org.example.persistencia.DoctorDAO; // Importa la interfaz o clase UserDAO, que define las operaciones de acceso a datos para la entidad User.
import org.example.utils.CBOption; // Importa la clase CBOption, probablemente una clase utilitaria para manejar opciones de un ComboBox (por ejemplo, para asociar un valor con un texto).
import org.example.utils.CUD; // Importa el enum CUD (Create, Update, Delete),  para indicar el tipo de operación que se está realizando (Crear, Actualizar, Eliminar).

import javax.swing.*;

import org.example.dominio.Doctor; // Importa la clase doctor, que representa la entidad de doctor en el dominio de la aplicación.

public class DoctorWriteForm extends JDialog {
    private JPanel MainPanel;
    private JTextField txtEspecialidad;
    private JTextField txtNombre;
    private JTextField txtExperiencia;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JComboBox cboDisponibilidad;
    private JLabel lblTitulo;

    private DoctorDAO doctorDAO; // Instancia de la clase DoctorDAO para interactuar con la base de datos de doctor.
    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.
    private CUD cud; // Variable para almacenar el tipo de operación (Create, Update, Delete) que se está realizando en este formulario.
    private Doctor en; // Variable para almacenar el objeto User que se está creando, actualizando o eliminando.


    // Constructor de la clase DoctorWriteForm. Recibe la ventana principal, el tipo de operación CUD y un objeto User como parámetros.
    public DoctorWriteForm(MainForm mainForm, CUD cud, Doctor doctor) {
        this.cud = cud; // Asigna el tipo de operación CUD recibida a la variable local 'cud'.
        this.en = doctor; // Asigna el objeto User recibido a la variable local 'en'.
        this.mainForm = mainForm; // Asigna la instancia de MainForm recibida a la variable local 'mainForm'.
        doctorDAO = new DoctorDAO(); // Crea una nueva instancia de doctorDAO al instanciar este formulario.
        setContentPane(MainPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, bloqueando la interacción con la ventana principal hasta que se cierre.
        init(); // Llama al método 'init' para inicializar y configure the form based on 'cud'
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.

        // Agrega un ActionListener al botón 'btnCancel' para cerrar la ventana actual (UserWriteForm).
        btnCancelar.addActionListener(s -> this.dispose());
        // Agrega an ActionListener to the 'btnOk' to trigger the save/update/delete action
        btnGuardar.addActionListener(s -> btnGuardar());
    }

    private void init() {
        // Inicializa el ComboBox de disponibilidad (cboDisponibilidad) con las opciones correspondientes.
        initcboDisponibilidad();

        // Realiza acciones específicas en la interfaz de usuario basadas en el tipo de operación (CUD).
        switch (this.cud) {
            case CREATE:
                // Si la operación es de creación, establece el título de la ventana como "Crear Usuario".
                setTitle("Crear Usuario");
                // Establece el texto del botón de acción principal (btnOk) como "Guardar".
                btnGuardar.setText("Guardar");
                break;
            case UPDATE:
                // Si la operación es de actualización, establece el título de la ventana como "Modificar Usuario".
                setTitle("Modificar Usuario");
                // Establece el texto del botón de acción principal (btnOk) como "Guardar".
                btnGuardar.setText("Guardar");
                break;
            case DELETE:
                // Si la operación es de eliminación, establece el título de la ventana como "Eliminar Usuario".
                setTitle("Eliminar Usuario");
                // Establece el texto del botón de acción principal (btnOk) como "Eliminar".
                btnGuardar.setText("Eliminar");
                break;
        }

        // Llama al método 'setValuesControls' para llenar los campos del formulario
        // con los valores del objeto Doctor proporcionado ('this.en').
        // Esto es especialmente útil para las operaciones de actualización y eliminación,
        // donde se deben mostrar los datos existentes del doctor.
        setValuesControls(this.en);
    }

    private void initcboDisponibilidad() {
        // Obtiene el modelo actual del ComboBox 'cboDisponibilidad' y lo castea a DefaultComboBoxModel
        // para poder manipular sus elementos.
        DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cboDisponibilidad.getModel();

        // Crea una nueva opción 'ACTIVO' con un valor asociado de byte 1 y la agrega al modelo del ComboBox.
        // Cuando esta opción se seleccione en el ComboBox, el valor subyacente será (byte)1.
        model.addElement(new CBOption("Disponible", (byte)1));

        // Crea una nueva opción 'INACTIVO' con un valor asociado de byte 2 y la agrega al modelo del ComboBox.
        // Cuando esta opción se seleccione en el ComboBox, el valor subyacente será (byte)2.
        model.addElement(new CBOption("No disponible", (byte)2));
    }

    private void setValuesControls(Doctor doctor) {
        // Llena el campo de texto 'txtNombre' con el nombre del usuario.
        txtNombre.setText(doctor.getNombre());

        // Llena el campo de texto 'txtEspecialidad' con la especialidad del doctor.
        txtEspecialidad.setText(doctor.getEspecialidad());

        // Llena el campo de texto 'txtEspecialidad' con la especialidad del doctor.
        txtExperiencia.setText(String.valueOf(doctor.getExperiencia()));

        // Seleccionar el estatus en el ComboBox 'cboDisponibilidad'.
        cboDisponibilidad.setSelectedItem(new CBOption(null, doctor.getDisponibilidad()));

        // Si la operación actual es la creación de un nuevo usuario (CUD.CREATE).

        // Si la operación actual es la eliminación de un usuario (CUD.DELETE).
        if (this.cud == CUD.DELETE) {
            // Deshabilita la edición del campo de texto 'txtNombre' para evitar modificaciones.
            txtNombre.setEditable(false);
            // Deshabilita la edición del campo de texto 'txtNombre' para evitar modificaciones.
            txtNombre.setEditable(false);
            txtExperiencia.setEditable(false);
            // Deshabilita el ComboBox 'cboDisponibilidad' para evitar cambios en el estatus.
            cboDisponibilidad.setEnabled(false);
        }

        // Si la operación actual no es la creación de un usuario (es decir, es actualización o eliminación).

    }

    private boolean getValuesControls() {
        boolean res = false; // Inicializa la variable 'res' a false (indicando inicialmente que la validación falla).

        // Obtiene la opción seleccionada del ComboBox 'cboDisponibilidad'.
        CBOption selectedOption = (CBOption) cboDisponibilidad.getSelectedItem();
        // Obtiene el valor del estatus de la opción seleccionada.
        // Si no hay ninguna opción seleccionada (selectedOption es null), se asigna el valor 0 al estatus.
        byte disponibilidad = selectedOption != null ? (byte) (selectedOption.getValue()) : (byte) 0;

        // Realiza una serie de validaciones en los campos de entrada:

        // 1. Verifica si el campo de texto 'txtNombre' está vacío (después de eliminar espacios en blanco al inicio y al final).
        if (txtNombre.getText().trim().isEmpty()) {
            return res; // Si está vacío, retorna false (validación fallida).
        }
        // 2. Verifica si el campo de texto 'txtNombre' está vacío (después de eliminar espacios en blanco al inicio y al final).
        else if (txtNombre.getText().trim().isEmpty()) {
            return res; // Si está vacío, retorna false (validación fallida).
        }
        // 3. Verifica si el estatus es igual a 0.
        // (Asume que 0 es un valor inválido o no seleccionado para el estatus).
        else if (disponibilidad == (byte) 0) {
            return res; // Si es 0, retorna false (validación fallida).
        }
        // 4. Verifica si la operación actual no es la creación (CUD.CREATE)
        // Y si el ID del objeto User 'en' es 0.
        // Esto podría indicar un error o inconsistencia en los datos para la actualización o eliminación.
        else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res; // Si se cumple la condición, retorna false (validación fallida).
        }

        // Si todas las validaciones anteriores pasan, se considera que los datos son válidos.
        res = true; // Establece 'res' a true.

        // Actualiza los atributos del objeto User 'en' con los valores ingresados en los campos:

        // Establece el nombre del usuario.
        this.en.setNombre(txtNombre.getText());
        // Establece el correo electrónico del usuario.
        this.en.setEspecialidad(txtEspecialidad.getText());
        this.en.setExperiencia(Float.parseFloat(txtExperiencia.getText()));
        // Establece el disponibilidad del usuario.
        this.en.setDisponibilidad(disponibilidad);

        // Retorna true, indicando que los datos son válidos y se han asignado al objeto User.
        return res;
    }

    private void btnGuardar() {
        try {
            // Obtener y validar los valores de los controles del formulario.
            boolean res = getValuesControls();

            // Si la validación de los controles fue exitosa.
            if (res) {
                boolean r = false; // Variable para almacenar el resultado de la operación de la base de datos.

                // Realiza la operación de la base de datos según el tipo de operación actual (CREATE, UPDATE, DELETE).
                switch (this.cud) {
                    case CREATE:
                        // Caso de creación de un nuevo usuario.
                        // Llama al método 'create' de userDAO para persistir el nuevo usuario (this.en).
                        Doctor doctor = doctorDAO.create(this.en);
                        // Verifica si la creación fue exitosa comprobando si el nuevo usuario tiene un ID asignado.
                        if (doctor.getId() > 0) {
                            r = true; // Establece 'r' a true si la creación fue exitosa.
                        }
                        break;
                    case UPDATE:
                        // Caso de actualización de un usuario existente.
                        // Llama al método 'update' de userDAO para guardar los cambios del usuario (this.en).
                        r = doctorDAO.update(this.en); // 'r' será true si la actualización fue exitosa, false en caso contrario.
                        break;
                    case DELETE:
                        // Caso de eliminación de un usuario.
                        // Llama al método 'delete' de userDAO para eliminar el usuario (this.en).
                        r = doctorDAO.delete(this.en); // 'r' será true si la eliminación fue exitosa, false en caso contrario.
                        break;
                }

                // Si la operación de la base de datos (creación, actualización o eliminación) fue exitosa.
                if (r) {
                    // Muestra un mensaje de éxito al usuario.
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    // Cierra la ventana actual (UserWriteForm).
                    this.dispose();
                } else {
                    // Si la operación de la base de datos falló.
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return; // Sale del método.
                }
            } else {
                // Si la validación de los controles falló (algún campo obligatorio está vacío o inválido).
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return; // Sale del método.
            }
        } catch (Exception ex) {
            // Captura cualquier excepción que ocurra durante el proceso (por ejemplo, errores de base de datos).
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return; // Sale del método.
        }
    }
}
