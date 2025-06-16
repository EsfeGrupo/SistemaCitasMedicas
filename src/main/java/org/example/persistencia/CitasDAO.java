package org.example.persistencia;

import org.example.dominio.Citas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Necesario para manejar LocalDateTime en la base de datos
import java.time.LocalDateTime; // Usaremos LocalDateTime para la fecha de pago
import java.sql.*;
import java.util.ArrayList;


public class CitasDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public CitasDAO() {
        conn = ConnectionManager.getInstance();
    }

    /*public Citas create(Citas cita) throws SQLException {
        Citas res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Citas (pacienteId, doctorId, fechaHora, estado) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, cita.getPacienteId());
            ps.setString(2, cita.getDoctorId());
            ps.setString(3, cita.getFechaHora());
            ps.setString(4, cita.getEstado());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating Cita failed, no ID obtained.");
                }
            }

            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al crear la cita: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }*/

    public Citas create(Citas cita) throws SQLException {
        Citas res = null;
        String sql = "INSERT INTO Citas (pacienteId, doctorId, fechaHora, estado) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = conn.connect();

            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cita.getPacienteId());
                ps.setString(2, cita.getDoctorId());
                ps.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
                ps.setString(4, cita.getEstado());

                int affectedRows = ps.executeUpdate();

                if (affectedRows != 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idGenerado = generatedKeys.getInt(1);
                            res = getById(idGenerado);
                        } else {
                            throw new SQLException("Creating Cita failed, no ID obtained.");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear la cita: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }

        return res;
    }


    public boolean update(Citas cita) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Citas SET fechaHora = ?, pacienteId = ?, doctorId = ?, estado = ? WHERE id = ?"
            );
            ps.setTimestamp(1, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(2, cita.getPacienteId());
            ps.setString(3, cita.getDoctorId());
            ps.setString(4, cita.getEstado());
            ps.setInt(5, cita.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }

            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al modificar la cita: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }

        return res;
    }

    public boolean delete(Citas cita) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Citas WHERE id = ?"
            );
            ps.setInt(1, cita.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }

            ps.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar la cita: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            conn.disconnect();
        }

        return res;
    }

    public ArrayList<Citas> search(String nombrePaciente) throws SQLException {
        ArrayList<Citas> records = new ArrayList<>();

        try {
            ps = conn.connect().prepareStatement(
                    "SELECT C.id, C.fechaHora, C.pacienteId, C.doctorId, C.estado, P.nombre AS nombrePaciente, D.nombre AS nombreDoctor " +
                            "FROM Citas C " +
                            "JOIN Pacientes P ON C.pacienteId = P.id " +
                            "JOIN Doctores D ON C.doctorId = D.id " +
                            "WHERE P.nombre LIKE ?"
            );
            ps.setString(1, "%" + nombrePaciente + "%");

            rs = ps.executeQuery();

            while (rs.next()) {
                int estadoNum = Integer.parseInt(rs.getString("estado")); // asumo que es un número almacenado como texto o ajusta si es int

                String estadoStr = switch (estadoNum) {
                    case 1 -> "Pendiente";
                    case 2 -> "Confirmada";
                    case 3 -> "Cancelada";
                    default -> "Desconocido";
                };

                Citas cita = new Citas(
                        rs.getInt("id"),
                        rs.getTimestamp("fechaHora").toLocalDateTime(),
                        rs.getString("nombrePaciente"),
                        rs.getString("nombreDoctor"),
                        estadoStr,
                        rs.getString("pacienteId"),
                        rs.getString("doctorId")
                );
                records.add(cita);
            }

            ps.close();
            rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar citas: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }

        return records;
    }

    public Citas getById(int id) throws SQLException {
        Citas cita = null;

        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id, fechaHora, pacienteId, doctorId, estado FROM Citas WHERE id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                cita = new Citas(
                        rs.getInt("id"),
                        rs.getTimestamp("fechaHora").toLocalDateTime(),
                        rs.getString("pacienteId"),
                        rs.getString("doctorId"),
                        rs.getString("estado")
                );
            }

            ps.close();
            rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener la cita por ID: " + ex.getMessage(), ex);
        } finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }

        return cita;
    }
}
