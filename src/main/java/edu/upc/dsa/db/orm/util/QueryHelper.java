package edu.upc.dsa.db.orm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class QueryHelper {

    public static String createQueryINSERT(Object entity) {
        StringBuffer sb = new StringBuffer("INSERT INTO ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (entity.getClass().getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(entity.getClass().getSimpleName());
        }

        sb.append(" (");

        String[] fields = ObjectHelper.getFields(entity);

        // Para Usuario, no incluimos ID
        if (entity.getClass().getSimpleName().equals("Usuario")) {
            boolean first = true;
            for (String field: fields) {
                if (first) {
                    sb.append(field);
                    first = false;
                } else {
                    sb.append(", ").append(field);
                }
            }
            sb.append(") VALUES (?");
            for (int i = 1; i < fields.length; i++) {
                sb.append(", ?");
            }
        } else {
            // Para otras entidades, seguimos con la lógica original
            sb.append("ID");
            for (String field: fields) {
                if (!field.equals("ID")) sb.append(", ").append(field);
            }
            sb.append(") VALUES (?");
            for (String field: fields) {
                if (!field.equals("ID")) sb.append(", ?");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    public static String createQuerySELECT(Class theClass, String pk) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (theClass.getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(theClass.getSimpleName());
        }

        sb.append(" WHERE "+pk+"= ?");

        return sb.toString();
    }

    public static String createQuerySELECTAll(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (theClass.getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(theClass.getSimpleName());
        }

        return sb.toString();
    }

    public static String createSelectFindAll(Class theClass, HashMap<String, String> params) {
        StringBuffer sb = new StringBuffer("SELECT * FROM ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (theClass.getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(theClass.getSimpleName());
        }

        sb.append(" WHERE 1=1");
        for (String key: params.keySet()) {
            sb.append(" AND "+key+"=?");
        }

        return sb.toString();
    }

    public static String createQueryUPDATE(Class clase, String SET, String Where) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (clase.getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(clase.getSimpleName());
        }

        if (Objects.equals(SET, "PASSWORD")){
            sb.append(" SET ").append(SET);
            sb.append(" = MD5(?) ");
            sb.append(" WHERE ");
            sb.append(Where);
            sb.append(" = ?");
        }
        else{
            sb.append(" SET ").append(SET);
            sb.append(" = ? ");
            sb.append(" WHERE ");
            sb.append(Where);
            sb.append(" = ?");
        }
        return sb.toString();
    }

    public static String createQuerySelectWithParams(Class theClass, HashMap params) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");

        // Utilizamos el nombre exacto de la tabla según la clase
        if (theClass.getSimpleName().equals("Usuario")) {
            sb.append("Usuario");
        } else {
            sb.append(theClass.getSimpleName());
        }

        sb.append(" WHERE (");

        params.forEach((k,v) ->{
            if(k.equals("password")){
                sb.append(k).append(" = MD5(").append("?").append(") AND ");
            }else {
                sb.append(k).append(" = ").append("?").append(" AND ");
            }
        });
        sb.delete(sb.length()-4, sb.length()-1);
        sb.append(")");

        return sb.toString();
    }
}