package org.example.persistencia;

import org.junit.jupiter.api.BeforeEach; // Anotación para indicar que el método se ejecuta antes de cada método de prueba.
import org.junit.jupiter.api.Test;       // Anotación para indicar que el método es un caso de prueba.
import org.example.dominio.Paciente;               // Clase que representa la entidad de paciente utilizada en las pruebas.

import java.util.ArrayList;              // Clase para crear listas dinámicas de objetos, utilizada en algunas pruebas.
import java.util.Random;                 // Clase para generar números aleatorios, útil para crear datos de prueba.
import java.sql.SQLException;             // Clase para manejar excepciones relacionadas con la base de datos, aunque no se espera que las pruebas unitarias interactúen directamente con ella (idealmente se mockean las dependencias).

import static org.junit.jupiter.api.Assertions.*; // Importación estática de métodos de aserción de JUnit 5 para verificar el comportamiento esperado en las pruebas.

import java.time.LocalDate; // Mantén esta importación, ya que la usas para las constantes


class PacienteDAOTest {
    private PacienteDAO PacienteDAO; // Instancia de la clase PacienteDAO que se va a probar.

    private static final LocalDate FECHA_NACIMIENTO_LOCAL_CREACION = LocalDate.of(1985, 1, 1); // Año, Mes, Día
    private static final LocalDate FECHA_NACIMIENTO_LOCAL_ACTUALIZACION = LocalDate.of(1990, 3, 15); // Año, Mes, Día

    @BeforeEach
    void setUp(){
        // Método que se ejecuta antes de cada método de prueba (@Test).
        // Su propósito es inicializar el entorno de prueba, en este caso,
        // creando una nueva instancia de PacienteDAO para cada prueba.
        PacienteDAO = new PacienteDAO();
    }
    private Paciente create(Paciente paciente) throws SQLException{
        // Llama al método 'create' del PacienteDAO para persistir el paciente en la base de datos (simulada).
        Paciente res = PacienteDAO.create(paciente);

        // Realiza aserciones para verificar que la creación del paciente fue exitosa
        // y que los datos del paciente retornado coinciden con los datos originales.
        assertNotNull(res, "El paciente creado no debería ser nulo."); // Verifica que el objeto retornado no sea nulo.
        assertEquals(paciente.getNombre(), res.getNombre(), "El nombre del paciente creado debe ser igual al original.");
        assertEquals(paciente.getDireccion(), res.getDireccion(), "El email del paciente creado debe ser igual al original.");
        assertEquals(paciente.getTelefono(), res.getTelefono(), "El telefono del paciente creado debe ser igual al original.");
        assertEquals(paciente.getFechaNacimiento(), res.getFechaNacimiento(), "La fecha de nacimiento del paciente creado debe ser igual a la original.");
        assertEquals(paciente.getGenero(), res.getGenero(), "El genero del paciente creado debe ser igual al original.");

        // Retorna el objeto Paciente creado (tal como lo devolvió el PacienteDAO).
        return res;
    }

    private void update(Paciente paciente) throws SQLException{
        // Modifica los atributos del objeto Paciente para simular una actualización.
        paciente.setNombre(paciente.getNombre() + "_modificado"); // Añade "_modificado" al final del nombre.
        paciente.setDireccion("Nueva Dirección " + paciente.getDireccion()); // Modifica la dirección.
        paciente.setTelefono("555-" + paciente.getTelefono()); // Añade un prefijo al teléfono.
        paciente.setGenero((byte) (paciente.getGenero() == 1 ? 2 : 1)); // Cambia el género (ejemplo: de 1 a 2, o de 2 a 1).

        paciente.setFechaNacimiento(FECHA_NACIMIENTO_LOCAL_ACTUALIZACION);

        // Llama al método 'update' del PacienteDAO para actualizar el paciente en la base de datos (simulada).
        boolean res = PacienteDAO.update(paciente);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(res, "La actualización del paciente debería ser exitosa.");

        // Llama al método 'getById' para verificar que los cambios se persistieron correctamente.
        // Aunque el método 'getById' ya tiene sus propias aserciones, esta llamada adicional
        // ayuda a asegurar que la actualización realmente tuvo efecto en la capa de datos.
        getById(paciente);
    }

    private void getById(Paciente paciente) throws SQLException {
        // Llama al método 'getById' del PacienteDAO para obtener un paciente por su ID.
        Paciente res = PacienteDAO.getById(paciente.getId());

        // Realiza aserciones para verificar que el paciente obtenido coincide
        // con el paciente original (o el paciente modificado en pruebas de actualización).
        assertNotNull(res, "El paciente obtenido por ID no debería ser nulo.");
        assertEquals(paciente.getId(), res.getId(), "El ID del paciente obtenido debe ser igual al original.");
        assertEquals(paciente.getNombre(), res.getNombre(), "El nombre del paciente creado debe ser igual al original.");
        assertEquals(paciente.getDireccion(), res.getDireccion(), "El email del paciente creado debe ser igual al original.");
        assertEquals(paciente.getTelefono(), res.getTelefono(), "El telefono del paciente creado debe ser igual al original.");
        // Comparamos las fechas de forma segura
        assertEquals(paciente.getFechaNacimiento(), res.getFechaNacimiento(), "La fecha de nacimiento del paciente obtenido debe ser igual a la original.");
        assertEquals(paciente.getGenero(), res.getGenero(), "El genero del paciente creado debe ser igual al original.");
    }

    private void search(Paciente paciente) throws SQLException {
        // Llama al método 'search' del PacienteDAO para buscar pacientes por nombre.
        ArrayList<Paciente> pacientes = PacienteDAO.search(paciente.getNombre());
        boolean find = false; // Variable para rastrear si se encontró un paciente con el nombre buscado.

        // Itera sobre la lista de pacientes devuelta por la búsqueda.
        for (Paciente pacienteItem : pacientes) {
            // Verifica si el nombre de cada paciente encontrado contiene la cadena de búsqueda.
            if (pacienteItem.getNombre().contains(paciente.getNombre())) {
                find = true; // Si se encuentra una coincidencia, se establece 'find' a true.
            }
            else{
                find = false; // Si un nombre no contiene la cadena de búsqueda, se establece 'find' a false.
                break;      // Se sale del bucle, ya que se esperaba que todos los resultados contuvieran la cadena.
            }
        }

        // Realiza una aserción para verificar que todos los pacientes con el nombre buscado fue encontrado.
        assertTrue(find, "el nombre buscado no fue encontrado : " + paciente.getNombre());
    }

    private void delete(Paciente paciente) throws SQLException{
        // Llama al método 'delete' del PacienteDAO para eliminar un paciente por su ID.
        boolean res = PacienteDAO.delete(paciente);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(res, "La eliminación del paciente debería ser exitosa.");

        // Intenta obtener el paciente por su ID después de la eliminación.
        Paciente res2 = PacienteDAO.getById(paciente.getId());

        // Realiza una aserción para verificar que el paciente ya no existe en la base de datos
        // después de la eliminación (el método 'getById' debería retornar null).
        assertNull(res2, "El paciente debería haber sido eliminado y no encontrado por ID.");
    }
    @Test
    void testPacienteDAO() throws SQLException {
        // Crea una instancia de la clase Random para generar datos de prueba aleatorios.
        Random random = new Random();
        // Genera un número aleatorio entre 1 y 1000 para asegurar la unicidad del teléfono en cada prueba.
        int num = random.nextInt(1000) + 1;

        Paciente paciente = new Paciente(0, "Paciente Prueba " + num, "Dirección de Prueba " + num,
                "123-456-" + num, FECHA_NACIMIENTO_LOCAL_CREACION, (byte) 1);

        // Llama al método 'create' para persistir el paciente de prueba en la base de datos y verifica su creación.
        Paciente testPaciente = create(paciente);

        // Llama al método 'update' para modificar los datos del paciente de prueba y verifica la actualización.
        update(testPaciente);

        // Llama al método 'search' para buscar pacientes por el nombre del paciente de prueba y verifica que se encuentre.
        search(testPaciente);

        // Llama al método 'delete' para eliminar el paciente de prueba de la base de datos y verifica la eliminación.
        delete(testPaciente);
    }

    //@Test
    void createPaciente() throws SQLException {
        // Define a specific birth date for the test patient
        LocalDate fechaNacimiento = LocalDate.of(1985, 10, 26);

        // Create a new Paciente object with test data
        Paciente paciente = new Paciente(0, "Juan Perez", "Avenida Siempre Viva 123",
                "7890-1234", fechaNacimiento, (byte) 1);

        // Call the create method on your pacienteDAO
        Paciente res = PacienteDAO.create(paciente);

        // Assert that the returned object is not null, indicating creation success
        assertNotNull(res);

        // Optional: You could add more assertions here, like checking if the ID was assigned
        // assertNotEquals(0, res.getId());
    }
}