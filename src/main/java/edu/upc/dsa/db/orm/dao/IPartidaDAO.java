package edu.upc.dsa.db.orm.dao;

import edu.upc.dsa.models.Partida;
import edu.upc.dsa.models.Objeto;
import java.util.List;

/**
 * Interfaz DAO para la entidad Partida.
 */
public interface IPartidaDAO {

    /**
     * Crea una nueva partida para un usuario.
     *
     * @param id_usuario Nombre del usuario
     * @param vidas Número de vidas iniciales
     * @param monedas Monedas iniciales
     * @param puntuacion Puntuación inicial
     * @return ID de la partida creada, o -1 si hubo error
     */
    int crearPartida(String id_usuario, int vidas, int monedas, int puntuacion);

    /**
     * Obtiene una partida por su ID.
     *
     * @param id_partida ID de la partida
     * @return La partida encontrada o null si no existe
     */
    Partida obtenerPartida(int id_partida);

    /**
     * Obtiene todas las partidas de un usuario.
     *
     * @param id_usuario Nombre del usuario
     * @return Lista de partidas del usuario
     */
    List<Partida> obtenerPartidasUsuario(String id_usuario);

    /**
     * Actualiza los datos de una partida existente.
     *
     * @param partida Partida con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    boolean actualizarPartida(Partida partida);

    /**
     * Actualiza la cantidad de vidas de una partida.
     *
     * @param id_partida ID de la partida
     * @param vidas Nuevo número de vidas
     * @return true si se actualizó correctamente, false en caso contrario
     */
    boolean actualizarVidas(int id_partida, int vidas);

    /**
     * Actualiza la cantidad de monedas de una partida.
     *
     * @param id_partida ID de la partida
     * @param monedas Nueva cantidad de monedas
     * @return true si se actualizó correctamente, false en caso contrario
     */
    boolean actualizarMonedas(int id_partida, int monedas);

    /**
     * Actualiza la puntuación de una partida.
     *
     * @param id_partida ID de la partida
     * @param puntuacion Nueva puntuación
     * @return true si se actualizó correctamente, false en caso contrario
     */
    boolean actualizarPuntuacion(int id_partida, int puntuacion);

    /**
     * Obtiene el inventario de una partida.
     *
     * @param id_partida ID de la partida
     * @return Lista de objetos en el inventario
     */
    List<Objeto> obtenerInventario(int id_partida);

    /**
     * Añade un objeto al inventario de una partida.
     *
     * @param id_partida ID de la partida
     * @param objeto Nombre del objeto
     * @param cantidad Cantidad a añadir
     * @return true si se añadió correctamente, false en caso contrario
     */
    boolean añadirObjetoInventario(int id_partida, String objeto, int cantidad);

    /**
     * Elimina un objeto del inventario de una partida.
     *
     * @param id_partida ID de la partida
     * @param objeto Nombre del objeto
     * @param cantidad Cantidad a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean eliminarObjetoInventario(int id_partida, String objeto, int cantidad);

    /**
     * Elimina una partida.
     *
     * @param id_partida ID de la partida a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean eliminarPartida(int id_partida);

    /**
     * Compra un objeto para el inventario de una partida
     * @param idPartida ID de la partida
     * @param objeto Nombre del objeto a comprar
     * @return true si se pudo comprar, false si no
     */
    boolean comprarObjeto(int idPartida, String objeto);

    /**
     * Verifica si el jugador tiene suficientes monedas para comprar un objeto
     * @param idPartida ID de la partida
     * @param objeto Nombre del objeto
     * @return true si tiene suficientes monedas, false si no
     */
    boolean tieneMonedasSuficientes(int idPartida, String objeto);

    /**
     * Usa un objeto del inventario (lo elimina)
     * @param idPartida ID de la partida
     * @param objeto Nombre del objeto a usar
     * @return true si se usó correctamente, false si no
     */
    boolean usarObjeto(int idPartida, String objeto);

    /**
     * Obtiene la lista de objetos en el inventario con sus cantidades
     * @param idPartida ID de la partida
     * @return Lista de objetos en el inventario
     */
    List<Objeto> verInventario(int idPartida);

    /**
     * Obtiene el mensaje de resultado de la última operación
     * @return Mensaje de resultado
     */
    String getMensajeResultado();
}
