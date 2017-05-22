package pro.batalin.models.properties;


import pro.batalin.ddl4j.DatabaseOperationException;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.observe.Observable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface ApplicationProperties extends Observable {
    LoginProperties getLoginProperties();

    Schemas getSchemas();

    Platform getPlatform() throws PlatformFactoryException, SQLException;

    Connection getConnection() throws SQLException;

    enum Event {
    }
}
