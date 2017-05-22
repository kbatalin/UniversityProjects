package pro.batalin.views;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.Tables;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.views.workspaces.EmptyWorkspace;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientGUI extends JFrame {
    private JPanel contentPane;
    private JPanel workspacePanel;
    private JComboBox<Schema> schemasComboBox;
    private JList<String> tableList;

    private ClientController clientController;

    public ClientGUI(ClientController clientController) {
        this.clientController = clientController;

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));

        workspacePanel.add(new EmptyWorkspace());

        initSchemasComboBox();
        initTableList();

        ApplicationProperties applicationProperties = clientController.getApplicationProperties();
        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMAS_LIST_UPDATED, () -> {
            initSchemasComboBox();
            repaint();
        });
        applicationProperties.getTables().addObserver(Tables.Event.TABLES_LIST_CHANGED, () -> {
            initTableList();
            repaint();
        });


        schemasComboBox.addActionListener(clientController::onSchemasComboBoxSelected);
    }

    private void initSchemasComboBox() {
        try {
            DefaultComboBoxModel<Schema> comboBoxModel = new DefaultComboBoxModel<>();
            clientController.getApplicationProperties().getSchemas().getSchemas().forEach(comboBoxModel::addElement);
            schemasComboBox.setModel(comboBoxModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTableList() {
        try {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            clientController.getApplicationProperties().getTables().getTablesNames().forEach(listModel::addElement);
            tableList.setModel(listModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
