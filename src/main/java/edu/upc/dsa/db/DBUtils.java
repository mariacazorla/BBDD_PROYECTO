package edu.upc.dsa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidades de configuración de acceso a la base de datos.
 * Proporciona los getters que espera FactorySession.
 */
public class DBUtils {

    // Nombre de la base de datos
    private static final String DB   = "juego_db";
    // Host donde corre MariaDB/MySQL
    private static final String HOST = "localhost";
    // Puerto por defecto de MariaDB
    private static final String PORT = "3306";
    // Usuario de la base de datos
    private static final String USER = "root";
    // Contraseña del usuario
    private static final String PASS = "Carretera";

    // Cargamos el driver al iniciar la clase
    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontró el driver de MariaDB: " + e.getMessage());
        }
    }

    /** Nombre de la base de datos */
    public static String getDb() {
        return DB;
    }

    /** Host de la base de datos */
    public static String getDbHost() {
        return HOST;
    }

    /** Puerto de la base de datos */
    public static String getDbPort() {
        return PORT;
    }

    /** Usuario para la conexión */
    public static String getDbUser() {
        return USER;
    }

    /** Contraseña para la conexión */
    public static String getDbPasswd() {
        return PASS;
    }

    /**
     * (Opcional) Método adicional para obtener directamente una Connection
     * sin pasar por FactorySession.
     */
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DB;
        return DriverManager.getConnection(url, USER, PASS);
    }
}

