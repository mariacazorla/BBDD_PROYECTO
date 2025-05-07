package edu.upc.dsa.db.orm;

import java.util.HashMap;
import java.util.List;

public interface Session<E> {
    void save(Object entity);                                           // Crud
    void close();
    public Object get(Class theClass, String pk, Object value);
    void update(Class theClass, String SET, String valueSET, String WHERE, String valueWHERE);                                         // crUd
    void delete(Object object);                                         // cruD
    List<Object> findAll(Class theClass);                               // cR
    List<Object> findAll(Class theClass, HashMap params);
    List<Object> query(String query, Class theClass, HashMap params);
}