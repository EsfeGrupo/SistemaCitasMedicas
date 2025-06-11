package org.example.persistencia;

import org.example.dominio.Paciente;
import org.junit.jupiter.api.BeforeEach; // Anotación para indicar que el método se ejecuta antes de cada método de prueba.
import org.junit.jupiter.api.Test;       // Anotación para indicar que el método es un caso de prueba.
import org.example.dominio.Doctor;               // Clase que representa la entidad de doctor utilizada en las pruebas.

import java.util.ArrayList;              // Clase para crear listas dinámicas de objetos, utilizada en algunas pruebas.
import java.util.Random;                 // Clase para generar números aleatorios, útil para crear datos de prueba.
import java.sql.SQLException;             // Clase para manejar excepciones relacionadas con la base de datos, aunque no se espera que las pruebas unitarias interactúen directamente con ella (idealmente se mockean las dependencias).

import static org.junit.jupiter.api.Assertions.*;

class DoctorDAOTest {
    private DoctorDAO DoctorDAO; // Instancia de la clase doctorDAO que se va a probar.

    @BeforeEach
    void setUp(){
        // Método que se ejecuta antes de cada método de prueba (@Test).
        // Su propósito es inicializar el entorno de prueba, en este caso,
        // creando una nueva instancia de doctorDAO para cada prueba.
        DoctorDAO = new DoctorDAO();
    }
    private Doctor create(Doctor doctor) throws SQLException{
        // Llama al método 'create' del DoctorDAO para persistir el doctor en la base de datos (simulada).
        Doctor res = DoctorDAO.create(doctor);

        // Realiza aserciones para verificar que la creación del doctor fue exitosa
        // y que los datos del doctor retornado coinciden con los datos originales.
        assertNotNull(res, "El doctor creado no debería ser nulo."); // Verifica que el objeto retornado no sea nulo.
        assertEquals(doctor.getNombre(), res.getNombre(), "El nombre del doctor creado debe ser igual al original.");
        assertEquals(doctor.getEspecialidad(), res.getEspecialidad(), "El email del doctor creado debe ser igual al original.");
        assertEquals(doctor.getExperiencia(), res.getExperiencia(), "El telefono del doctor creado debe ser igual al original.");
        assertEquals(doctor.getDisponibilidad(), res.getDisponibilidad(), "El genero del doctor creado debe ser igual al original.");

        // Retorna el objeto doctor creado (tal como lo devolvió el DoctorDAO).
        return res;
    }

    private void update(Doctor doctor) throws SQLException{
        // Modifica los atributos del objeto doctor para simular una actualización.
        doctor.setNombre(doctor.getNombre() + "_modificado"); // Añade "_modificado" al final del nombre.
        doctor.setEspecialidad("Nueva Dirección " + doctor.getEspecialidad()); // Modifica la dirección.
        doctor.setDisponibilidad((byte) (doctor.getDisponibilidad() == 1 ? 2 : 1)); // Cambia el género (ejemplo: de 1 a 2, o de 2 a 1).


        // Llama al método 'update' del DoctorDAO para actualizar el doctor en la base de datos (simulada).
        boolean res = DoctorDAO.update(doctor);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(res, "La actualización del doctor debería ser exitosa.");

        // Llama al método 'getById' para verificar que los cambios se persistieron correctamente.
        // Aunque el método 'getById' ya tiene sus propias aserciones, esta llamada adicional
        // ayuda a asegurar que la actualización realmente tuvo efecto en la capa de datos.
        getById(doctor);
    }

    private void getById(Doctor doctor) throws SQLException {
        // Llama al método 'getById' del DoctorDAO para obtener un doctor por su ID.
        Doctor res = DoctorDAO.getById(doctor.getId());

        // Realiza aserciones para verificar que el doctor obtenido coincide
        // con el doctor original (o el doctor modificado en pruebas de actualización).
        assertNotNull(res, "El doctor obtenido por ID no debería ser nulo.");
        assertEquals(doctor.getId(), res.getId(), "El ID del doctor obtenido debe ser igual al original.");
        assertEquals(doctor.getNombre(), res.getNombre(), "El nombre del doctor creado debe ser igual al original.");
        assertEquals(doctor.getEspecialidad(), res.getEspecialidad(), "El email del doctor creado debe ser igual al original.");
        assertEquals(doctor.getExperiencia(), res.getExperiencia(), "El telefono del doctor creado debe ser igual al original.");
        assertEquals(doctor.getDisponibilidad(), res.getDisponibilidad(), "El genero del doctor creado debe ser igual al original.");
    }

    private void search(Doctor doctor) throws SQLException {
        // Llama al método 'search' del DoctorDAO para buscar doctores por nombre.
        ArrayList<Doctor> doctores = DoctorDAO.search(doctor.getNombre());
        boolean find = false; // Variable para rastrear si se encontró un doctor con el nombre buscado.

        // Itera sobre la lista de doctores devuelta por la búsqueda.
        for (Doctor doctorItem : doctores) {
            // Verifica si el nombre de cada doctor encontrado contiene la cadena de búsqueda.
            if (doctorItem.getNombre().contains(doctor.getNombre())) {
                find = true; // Si se encuentra una coincidencia, se establece 'find' a true.
            }
            else{
                find = false; // Si un nombre no contiene la cadena de búsqueda, se establece 'find' a false.
                break;      // Se sale del bucle, ya que se esperaba que todos los resultados contuvieran la cadena.
            }
        }

        // Realiza una aserción para verificar que todos los doctores con el nombre buscado fue encontrado.
        assertTrue(find, "el nombre buscado no fue encontrado : " + doctor.getNombre());
    }

    private void delete(Doctor doctor) throws SQLException{
        // Llama al método 'delete' del DoctorDAO para eliminar un doctor por su ID.
        boolean res = DoctorDAO.delete(doctor);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(res, "La eliminación del doctor debería ser exitosa.");

        // Intenta obtener el doctor por su ID después de la eliminación.
        Doctor res2 = DoctorDAO.getById(doctor.getId());

        // Realiza una aserción para verificar que el doctor ya no existe en la base de datos
        // después de la eliminación (el método 'getById' debería retornar null).
        assertNull(res2, "El doctor debería haber sido eliminado y no encontrado por ID.");
    }
    @Test
    void testDoctorDAO() throws SQLException {
        // Crea una instancia de la clase Random para generar datos de prueba aleatorios.
        Random random = new Random();
        // Genera un número aleatorio entre 1 y 1000 para asegurar la unicidad del teléfono en cada prueba.
        int num = random.nextInt(1000) + 1;

        Doctor doctor = new Doctor(0, "Doctor Prueba " + num, "Cardiología " + num, 5.0f, (byte) 2);

        // Llama al método 'create' para persistir el paciente de prueba en la base de datos y verifica su creación.
        Doctor testDoctor = create(doctor);

        // Llama al método 'update' para modificar los datos del paciente de prueba y verifica la actualización.
        update(testDoctor);

        // Llama al método 'search' para buscar pacientes por el nombre del paciente de prueba y verifica que se encuentre.
        search(testDoctor);

        // Llama al método 'delete' para eliminar el paciente de prueba de la base de datos y verifica la eliminación.
        delete(testDoctor);
    }
    @Test
    void create() {
    }
}