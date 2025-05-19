package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.models.Objeto;
import java.util.List;

/**
 * Interfaz DAO que declara las operaciones relacionadas con la tienda del juego.
 */
public interface ITiendaDAO {

    /**
     * Añade un nuevo objeto a la tienda
     *
     * @param objeto Nombre del objeto
     * @param categoria Categoría del objeto
     * @param precio Precio del objeto
     * @return true si se añadió correctamente
     */
    boolean añadirObjetoTienda(String objeto, String categoria, int precio);

    /**
     * Obtiene información sobre un objeto específico
     *
     * @param objeto Nombre del objeto
     * @return Información del objeto o null si no existe
     */
    Objeto obtenerObjeto(String objeto);

    /**
     * Lista todos los objetos disponibles en la tienda
     *
     * @return Lista de objetos en la tienda
     */
    List<Objeto> listarObjetosTienda();

    /**
     * Obtiene el mensaje de resultado de la última operación
     * @return Mensaje de resultado
     */
    String getMensajeResultado();
}