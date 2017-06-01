package pro.batalin.models.db.constraints;

/**
 * @author Kirill Batalin (kir55rus)
 */
public interface Constraint {
    String getColumn();
    String getPattern();
    String getValue();
    String getConstraint();
}
