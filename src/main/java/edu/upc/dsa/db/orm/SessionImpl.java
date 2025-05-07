package edu.upc.dsa.db.orm;

import edu.upc.dsa.db.orm.util.ObjectHelper;
import edu.upc.dsa.db.orm.util.QueryHelper;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SessionImpl implements Session {
    private final Connection conn;

    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public void save(Object entity) {
        String insertQuery = QueryHelper.createQueryINSERT(entity);
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(insertQuery);

            // Manejo especial para Usuario
            if (entity.getClass().getSimpleName().equals("Usuario")) {
                String[] fields = ObjectHelper.getFields(entity);
                for (int i = 0; i < fields.length; i++) {
                    pstm.setObject(i+1, ObjectHelper.getter(entity, fields[i]));
                }
            } else {
                // Para otras entidades, mantén la lógica original
                pstm.setObject(1, 0);
                int i = 2;
                for (String field: ObjectHelper.getFields(entity)) {
                    pstm.setObject(i++, ObjectHelper.getter(entity, field));
                }
            }

            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // El resto de la implementación permanece igual...

    public void close() {
        try {
            this.conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(Class theClass, String pk, Object value) {
        String selectQuery = QueryHelper.createQuerySELECT(theClass, pk);
        ResultSet rs = null;
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, value);
            rs = pstm.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            Object o = theClass.newInstance();

            if (rs.next()) {
                for (int i=1; i<=numberOfColumns; i++){
                    String columnName = rsmd.getColumnName(i);
                    ObjectHelper.setter(o, columnName, rs.getObject(i));
                }
            }
            return o;

        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void update(Class theClass, String SET, String valueSET, String WHERE, String valueWHERE) {
        String updateQuery = QueryHelper.createQueryUPDATE(theClass, SET, WHERE);
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(updateQuery);
            pstm.setObject(1, valueSET);
            pstm.setObject(2, valueWHERE);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(Object object) {
        // Implementación si la necesitas
    }

    public List<Object> findAll(Class theClass) {
        String query = QueryHelper.createQuerySELECTAll(theClass);
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Object> list = new LinkedList<>();

        try {
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();

            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();

            while (rs.next()){
                Object o = theClass.newInstance();
                for (int j=1; j<=numberOfColumns; j++){
                    String columnName = metadata.getColumnName(j);
                    ObjectHelper.setter(o, columnName, rs.getObject(j));
                }
                list.add(o);
            }

            return list;
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<Object> findAll(Class theClass, HashMap params) {
        String query = QueryHelper.createQuerySelectWithParams(theClass, params);
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Object> list = new LinkedList<>();

        try {
            pstm = conn.prepareStatement(query);

            int i = 1;
            for(Object v : params.values()){
                pstm.setObject(i++, v);
            }

            rs = pstm.executeQuery();

            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();

            while (rs.next()){
                Object o = theClass.newInstance();
                for (int j=1; j<=numberOfColumns; j++){
                    String columnName = metadata.getColumnName(j);
                    ObjectHelper.setter(o, columnName, rs.getObject(j));
                }
                list.add(o);
            }

            return list;
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstm != null) pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<Object> query(String query, Class theClass, HashMap params) {
        return null;
    }
}