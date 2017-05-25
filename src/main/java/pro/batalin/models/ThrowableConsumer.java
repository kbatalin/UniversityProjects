package pro.batalin.models;

import java.sql.SQLException;

/**
 * @author Kirill Batalin (kir55rus)
 */
public interface ThrowableConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
