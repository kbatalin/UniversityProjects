package pro.batalin.models.db;

import pro.batalin.ddl4j.DatabaseOperationException;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObservableBase;
import pro.batalin.models.observe.ObserveEvent;
import pro.batalin.models.properties.ApplicationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableReport extends ObservableBase implements Observable {
    private ApplicationProperties applicationProperties;
    private Table table;
    private List<String[]> data;

    public TableReport(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        data = new ArrayList<>();

        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, () -> {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public enum Event implements ObserveEvent {
        TABLE_CHANGED,
    }

    public Table getTableStructure() {
        return table;
    }

    public void update() throws SQLException, PlatformFactoryException {
        Schema schema = applicationProperties.getSchemas().getSelected();
        String table = applicationProperties.getTables().getSelected();

        update(schema, table);
    }

    public void update(Schema schema, String table) throws SQLException, PlatformFactoryException {
        Platform platform = applicationProperties.getPlatform();
        this.table = platform.loadTable(schema, table);
        data.clear();

        if (this.table == null) {
            this.table = null;
            notifyObservers(Event.TABLE_CHANGED);
            return;
        }

        Connection connection = applicationProperties.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schema.getName() + "." + table);
//        statement.setString(1, table);
        ResultSet resultSet = statement.executeQuery();

        int columnCount = this.table.getColumns().size();
        while (resultSet.next()) {
            String[] line = new String[columnCount];

            for(int i = 0; i < columnCount; ++i) {
                line[i] = resultSet.getString(i + 1);
            }

            data.add(line);
        }

        notifyObservers(Event.TABLE_CHANGED);
    }

    public List<String[]> getData() {
        return data;
    }
}
