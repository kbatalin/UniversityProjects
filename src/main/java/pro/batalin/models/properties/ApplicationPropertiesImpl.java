package pro.batalin.models.properties;

import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.Tables;
import pro.batalin.models.db.thread.DBThread;
import pro.batalin.models.observe.ObservableBase;

import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl extends ObservableBase implements ApplicationProperties {
    private LoginProperties loginProperties;
    private Schemas schemas;
    private Tables tables;
    private TableData tableData;
    private DBThread dbThread;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) throws ClassNotFoundException, SQLException, PlatformFactoryException {
        this.loginProperties = loginProperties;
        this.schemas = new Schemas(this);
        this.tables = new Tables(this);
        this.tableData = new TableData(this);
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
    public TableData getTableData() {
        return tableData;
    }

    @Override
    public DBThread getDBThread() {
        return dbThread;
    }
}
