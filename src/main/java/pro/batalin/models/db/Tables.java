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
    private List<String> tables;
    private Schema schema;

    public Tables(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.tables = new ArrayList<>();
    }

    public enum Event implements ObserveEvent {
        TABLES_LIST_CHANGED,
        SCHEMA_CHANGED,
    }

    public void update() throws SQLException, PlatformFactoryException {
        Platform platform = applicationProperties.getPlatform();
        List<String> tables = platform.loadTables(schema.getName());

        boolean hasChanges = !ListUtils.hasSameItems(tables, this.tables);

        if (!hasChanges) {
            return;
        }

        this.tables = tables;
        notifyObservers(Event.TABLES_LIST_CHANGED);
    }

    public void update(Schema schema) throws SQLException, PlatformFactoryException {
        if (!schema.getName().equals(this.schema.getName())) {
            this.schema = schema;
            notifyObservers(Event.SCHEMA_CHANGED);
        }

        update();
    }

    public List<String> getTables() {
        return tables;
    }

    public Schema getSchema() {
        return schema;
    }
}
