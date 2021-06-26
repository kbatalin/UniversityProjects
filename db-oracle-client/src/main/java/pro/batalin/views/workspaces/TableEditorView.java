package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.Types;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.sql.InsertPattern;
import pro.batalin.models.db.sql.UpdatePattern;
import pro.batalin.models.db.sql.constraints.Constraint;
import pro.batalin.models.db.sql.constraints.EqualsConstraint;
import pro.batalin.views.workspaces.tables.DateEditor;
import pro.batalin.views.workspaces.tables.DateRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableEditorView extends WorkspaceBase {
    private JPanel contentPanel;
    private JTable table;

    private final ClientController clientController;

    public TableEditorView(ClientController clientController) {
        super(WorkspaceType.TABLE_EDITOR);

        this.clientController = clientController;
        TableData tableData = clientController.getApplicationProperties().getTableData();

        setLayout(new BorderLayout());
        add(contentPanel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tableData.addObserver(TableData.Event.TABLE_LOADED, e -> initTable());
        tableData.addObserver(TableData.Event.EDIT_ERROR, this::onEditError);
        tableData.addObserver(TableData.Event.INSERT_ERROR, this::onInsertError);
        tableData.addObserver(TableData.Event.DELETE_ERROR, this::onDeleteError);

        table.setDefaultRenderer(java.sql.Timestamp.class, new DateRenderer());
        table.setDefaultEditor(java.sql.Timestamp.class, new DateEditor());

        InputMap inputMap = table.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = table.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                onDeleteRow(evt);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "insert");
        actionMap.put("insert", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                onInsertRow(evt);
            }
        });

        initTable();
        setVisible(true);
    }

    private void onDeleteError(Object args) {
        if (!(args instanceof SQLException)) {
            return;
        }

        onError("Delete error", (SQLException) args);
    }

    private void onInsertError(Object args) {
        if (!(args instanceof SQLException)) {
            return;
        }

        onError("Insert error", (SQLException) args);
    }

    private void onEditError(Object args) {
        if (!(args instanceof SQLException)) {
            return;
        }

        onError("Edit error", (SQLException) args);
    }

    private void onError(String type, SQLException exception) {
        String msg = String.format("%s%n%s", type, exception.getLocalizedMessage());

        JOptionPane.showMessageDialog(this,
                msg,
                "Database operation error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void onDeleteRow(ActionEvent actionEvent) {
        int[] rows = table.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int row : rows) {
            if (row < 0) {
                continue;
            }

            row = table.convertRowIndexToModel(row);

            if (row == model.getRowCount() - 1) { //insert-line
                continue;
            }

            Vector rowVector = (Vector) model.getDataVector().get(row);

            Table table = clientController.getApplicationProperties().getTableData().getTableStructure();
            java.util.List<Constraint> data = new ArrayList<>();
            for(int i = 0; i < rowVector.size(); ++i) {
                String name = model.getColumnName(i);
                Object value = rowVector.get(i);
                data.add(new EqualsConstraint(table.getSchema(), table.getName(), name, value));
            }

            clientController.onDeleteDataRow(data);
        }
    }

    private void onInsertRow(ActionEvent actionEvent) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (model.getRowCount() < 1) {
            return;
        }

        Vector rowVector = (Vector) model.getDataVector().get(model.getRowCount() - 1);

        Table table = clientController.getApplicationProperties().getTableData().getTableStructure();
        java.util.List<InsertPattern> data = new ArrayList<>();
        for(int i = 0; i < rowVector.size(); ++i) {
            String name = model.getColumnName(i);
            Object value = rowVector.get(i);
            data.add(new InsertPattern(table.getSchema(), table.getName(), name, value));
        }

        clientController.onInsertData(data);
    }

    private void onEditCell(Object newValue, int row, int column) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Vector rowVector = (Vector) model.getDataVector().get(row);

        Table table = clientController.getApplicationProperties().getTableData().getTableStructure();

        java.util.List<Constraint> constraints = new ArrayList<>();
        for(int i = 0; i < rowVector.size(); ++i) {
            String name = model.getColumnName(i);
            Object value = rowVector.get(i);
            constraints.add(new EqualsConstraint(table.getSchema(), table.getName(), name, value));
        }


        List<UpdatePattern> data = new ArrayList<>();
        data.add(new UpdatePattern(table.getSchema(), table.getName(), model.getColumnName(column), newValue));

        clientController.onEditData(data, constraints);
    }

    private void initTable() {
        SwingUtilities.invokeLater(() -> {
            TableData tableData = clientController.getApplicationProperties().getTableData();
            Table tableStructure = tableData.getTableStructure();
            if (tableStructure == null) {
                return;
            }

            Object[] titles = tableStructure.getColumns().stream()
                    .map(Column::getName)
                    .collect(Collectors.toList())
                    .toArray();

            List<Object[]> data = tableData.getData();

            DefaultTableModel tableModel = new DefaultTableModel(titles, 0) {
                @Override
                public void setValueAt(Object value, int row, int column) {
                    if (row < data.size()) {
                        onEditCell(value, row, column);
                    } else {
                        super.setValueAt(value, row, column);
                    }
                }

                @Override
                public Class<?> getColumnClass(int i) {
                    Column column = tableStructure.getColumns().get(i);
                    Class clazz = Types.toJava(column.getType().getType());

                    if (clazz == null) {
                        System.err.println("Type not found!!!!");
                    }

                    return clazz;
                }
            };

            for (Object[] line : data) {
                tableModel.addRow(line);
            }

            Object[] newDataLine = new Object[titles.length];
            newDataLine[0] = "...";
            tableModel.addRow(newDataLine);

            table.setModel(tableModel);
        });
    }
}
