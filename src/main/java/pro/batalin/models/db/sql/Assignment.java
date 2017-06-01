package pro.batalin.models.db.sql;

import pro.batalin.ddl4j.model.Schema;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class Assignment implements Pattern {
    private Schema schema;
    private String table;
    private String column;
    private Object value;

    public Assignment() {
    }

    public Assignment(Schema schema, String table, String column, Object value) {
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String getPattern() {
        return String.format("%s.%s.%s = ?", schema.getName(), table, column);
    }
}
