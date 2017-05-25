package pro.batalin.views;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableReport;
import pro.batalin.models.db.Tables;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.views.workspaces.EmptyWorkspace;
import pro.batalin.views.workspaces.TableReportView;
import pro.batalin.views.workspaces.WorkspaceBase;
import pro.batalin.views.workspaces.WorkspaceType;

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
    private WorkspaceBase workspace;

    public ClientGUI(ClientController clientController) {
        this.clientController = clientController;

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));

        GridBagConstraints workspaceLayoutConstraints = new GridBagConstraints();
        workspaceLayoutConstraints.gridx = 0;
        workspaceLayoutConstraints.gridy = 0;
        workspaceLayoutConstraints.gridwidth = 1;
        workspaceLayoutConstraints.gridheight = 1;
        workspaceLayoutConstraints.fill = GridBagConstraints.BOTH;
        workspaceLayoutConstraints.ipadx = 0;
        workspaceLayoutConstraints.ipady = 0;
        workspaceLayoutConstraints.weightx = 1.;
        workspaceLayoutConstraints.weighty = 1.;

        workspace = new EmptyWorkspace();
        workspacePanel.add(workspace, workspaceLayoutConstraints);

        initSchemasComboBox();
        initTableList();

        ApplicationProperties applicationProperties = clientController.getApplicationProperties();

        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMAS_LIST_CHANGED, this::initSchemasComboBox);
        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMA_SELECTED, this::onSchemaSelected);
        applicationProperties.getTables().addObserver(Tables.Event.TABLES_LIST_CHANGED, this::initTableList);
        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, this::onTableSelected);


        schemasComboBox.addActionListener(clientController::onSchemasComboBoxSelected);
        tableList.addListSelectionListener(clientController::onTableSelected);
    }

    private void onSchemaSelected() {
        workspace = new EmptyWorkspace();
        repaint();
    }

    private void onTableSelected() {
        if (workspace.getWorkspaceType() == WorkspaceType.TABLE_REPORT) {
            return;
        }

        workspace = new TableReportView(clientController);
        repaint();
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
