package org.example.persistencia;

import java.sql.PreparedStatement; // Clase para ejecutar consultas SQL preparadas, previniendo inyecciones SQL.
import java.sql.ResultSet;        // Interfaz para representar el resultado de una consulta SQL.
import java.sql.SQLException;     // Clase para manejar errores relacionados con la base de datos SQL.
import java.util.ArrayList;       // Clase para crear listas dinámicas de objetos.

import org.example.dominio.Doctor;

public class DoctorDAO {
    private ConnectionManager conn; // Objeto para gestionar la conexión con la base de datos.
    private PreparedStatement ps;   // Objeto para ejecutar consultas SQL preparadas.
    private ResultSet rs;           // Objeto para almacenar el resultado de una consulta SQL.
    public DoctorDAO(){
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param doctor El objeto Doctor que contiene la información del nuevo usuario a crear.
     * Se espera que el objeto Doctor tenga los campos 'name', 'passwordHash',
     * 'email' y 'status' correctamente establecidos. El campo 'id' será
     * generado automáticamente por la base de datos.
     * @return El objeto Doctor recién creado, incluyendo el ID generado por la base de datos,
     * o null si ocurre algún error durante la creación.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la creación del usuario.
     */
    public Doctor create(Doctor doctor) throws SQLException {
        Doctor res = null; // Variable para almacenar el usuario creado que se retornará.
        try{
            // Preparar la sentencia SQL para la inserción de un nuevo usuario.
            // Se especifica que se retornen las claves generadas automáticamente.
            PreparedStatement ps = conn.connect().prepareStatement(
                    "INSERT INTO " +
                            "Doctores (nombre, especialidad, experiencia, disponibilidad)" +
                            "VALUES (?, ?, ?, ? )",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            // Establecer los valores de los parámetros en la sentencia preparada.
            ps.setString(1, doctor.getNombre()); // Asignar el nombre del doctor.
            ps.setString(2, doctor.getEspecialidad()); // Asignar la direccion del doctor.
            ps.setFloat(3, doctor.getExperiencia()); // Asignar el nombre del usuario.
            ps.setByte(4, doctor.getDisponibilidad());   // Asignar el estado del usuario.

            // Ejecutar la sentencia de inserción y obtener el número de filas afectadas.
            int affectedRows = ps.executeUpdate();

            // Verificar si la inserción fue exitosa (al menos una fila afectada).
            if (affectedRows != 0) {
                // Obtener las claves generadas automáticamente por la base de datos (en este caso, el ID).
                ResultSet  generatedKeys = ps.getGeneratedKeys();
                // Mover el cursor al primer resultado (si existe).
                if (generatedKeys.next()) {
                    // Obtener el ID generado. Generalmente la primera columna contiene la clave primaria.
                    int idGenerado= generatedKeys.getInt(1);
                    // Recuperar el usuario completo utilizando el ID generado.
                    res = getById(idGenerado);
                } else {
                    // Lanzar una excepción si la creación del usuario falló y no se obtuvo un ID.
                    throw new SQLException("Creating Doctor failed, no ID obtained.");
                }
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al crear el Doctor: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return res; // Retornar el usuario creado (con su ID asignado) o null si hubo un error.
    }

    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param doctor El objeto Doctor que contiene la información actualizada del usuario.
     * Se requiere que el objeto Doctor tenga los campos 'id', 'name', 'email' y 'status'
     * correctamente establecidos para realizar la actualización.
     * @return true si la actualización del usuario fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la actualización del usuario.
     */
    public boolean update(Doctor doctor) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización fue exitosa.
        try{
            // Preparar la sentencia SQL para actualizar la información de un usuario.
            ps = conn.connect().prepareStatement(
                    "UPDATE Doctores " +
                            "SET nombre = ?, especialidad = ?, experiencia = ?, disponibilidad = ? " +
                            "WHERE id = ?"
            );

            // Establecer los valores de los parámetros en la sentencia preparada.
            ps.setString(1, doctor.getNombre()); // Asignar el nombre del doctor.
            ps.setString(2, doctor.getEspecialidad()); // Asignar la direccion del doctor.
            ps.setFloat(3, doctor.getExperiencia()); // Asignar el nombre del usuario.
            ps.setByte(4, doctor.getDisponibilidad());   // Asignar el estado del usuario.
            ps.setInt(5, doctor.getId());       // Establecer la condición WHERE para identificar el usuario a actualizar por su ID.


            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al modificar el doctor: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de actualización.
    }

    /**
     * Elimina un usuario de la base de datos basándose en su ID.
     *
     * @param doctor El objeto Doctor que contiene el ID del usuario a eliminar.
     * Se requiere que el objeto Doctor tenga el campo 'id' correctamente establecido.
     * @return true si la eliminación del usuario fue exitosa (al menos una fila afectada),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la eliminación del usuario.
     */
    public boolean delete(Doctor doctor) throws SQLException{
        boolean res = false; // Variable para indicar si la eliminación fue exitosa.
        try{
            // Preparar la sentencia SQL para eliminar un usuario por su ID.
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Doctores WHERE id = ?"
            );
            // Establecer el valor del parámetro en la sentencia preparada (el ID del usuario a eliminar).
            ps.setInt(1, doctor.getId());

            // Ejecutar la sentencia de eliminación y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la eliminación fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al eliminar el doctor: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de eliminación.
    }

    /**
     * Busca usuarios en la base de datos cuyo nombre contenga la cadena de búsqueda proporcionada.
     * La búsqueda se realiza de forma parcial, es decir, si el nombre del usuario contiene
     * la cadena de búsqueda (ignorando mayúsculas y minúsculas), será incluido en los resultados.
     *
     * @param nombre La cadena de texto a buscar dentro de los nombres de los usuarios.
     * @return Un ArrayList de objetos Doctor que coinciden con el criterio de búsqueda.
     * Retorna una lista vacía si no se encuentran usuarios con el nombre especificado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la búsqueda de usuarios.
     */
    public ArrayList<Doctor> search(String nombre) throws SQLException{
        ArrayList<Doctor> records  = new ArrayList<>(); // Lista para almacenar los usuarios encontrados.

        try {
            // Preparar la sentencia SQL para buscar usuarios por nombre (usando LIKE para búsqueda parcial).
            ps = conn.connect().prepareStatement("SELECT id, nombre, especialidad, experiencia, disponibilidad " +
                    "FROM Doctores " +
                    "WHERE nombre LIKE ?");

            // Establecer el valor del parámetro en la sentencia preparada.
            // El '%' al inicio y al final permiten la búsqueda de la cadena 'nombre' en cualquier parte del nombre del usuario.
            ps.setString(1, "%" + nombre + "%");

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Iterar a través de cada fila del resultado.
            while (rs.next()){
                // Crear un nuevo objeto Doctor para cada registro encontrado.
                Doctor doctor = new Doctor();
                // Asignar los valores de las columnas a los atributos del objeto doctor.
                doctor.setId(rs.getInt(1));       // Obtener el ID del usuario.
                doctor.setNombre(rs.getString(2));   // Obtener el nombre del usuario.
                doctor.setEspecialidad(rs.getString(3));  // Obtener el correo electrónico del usuario.
                doctor.setExperiencia(rs.getFloat(4));   // Obtener el nombre del usuario.
                doctor.setDisponibilidad(rs.getByte(5));    // Obtener el estado del usuario.
                // Agregar el objeto doctor a la lista de resultados.
                records.add(doctor);
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al buscar doctores: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return records; // Retornar la lista de usuarios encontrados.
    }

    /**
     * Obtiene un usuario de la base de datos basado en su ID.
     *
     * @param id El ID del usuario que se desea obtener.
     * @return Un objeto Doctor si se encuentra un usuario con el ID especificado,
     * null si no se encuentra ningún usuario con ese ID.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     * durante la obtención del usuario.
     */
    public Doctor getById(int id) throws SQLException{
        Doctor doctor  = new Doctor(); // Inicializar un objeto doctor que se retornará.

        try {
            // Preparar la sentencia SQL para seleccionar un usuario por su ID.
            ps = conn.connect().prepareStatement("SELECT id, nombre, especialidad, experiencia, disponibilidad " +
                    "FROM Doctores " +
                    "WHERE id = ?");

            // Establecer el valor del parámetro en la sentencia preparada (el ID a buscar).
            ps.setInt(1, id);

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Verificar si se encontró algún registro.
            if (rs.next()) {
                // Si se encontró un usuario, asignar los valores de las columnas al objeto doctor.
                doctor.setId(rs.getInt(1));       // Obtener el ID del usuario.
                doctor.setNombre(rs.getString(2));   // Obtener el nombre del usuario.
                doctor.setEspecialidad(rs.getString(3));  // Obtener el correo electrónico del usuario.
                doctor.setExperiencia(rs.getFloat(4));   // Obtener el nombre del usuario.
                doctor.setDisponibilidad(rs.getByte(5));    // Obtener el estado del usuario.

            } else {
                // Si no se encontró ningún usuario con el ID especificado, establecer el objeto doctor a null.
                doctor = null;
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al obtener un doctor por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return doctor; // Retornar el objeto doctor encontrado o null si no existe.
    }
}
