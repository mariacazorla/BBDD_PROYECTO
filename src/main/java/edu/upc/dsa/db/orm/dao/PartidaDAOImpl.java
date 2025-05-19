package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.db.DBUtils;
import edu.upc.dsa.models.Partida;
import edu.upc.dsa.models.Objeto;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación corregida de la interfaz PartidaDAO.
 */
public class PartidaDAOImpl implements IPartidaDAO {

    private static final Logger logger = Logger.getLogger(PartidaDAOImpl.class);
    private String mensajeResultado;

    public String getMensajeResultado() {
        return mensajeResultado;
    }

    @Override
    public int crearPartida(String id_usuario, int vidas, int monedas, int puntuacion) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int idPartida = -1;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar la partida
            String sql = "INSERT INTO Partida (id_usuario, vidas, monedas, puntuacion) VALUES (?, ?, ?, ?)";
            pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, id_usuario);
            pstm.setInt(2, vidas);
            pstm.setInt(3, monedas);
            pstm.setInt(4, puntuacion);

            int filasAfectadas = pstm.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    idPartida = rs.getInt(1);
                    conn.commit(); // Confirmar transacción

                    mensajeResultado = "¡Partida creada correctamente para " + id_usuario + "!";
                    logger.info("Partida creada con ID: " + idPartida);
                } else {
                    conn.rollback(); // Revertir en caso de error
                    mensajeResultado = "No se pudo obtener el ID de la partida creada";
                    logger.error(mensajeResultado);
                }
            } else {
                conn.rollback(); // Revertir en caso de error
                mensajeResultado = "No se pudo crear la partida";
                logger.error(mensajeResultado);
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Revertir en caso de error
            } catch (Exception ex) {
                logger.error("Error al hacer rollback", ex);
            }

            mensajeResultado = "Error al crear la partida: " + e.getMessage();
            logger.error("Error al crear partida para " + id_usuario, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar autocommit
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return idPartida;
    }

    @Override
    public Partida obtenerPartida(int id_partida) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Partida partida = null;

        try {
            conn = DBUtils.getConnection();

            // Obtener datos básicos de la partida
            String sql = "SELECT * FROM Partida WHERE id_partida = ?";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id_partida);

            rs = pstm.executeQuery();

            if (rs.next()) {
                partida = new Partida();
                partida.setId_partida(rs.getInt("id_partida"));
                partida.setId_usuario(rs.getString("id_usuario"));
                partida.setVidas(rs.getInt("vidas"));
                partida.setMonedas(rs.getInt("monedas"));
                partida.setPuntuacion(rs.getInt("puntuacion"));

                // Cargar el inventario
                List<Objeto> inventario = obtenerInventario(id_partida);
                partida.setInventario(inventario);
            } else {
                mensajeResultado = "No se encontró la partida con ID: " + id_partida;
                logger.warn("Intento de obtener partida inexistente: " + id_partida);
            }

        } catch (Exception e) {
            mensajeResultado = "Error al obtener la partida: " + e.getMessage();
            logger.error("Error al obtener partida " + id_partida, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return partida;
    }

    @Override
    public List<Partida> obtenerPartidasUsuario(String id_usuario) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Partida> partidas = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // Obtener partidas del usuario
            String sql = "SELECT * FROM Partida WHERE id_usuario = ?";
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, id_usuario);

            rs = pstm.executeQuery();

            while (rs.next()) {
                Partida partida = new Partida();
                int idPartida = rs.getInt("id_partida");

                partida.setId_partida(idPartida);
                partida.setId_usuario(rs.getString("id_usuario"));
                partida.setVidas(rs.getInt("vidas"));
                partida.setMonedas(rs.getInt("monedas"));
                partida.setPuntuacion(rs.getInt("puntuacion"));

                // Cargar el inventario para cada partida
                List<Objeto> inventario = obtenerInventario(idPartida);
                partida.setInventario(inventario);

                partidas.add(partida);
            }

        } catch (Exception e) {
            mensajeResultado = "Error al obtener las partidas del usuario: " + e.getMessage();
            logger.error("Error al obtener partidas para usuario " + id_usuario, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return partidas;
    }

    @Override
    public boolean actualizarPartida(Partida partida) {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Actualizar datos básicos de la partida
            String sql = "UPDATE Partida SET vidas = ?, monedas = ?, puntuacion = ? WHERE id_partida = ?";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, partida.getVidas());
            pstm.setInt(2, partida.getMonedas());
            pstm.setInt(3, partida.getPuntuacion());
            pstm.setInt(4, partida.getId_partida());

            int filasAfectadas = pstm.executeUpdate();

            if (filasAfectadas > 0) {
                // Actualizar el inventario - primero eliminamos todo
                pstm.close();

                String deleteSql = "DELETE FROM Partida_Inventario WHERE id_partida = ?";
                pstm = conn.prepareStatement(deleteSql);
                pstm.setInt(1, partida.getId_partida());
                pstm.executeUpdate();

                // Y luego insertamos los objetos actuales
                if (partida.getInventario() != null && !partida.getInventario().isEmpty()) {
                    pstm.close();

                    String insertSql = "INSERT INTO Partida_Inventario (id_partida, objeto, cantidad) VALUES (?, ?, ?)";
                    pstm = conn.prepareStatement(insertSql);

                    for (Objeto item : partida.getInventario()) {
                        pstm.setInt(1, partida.getId_partida());
                        pstm.setString(2, item.getObjeto());
                        pstm.setInt(3, item.getCantidad());
                        pstm.addBatch(); // Añadir a lote para ejecución eficiente
                    }

                    pstm.executeBatch();
                }

                conn.commit(); // Confirmar transacción

                mensajeResultado = "¡Partida actualizada correctamente!";
                logger.info("Partida actualizada: " + partida.getId_partida());

                return true;
            } else {
                conn.rollback(); // Revertir en caso de error
                mensajeResultado = "No se encontró la partida con ID: " + partida.getId_partida();
                logger.warn("Intento de actualizar partida inexistente: " + partida.getId_partida());
                return false;
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Revertir en caso de error
            } catch (Exception ex) {
                logger.error("Error al hacer rollback", ex);
            }

            mensajeResultado = "Error al actualizar la partida: " + e.getMessage();
            logger.error("Error al actualizar partida " + partida.getId_partida(), e);
            return false;
        } finally {
            try {
                if (pstm != null) pstm.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar autocommit
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }
    }

    @Override
    public boolean actualizarVidas(int id_partida, int vidas) {
        Partida partida = obtenerPartida(id_partida);
        if (partida == null) return false;

        partida.setVidas(vidas);
        return actualizarPartida(partida);
    }

    @Override
    public boolean actualizarMonedas(int id_partida, int monedas) {
        Partida partida = obtenerPartida(id_partida);
        if (partida == null) return false;

        partida.setMonedas(monedas);
        return actualizarPartida(partida);
    }

    @Override
    public boolean actualizarPuntuacion(int id_partida, int puntuacion) {
        Partida partida = obtenerPartida(id_partida);
        if (partida == null) return false;

        partida.setPuntuacion(puntuacion);
        return actualizarPartida(partida);
    }

    @Override
    public List<Objeto> obtenerInventario(int id_partida) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Objeto> inventario = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // Obtener objetos del inventario
            String sql = "SELECT objeto, cantidad FROM Partida_Inventario WHERE id_partida = ?";
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id_partida);

            rs = pstm.executeQuery();

            while (rs.next()) {
                Objeto item = new Objeto();
                item.setObjeto(rs.getString("objeto"));
                item.setCantidad(rs.getInt("cantidad"));

                inventario.add(item);
            }

        } catch (Exception e) {
            logger.error("Error al obtener inventario para partida " + id_partida, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return inventario;
    }

    @Override
    public boolean añadirObjetoInventario(int id_partida, String objeto, int cantidad) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();

            // Verificar si el objeto ya está en el inventario
            String checkSql = "SELECT cantidad FROM Partida_Inventario WHERE id_partida = ? AND objeto = ?";
            pstm = conn.prepareStatement(checkSql);
            pstm.setInt(1, id_partida);
            pstm.setString(2, objeto);

            rs = pstm.executeQuery();

            if (rs.next()) {
                // Si el objeto ya existe, actualizar cantidad
                int cantidadActual = rs.getInt("cantidad");
                pstm.close();

                String updateSql = "UPDATE Partida_Inventario SET cantidad = ? WHERE id_partida = ? AND objeto = ?";
                pstm = conn.prepareStatement(updateSql);
                pstm.setInt(1, cantidadActual + cantidad);
                pstm.setInt(2, id_partida);
                pstm.setString(3, objeto);

                pstm.executeUpdate();
            } else {
                // Si el objeto no existe, insertar nuevo
                pstm.close();

                String insertSql = "INSERT INTO Partida_Inventario (id_partida, objeto, cantidad) VALUES (?, ?, ?)";
                pstm = conn.prepareStatement(insertSql);
                pstm.setInt(1, id_partida);
                pstm.setString(2, objeto);
                pstm.setInt(3, cantidad);

                pstm.executeUpdate();
            }

            mensajeResultado = "¡Objeto añadido al inventario correctamente!";
            logger.info("Objeto añadido a inventario: " + objeto + " (x" + cantidad + ") a partida " + id_partida);

            return true;

        } catch (Exception e) {
            mensajeResultado = "Error al añadir objeto al inventario: " + e.getMessage();
            logger.error("Error al añadir objeto a inventario", e);
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }
    }

    @Override
    public boolean eliminarObjetoInventario(int id_partida, String objeto, int cantidad) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();

            // Verificar si el objeto está en el inventario
            String checkSql = "SELECT cantidad FROM Partida_Inventario WHERE id_partida = ? AND objeto = ?";
            pstm = conn.prepareStatement(checkSql);
            pstm.setInt(1, id_partida);
            pstm.setString(2, objeto);

            rs = pstm.executeQuery();

            if (rs.next()) {
                int cantidadActual = rs.getInt("cantidad");
                pstm.close();

                if (cantidadActual <= cantidad) {
                    // Si la cantidad a eliminar es mayor o igual, eliminar el objeto completamente
                    String deleteSql = "DELETE FROM Partida_Inventario WHERE id_partida = ? AND objeto = ?";
                    pstm = conn.prepareStatement(deleteSql);
                    pstm.setInt(1, id_partida);
                    pstm.setString(2, objeto);
                } else {
                    // Si no, reducir la cantidad
                    String updateSql = "UPDATE Partida_Inventario SET cantidad = ? WHERE id_partida = ? AND objeto = ?";
                    pstm = conn.prepareStatement(updateSql);
                    pstm.setInt(1, cantidadActual - cantidad);
                    pstm.setInt(2, id_partida);
                    pstm.setString(3, objeto);
                }

                pstm.executeUpdate();

                mensajeResultado = "¡Objeto eliminado del inventario correctamente!";
                logger.info("Objeto eliminado de inventario: " + objeto + " (x" + cantidad + ") de partida " + id_partida);

                return true;
            } else {
                mensajeResultado = "El objeto no se encuentra en el inventario.";
                logger.warn("Intento de eliminar objeto inexistente del inventario: " + objeto);
                return false;
            }

        } catch (Exception e) {
            mensajeResultado = "Error al eliminar objeto del inventario: " + e.getMessage();
            logger.error("Error al eliminar objeto de inventario", e);
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }
    }

    @Override
    public boolean eliminarPartida(int id_partida) {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Primero eliminar los objetos del inventario (aunque lo haría CASCADE)
            String deleteInventarioSql = "DELETE FROM Partida_Inventario WHERE id_partida = ?";
            pstm = conn.prepareStatement(deleteInventarioSql);
            pstm.setInt(1, id_partida);
            pstm.executeUpdate();

            // Luego eliminar la partida
            pstm.close();
            String deletePartidaSql = "DELETE FROM Partida WHERE id_partida = ?";
            pstm = conn.prepareStatement(deletePartidaSql);
            pstm.setInt(1, id_partida);

            int filasAfectadas = pstm.executeUpdate();

            if (filasAfectadas > 0) {
                conn.commit(); // Confirmar transacción

                mensajeResultado = "¡Partida eliminada correctamente!";
                logger.info("Partida eliminada: " + id_partida);

                return true;
            } else {
                conn.rollback(); // Revertir en caso de error

                mensajeResultado = "No se encontró la partida con ID: " + id_partida;
                logger.warn("Intento de eliminar partida inexistente: " + id_partida);

                return false;
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Revertir en caso de error
            } catch (Exception ex) {
                logger.error("Error al hacer rollback", ex);
            }

            mensajeResultado = "Error al eliminar la partida: " + e.getMessage();
            logger.error("Error al eliminar partida " + id_partida, e);

            return false;
        } finally {
            try {
                if (pstm != null) pstm.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar autocommit
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }
    }

    @Override
    public boolean comprarObjeto(int idPartida, String objeto) {
        // Primero verificamos que tiene monedas suficientes
        if (!tieneMonedasSuficientes(idPartida, objeto)) {
            mensajeResultado = "No tienes suficientes monedas para comprar este objeto";
            logger.warn("No hay monedas suficientes para comprar " + objeto);
            return false;
        }

        try {
            // Obtenemos datos necesarios
            Partida partida = obtenerPartida(idPartida);
            ITiendaDAO tiendaDAO = new TiendaDAOImpl();
            Objeto infoObjeto = tiendaDAO.obtenerObjeto(objeto);

            if (partida == null || infoObjeto == null) {
                mensajeResultado = "No se encontró la partida o el objeto en la tienda";
                logger.error("No se encontró la partida o el objeto");
                return false;
            }

            // Restamos el coste de las monedas
            int monedasActuales = partida.getMonedas();
            int precioObjeto = infoObjeto.getPrecio();
            int nuevasMonedas = monedasActuales - precioObjeto;

            // Actualizamos las monedas
            boolean monedaActualizada = actualizarMonedas(idPartida, nuevasMonedas);
            if (!monedaActualizada) {
                mensajeResultado = "Error al actualizar monedas";
                logger.error("Error al actualizar monedas");
                return false;
            }

            // Añadimos el objeto al inventario
            boolean objetoAñadido = añadirObjetoInventario(idPartida, objeto, 1);
            if (!objetoAñadido) {
                // Si algo sale mal, devolvemos las monedas
                actualizarMonedas(idPartida, monedasActuales);
                mensajeResultado = "Error al añadir objeto al inventario";
                logger.error("Error al añadir objeto al inventario");
                return false;
            }

            mensajeResultado = "¡Has comprado " + objeto + " por " + precioObjeto + " monedas!";
            logger.info("Objeto " + objeto + " comprado para partida " + idPartida);
            return true;

        } catch (Exception e) {
            mensajeResultado = "Error al comprar objeto: " + e.getMessage();
            logger.error("Error al comprar objeto", e);
            return false;
        }
    }

    @Override
    public boolean tieneMonedasSuficientes(int idPartida, String objeto) {
        try {
            // Obtenemos la partida y el objeto
            Partida partida = obtenerPartida(idPartida);
            ITiendaDAO tiendaDAO = new TiendaDAOImpl();
            Objeto infoObjeto = tiendaDAO.obtenerObjeto(objeto);

            if (partida == null || infoObjeto == null) {
                mensajeResultado = "No se encontró la partida o el objeto";
                return false;
            }

            // Comparamos monedas con precio
            int monedasJugador = partida.getMonedas();
            int precioObjeto = infoObjeto.getPrecio();

            boolean tieneSuficiente = monedasJugador >= precioObjeto;

            if (!tieneSuficiente) {
                mensajeResultado = "Necesitas " + precioObjeto + " monedas, pero solo tienes " + monedasJugador;
            }

            return tieneSuficiente;

        } catch (Exception e) {
            mensajeResultado = "Error al verificar monedas: " + e.getMessage();
            logger.error("Error al verificar monedas", e);
            return false;
        }
    }

    @Override
    public boolean usarObjeto(int idPartida, String objeto) {
        try {
            // Verificamos que el objeto está en el inventario
            Partida partida = obtenerPartida(idPartida);
            if (partida == null) {
                mensajeResultado = "No se encontró la partida: " + idPartida;
                logger.error("No se encontró la partida: " + idPartida);
                return false;
            }

            boolean tieneObjeto = false;
            for (Objeto item : partida.getInventario()) {
                if (item.getObjeto().equals(objeto)) {
                    tieneObjeto = true;
                    break;
                }
            }

            if (!tieneObjeto) {
                mensajeResultado = "No tienes este objeto en el inventario";
                logger.warn("El objeto " + objeto + " no está en el inventario");
                return false;
            }

            // Si es una poción de vida, aumentamos las vidas
            if (objeto.equals("Poción de vida")) {
                int vidasActuales = partida.getVidas();
                actualizarVidas(idPartida, vidasActuales + 1);
                mensajeResultado = "¡Has usado una poción y ganado una vida!";
                logger.info("Poción usada: +1 vida para partida " + idPartida);
            } else {
                mensajeResultado = "Has usado " + objeto;
            }

            // Eliminamos una unidad del objeto del inventario
            boolean eliminado = eliminarObjetoInventario(idPartida, objeto, 1);
            return eliminado;

        } catch (Exception e) {
            mensajeResultado = "Error al usar objeto: " + e.getMessage();
            logger.error("Error al usar objeto", e);
            return false;
        }
    }

    @Override
    public List<Objeto> verInventario(int idPartida) {
        try {
            // Ya tenemos el método obtenerInventario, podemos usarlo
            List<Objeto> inventario = obtenerInventario(idPartida);

            if (inventario.isEmpty()) {
                mensajeResultado = "Tu inventario está vacío";
                logger.info("El inventario de la partida " + idPartida + " está vacío");
            } else {
                mensajeResultado = "Tienes " + inventario.size() + " tipos de objetos diferentes";
                logger.info("Inventario de partida " + idPartida + ": " + inventario.size() + " tipos de objetos");
            }

            return inventario;

        } catch (Exception e) {
            mensajeResultado = "Error al ver inventario: " + e.getMessage();
            logger.error("Error al obtener inventario", e);
            return new ArrayList<>(); // Devolvemos lista vacía si hay error
        }
    }
}