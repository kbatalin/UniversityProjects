package pro.batalin.models.db.constraints;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class EqualsConstraint implements Constraint {
    private String column;
    private String value;

    public EqualsConstraint() {
    }

    public EqualsConstraint(String column, String value) {
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
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
