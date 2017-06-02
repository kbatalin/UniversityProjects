package pro.batalin.models.db.sql;

import pro.batalin.ddl4j.model.Schema;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class UpdatePattern extends PatternImpl implements Pattern {
    private String column;
    private Object value;

    public UpdatePattern() {
    }

    public UpdatePattern(Schema schema, String table, String column, Object value) {
        super(schema, table);
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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String getPattern() {
        return String.format("%s.%s.%s = ?", getSchema().getName(), getTable(), column);
    }
}
