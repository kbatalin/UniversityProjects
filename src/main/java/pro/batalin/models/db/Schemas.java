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
public class Schemas extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private List<Schema> schemas;
    private Schema selected;

    public Schemas(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        schemas = new ArrayList<>();
    }

    public enum Event implements ObserveEvent {
        SCHEMAS_LIST_CHANGED,
        SCHEMA_SELECTED,
    }

    public void update() throws SQLException, PlatformFactoryException {
        Platform platform = applicationProperties.getPlatform();
        List<Schema> schemas = platform.loadSchemas();

        boolean hasChanges = !ListUtils.hasSameItems(schemas, this.schemas);

        if (!hasChanges) {
            return;
        }

        this.schemas = schemas;
        notifyObservers(Event.SCHEMAS_LIST_CHANGED);
    }

    public List<Schema> getSchemas() throws SQLException, PlatformFactoryException {
        update();
        return schemas;
    }

    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    public Schema getSelected() {
        return selected;
    }

    public void setSelected(Schema selected) {
        if (this.selected == null || !this.selected.getName().equals(selected.getName())) {
            this.selected = selected;
            notifyObservers(Event.SCHEMA_SELECTED);
        }
    }
}
