package org.example.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Necesario para manejar LocalDateTime en la base de datos
import java.util.ArrayList;

import org.example.dominio.Pago; // Importar la nueva clase de dominio Pago



public class PagoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public PagoDAO(){
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo registro de pago en la base de datos.
     *
     * @param pago El objeto Pago que contiene la información del nuevo pago a crear.
     * Se espera que tenga 'citaId', 'monto' y 'fechaPago' establecidos.
     * El campo 'id' será generado automáticamente por la base de datos.
     * @return El objeto Pago recién creado, incluyendo el ID generado por la base de datos,
     * o null si ocurre algún error durante la creación.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la creación del pago.
     */
    public Pago create(Pago pago) throws SQLException {
        Pago res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Pagos (citaId, monto, fechaPago) VALUES (?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, pago.getCitaId());
            ps.setFloat(2, pago.getMonto());
            // Convertir LocalDateTime a Timestamp para la base de datos
            ps.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado); // Recuperar el pago completo usando el ID generado
                } else {
                    throw new SQLException("Creating pago failed, no ID obtained.");
                }
            }
            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el pago: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }

    /**
     * Actualiza la información de un pago existente en la base de datos.
     *
     * @param pago El objeto Pago que contiene la información actualizada del pago.
     * Se requiere que el objeto Pago tenga los campos 'id', 'monto' y 'fechaPago'
     * correctamente establecidos para realizar la actualización.
     * @return true si la actualización del pago fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la actualización del pago.
     */
    public boolean update(Pago pago) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Pagos " +
                            "SET monto = ?, fechaPago = ? " +
                            "WHERE id = ?"
            );

            ps.setFloat(1, pago.getMonto());
            // Convertir LocalDateTime a Timestamp para la base de datos
            ps.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            ps.setInt(3, pago.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al modificar el pago: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }

    /**
     * Elimina un pago de la base de datos basándose en su ID.
     *
     * @param pago El objeto Pago que contiene el ID del pago a eliminar.
     * Se requiere que el objeto Pago tenga el campo 'id' correctamente establecido.
     * @return true si la eliminación del pago fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la eliminación del pago.
     */
    public boolean delete(Pago pago) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Pagos WHERE id = ?"
            );
            ps.setInt(1, pago.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el pago: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }

    /**
     * Busca pagos en la base de datos asociados a un ID de cita específico.
     *
     * @param citaId El ID de la cita para la cual se desean buscar los pagos.
     * @return Un ArrayList de objetos Pago que coinciden con el criterio de búsqueda.
     * Retorna una lista vacía si no se encuentran pagos para la cita especificada.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la búsqueda de pagos.
     */
    public ArrayList<Pago> search(String citaId) throws SQLException {
        ArrayList<Pago> records = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id, citaId, monto, fechaPago FROM Pagos WHERE citaId LIKE ?"
            );
            ps.setString(1, "%" + citaId + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago(
                );
               records.add(pago);
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar pagos por ID de cita: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            rs = null;
            conn.disconnect();
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
        try {
            ps = conn.connect().prepareStatement("SELECT id, citaId, monto, fechaPago FROM Pagos WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pago = new Pago();
                pago.setId(rs.getInt("id"));
                pago.setCitaId(rs.getString("citaId"));
                pago.setMonto(rs.getFloat("monto"));
                // Convertir Timestamp de la DB a LocalDateTime
                pago.setFechaPago(rs.getTimestamp("fechaPago").toLocalDateTime());
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener un pago por id: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            rs = null;
            conn.disconnect();
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
        try {
            ps = conn.connect().prepareStatement("SELECT id, citaId, monto, fechaPago FROM Pagos");
            rs = ps.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago();
                pago.setId(rs.getInt("id"));
                pago.setCitaId(rs.getString("citaId"));
                pago.setMonto(rs.getFloat("monto"));
                pago.setFechaPago(rs.getTimestamp("fechaPago").toLocalDateTime());
                records.add(pago);
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los pagos: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }
        return records;
    }
}
