package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.db.orm.FactorySession;
import edu.upc.dsa.db.orm.Session;
import edu.upc.dsa.models.Usuario;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class UserDAOImpl implements IUserDAO {
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class);
    private String mensajeResultado;

    public String getMensajeResultado() {
        return mensajeResultado;
    }

    @Override
    public boolean registrarUsuario(String usuario, String password) {
        Session session = null;
        try {
            session = FactorySession.openSession();

            // Verificar si el usuario ya existe
            HashMap<String, Object> params = new HashMap<>();
            params.put("nombre_usuario", usuario);

            List<Object> result = session.findAll(Usuario.class, params);
            if (!result.isEmpty()) {
                mensajeResultado = "¡Vaya! El nombre de usuario '" + usuario + "' ya está en uso. Por favor, prueba con otro.";
                logger.warn("Intento de registro con nombre de usuario existente: " + usuario);
                return false;
            }

            // Si no existe, proceder con el registro
            Usuario user = new Usuario();
            user.setNombre_usuario(usuario);
            user.setContraseña(password);

            session.save(user);
            mensajeResultado = "¡Bienvenido, '" + usuario + "'! Tu cuenta se ha creado correctamente.";
            logger.info("Usuario registrado: " + usuario);

            return true;

        } catch (Exception e) {
            mensajeResultado = "Ha ocurrido un error durante el registro. Por favor, inténtalo de nuevo más tarde.";
            logger.error("Error al registrar usuario " + usuario, e);
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean loginUsuario(String nombre_usuario, String contraseña) {
        Session session = null;
        boolean ok = false;

        try {
            session = FactorySession.openSession();

            // Primero verificamos si el usuario existe
            HashMap<String, Object> userParams = new HashMap<>();
            userParams.put("nombre_usuario", nombre_usuario);

            List<Object> userResult = session.findAll(Usuario.class, userParams);
            if (userResult.isEmpty()) {
                mensajeResultado = "El usuario '" + nombre_usuario + "' no existe. ¿Quieres registrarte?";
                logger.warn("Intento de login con usuario inexistente: " + nombre_usuario);
                return false;
            }

            // Si el usuario existe, verificamos la contraseña
            HashMap<String, Object> loginParams = new HashMap<>();
            loginParams.put("nombre_usuario", nombre_usuario);
            loginParams.put("contraseña", contraseña);

            List<Object> result = session.findAll(Usuario.class, loginParams);
            ok = !result.isEmpty();

            if (ok) {
                mensajeResultado = "¡Hola de nuevo, " + nombre_usuario + "! Has iniciado sesión correctamente.";
                logger.info("Login exitoso para '" + nombre_usuario + "'");
            } else {
                mensajeResultado = "La contraseña introducida no es correcta. Por favor, inténtalo de nuevo.";
                logger.warn("Login fallido por contraseña incorrecta para '" + nombre_usuario + "'");
            }

        } catch (Exception e) {
            mensajeResultado = "Ha ocurrido un error al iniciar sesión. Por favor, inténtalo de nuevo más tarde.";
            logger.error("Error en loginUsuario para " + nombre_usuario, e);
        } finally {
            if (session != null) session.close();
        }

        return ok;
    }
}