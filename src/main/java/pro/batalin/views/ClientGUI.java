package pro.batalin.views;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.Tables;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.views.status_bar.StatusBar;
import pro.batalin.views.status_bar.indicators.LoadingIndicator;
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
    private JPanel statusBarPanel;
    private ClientController clientController;
    private WorkspaceBase workspace;
    private GridBagConstraints workspaceLayoutConstraints;
    private StatusBar statusBar;

    public ClientGUI(ClientController clientController) {
        this.clientController = clientController;

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));

        workspaceLayoutConstraints = new GridBagConstraints();
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

        statusBar = new StatusBar();
        statusBar.addIndicator("loading", new LoadingIndicator(), false);
        statusBarPanel.setLayout(new BorderLayout());
        statusBarPanel.add(statusBar);

        ApplicationProperties applicationProperties = clientController.getApplicationProperties();

        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMAS_LIST_LOADED, this::onSchemasListLoaded);
        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMA_SELECTED, this::onSchemaSelected);
        applicationProperties.getTables().addObserver(Tables.Event.TABLES_LIST_LOADED, this::onTablesListLoaded);
        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, this::onTableSelected);
        applicationProperties.getTableData().addObserver(TableData.Event.TABLE_LOADED, this::onTableLoaded);

        schemasComboBox.addActionListener(clientController::onSchemasComboBoxSelected);
        tableList.addListSelectionListener(clientController::onTableSelected);

        initSchemasComboBox();
        initTableList();
    }

    private void onSchemaSelected() {
        statusBar.setIndicatorVisible("loading", true);

        SwingUtilities.invokeLater(() -> {
            if (workspace.getWorkspaceType() == WorkspaceType.EMPTY) {
                return;
            }

            replaceWorkspace(new EmptyWorkspace());
        });
    }

    private void onSchemasListLoaded() {
        SwingUtilities.invokeLater(this::initSchemasComboBox);
    }

    private void onTablesListLoaded() {
        SwingUtilities.invokeLater(() -> {
            initTableList();

            statusBar.setIndicatorVisible("loading", false);
        });
    }

    private void onTableSelected() {
        statusBar.setIndicatorVisible("loading", true);

        SwingUtilities.invokeLater(() -> {
            if (workspace.getWorkspaceType() == WorkspaceType.TABLE_REPORT) {
                return;
            }

            replaceWorkspace(new TableReportView(clientController));
        });
    }

    private void onTableLoaded() {
        SwingUtilities.invokeLater(() -> statusBar.setIndicatorVisible("loading", false));
    }

    private void replaceWorkspace(WorkspaceBase workspace) {
        workspacePanel.removeAll();
        this.workspace = workspace;
        workspacePanel.add(workspace, workspaceLayoutConstraints);
        workspacePanel.revalidate();
        workspacePanel.repaint();
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
