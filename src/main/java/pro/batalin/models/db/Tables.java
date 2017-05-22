package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.util.ListUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class Tables extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private List<String> tablesNames;
    private Schema schema;

    public Tables(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.tablesNames = new ArrayList<>();
    }

    public enum Event implements ObserveEvent {
        TABLES_LIST_CHANGED,
        SCHEMA_CHANGED,
    }

    public void update() throws SQLException, PlatformFactoryException {
        if (schema == null) {
            return;
        }

        Platform platform = applicationProperties.getPlatform();
        List<String> tablesNames = platform.loadTables(schema.getName());

        boolean hasChanges = !ListUtils.hasSameItems(tablesNames, this.tablesNames);

        if (!hasChanges) {
            return;
        }

        this.tablesNames = tablesNames;
        notifyObservers(Event.TABLES_LIST_CHANGED);
    }

    public void update(Schema schema) throws SQLException, PlatformFactoryException {
        if (this.schema == null || !schema.getName().equals(this.schema.getName())) {
            this.schema = schema;
            notifyObservers(Event.SCHEMA_CHANGED);
        }

        update();
    }

    public List<String> getTablesNames() throws SQLException, PlatformFactoryException {
        update();
        return tablesNames;
    }

    public Schema getSchema() {
        return schema;
    }
}
