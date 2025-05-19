package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.models.Usuario;
import edu.upc.dsa.models.Partida;
import java.util.List;

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

    /**
     * Inicia una nueva partida para un usuario
     * @param nombreUsuario Nombre del usuario
     * @return El ID de la partida creada o -1 si falla
     */
    int iniciarPartida(String nombreUsuario);

    /**
     * Obtiene el mensaje de resultado de la última operación
     * @return Mensaje de resultado
     */
    String getMensajeResultado();
}