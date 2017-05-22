package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.models.db.TableReport;
import pro.batalin.models.db.Tables;
import pro.batalin.models.properties.ApplicationProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableViewWorkspace extends JPanel {
    private JPanel contentPanel;
    private JPanel tablePanel;

    private ClientController clientController;

    public TableViewWorkspace(ClientController clientController) {
        this.clientController = clientController;

        add(contentPanel);

        tablePanel.add(new JLabel("FDDF"));

        ApplicationProperties applicationProperties = clientController.getApplicationProperties();
        applicationProperties.getTableReport().addObserver(TableReport.Event.TABLE_CHANGED, this::initTable);

        setVisible(true);
    }

    private void initTable() {
        ApplicationProperties applicationProperties = clientController.getApplicationProperties();

        tablePanel.removeAll();

        TableReport tableReport = applicationProperties.getTableReport();
        if (tableReport == null) {
            return;
        }

        Object[] titles = tableReport.getTableStructure().getColumns().stream()
                .map(Column::getName)
                .collect(Collectors.toList())
                .toArray();

        DefaultTableModel tableModel = new DefaultTableModel(titles, 0);

        for (String[] line : tableReport.getData()) {
            tableModel.addRow(line);
        }

        JTable table = new JTable(tableModel);
        tablePanel.add(table);

//        tablePanel.add(new JLabel("$4444444444444444444"));
//        scrollPane.revalidate();
//        scrollPane.validate();
    }
}
