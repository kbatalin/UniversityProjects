package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.sql.Assignment;
import pro.batalin.models.db.sql.constraints.Constraint;
import pro.batalin.models.db.sql.constraints.EqualsConstraint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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

        tableData.addObserver(TableData.Event.TABLE_LOADED, this::initTable);

        InputMap inputMap = table.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = table.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                onDeleteRow(evt);
            }
        });

        initTable();
        setVisible(true);
    }

    private void onDeleteRow(ActionEvent actionEvent) {
        int[] rows = table.getSelectedRows();

        for (int row : rows) {
            if (row < 0) {
                continue;
            }

            row = table.convertRowIndexToModel(row);
            DefaultTableModel model = (DefaultTableModel) table.getModel();

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


        List<Assignment> data = new ArrayList<>();
        data.add(new Assignment(table.getSchema(), table.getName(), model.getColumnName(column), newValue));

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

            DefaultTableModel tableModel = new DefaultTableModel(titles, 0) {
                @Override
                public void setValueAt(Object value, int row, int column) {
                    onEditCell(value, row, column);
                }
            };

            for (Object[] line : tableData.getData()) {
                tableModel.addRow(line);
            }

            table.setModel(tableModel);
        });
    }
}
