package pro.batalin.models.db.sql.constraints;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.db.sql.PatternImpl;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class EqualsConstraint extends PatternImpl implements Constraint {
    private String column;
    private Object value;

    public EqualsConstraint() {
    }

    public EqualsConstraint(Schema schema, String table, String column, Object value) {
        super(schema, table);
        this.column = column;
        this.value = value;
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getPattern() {
        return String.format("%s.%s.%s = ?", getSchema(), getTable(), column);
    }

    @Override
    public String getConstraint() {
        return "=";
    }
}
