package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.Types;
import pro.batalin.models.db.TableData;
import pro.batalin.views.workspaces.tables.DateRenderer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableReportView extends WorkspaceBase {
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JTable table;

    private final ClientController clientController;

    public TableReportView(ClientController clientController) {
        super(WorkspaceType.TABLE_REPORT);

        this.clientController = clientController;
        TableData tableData = clientController.getApplicationProperties().getTableData();

        setLayout(new BorderLayout());
        add(contentPanel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setDefaultRenderer(java.sql.Timestamp.class, new DateRenderer());

        tableData.addObserver(TableData.Event.TABLE_LOADED, e -> initTable());

        initTable();
        setVisible(true);
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
                public boolean isCellEditable(int row, int column) {
                    return false;
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

            for (Object[] line : tableData.getData()) {
                tableModel.addRow(line);
            }

            table.setModel(tableModel);
        });
    }
}
