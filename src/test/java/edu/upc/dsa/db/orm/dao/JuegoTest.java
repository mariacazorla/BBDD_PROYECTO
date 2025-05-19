package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.db.DBUtils;
import edu.upc.dsa.models.Objeto;
import edu.upc.dsa.models.Partida;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Test simplificado que prueba el flujo principal del juego.
 */
public class JuegoTest {

    private static final Logger logger = Logger.getLogger(JuegoTest.class);

    private IUserDAO userDAO;
    private IPartidaDAO partidaDAO;
    private ITiendaDAO tiendaDAO;

    @Before
    public void setUp() {
        limpiarTablas();

        userDAO = new UserDAOImpl();
        partidaDAO = new PartidaDAOImpl();
        tiendaDAO = new TiendaDAOImpl();
    }

    @After
    public void tearDown() {
        limpiarTablas();
    }

    @Test
    public void testFlujoCompleto() {
        // ===================== TIENDA =====================
        logger.info("=== PRUEBAS TIENDA ===");

        // 1. Añadir 3 objetos a la tienda
        logger.info("Añadiendo objetos a la tienda...");
        tiendaDAO.añadirObjetoTienda("Espada", "ARMAS", 50);
        tiendaDAO.añadirObjetoTienda("Escudo", "ARMADURAS", 30);
        tiendaDAO.añadirObjetoTienda("Poción de vida", "POCIONES", 25);

        // 2. Intentar añadir un objeto que ya existe
        logger.info("Intentando añadir objeto duplicado...");
        boolean resultadoDuplicado = tiendaDAO.añadirObjetoTienda("Espada", "ARMAS", 60);
        Assert.assertFalse("Añadir objeto duplicado debería fallar", resultadoDuplicado);
        logger.info("Resultado: " + tiendaDAO.getMensajeResultado());

        // 3. Ver todos los objetos de la tienda
        logger.info("Listando objetos de la tienda:");
        List<Objeto> objetosTienda = tiendaDAO.listarObjetosTienda();
        for (Objeto obj : objetosTienda) {
            logger.info("- " + obj.getObjeto() + " (" + obj.getCategoria() + "): " + obj.getPrecio() + " monedas");
        }

        // ===================== USUARIO =====================
        logger.info("\n=== PRUEBAS USUARIO ===");

        // 4. Registrar a María
        logger.info("Registrando usuario María...");
        boolean registroMaria = userDAO.registrarUsuario("Maria", "password123");
        Assert.assertTrue("El registro de María debería ser exitoso", registroMaria);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // 5. Intentar registrar a María de nuevo
        logger.info("Intentando registrar a María de nuevo...");
        boolean registroDuplicado = userDAO.registrarUsuario("Maria", "otrapassword");
        Assert.assertFalse("El segundo registro de María debería fallar", registroDuplicado);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // 6. Intentar login con Miguel (no existe)
        logger.info("Intentando login con Miguel (no existe)...");
        boolean loginMiguel = userDAO.loginUsuario("Miguel", "cualquiera");
        Assert.assertFalse("El login de Miguel debería fallar", loginMiguel);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // 7. Login de María
        logger.info("Haciendo login con María...");
        boolean loginMaria = userDAO.loginUsuario("Maria", "password123");
        Assert.assertTrue("El login de María debería ser exitoso", loginMaria);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // ===================== PARTIDA =====================
        logger.info("\n=== PRUEBAS PARTIDA ===");

        // 8. Crear partida para María
        logger.info("Creando partida para María...");
        int idPartida = userDAO.iniciarPartida("Maria");
        Assert.assertTrue("El ID de partida debería ser positivo", idPartida > 0);
        logger.info("Partida creada con ID: " + idPartida);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // 9. Ver datos de la partida de María
        logger.info("Consultando datos de la partida de María...");
        Partida partida = partidaDAO.obtenerPartida(idPartida);
        logger.info("Datos de partida: ");
        logger.info("- Usuario: " + partida.getId_usuario());
        logger.info("- Vidas: " + partida.getVidas());
        logger.info("- Monedas: " + partida.getMonedas());
        logger.info("- Puntuación: " + partida.getPuntuacion());

        // 10. Comprar un objeto
        logger.info("\nComprando un objeto (Poción de vida)...");
        boolean compraExitosa = partidaDAO.comprarObjeto(idPartida, "Poción de vida");
        Assert.assertTrue("La compra debería ser exitosa", compraExitosa);
        logger.info("Resultado: " + partidaDAO.getMensajeResultado());

        // 11. Ver inventario tras compra
        logger.info("Inventario tras compra:");
        List<Objeto> inventarioTrasCompra = partidaDAO.verInventario(idPartida);
        for (Objeto obj : inventarioTrasCompra) {
            logger.info("- " + obj.getObjeto() + ": " + obj.getCantidad() + " unidad(es)");
        }

        // 12. Ver monedas restantes
        partida = partidaDAO.obtenerPartida(idPartida);
        logger.info("Monedas restantes: " + partida.getMonedas());

        // 13. Usar el objeto
        logger.info("\nUsando la Poción de vida...");
        boolean usoExitoso = partidaDAO.usarObjeto(idPartida, "Poción de vida");
        Assert.assertTrue("El uso del objeto debería ser exitoso", usoExitoso);
        logger.info("Resultado: " + partidaDAO.getMensajeResultado());

        // 14. Ver inventario tras usar objeto
        logger.info("Inventario tras usar objeto:");
        List<Objeto> inventarioFinal = partidaDAO.verInventario(idPartida);
        if (inventarioFinal.isEmpty()) {
            logger.info("El inventario está vacío");
        } else {
            for (Objeto obj : inventarioFinal) {
                logger.info("- " + obj.getObjeto() + ": " + obj.getCantidad() + " unidad(es)");
            }
        }

        // 15. Ver vidas tras usar poción
        partida = partidaDAO.obtenerPartida(idPartida);
        logger.info("Vidas tras usar poción: " + partida.getVidas());

        // ===================== MÚLTIPLES PARTIDAS =====================
        logger.info("\n=== PRUEBAS MÚLTIPLES PARTIDAS ===");

        // 16. Crear una segunda partida para María
        logger.info("Creando una segunda partida para María...");
        int idPartida2 = userDAO.iniciarPartida("Maria");
        Assert.assertTrue("El ID de la segunda partida debería ser positivo", idPartida2 > 0);
        logger.info("Segunda partida creada con ID: " + idPartida2);
        logger.info("Resultado: " + userDAO.getMensajeResultado());

        // 17. Ver todas las partidas de María
        logger.info("\nListando todas las partidas de María:");
        List<Partida> partidasMaria = partidaDAO.obtenerPartidasUsuario("Maria");
        Assert.assertEquals("María debería tener 2 partidas", 2, partidasMaria.size());

        for (Partida p : partidasMaria) {
            logger.info("Partida ID: " + p.getId_partida());
            logger.info("- Vidas: " + p.getVidas());
            logger.info("- Monedas: " + p.getMonedas());
            logger.info("- Puntuación: " + p.getPuntuacion());

            List<Objeto> inv = p.getInventario();
            if (inv != null && !inv.isEmpty()) {
                logger.info("- Inventario:");
                for (Objeto o : inv) {
                    logger.info("  • " + o.getObjeto() + ": " + o.getCantidad() + " unidad(es)");
                }
            } else {
                logger.info("- Inventario: vacío");
            }
            logger.info("-----------------------");
        }

        logger.info("\n¡Prueba de flujo completo finalizada con éxito!");
    }

    private void limpiarTablas() {
        Connection conn = null;

        try {
            conn = DBUtils.getConnection();

            // Desactivar restricciones de clave foránea
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");

            // Limpiar todas las tablas relacionadas
            conn.createStatement().executeUpdate("DELETE FROM Partida_Inventario");
            conn.createStatement().executeUpdate("DELETE FROM Partida");
            conn.createStatement().executeUpdate("DELETE FROM Tienda");
            conn.createStatement().executeUpdate("DELETE FROM Usuario");

            // Reactivar restricciones
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1");

        } catch (SQLException e) {
            logger.error("Error al limpiar tablas: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}