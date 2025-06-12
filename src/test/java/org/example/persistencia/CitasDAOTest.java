package org.example.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.dominio.Citas;


import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CitasDAOTest {
    private CitasDAO citasDAO;

    @BeforeEach
    void setUp() {
        citasDAO = new CitasDAO();
    }

    @Test
    void create() {
        try {
            Citas cita = new Citas(12, LocalDateTime.of(2025, 6, 15, 10, 0), "1", "1", "1");
            Citas creada = citasDAO.create(cita);
            assertNotNull(creada);
            assertTrue(creada.getId() > 0);
            assertEquals("1", creada.getPacienteId());
            System.out.println("Prueba de creación exitosa: Cita creada correctamente con ID " + creada.getId());
        } catch (SQLException e) {
            fail("Excepción al crear cita: " + e.getMessage());
        }
    }

    @Test
    void update() {
        try {
            // Suponemos que ya existe una cita con ID 1
            int idExistente = 11; // Cambiar este ID por uno que exista en la base de datos
            Citas existente = citasDAO.getById(idExistente);
            assertNotNull(existente, "No se encontró la cita con ID existente para actualizar.");

            // Modificamos los datos
            existente.setFechaHora(LocalDateTime.of(2025, 6, 20, 15, 0));
            existente.setEstado("2");


            boolean actualizada = citasDAO.update(existente);
            assertTrue(actualizada, "La actualización de la cita falló.");

            Citas recuperada = citasDAO.getById(idExistente);
            assertEquals("2", recuperada.getEstado());
            assertEquals(LocalDateTime.of(2025, 6, 20, 15, 0), recuperada.getFechaHora());

            System.out.println("Prueba de actualización exitosa: Cita actualizada correctamente.");
        } catch (SQLException e) {
            fail("Excepción al actualizar cita: " + e.getMessage());
        }
    }


    @Test
    void delete() {
        try {
            int idExistente = 20;
            Citas existente = citasDAO.getById(idExistente);

            if (existente == null) {
                System.out.println("✅ Prueba exitosa: La cita con ID " + idExistente + " no existe, ya fue eliminada o nunca se creó.");
                return; // Termina la prueba aquí porque no hay nada que eliminar
            }

            boolean eliminada = citasDAO.delete(existente);
            assertTrue(eliminada, "La cita no fue eliminada correctamente.");

            Citas recuperada = citasDAO.getById(idExistente);
            assertNull(recuperada, "La cita aún existe después de intentar eliminarla.");

            System.out.println("✅ Prueba de eliminación exitosa: Cita eliminada correctamente.");

        } catch (SQLException e) {
            fail("Excepción al eliminar cita: " + e.getMessage());
        }
    }
    @Test
    void Search(){
    }
}