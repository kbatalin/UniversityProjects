package pro.batalin.controllers;

import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.DBType;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.model.alters.Alter;
import pro.batalin.ddl4j.model.alters.column.DropColumnAlter;
import pro.batalin.ddl4j.model.alters.column.RenameColumnAlter;
import pro.batalin.ddl4j.model.alters.constraint.AddConstraintForeignKeyAlter;
import pro.batalin.ddl4j.model.alters.constraint.AddConstraintPrimaryAlter;
import pro.batalin.ddl4j.model.alters.constraint.AddConstraintUniqueAlter;
import pro.batalin.ddl4j.model.constraints.ForeignKey;
import pro.batalin.ddl4j.model.constraints.PrimaryKey;
import pro.batalin.ddl4j.model.constraints.Unique;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.models.db.TableStructure;
import pro.batalin.models.db.sql.InsertPattern;
import pro.batalin.models.db.sql.UpdatePattern;
import pro.batalin.models.db.sql.constraints.Constraint;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.ApplicationPropertiesImpl;
import pro.batalin.models.properties.LoginPropertiesImpl;
import pro.batalin.views.ClientGUI;
import pro.batalin.views.workspaces.*;
import pro.batalin.views.workspaces.templates.TableColumnView;
import pro.batalin.views.workspaces.templates.TableForeignKeyView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientController {
    private ApplicationProperties applicationProperties;

    private ClientGUI clientGUI;

    public void run() {
        try {
            applicationProperties = new ApplicationPropertiesImpl(new LoginPropertiesImpl());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Startup error: " + e.getMessage(),"Startup error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        //todo: remove
        applicationProperties.getLoginProperties().setUsername("TEST_USER");
        applicationProperties.getLoginProperties().setPassword("TEST_PASS");

        LoginController loginController = new LoginController(applicationProperties);
        loginController.run();

        if (!loginController.isAuthorized()) {
            System.exit(0);
        }

        applicationProperties.getSchemas().update();

        clientGUI = new ClientGUI(this);
        clientGUI.pack();
        clientGUI.setLocationRelativeTo(null);
        clientGUI.setVisible(true);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public void onSchemasComboBoxSelected(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof JComboBox)) {
            return;
        }

        JComboBox comboBox = (JComboBox) source;
        Object selected = comboBox.getSelectedItem();
        if (!(selected instanceof Schema)) {
            return;
        }

        Schema schema = (Schema) selected;

        applicationProperties.getSchemas().setSelected(schema);
    }

    public void onTableSelected(ListSelectionEvent listSelectionEvent) {
        Object source = listSelectionEvent.getSource();
        if (!(source instanceof JList)) {
            return;
        }

        JList jList = (JList) source;

        Object selected = jList.getSelectedValue();
        if (!(selected instanceof String)) {
            return;
        }

        String table = (String) selected;
        applicationProperties.getTables().setSelectedTable(table);
    }

    public void onInsertNewDataRow(String[] data) {

    }

    public void onDeleteDataRow(List<Constraint> constrains) {
        applicationProperties.getTableData().delete(constrains);
    }

    public void onEditData(List<UpdatePattern> data, List<Constraint> constrains) {
        applicationProperties.getTableData().edit(data, constrains);
    }

    public void onInsertData(List<InsertPattern> data) {
        applicationProperties.getTableData().insert(data);
    }

    public void onUpdateTableButtonClicked(ActionEvent actionEvent) {
        WorkspaceBase workspaceBase = clientGUI.getWorkspace();
        if (workspaceBase == null || workspaceBase.getWorkspaceType() != WorkspaceType.TABLE_CREATOR) {
            return;
        }

        if (!(workspaceBase instanceof TableCreatorView)) {
            return;
        }

        TableCreatorView creatorView = (TableCreatorView) workspaceBase;

        Schema schema = applicationProperties.getSchemas().getSelected();
        String tableName = creatorView.getTableName();

        applicationProperties.getDBThread().addTask(platform -> {
            Connection connection = platform.getConnection();
            try {
                connection.setAutoCommit(false);

                TableStructure tableStructure = loadTableStructure(platform, schema, tableName);
                Table table = tableStructure.getTable();

                List<Alter> alters = new ArrayList<>();

                Map<String, Column> newColumns = creatorView.getColumnViewList().stream()
                        .filter(e -> e.getOldColumn() != null && e.getOldColumn().getName() != null)
                        .collect(Collectors.toMap(
                                e -> e.getOldColumn().getName(),
                                e -> {
                                    Column column = new Column();
                                    column.setName(e.getColumnName());
                                    column.setType(new DBType(e.getType()));
                                    column.setSize(e.getTypeSize());
                                    column.setDefaultValue(e.getDefaultValue());
                                    column.setPrimaryKey(e.isPrimaryKey());
                                    column.setUnique(e.isUnique());
                                    column.setRequired(e.isNotNull());
                                    return column;
                        }));

                boolean primaryKeyChanged = false;
                for (Column column : table.getColumns()) {
                    if (!newColumns.containsKey(column.getName())) {
                        alters.add(new DropColumnAlter(table, column));
                        continue;
                    }

                    Column newColumn = newColumns.get(column.getName());
                    if (newColumn.equals(column)) {
                        continue;
                    }

                    if (!column.getName().equals(newColumn.getName())) {
                        alters.add(new RenameColumnAlter(table, column, newColumn));
                    }

                    if (column.isPrimaryKey() != newColumn.isPrimaryKey()) {
                        primaryKeyChanged = true;
                    }
                }

                for (Alter alter : alters) {
                    platform.executeAlter(alter);
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

            applicationProperties.getTables().update();
            SwingUtilities.invokeLater(() -> {
                clientGUI.replaceWorkspace(new EmptyWorkspace());
            });
        }, e -> {
            JOptionPane.showMessageDialog(clientGUI,
                    "DB error: " + e.getLocalizedMessage(),
                    "Table modification error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onCreateTableButtonClicked(ActionEvent actionEvent) {
        WorkspaceBase workspaceBase = clientGUI.getWorkspace();
        if (workspaceBase == null || workspaceBase.getWorkspaceType() != WorkspaceType.TABLE_CREATOR) {
            return;
        }

        if (!(workspaceBase instanceof TableCreatorView)) {
            return;
        }

        TableCreatorView creatorView = (TableCreatorView) workspaceBase;

        Schema schema = applicationProperties.getSchemas().getSelected();
        String tableName = creatorView.getTableName();

        Table table = new Table();
        table.setSchema(schema);
        table.setName(tableName);

        List<Column> primaryKey = new ArrayList<>();

        List<Alter> alters = new ArrayList<>();
        for (TableColumnView columnView : creatorView.getColumnViewList()) {
            Column column = new Column();
            column.setName(columnView.getColumnName());
            column.setType(new DBType(columnView.getType()));
            column.setSize(columnView.getTypeSize());
            column.setDefaultValue(columnView.getDefaultValue());
            column.setRequired(columnView.isNotNull());

            table.addColumn(column);

            if (columnView.isUnique()) {
                String alterName = String.format("unique_%s_%s_%s", schema.getName(), tableName, column.getName());
                alters.add(new AddConstraintUniqueAlter(table, alterName, Collections.singletonList(column)));
            }

            if (columnView.isPrimaryKey()) {
                primaryKey.add(column);
            }
        }

        if (!primaryKey.isEmpty()) {
            String pkName = String.format("pk_%s_%s", schema.getName(), tableName);
            alters.add(new AddConstraintPrimaryAlter(table, pkName, primaryKey));
        }

        for (TableForeignKeyView foreignKeyView : creatorView.getForeignKeyViewList()) {
            String columnName = foreignKeyView.getFromColumn();
            String refTableName = foreignKeyView.getToTable();
            String refColumnName = foreignKeyView.getToColumn();

            Column column = new Column();
            column.setName(columnName);

            Table refTable = new Table();
            refTable.setName(refTableName);
            Column refColumn = new Column();
            refColumn.setName(refColumnName);
            refTable.addColumn(refColumn);

            String alterName = String.format("fk_%s_%s_%s_%s", table.getName(), column.getName(), refTable.getName(), refColumn.getName());
            alters.add(new AddConstraintForeignKeyAlter(table, column, refTable, refColumn, alterName));
        }

        applicationProperties.getDBThread().addTask(platform -> {
            Connection connection = platform.getConnection();
            try {
                connection.setAutoCommit(false);

                platform.createTable(table);

                for (Alter alter : alters) {
                    platform.executeAlter(alter);
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }


            applicationProperties.getTables().update();
            SwingUtilities.invokeLater(() -> {
                clientGUI.replaceWorkspace(new EmptyWorkspace());
            });
        }, e -> {
            JOptionPane.showMessageDialog(clientGUI,
                    "DB error: " + e.getLocalizedMessage(),
                    "Table creation error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onCancelCreateTableButtonClicked(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            clientGUI.replaceWorkspace(new EmptyWorkspace());
        });
    }

    public void onReportMenuClicked(ActionEvent actionEvent) {
        onTableSelected();
    }

    public void onEditDataMenuClicked(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            clientGUI.replaceWorkspace(new TableEditorView(this));
        });
    }

    public void onEditTableMenuClicked(ActionEvent actionEvent) {
        Schema schema = applicationProperties.getSchemas().getSelected();
        String tableName = applicationProperties.getTables().getSelectedTable();

        applicationProperties.getDBThread().addTask(platform -> {
            TableStructure tableStructure = loadTableStructure(platform, schema, tableName);

            SwingUtilities.invokeLater(() -> {
                clientGUI.replaceWorkspace(new TableCreatorView(this, tableStructure));
            });
        }, e -> {
            JOptionPane.showMessageDialog(clientGUI,
                    "DB error: " + e.getLocalizedMessage(),
                    "Table loading error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private TableStructure loadTableStructure(Platform platform, Schema schema, String tableName) throws SQLException {
        TableStructure tableStructure = new TableStructure();

        Table table = platform.loadTable(schema, tableName);
        tableStructure.setTable(table);

        Set<String> pkColumns = new HashSet<>();
        List<String> pkNames = platform.loadPrimaryKeys(table);
        if (pkNames != null && !pkNames.isEmpty()) {
            PrimaryKey primaryKey = platform.loadPrimaryKey(schema, pkNames.get(0));
            tableStructure.setPrimaryKey(primaryKey);
            pkColumns = primaryKey.getColumns().stream()
                    .map(Column::getName)
                    .collect(Collectors.toSet());
        }

        List<String> fkNames = platform.loadForeignKeys(table);
        for (String fkName : fkNames) {
            ForeignKey foreignKey = platform.loadForeignKey(schema, fkName);
            if (foreignKey == null) {
                continue;
            }

            tableStructure.getForeignKeys().add(foreignKey);
        }

        Set<String> unColumns = new HashSet<>();
        List<String> unNames = platform.loadUniques(table);
        for (String unName : unNames) {
            if (tableStructure.getPrimaryKey() != null && tableStructure.getPrimaryKey().getName().equals(unName)) {
                continue;
            }

            Unique unique = platform.loadUnique(schema, unName);
            if (unName == null) {
                continue;
            }

            tableStructure.getUniques().add(unique);
            unColumns.add(unique.getColumn().getName());
        }

        for (Column column : table.getColumns()) {
            column.setPrimaryKey(pkColumns.contains(column.getName()));
            column.setUnique(unColumns.contains(column.getName()));
        }

        return tableStructure;
    }

    public void onDropTableMenuClicked(ActionEvent actionEvent) {
        Schema selectedSchema = applicationProperties.getSchemas().getSelected();
        String selectedTable = applicationProperties.getTables().getSelectedTable();

        applicationProperties.getDBThread().addTask(platform -> {
            Table table = platform.loadTable(selectedSchema, selectedTable);
            if (table == null) {
                return;
            }

            platform.dropTable(table);

            applicationProperties.getTables().update();
            SwingUtilities.invokeLater(() -> {
                clientGUI.replaceWorkspace(new EmptyWorkspace());
            });
        }, e -> {
            JOptionPane.showMessageDialog(clientGUI,
                    "DB error: " + e.getLocalizedMessage(),
                    "Table dropping error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onCreateTableMenuClicked(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            clientGUI.replaceWorkspace(new TableCreatorView(this));
        });
    }

    public void onSchemaSelected() {
        SwingUtilities.invokeLater(() -> {
            clientGUI.replaceWorkspace(new EmptyWorkspace());
        });
    }

    public void onTableSelected() {
        SwingUtilities.invokeLater(() -> {
            clientGUI.replaceWorkspace(new TableReportView(this));
        });
    }
}
