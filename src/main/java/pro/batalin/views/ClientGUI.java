package pro.batalin.views;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.Tables;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.views.status_bar.StatusBar;
import pro.batalin.views.status_bar.indicators.LoadingIndicator;
import pro.batalin.views.workspaces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private final StatusBar statusBar;
    private JPopupMenu tableOptionsPopupMenu;

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

        initPopupMenu();

        ApplicationProperties applicationProperties = clientController.getApplicationProperties();

        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMAS_LIST_LOADED, e -> onSchemasListLoaded());
        applicationProperties.getSchemas().addObserver(Schemas.Event.SCHEMA_SELECTED, e -> onSchemaSelected());
        applicationProperties.getTables().addObserver(Tables.Event.TABLES_LIST_LOADED, e -> onTablesListLoaded());
        applicationProperties.getTables().addObserver(Tables.Event.TABLE_SELECTED, e -> onTableSelected());
        applicationProperties.getTableData().addObserver(TableData.Event.TABLE_LOADED, e -> onTableLoaded());

        schemasComboBox.addActionListener(clientController::onSchemasComboBoxSelected);
        tableList.addListSelectionListener(clientController::onTableSelected);

        initSchemasComboBox();
        initTableList();
    }

    private void initPopupMenu() {
        tableOptionsPopupMenu = new JPopupMenu("Actions");
        JMenuItem reportMenu = new JMenuItem("Show report");
        reportMenu.addActionListener(this::onReportMenuClicked);
        tableOptionsPopupMenu.add(reportMenu);

        JMenuItem editDataMenu = new JMenuItem("Edit data");
        editDataMenu.addActionListener(this::onEditDataMenuClicked);
        tableOptionsPopupMenu.add(editDataMenu);

        JMenuItem editTableMenu = new JMenuItem("Edit table");
        editTableMenu.addActionListener(this::onEditTableMenuClicked);
        tableOptionsPopupMenu.add(editTableMenu);

        JMenuItem dropMenu = new JMenuItem("Drop table");
        dropMenu.addActionListener(this::onDropTableMenuClicked);
        tableOptionsPopupMenu.add(dropMenu);

//        tableOptionsPopupMenu.add(new JPopupMenu.Separator());

        JMenuItem createMenu = new JMenuItem("Create table");
        createMenu.addActionListener(this::onCreateTableMenuClicked);
        tableOptionsPopupMenu.add(createMenu);

        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                onTableListMouseClicked(mouseEvent);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                onTableListMousePressed(mouseEvent);
            }
        });
    }

    private void onReportMenuClicked(ActionEvent actionEvent) {
        onTableSelected();
    }

    private void onEditDataMenuClicked(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            if (workspace.getWorkspaceType() == WorkspaceType.TABLE_EDITOR) {
                return;
            }

            synchronized (statusBar) {
                if(clientController.getApplicationProperties().getTableData().isLoading()) {
                    statusBar.setIndicatorVisible("loading", true);
                }
            }
            replaceWorkspace(new TableEditorView(clientController));
        });
    }

    private void onEditTableMenuClicked(ActionEvent actionEvent) {

    }

    private void onDropTableMenuClicked(ActionEvent actionEvent) {

    }

    private void onCreateTableMenuClicked(ActionEvent actionEvent) {

    }

    private void onTableListMouseClicked(MouseEvent mouseEvent) {
        if(!SwingUtilities.isRightMouseButton(mouseEvent)
                || tableList.isSelectionEmpty()) {
            return;
        }

        tableOptionsPopupMenu.show(tableList, mouseEvent.getX(), mouseEvent.getY());
    }

    private void onTableListMousePressed(MouseEvent mouseEvent) {
        if (!SwingUtilities.isLeftMouseButton(mouseEvent) && !SwingUtilities.isRightMouseButton(mouseEvent)) {
            return;
        }

        int row = tableList.locationToIndex(mouseEvent.getPoint());
        if (row == -1) {
            return;
        }

        tableList.setSelectedIndex(row);
    }

    private void onSchemaSelected() {
        SwingUtilities.invokeLater(() -> {
            if (workspace.getWorkspaceType() == WorkspaceType.EMPTY) {
                return;
            }

            statusBar.setIndicatorVisible("loading", true);
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
        SwingUtilities.invokeLater(() -> {
            if (workspace.getWorkspaceType() == WorkspaceType.TABLE_REPORT) {
                return;
            }

            synchronized (statusBar) {
                if (clientController.getApplicationProperties().getTableData().isLoading()) {
                    statusBar.setIndicatorVisible("loading", true);
                }
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
