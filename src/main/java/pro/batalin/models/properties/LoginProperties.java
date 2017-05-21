package pro.batalin.models.properties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface LoginProperties {
    String getHostname();

    void setHostname(String hostname);

    String getPort();

    void setPort(String port);

    String getSid();

    void setSid(String sid);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getConnectionString();

    Connection getConnection() throws SQLException;
}
