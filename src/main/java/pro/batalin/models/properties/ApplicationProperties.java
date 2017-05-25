package pro.batalin.models.properties;


import pro.batalin.ddl4j.DatabaseOperationException;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.ThrowableConsumer;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableReport;
import pro.batalin.models.db.Tables;
import pro.batalin.models.db.thread.DBThread;
import pro.batalin.models.db.thread.DBThreadTask;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObserveEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface ApplicationProperties extends Observable {
    LoginProperties getLoginProperties();

    Schemas getSchemas();

    Tables getTables();

    TableReport getTableReport();

    DBThread getDBThread();

    enum Event implements ObserveEvent {
    }
}
