package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class Schemas extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private List<Schema> schemas = new ArrayList<>();;
    private Schema selected;

    public Schemas(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public enum Event implements ObserveEvent {
        SCHEMAS_LIST_LOADED,
        SCHEMA_SELECTED,
    }

    public void update() {
        applicationProperties.getDBThread().addTask(platform -> {
            this.schemas = platform.loadSchemas();
            notifyObservers(Event.SCHEMAS_LIST_LOADED);
        });
    }

    public List<Schema> getSchemas() {
        return schemas;
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
