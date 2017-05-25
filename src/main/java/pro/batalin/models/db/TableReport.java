package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableReport extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private String table;
    private Schema schema;
    private Table tableStructure;
    private List<String[]> data;

    public TableReport(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        data = new ArrayList<>();

        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, this::update);
    }

    public enum Event implements ObserveEvent {
        TABLE_LOADED,
    }

    public Table getTableStructure() {
        return tableStructure;
    }

    public void update() {
        schema = applicationProperties.getSchemas().getSelected();
        table = applicationProperties.getTables().getSelectedTable();

        applicationProperties.getDBThread().addTask(platform -> {
            tableStructure = platform.loadTable(schema, table);
            data.clear();

            if (tableStructure == null) {
                tableStructure = null;
                notifyObservers(Event.TABLE_LOADED);
                return;
            }

            Connection connection = platform.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schema.getName() + "." + table);
            ResultSet resultSet = statement.executeQuery();

            int columnCount = tableStructure.getColumns().size();
            while (resultSet.next()) {
                String[] line = new String[columnCount];

                for(int i = 0; i < columnCount; ++i) {
                    line[i] = resultSet.getString(i + 1);
                }

                data.add(line);
            }

            notifyObservers(Event.TABLE_LOADED);
        });
    }

    public List<String[]> getData() {
        return data;
    }
}
