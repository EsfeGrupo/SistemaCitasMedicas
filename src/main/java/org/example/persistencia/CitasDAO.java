package org.example.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CitasDAO {
    private Connection connection;

    public CitasDAO(Connection connection) {
        this.connection = connection;
    }

    public void createCita(String details) throws SQLException {
        String sql = "INSERT INTO citas (details) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, details);
            pstmt.executeUpdate();
        }
    }

    public ResultSet readCitas() throws SQLException {
        String sql = "SELECT * FROM citas";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public void updateCita(int id, String newDetails) throws SQLException {
        String sql = "UPDATE citas SET details = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newDetails);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteCita(int id) throws SQLException {
        String sql = "DELETE FROM citas WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
