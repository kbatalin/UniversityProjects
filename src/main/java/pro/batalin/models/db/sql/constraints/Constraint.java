package pro.batalin.models.db.sql.constraints;

import pro.batalin.models.db.sql.Pattern;

/**
 * @author Kirill Batalin (kir55rus)
 */
public interface Constraint extends Pattern {
    String getColumn();
    String getConstraint();
}
