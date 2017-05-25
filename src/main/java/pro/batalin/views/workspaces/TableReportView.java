package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.TableReport;

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

    private ClientController clientController;
    private TableReport tableReport;

    public TableReportView(ClientController clientController) {
        super(WorkspaceType.TABLE_REPORT);

        this.clientController = clientController;
        this.tableReport = clientController.getApplicationProperties().getTableReport();

        setLayout(new BorderLayout());
        add(contentPanel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        initTable();

        tableReport.addObserver(TableReport.Event.TABLE_CHANGED, this::initTable);

        setVisible(true);
    }

    private void initTable() {
        Table tableStructure = tableReport.getTableStructure();
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
        };

        for (String[] line : tableReport.getData()) {
            tableModel.addRow(line);
        }

        table.setModel(tableModel);
    }
}
