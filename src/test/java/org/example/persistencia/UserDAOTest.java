package org.example.persistencia;

import org.example.dominio.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    static UserDAO userDAO;
    static User testUser;

    @BeforeAll
    static void setup() {
        userDAO = new UserDAO();

        // Crear usuario de prueba
        testUser = new User();
        testUser.setName("Usuario de Prueba");
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("1234");  // Se hasheará internamente
        testUser.setStatus((byte) 1);
    }

    @Test
    @Order(1)
    void testCreateUser() throws SQLException {
        User created = userDAO.createUser(testUser);
        assertNotNull(created);
        assertTrue(created.getId() > 0);
        testUser.setId(created.getId()); // guardar ID para futuras pruebas
    }

    @Test
    @Order(2)
    void testLogin() throws SQLException {
        String hashed = userDAO.hashPassword("1234");
        User loggedIn = userDAO.login("testuser@example.com", hashed);
        assertNotNull(loggedIn);
        assertEquals(testUser.getEmail(), loggedIn.getEmail());
    }

    @Test
    @Order(3)
    void testUpdateUser() throws SQLException {
        testUser.setName("Usuario Actualizado");
        testUser.setEmail("usuario_actualizado@example.com");
        boolean updated = userDAO.updateUser(testUser);
        assertTrue(updated);
    }

    @Test
    @Order(4)
    void testActualizarPassword() throws SQLException {
        testUser.setPasswordHash("nuevaClave123");
        boolean updated = userDAO.actualizarPassword(testUser);
        assertTrue(updated);

        // Verificar login con nueva clave
        String hashed = userDAO.hashPassword("nuevaClave123");
        User loggedIn = userDAO.login("usuario_actualizado@example.com", hashed);
        assertNotNull(loggedIn);
    }

    @Test
    @Order(5)
    void testDeleteUser() throws SQLException {
        boolean deleted = userDAO.deleteUser(testUser);
        assertTrue(deleted);
    }
}
