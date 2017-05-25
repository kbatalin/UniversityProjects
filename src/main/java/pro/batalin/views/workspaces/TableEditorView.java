package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.TableData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

            DefaultTableModel tableModel = new DefaultTableModel(titles, 0);

            for (String[] line : tableData.getData()) {
                tableModel.addRow(line);
            }

            Object[] addLine = new Object[titles.length];
            addLine[0] = "...";
            tableModel.addRow(addLine);

            table.setModel(tableModel);
        });
    }
}
