package edu.upc.dsa.db.orm;

import edu.upc.dsa.db.orm.util.ObjectHelper;
import edu.upc.dsa.db.orm.util.QueryHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implementación de la interfaz Session que gestiona operaciones con la base de datos.
 * Esta clase convierte objetos Java en registros de base de datos y viceversa.
 */
public class SessionImpl implements Session {
    // Conexión a la base de datos
    private final Connection conexion;

    /**
     * Constructor que recibe una conexión a la base de datos.
     */
    public SessionImpl(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Guarda un objeto en la base de datos.
     */
    @Override
    public void save(Object objeto) {
        // Crear la consulta SQL INSERT basada en el objeto
        String consultaInsert = QueryHelper.createQueryINSERT(objeto);
        PreparedStatement statement = null;

        try {
            // Preparar la consulta
            statement = conexion.prepareStatement(consultaInsert);

            // Obtener los campos del objeto
            String[] nombresCampos = ObjectHelper.getFields(objeto);
            int posicionParametro = 1;

            // Asignar los valores de cada campo como parámetros de la consulta
            for (String nombreCampo : nombresCampos) {
                Object valorCampo = ObjectHelper.getter(objeto, nombreCampo);
                // Solo incluir campos no nulos
                if (valorCampo != null) {
                    statement.setObject(posicionParametro++, valorCampo);
                }
            }

            // Ejecutar la consulta
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar objeto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca un objeto por su clave primaria.
     */
    @Override
    public Object get(Class tipoClase, String nombreClavePrimaria, Object valorClavePrimaria) {
        // Crear consulta SELECT con condición de clave primaria
        String consultaSelect = QueryHelper.createQuerySELECT(tipoClase, nombreClavePrimaria);
        ResultSet resultados = null;
        PreparedStatement statement = null;
        Object objetoEncontrado = null;

        try {
            // Preparar la consulta con el valor de la clave primaria
            statement = conexion.prepareStatement(consultaSelect);
            statement.setObject(1, valorClavePrimaria);

            // Ejecutar la consulta
            resultados = statement.executeQuery();

            // Si encontramos un resultado, crear un objeto con esos datos
            if (resultados.next()) {
                // Crear una instancia vacía del tipo solicitado
                objetoEncontrado = tipoClase.newInstance();
                ResultSetMetaData metadatos = resultados.getMetaData();

                // Recorrer todas las columnas y asignar sus valores al objeto
                for (int i = 1; i <= metadatos.getColumnCount(); i++) {
                    String nombreColumna = metadatos.getColumnName(i);
                    Object valorColumna = resultados.getObject(i);
                    // Usar reflexión para asignar el valor al campo correspondiente
                    ObjectHelper.setter(objetoEncontrado, nombreColumna, valorColumna);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.out.println("Error al buscar objeto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (resultados != null) resultados.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return objetoEncontrado;
    }

    /**
     * Obtiene todos los objetos de una tabla.
     */
    @Override
    public List<Object> findAll(Class tipoClase) {
        // Crear consulta SELECT para todos los registros
        String consulta = QueryHelper.createQuerySELECTAll(tipoClase);
        // Ejecutar la consulta sin criterios adicionales
        return ejecutarConsultaYMapearResultados(tipoClase, consulta, new HashMap<>());
    }

    /**
     * Busca objetos que cumplan con ciertos criterios.
     */
    @Override
    public List<Object> findAll(Class tipoClase, HashMap<String, Object> criteriosBusqueda) {
        // Crear consulta SELECT con los criterios de búsqueda
        String consulta = QueryHelper.createQuerySelectWithParams(tipoClase, criteriosBusqueda);
        // Ejecutar la consulta con los criterios proporcionados
        return ejecutarConsultaYMapearResultados(tipoClase, consulta, criteriosBusqueda);
    }

    /**
     * Actualiza un objeto en la base de datos.
     */
    @Override
    public void update(Object objeto, String nombreClavePrimaria) {
        // Recopilar los campos a actualizar
        HashMap<String, Object> camposActualizar = new HashMap<>();
        String[] nombresCampos = ObjectHelper.getFields(objeto);

        // Crear un mapa con todos los campos excepto la clave primaria
        for (String nombreCampo : nombresCampos) {
            if (!nombreCampo.equals(nombreClavePrimaria)) {
                Object valorCampo = ObjectHelper.getter(objeto, nombreCampo);
                camposActualizar.put(nombreCampo, valorCampo);
            }
        }

        // Obtener el valor de la clave primaria
        Object valorClavePrimaria = ObjectHelper.getter(objeto, nombreClavePrimaria);

        // Crear consulta UPDATE
        String consultaUpdate = QueryHelper.createQueryUPDATE(objeto.getClass(), camposActualizar, nombreClavePrimaria, valorClavePrimaria);
        PreparedStatement statement = null;

        try {
            // Preparar la consulta
            statement = conexion.prepareStatement(consultaUpdate);

            // Asignar valores de los campos a actualizar
            int posicionParametro = 1;
            for (Object valor : camposActualizar.values()) {
                statement.setObject(posicionParametro++, valor);
            }

            // Asignar valor de la clave primaria para la condición WHERE
            statement.setObject(posicionParametro, valorClavePrimaria);

            // Ejecutar la actualización
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al actualizar objeto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Elimina un objeto de la base de datos.
     */
    @Override
    public void delete(Class tipoClase, String nombreClavePrimaria, Object valorClavePrimaria) {
        // Crear consulta DELETE
        String consultaDelete = QueryHelper.createQueryDELETE(tipoClase, nombreClavePrimaria);

        try (PreparedStatement statement = conexion.prepareStatement(consultaDelete)) {
            // Asignar el valor de la clave primaria
            statement.setObject(1, valorClavePrimaria);
            // Ejecutar la eliminación
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar objeto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la sesión y libera los recursos.
     */
    @Override
    public void close() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para ejecutar consultas SELECT y mapear los resultados a objetos.
     */
    private List<Object> ejecutarConsultaYMapearResultados(Class tipoClase, String consulta, HashMap<String, Object> parametros) {
        List<Object> listaResultados = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultados = null;

        try {
            // Preparar la consulta
            statement = conexion.prepareStatement(consulta);

            // Asignar los valores de los parámetros
            int posicionParametro = 1;
            for (Object valor : parametros.values()) {
                statement.setObject(posicionParametro++, valor);
            }

            // Ejecutar la consulta
            resultados = statement.executeQuery();
            ResultSetMetaData metadatos = resultados.getMetaData();
            int cantidadColumnas = metadatos.getColumnCount();

            // Recorrer resultados y crear objetos
            while (resultados.next()) {
                // Crear nueva instancia para cada resultado
                Object instancia = tipoClase.newInstance();

                // Asignar valores de cada columna al objeto
                for (int i = 1; i <= cantidadColumnas; i++) {
                    String nombreColumna = metadatos.getColumnName(i);
                    Object valorColumna = resultados.getObject(i);
                    ObjectHelper.setter(instancia, nombreColumna, valorColumna);
                }

                // Añadir el objeto a la lista de resultados
                listaResultados.add(instancia);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.out.println("Error al ejecutar consulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (resultados != null) resultados.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listaResultados;
    }
}