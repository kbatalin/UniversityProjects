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
    private String selected;

    public Tables(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.tablesNames = new ArrayList<>();

        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMA_SELECTED, () -> {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public enum Event implements ObserveEvent {
        TABLES_LIST_CHANGED,
        TABLE_SELECTED,
    }

    public void update() throws SQLException, PlatformFactoryException {
        Schema schema = applicationProperties.getSchemas().getSelected();
        update(schema);
    }

    public void update(Schema schema) throws SQLException, PlatformFactoryException {
        if (schema == null) {
            tablesNames.clear();
            selected = null;
            notifyObservers(Event.TABLES_LIST_CHANGED);
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

    public List<String> getTablesNames() throws SQLException, PlatformFactoryException {
        return tablesNames;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        if (this.selected == null || !this.selected.equals(selected)) {
            this.selected = selected;
            notifyObservers(Event.TABLE_SELECTED);
        }
    }
}
