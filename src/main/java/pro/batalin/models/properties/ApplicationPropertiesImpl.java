package pro.batalin.models.properties;

import pro.batalin.ddl4j.DatabaseOperationException;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactory;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.ThrowableConsumer;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableReport;
import pro.batalin.models.db.Tables;
import pro.batalin.models.db.thread.DBThread;
import pro.batalin.models.db.thread.DBThreadTask;
import pro.batalin.models.observe.ObservableBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl extends ObservableBase implements ApplicationProperties {
    private LoginProperties loginProperties;
    private Schemas schemas;
    private Tables tables;
    private TableReport tableReport;
    private DBThread dbThread;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) throws ClassNotFoundException, SQLException, PlatformFactoryException {
        this.loginProperties = loginProperties;
        this.schemas = new Schemas(this);
        this.tables = new Tables(this);
        this.tableReport = new TableReport(this);
        this.dbThread = new DBThread();
        this.dbThread.start();
    }

    @Override
    public LoginProperties getLoginProperties() {
        return loginProperties;
    }

    @Override
    public Schemas getSchemas() {
        return schemas;
    }

    @Override
    public Tables getTables() {
        return tables;
    }

    @Override
    public TableReport getTableReport() {
        return tableReport;
    }

    @Override
    public DBThread getDBThread() {
        return dbThread;
    }
}
