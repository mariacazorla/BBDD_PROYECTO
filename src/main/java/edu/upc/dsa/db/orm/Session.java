package edu.upc.dsa.db.orm;

import java.util.HashMap;
import java.util.List;

/**
 * Interfaz que define operaciones básicas para gestionar objetos en la base de datos.
 * Actúa como una sesión de trabajo con la base de datos.
 */
public interface Session {
    /**
     * Guarda un objeto en la base de datos.
     *
     * @param objeto El objeto a guardar
     */
    void save(Object objeto);

    /**
     * Busca un objeto por su clave primaria.
     *
     * @param tipoClase La clase del objeto que se busca
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     * @param valorClavePrimaria Valor de la clave primaria a buscar
     * @return El objeto encontrado o null si no existe
     */
    Object get(Class tipoClase, String nombreClavePrimaria, Object valorClavePrimaria);

    /**
     * Obtiene todos los objetos de una tabla.
     *
     * @param tipoClase La clase que representa la tabla
     * @return Lista con todos los objetos encontrados
     */
    List<Object> findAll(Class tipoClase);

    /**
     * Busca objetos que cumplan con ciertos criterios.
     *
     * @param tipoClase La clase que representa la tabla
     * @param criteriosBusqueda Mapa con los criterios de búsqueda (columna, valor)
     * @return Lista de objetos que cumplen los criterios
     */
    List<Object> findAll(Class tipoClase, HashMap<String, Object> criteriosBusqueda);

    /**
     * Actualiza un objeto en la base de datos.
     *
     * @param objeto El objeto con los datos actualizados
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     */
    void update(Object objeto, String nombreClavePrimaria);

    /**
     * Elimina un objeto de la base de datos.
     *
     * @param tipoClase La clase que representa la tabla
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     * @param valorClavePrimaria Valor de la clave primaria del objeto a eliminar
     */
    void delete(Class tipoClase, String nombreClavePrimaria, Object valorClavePrimaria);

    /**
     * Cierra la sesión y libera los recursos.
     */
    void close();
}