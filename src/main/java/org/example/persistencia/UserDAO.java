package org.example.persistencia;

import org.example.dominio.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.example.dominio.User;

import java.sql.*;

/**
 * Clase DAO para la entidad User.
 * Se encarga de las operaciones CRUD relacionadas con la tabla Users.
 */
public class UserDAO {

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param user El objeto User que contiene la información del nuevo usuario a crear.
     *             Se espera que tenga 'name', 'passwordHash', 'email' y 'status' definidos.
     * @return El objeto User actualizado con el ID generado, o null si ocurre un error.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, passwordHash, email, status) VALUES (?, ?, ?, ?)";

        String hashedPassword = hashPassword(user.getPasswordHash());

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());
            stmt.setByte(4, user.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear el usuario, no se insertó ninguna fila.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    user.setPasswordHash(hashedPassword); // Actualizar con contraseña hasheada
                } else {
                    throw new SQLException("No se pudo obtener el ID del usuario creado.");
                }
            }

            return user;

        } catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            return null;
        }
    }


    /**
     * Verifica si un usuario puede iniciar sesión con el correo y contraseña proporcionados.
     *
     * @param email        El correo electrónico del usuario.
     * @param passwordHash El hash de la contraseña.
     * @return El objeto User si las credenciales son válidas y el usuario está activo, o null si no.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public User login(String email, String passwordHash) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ? AND passwordHash = ? AND status = 1";

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("passwordHash"),
                            rs.getByte("status")
                    );
                } else {
                    return null; // Usuario no encontrado o inactivo
                }
            }

        } catch (SQLException e) {
            System.err.println("Error durante el login: " + e.getMessage());
            return null;
        }
    }
    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param user El objeto User que contiene la información actualizada del usuario.
     * Se requiere que el objeto User tenga los campos 'id', 'name', 'email' y 'status'
     * correctamente establecidos para realizar la actualización.
     * @return true si la actualización del usuario fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la actualización del usuario.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET name = ?, email = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setByte(3, user.getStatus());
            stmt.setInt(4, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            throw e;
        }
    }
    /**
     * Elimina un usuario de la base de datos basándose en su ID.
     *
     * @param user El objeto User que contiene el ID del usuario a eliminar.
     * Se requiere que el objeto User tenga el campo 'id' correctamente establecido.
     * @return true si la eliminación del usuario fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la eliminación del usuario.
     */
    public boolean deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
            throw e;
        }
    }
    /**
     * Busca usuarios en la base de datos cuyo nombre contenga la cadena de búsqueda proporcionada.
     * La búsqueda se realiza de forma parcial, es decir, si el nombre del usuario contiene
     * la cadena de búsqueda (ignorando mayúsculas y minúsculas), será incluido en los resultados.
     *
     * @param name La cadena de texto a buscar dentro de los nombres de los usuarios.
     * @return Un ArrayList de objetos User que coinciden con el criterio de búsqueda.
     * Retorna una lista vacía si no se encuentran usuarios con el nombre especificado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la búsqueda de usuarios.
     */
    public ArrayList<User> searchUsersByName(String name) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE LOWER(name) LIKE LOWER(?)";

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("passwordHash"),
                            rs.getByte("status")
                    );
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por nombre: " + e.getMessage());
            throw e;
        }

        return users;
    }
    /**
     * Actualiza la contraseña de un usuario existente en la base de datos.
     * La nueva contraseña proporcionada se hashea antes de ser almacenada.
     *
     * @param user El objeto User que contiene el ID del usuario cuya contraseña se
     * actualizará y la nueva contraseña (sin hashear) en el campo 'passwordHash'.
     * Se requiere que los campos 'id' y 'passwordHash' del objeto User estén
     * correctamente establecidos.
     * @return true si la actualización de la contraseña fue exitosa (al menos una
     * fila afectada), false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la actualización de la contraseña.
     */
    public boolean actualizarPassword(User user) throws SQLException {
        String sql = "UPDATE Users SET passwordHash = ? WHERE id = ?";
        String hashedPassword = hashPassword(user.getPasswordHash());

        try (Connection conn = ConnectionManager.getInstance().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Método auxiliar para generar el hash SHA-256 de una cadena.
     *
     * @param password La contraseña sin hashear.
     * @return El hash SHA-256 en formato hexadecimal.
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }
}


