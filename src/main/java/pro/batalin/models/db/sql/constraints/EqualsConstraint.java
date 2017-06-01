package pro.batalin.models.db.sql.constraints;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class EqualsConstraint implements Constraint {
    private String column;
    private Object value;

    public EqualsConstraint() {
    }

    public EqualsConstraint(String column, Object value) {
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
        return String.format("%s = ?", column);
    }

    @Override
    public String getConstraint() {
        return "=";
    }
}
