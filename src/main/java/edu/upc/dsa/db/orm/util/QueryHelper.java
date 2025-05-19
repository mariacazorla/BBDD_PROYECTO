package edu.upc.dsa.db.orm.util;

import java.util.HashMap;

/**
 * Clase de utilidad para generar consultas SQL a partir de objetos y clases.
 * Permite trabajar con cualquier tabla de forma genérica.
 */
public class QueryHelper {

    /**
     * Crea una consulta SQL INSERT para guardar un objeto en la base de datos.
     *
     * @param objeto El objeto a insertar
     * @return Consulta SQL INSERT preparada
     */
    public static String createQueryINSERT(Object objeto) {
        // Obtener el nombre de la tabla desde la clase del objeto
        String nombreTabla = obtenerNombreTabla(objeto.getClass());

        StringBuilder consulta = new StringBuilder("INSERT INTO ")
                .append(nombreTabla)
                .append(" (");

        // Obtener los nombres de los campos del objeto
        String[] nombresCampos = ObjectHelper.getFields(objeto);
        boolean esPrimerCampo = true;

        // Lista de campos a incluir en la consulta
        StringBuilder camposConsulta = new StringBuilder();
        // Lista de marcadores de parámetros (?) para valores
        StringBuilder marcadoresValores = new StringBuilder();

        // Construir las listas de campos y marcadores
        for (String nombreCampo : nombresCampos) {
            Object valorCampo = ObjectHelper.getter(objeto, nombreCampo);
            // Solo incluir campos no nulos
            if (valorCampo != null) {
                if (!esPrimerCampo) {
                    camposConsulta.append(", ");
                    marcadoresValores.append(", ");
                }
                camposConsulta.append(nombreCampo);
                marcadoresValores.append("?");
                esPrimerCampo = false;
            }
        }

        // Completar la consulta SQL
        consulta.append(camposConsulta)
                .append(") VALUES (")
                .append(marcadoresValores)
                .append(")");

        return consulta.toString();
    }

    /**
     * Crea una consulta SQL SELECT para buscar un registro por su clave primaria.
     *
     * @param tipoClase La clase correspondiente a la tabla
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     * @return Consulta SQL SELECT preparada
     */
    public static String createQuerySELECT(Class tipoClase, String nombreClavePrimaria) {
        String nombreTabla = obtenerNombreTabla(tipoClase);
        return "SELECT * FROM " + nombreTabla + " WHERE " + nombreClavePrimaria + " = ?";
    }

    /**
     * Crea una consulta SQL SELECT para recuperar todos los registros de una tabla.
     *
     * @param tipoClase La clase correspondiente a la tabla
     * @return Consulta SQL SELECT para todos los registros
     */
    public static String createQuerySELECTAll(Class tipoClase) {
        String nombreTabla = obtenerNombreTabla(tipoClase);
        return "SELECT * FROM " + nombreTabla;
    }

    /**
     * Crea una consulta SQL SELECT con criterios de búsqueda.
     *
     * @param tipoClase La clase correspondiente a la tabla
     * @param criteriosBusqueda Mapa con criterios de búsqueda (columna, valor)
     * @return Consulta SQL SELECT con filtros
     */
    public static String createQuerySelectWithParams(Class tipoClase, HashMap<String, Object> criteriosBusqueda) {
        String nombreTabla = obtenerNombreTabla(tipoClase);

        StringBuilder consulta = new StringBuilder("SELECT * FROM ")
                .append(nombreTabla);

        // Añadir criterios de búsqueda si existen
        if (!criteriosBusqueda.isEmpty()) {
            consulta.append(" WHERE ");
            boolean esPrimerCriterio = true;

            for (String nombreCampo : criteriosBusqueda.keySet()) {
                if (!esPrimerCriterio) {
                    consulta.append(" AND ");
                }
                consulta.append(nombreCampo).append(" = ?");
                esPrimerCriterio = false;
            }
        }

        return consulta.toString();
    }

    /**
     * Crea una consulta SQL UPDATE para modificar un registro.
     *
     * @param tipoClase La clase correspondiente a la tabla
     * @param camposActualizar Mapa con los campos a actualizar
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     * @param valorClavePrimaria Valor de la clave primaria para filtrar
     * @return Consulta SQL UPDATE preparada
     */
    public static String createQueryUPDATE(Class tipoClase, HashMap<String, Object> camposActualizar,
                                           String nombreClavePrimaria, Object valorClavePrimaria) {
        String nombreTabla = obtenerNombreTabla(tipoClase);

        StringBuilder consulta = new StringBuilder("UPDATE ")
                .append(nombreTabla)
                .append(" SET ");

        boolean esPrimerCampo = true;
        for (String nombreCampo : camposActualizar.keySet()) {
            if (!esPrimerCampo) {
                consulta.append(", ");
            }
            consulta.append(nombreCampo).append(" = ?");
            esPrimerCampo = false;
        }

        consulta.append(" WHERE ").append(nombreClavePrimaria).append(" = ?");

        return consulta.toString();
    }

    /**
     * Crea una consulta SQL DELETE para eliminar un registro.
     *
     * @param tipoClase La clase correspondiente a la tabla
     * @param nombreClavePrimaria Nombre de la columna que es clave primaria
     * @return Consulta SQL DELETE preparada
     */
    public static String createQueryDELETE(Class tipoClase, String nombreClavePrimaria) {
        String nombreTabla = obtenerNombreTabla(tipoClase);
        return "DELETE FROM " + nombreTabla + " WHERE " + nombreClavePrimaria + " = ?";
    }

    /**
     * Obtiene el nombre de la tabla correspondiente a una clase.
     * Por convención, usa el nombre de la clase.
     *
     * @param tipoClase La clase para la que se necesita el nombre de tabla
     * @return Nombre de la tabla en la base de datos
     */
    private static String obtenerNombreTabla(Class tipoClase) {
        // Se podría usar una anotación @Table, pero por simplicidad usamos el nombre de la clase
        return tipoClase.getSimpleName();
    }
}