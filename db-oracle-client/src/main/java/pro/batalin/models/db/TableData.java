package pro.batalin.models.db;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.sql.InsertPattern;
import pro.batalin.models.db.sql.UpdatePattern;
import pro.batalin.models.db.sql.Pattern;
import pro.batalin.models.db.sql.constraints.Constraint;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableData extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private String table;
    private Schema schema;
    private Table tableStructure;
    private List<Object[]> data;
    private AtomicBoolean loading = new AtomicBoolean(false);

    public TableData(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        data = new ArrayList<>();

        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, e -> update());
    }

    public enum Event implements ObserveEvent {
        TABLE_LOADED,
        DELETE_ERROR,
        EDIT_ERROR,
        UPDATE_ERROR,
        INSERT_ERROR,
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

            String sql = String.format("SELECT * FROM %s.%s", schema.getName(), table);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            int columnCount = tableStructure.getColumns().size();
            while (resultSet.next()) {
                Object[] line = new Object[columnCount];

                for(int i = 0; i < columnCount; ++i) {
                    line[i] = resultSet.getObject(i + 1);
                }

                data.add(line);
            }

            loading.set(false);
            notifyObservers(Event.TABLE_LOADED);
        });
    }

    public List<Object[]> getData() {
        return data;
    }

    public boolean isLoading() {
        return loading.get();
    }

    public void delete(List<Constraint> constraints) {
        Schema schema = this.schema;
        String tableName = this.table;
        applicationProperties.getDBThread().addTask(platform -> {
            if (tableName == null || tableName.isEmpty() || schema == null) {
                return;
            }

            List<Constraint> notNullConstraints = constraints.stream()
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toList());

            String constraintsPattern = notNullConstraints.stream()
                    .map(Constraint::getPattern)
                    .collect(Collectors.joining(" AND "));

            String sql = String.format("DELETE FROM %s.%s WHERE %s", schema.getName(), tableName, constraintsPattern);

            Connection connection = platform.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int i = 0; i < notNullConstraints.size(); ++i) {
                statement.setObject(i + 1, notNullConstraints.get(i).getValue());
            }

            statement.executeUpdate();
            update();
        }, exception -> {
            notifyObservers(Event.DELETE_ERROR, exception);
            update();
        });
    }

    public void edit(List<UpdatePattern> data, List<Constraint> constraints) {
        Schema schema = this.schema;
        String tableName = this.table;
        applicationProperties.getDBThread().addTask(platform -> {
            if (tableName == null || tableName.isEmpty() || schema == null) {
                return;
            }

            String updatedData = data.stream()
                    .map(Pattern::getPattern)
                    .collect(Collectors.joining(", "));

            List<Constraint> notNullConstraints = constraints.stream()
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toList());

            String constraintsPattern = notNullConstraints.stream()
                    .map(Constraint::getPattern)
                    .collect(Collectors.joining(" AND "));

            String sql = String.format("UPDATE %s.%s SET %s WHERE %s",
                    schema.getName(),
                    tableName,
                    updatedData,
                    constraintsPattern);

            Connection connection = platform.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int i = 0; i < data.size(); ++i) {
                statement.setObject(i + 1, data.get(i).getValue());
            }
            for(int i = 0; i < notNullConstraints.size(); ++i) {
                statement.setObject(i + data.size() + 1, notNullConstraints.get(i).getValue());
            }

            statement.executeUpdate();
            update();
        }, exception -> {
            notifyObservers(Event.EDIT_ERROR, exception);
        });
    }

    public void insert(List<InsertPattern> data) {
        Schema schema = this.schema;
        String tableName = this.table;
        applicationProperties.getDBThread().addTask(platform -> {
            if (tableName == null || tableName.isEmpty() || schema == null) {
                return;
            }

            String insertData = data.stream()
                    .map(Pattern::getPattern)
                    .collect(Collectors.joining(", "));

            String insertPattern = Stream.generate(() -> "?")
                    .limit(data.size())
                    .collect(Collectors.joining(", "));

            String sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s)",
                    schema.getName(),
                    tableName,
                    insertData,
                    insertPattern);

            Connection connection = platform.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int i = 0; i < data.size(); ++i) {
                statement.setObject(i + 1, data.get(i).getValue());
            }

            statement.executeUpdate();
            update();
        }, exception -> {
            notifyObservers(Event.INSERT_ERROR, exception);
        });
    }
}
