package pro.batalin.models;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class Types {
    private static Map<String, Class> types = new HashMap<>();

    static {
        types.put("CHAR", java.lang.String.class);
        types.put("CHARACTER", java.lang.String.class);
        types.put("LONG", java.lang.String.class);
        types.put("VARCHAR", java.lang.String.class);
        types.put("VARCHAR2", java.lang.String.class);

        types.put("RAW", byte[].class);
        types.put("LONG RAW", byte[].class);

        types.put("BINARY_INTEGER", int.class);
        types.put("NATURAL", int.class);
        types.put("NATURALN", int.class);
        types.put("PLS_INTEGER", int.class);
        types.put("POSITIVE", int.class);
        types.put("POSITIVEN", int.class);
        types.put("SIGNTYPE", int.class);
        types.put("INT", int.class);
        types.put("INTEGER", int.class);
        types.put("SMALLINT", int.class);

        types.put("DEC", java.math.BigDecimal.class);
        types.put("DECIMAL", java.math.BigDecimal.class);
        types.put("NUMBER", java.math.BigDecimal.class);
        types.put("NUMERIC", java.math.BigDecimal.class);

        types.put("DOUBLE PRECISION", double.class);
        types.put("FLOAT", double.class);

        types.put("REAL", float.class);

        types.put("DATE", java.sql.Timestamp.class);
        types.put("TIMESTAMP", java.sql.Timestamp.class);
        types.put("TIMESTAMP WITH TZ", java.sql.Timestamp.class);
        types.put("TIMESTAMP WITH LOCAL TZ", java.sql.Timestamp.class);

        types.put("BOOLEAN", boolean.class);

        types.put("CLOB", java.sql.Clob.class);
        types.put("BLOB", java.sql.Blob.class);
    }

    public static Class toJava(String dbType) {
        Class clazz = types.get(dbType);
        if (clazz != null) {
            return clazz;
        }

        return Object.class;
    }
}
