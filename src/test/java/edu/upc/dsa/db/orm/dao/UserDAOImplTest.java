package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.db.DBUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAOImplTest {

    private IUserDAO userDAO;

    @Before
    public void setUp() {
        // Inicializar la base de datos si es necesario
        limpiarTabla();

        // Inicializar el DAO
        userDAO = new UserDAOImpl();
    }

    @After
    public void tearDown() {
        // Limpiar tabla después de cada test
        limpiarTabla();
    }

    @Test
    public void testRegistroYLogin() {
        // Registrar un usuario
        userDAO.registrarUsuario("jordi", "pass123");

        // Verificar que el login funciona con esas credenciales
        boolean loginResult = userDAO.loginUsuario("jordi", "pass123");
        Assert.assertTrue(loginResult);
    }

    @Test
    public void testLoginSinRegistro() {
        // Intentar login con un usuario que no existe
        boolean loginResult = userDAO.loginUsuario("miguel", "clave123");
        Assert.assertFalse(loginResult);
    }

    @Test
    public void testRegistroUsuarioDuplicado() {
        // Registrar un usuario
        String nombreUsuario = "maria";
        String password1 = "1234";
        boolean primerRegistro = userDAO.registrarUsuario(nombreUsuario, password1);

        // Intentar registrar otro usuario con el mismo nombre pero distinta contraseña
        String password2 = "5678";
        boolean segundoRegistro = userDAO.registrarUsuario(nombreUsuario, password2);

        // Verificar que el primer registro fue exitoso y el segundo falló
        Assert.assertTrue(primerRegistro);
        Assert.assertFalse(segundoRegistro);

        // Verificar que se puede acceder con las credenciales del primer registro
        boolean loginPrimerRegistro = userDAO.loginUsuario(nombreUsuario, password1);
        Assert.assertTrue(loginPrimerRegistro);

        // Verificar que NO se puede acceder con el segundo password
        boolean loginSegundoRegistro = userDAO.loginUsuario(nombreUsuario, password2);
        Assert.assertFalse(loginSegundoRegistro);
    }

    private void limpiarTabla() {
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM Usuario")) {
            pst.executeUpdate();
        } catch (SQLException e) {
            // Es normal que falle si la tabla no existe aún
            System.out.println("Aviso: No se pudo limpiar la tabla Usuario. Esto es normal si es la primera ejecución.");
        }
    } 
}