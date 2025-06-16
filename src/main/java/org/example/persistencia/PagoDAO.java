package org.example.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.example.dominio.Pago;

public class PagoDAO {
    private ConnectionManager connManager; // Renombrado 'conn' a 'connManager' para mayor claridad

    public PagoDAO(){
        connManager = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo registro de pago en la base de datos.
     *
     * @param pago El objeto Pago que contiene la información del nuevo pago a crear.
     * @return El objeto Pago recién creado, incluyendo el ID generado por la base de datos,
     * o null si ocurre algún error durante la creación.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la creación del pago.
     */
    public Pago create(Pago pago) throws SQLException {
        String sql = "INSERT INTO Pagos (citaId, monto, fechaPago) VALUES (?, ?, ?)";
        Pago createdPago = null;

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect(); // Obtener la conexión dentro del try-with-resources
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pago.getCitaId());
            ps.setFloat(2, pago.getMonto());
            ps.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) { // try-with-resources anidado para ResultSet
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        pago.setId(idGenerado); // Establece el ID generado en el objeto original
                        createdPago = pago; // Asigna el objeto Pago actualizado

                        // Puedes mantener esto si quieres obtener explícitamente el objeto de la DB
                        // después de la inserción, pero a menudo es redundante si solo estableces el ID.
                        // createdPago = getById(idGenerado);
                    } else {
                        throw new SQLException("La creación del pago falló, no se obtuvo ID.");
                    }
                } // generatedKeys se cierra automáticamente aquí
            } else {
                System.out.println("No se afectaron filas, el pago no fue creado.");
            }

        } catch (SQLException ex) {
            System.err.println("Error al crear el pago: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        // No se necesita un bloque finally manual para cerrar recursos
        return createdPago;
    }

    /**
     * Actualiza la información de un pago existente en la base de datos.
     *
     * @param pago El objeto Pago que contiene la información actualizada del pago.
     * @return true si la actualización del pago fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la actualización del pago.
     */
    public boolean update(Pago pago) throws SQLException {
        // Asumiendo que citaId también es actualizable
        String sql = "UPDATE Pagos SET citaId = ?, monto = ?, fechaPago = ? WHERE id = ?";
        boolean res = false;

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, pago.getCitaId());
            ps.setFloat(2, pago.getMonto());
            ps.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));
            ps.setInt(4, pago.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }

        } catch (SQLException ex) {
            System.err.println("Error al modificar el pago: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return res;
    }

    /**
     * Elimina un pago de la base de datos basándose en su ID.
     *
     * @param pago El objeto Pago que contiene el ID del pago a eliminar.
     * @return true si la eliminación del pago fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la eliminación del pago.
     */
    public boolean delete(Pago pago) throws SQLException {
        String sql = "DELETE FROM Pagos WHERE id = ?";
        boolean res = false;

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, pago.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }

        } catch (SQLException ex) {
            System.err.println("Error al eliminar el pago: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return res;
    }

    /**
     * Busca pagos en la base de datos asociados a un ID de cita específico.
     *
     * @param filtroNombrePaciente El ID de la cita para la cual se desean buscar los pagos.
     * @return Un ArrayList de objetos Pago que coinciden con el criterio de búsqueda.
     * Retorna una lista vacía si no se encuentran pagos para la cita especificada.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la búsqueda de pagos.
     */
    public ArrayList<Pago> search(String filtroNombrePaciente) throws SQLException {
        ArrayList<Pago> records = new ArrayList<>();
        // Revertido a buscar por citaId, según el dominio de Pago.
        // Si necesitas buscar por nombre de paciente, necesitarás un JOIN con Citas y Pacientes.
        String sql = "SELECT P.id, P.citaId, P.monto, P.fechaPago, Pa.nombre AS nombrePaciente " +
                "FROM Pagos P " +
                "JOIN Citas C ON P.citaId = C.id " +
                "JOIN Pacientes Pa ON C.pacienteId = Pa.id " +
                "WHERE Pa.nombre LIKE ?";

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + filtroNombrePaciente + "%"); // Usar el filtro para el nombre


            try (ResultSet rs = ps.executeQuery()) { // try-with-resources anidado para ResultSet
                while (rs.next()) {
                    Pago pago = new Pago();
                    pago.setId(rs.getInt("id"));
                    pago.setCitaId(rs.getString("citaId"));
                    pago.setMonto(rs.getFloat("monto"));
                    pago.setFechaPago(rs.getTimestamp("fechaPago").toLocalDateTime());
                    pago.setNombrePaciente(rs.getString("nombrePaciente")); // <--- ¡OBTENER EL NUEVO CAMPO!
                    records.add(pago);
                }
            } // rs se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al buscar pagos por ID de cita: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return records;
    }

    /**
     * Obtiene un pago de la base de datos basado en su ID.
     *
     * @param id El ID del pago que se desea obtener.
     * @return Un objeto Pago si se encuentra un pago con el ID especificado,
     * null si no se encuentra ningún pago con ese ID.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la obtención del pago.
     */
    public Pago getById(int id) throws SQLException {
        Pago pago = null;
        String sql = "SELECT id, citaId, monto, fechaPago FROM Pagos WHERE id = ?";

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) { // try-with-resources anidado para ResultSet
                if (rs.next()) {
                    pago = new Pago();
                    pago.setId(rs.getInt("id"));
                    pago.setCitaId(rs.getString("citaId"));
                    pago.setMonto(rs.getFloat("monto"));
                    pago.setFechaPago(rs.getTimestamp("fechaPago").toLocalDateTime());
                }
            } // rs se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al obtener un pago por id: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return pago;
    }

    /**
     * Obtiene todos los pagos de la base de datos.
     *
     * @return Un ArrayList de objetos Pago. Retorna una lista vacía si no hay pagos.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public ArrayList<Pago> getAll() throws SQLException {
        ArrayList<Pago> records = new ArrayList<>();
        String sql = "SELECT P.id, P.citaId, P.monto, P.fechaPago, Pa.nombre AS nombrePaciente " +
                "FROM Pagos P " +
                "JOIN Citas C ON P.citaId = C.id " + // Asumiendo que citaId en Pagos es FOREIGN KEY a Citas.id
                "JOIN Pacientes Pa ON C.pacienteId = Pa.id";

        // --- USANDO TRY-WITH-RESOURCES ---
        try (Connection connection = connManager.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) { // try-with-resources anidado para ResultSet
                while (rs.next()) {
                    Pago pago = new Pago();
                    pago.setId(rs.getInt("id"));
                    pago.setCitaId(rs.getString("citaId"));
                    pago.setMonto(rs.getFloat("monto"));
                    pago.setFechaPago(rs.getTimestamp("fechaPago").toLocalDateTime());
                    pago.setNombrePaciente(rs.getString("nombrePaciente")); // <--- ¡OBTENER EL NUEVO CAMPO!
                    records.add(pago);
                }
            } // rs se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al obtener todos los pagos: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return records;
    }
}