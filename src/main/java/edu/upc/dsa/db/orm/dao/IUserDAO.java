package edu.upc.dsa.db.orm.dao;

/**
 * Interfaz DAO que declara las operaciones de registro y login de usuarios.
 */
public interface IUserDAO {

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param usuario  Nombre de usuario (clave primaria).
     * @param password Contraseña en texto plano (en producción debería estar hasheada).
     * @return true si el registro fue exitoso, false si el usuario ya existe
     */
    boolean registrarUsuario(String usuario, String password);

    /**
     * Verifica las credenciales de un usuario para hacer login.
     *
     * @param nombre_usuario Nombre de usuario.
     * @param contraseña     Contraseña a verificar.
     * @return true si las credenciales coinciden con un registro en la base de datos; false en caso contrario.
     */
    boolean loginUsuario(String nombre_usuario, String contraseña);
}