package org.example.persistencia;

import org.example.dominio.Citas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.example.dominio.Pago;


import java.sql.SQLException;
import java.time.LocalDateTime; // Para manejar fechas y horas

import static org.junit.jupiter.api.Assertions.*;

class PagoDAOTest {
    private PagoDAO pagoDAO; // Instancia del DAO para pagos
    private Citas citaDePrueba;

    @BeforeEach
    void setUp() {
        // Inicializa el PagoDAO antes de cada prueba
        pagoDAO = new PagoDAO();
    }

    @Disabled
    void create() {
        try {
            Pago pago = new Pago("12", 50.75f, LocalDateTime.of(2025, 7, 20, 14, 30));
            Pago creado = pagoDAO.create(pago);

            assertNotNull(creado);
            assertTrue(creado.getId() > 0);
            assertEquals("1", creado.getCitaId());
            assertEquals(50.75f, creado.getMonto(), 0.001f);
            assertEquals(LocalDateTime.of(2025, 7, 20, 14, 30).getSecond(), creado.getFechaPago().getSecond());

            System.out.println("✅ Prueba de creación exitosa: Pago creado correctamente con ID " + creado.getId());
        }catch (SQLException e) {
            fail("❌ Excepción al crear pago: " + e.getMessage());
        }
    }

    @Disabled
    void update() {
        System.out.println("--- Ejecutando prueba: update ---");
        Pago pagoParaActualizar = null;
        try {
            // PASO 1: Crear un pago para asegurar que tenemos uno para actualizar
            String citaIdOriginal = "12";
            float montoOriginal = 100.00f;
            LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 1, 12, 0);
            pagoParaActualizar = pagoDAO.create(new Pago(citaIdOriginal, montoOriginal, fechaOriginal));
            assertNotNull(pagoParaActualizar, "No se pudo crear un pago para la prueba de actualización.");
            System.out.println("  Pago inicial creado con ID: " + pagoParaActualizar.getId());

            // PASO 2: Modificar los datos del pago
            float nuevoMonto = 120.50f;
            LocalDateTime nuevaFechaPago = LocalDateTime.of(2025, 12, 25, 9, 30); // Nueva fecha

            pagoParaActualizar.setMonto(nuevoMonto);
            pagoParaActualizar.setFechaPago(nuevaFechaPago);

            // Intenta actualizar el pago
            boolean actualizada = pagoDAO.update(pagoParaActualizar);
            assertTrue(actualizada, "La actualización del pago debería ser exitosa.");
            System.out.println("  Actualización ejecutada.");

            // PASO 3: Recuperar el pago de la base de datos y verificar los cambios
            Pago pagoRecuperado = pagoDAO.getById(pagoParaActualizar.getId());
            assertNotNull(pagoRecuperado, "El pago no debería ser nulo después de la actualización.");
            assertEquals(nuevoMonto, pagoRecuperado.getMonto(), 0.001, "El monto actualizado no coincide.");
            assertEquals(nuevaFechaPago.withNano(0), pagoRecuperado.getFechaPago().withNano(0), "La fecha de pago actualizada no coincide."); // Ignorar nanosegundos

            System.out.println("✅ Prueba de actualización exitosa: Pago actualizado correctamente.");
        } catch (SQLException e) {
            fail("❌ Excepción al actualizar pago: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
        } finally {
            // Opcional: limpiar el pago creado para no dejar basura en la DB
            if (pagoParaActualizar != null && pagoParaActualizar.getId() > 0) {
                try {
                    pagoDAO.delete(pagoParaActualizar);
                    System.out.println("  Pago con ID " + pagoParaActualizar.getId() + " eliminado después de la prueba de actualización.");
                } catch (SQLException e) {
                    System.err.println("Advertencia: No se pudo eliminar el pago de limpieza con ID " + pagoParaActualizar.getId() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("--- Prueba 'update' finalizada ---\n");
    }

    @Disabled
    void delete() {
        System.out.println("--- Ejecutando prueba: delete ---");
        Pago pagoParaEliminar = null;
        try {
            // PASO 1: Crear un pago para asegurar que tenemos uno para eliminar
            String citaIdOriginal = "1";
            float montoOriginal = 75.20f;
            LocalDateTime fechaOriginal = LocalDateTime.now().minusDays(5); // 5 días atrás
            pagoParaEliminar = pagoDAO.create(new Pago(citaIdOriginal, montoOriginal, fechaOriginal));
            assertNotNull(pagoParaEliminar, "No se pudo crear un pago para la prueba de eliminación.");
            System.out.println("  Pago inicial creado con ID: " + pagoParaEliminar.getId());

            // PASO 2: Intentar eliminar el pago
            boolean eliminada = pagoDAO.delete(pagoParaEliminar);
            assertTrue(eliminada, "La eliminación del pago debería ser exitosa.");
            System.out.println("  Eliminación ejecutada.");

            // PASO 3: Intentar recuperar el pago y verificar que ya no existe
            Pago pagoRecuperadoDespuesDeEliminar = pagoDAO.getById(pagoParaEliminar.getId());
            assertNull(pagoRecuperadoDespuesDeEliminar, "El pago debería ser nulo después de haber sido eliminado.");

            System.out.println("✅ Prueba de eliminación exitosa: Pago eliminado correctamente.");

        } catch (SQLException e) {
            fail("❌ Excepción al eliminar pago: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
        } finally {
            // No necesitamos un bloque finally para eliminar aquí porque la prueba ya lo hizo.
            // Si la eliminación falló, la prueba ya habrá fallado.
        }
        System.out.println("--- Prueba 'delete' finalizada ---\n");
    }
}