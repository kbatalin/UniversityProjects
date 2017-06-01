package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.constraints.Constraint;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableData extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private String table;
    private Schema schema;
    private Table tableStructure;
    private List<String[]> data;
    private AtomicBoolean loading = new AtomicBoolean(false);

    public TableData(ApplicationProperties applicationProperties) {
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
        loading.set(true);
        schema = applicationProperties.getSchemas().getSelected();
        table = applicationProperties.getTables().getSelectedTable();

        applicationProperties.getDBThread().addTask(platform -> {
            tableStructure = platform.loadTable(schema, table);
            data.clear();

            if (tableStructure == null) {
                tableStructure = null;
                loading.set(false);
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

            loading.set(false);
            notifyObservers(Event.TABLE_LOADED);
        });
    }

    public List<String[]> getData() {
        return data;
    }

    public boolean isLoading() {
        return loading.get();
    }

    public void deleteRow(List<Constraint> constraints) {
        Schema schema = this.schema;
        String tableName = this.table;
        applicationProperties.getDBThread().addTask(platform -> {
            if (tableName == null || tableName.isEmpty()) {
                return;
            }

            String constraintsPattern = constraints.stream()
                    .map(Constraint::getPattern)
                    .collect(Collectors.joining(" AND "));

            String sql = String.format("DELETE FROM %s.%s WHERE %s", schema.getName(), tableName, constraintsPattern);

            Connection connection = platform.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int i = 0; i < constraints.size(); ++i) {
                statement.setString(i + 1, constraints.get(i).getValue());
            }

            statement.executeQuery();
            update();
        });
    }
}
