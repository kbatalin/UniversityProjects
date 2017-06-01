package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
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
    private String selectedTable;

    public Tables(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.tablesNames = new ArrayList<>();

        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMA_SELECTED, this::update);
    }

    public enum Event implements ObserveEvent {
        TABLES_LIST_LOADED,
        TABLE_SELECTED,
    }

    public void update() {
        schema = applicationProperties.getSchemas().getSelected();

        applicationProperties.getDBThread().addTask(platform -> {
            if (schema == null) {
                tablesNames.clear();
                selectedTable = null;
                notifyObservers(Event.TABLES_LIST_LOADED);
                return;
            }

            this.tablesNames = platform.loadTables(schema.getName());
            notifyObservers(Event.TABLES_LIST_LOADED);
        });
    }

    public List<String> getTablesNames() throws SQLException, PlatformFactoryException {
        return tablesNames;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
        notifyObservers(Event.TABLE_SELECTED);
    }
}
