package pro.batalin.models.properties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface ApplicationProperties {
    LoginProperties getLoginProperties();

    Connection getConnection() throws SQLException;
}
