package pro.batalin.models.properties;

import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObserveEvent;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface LoginProperties extends Observable {
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

    enum Event implements ObserveEvent {
        HOSTNAME_CHANGED,
        PORT_CHANGED,
        SID_CHANGED,
        USERNAME_CHANGED,
        PASSWORD_CHANGED,
    }
}
