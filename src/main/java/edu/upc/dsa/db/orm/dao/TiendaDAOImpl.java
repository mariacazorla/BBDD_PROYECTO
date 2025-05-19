package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.db.DBUtils;
import edu.upc.dsa.models.Objeto;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz ITiendaDAO.
 */
public class TiendaDAOImpl implements ITiendaDAO {

    private static final Logger logger = Logger.getLogger(TiendaDAOImpl.class);
    private String mensajeResultado;

    public String getMensajeResultado() {
        return mensajeResultado;
    }

    @Override
    public boolean añadirObjetoTienda(String objeto, String categoria, int precio) {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = DBUtils.getConnection();

            // Verificar si el objeto ya existe
            String checkSql = "SELECT objeto FROM Tienda WHERE objeto = ?";
            pstm = conn.prepareStatement(checkSql);
            pstm.setString(1, objeto);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                mensajeResultado = "El objeto ya existe en la tienda";
                logger.warn("Intento de añadir objeto existente: " + objeto);
                rs.close();
                return false;
            }
            rs.close();
            pstm.close();

            // Insertar el nuevo objeto
            String insertSql = "INSERT INTO Tienda (objeto, categoria, precio) VALUES (?, ?, ?)";
            pstm = conn.prepareStatement(insertSql);
            pstm.setString(1, objeto);
            pstm.setString(2, categoria);
            pstm.setInt(3, precio);

            int filasAfectadas = pstm.executeUpdate();

            if (filasAfectadas > 0) {
                mensajeResultado = "¡Objeto añadido correctamente a la tienda!";
                logger.info("Objeto añadido a la tienda: " + objeto);
                return true;
            } else {
                mensajeResultado = "No se pudo añadir el objeto a la tienda";
                logger.error(mensajeResultado);
                return false;
            }

        } catch (Exception e) {
            mensajeResultado = "Error al añadir objeto a la tienda: " + e.getMessage();
            logger.error("Error al añadir objeto", e);
            return false;
        } finally {
            try {
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }
    }

    @Override
    public Objeto obtenerObjeto(String objeto) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Objeto objetoTienda = null;

        try {
            conn = DBUtils.getConnection();

            String sql = "SELECT * FROM Tienda WHERE objeto = ?";
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, objeto);

            rs = pstm.executeQuery();

            if (rs.next()) {
                objetoTienda = new Objeto();
                objetoTienda.setObjeto(rs.getString("objeto"));
                objetoTienda.setCategoria(rs.getString("categoria"));
                objetoTienda.setPrecio(rs.getInt("precio"));
            } else {
                mensajeResultado = "No se encontró el objeto en la tienda: " + objeto;
                logger.warn(mensajeResultado);
            }

        } catch (Exception e) {
            mensajeResultado = "Error al obtener objeto de la tienda: " + e.getMessage();
            logger.error("Error al obtener objeto", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return objetoTienda;
    }

    @Override
    public List<Objeto> listarObjetosTienda() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Objeto> listaObjetos = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            String sql = "SELECT * FROM Tienda";
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();

            while (rs.next()) {
                Objeto objetoTienda = new Objeto();
                objetoTienda.setObjeto(rs.getString("objeto"));
                objetoTienda.setCategoria(rs.getString("categoria"));
                objetoTienda.setPrecio(rs.getInt("precio"));

                listaObjetos.add(objetoTienda);
            }

            if (listaObjetos.isEmpty()) {
                mensajeResultado = "No hay objetos disponibles en la tienda";
                logger.info(mensajeResultado);
            } else {
                mensajeResultado = "Se encontraron " + listaObjetos.size() + " objetos en la tienda";
                logger.info(mensajeResultado);
            }

        } catch (Exception e) {
            mensajeResultado = "Error al listar objetos de la tienda: " + e.getMessage();
            logger.error("Error al listar objetos", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Error al cerrar recursos", e);
            }
        }

        return listaObjetos;
    }
}
