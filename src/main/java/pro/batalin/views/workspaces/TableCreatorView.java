package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.views.workspaces.templates.TableColumnView;
import pro.batalin.views.workspaces.templates.TableForeignKeyView;
import pro.batalin.views.workspaces.templates.TablePrimaryKeyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableCreatorView extends WorkspaceBase {
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JTextField tableNameField;
    private JScrollPane columnsScrollPane;
    private JPanel columns;
    private JButton executeButton;
    private JButton cancelButton;
    private JPanel primaryKeys;
    private JScrollPane pkScrollPane;
    private JScrollPane fkScrollPane;
    private JPanel foreignKeys;
    private ClientController clientController;

    private TableColumnView selectedColumn = null;
    private TablePrimaryKeyView selectedPk = null;
    private TableForeignKeyView selectedFk = null;

    private JPopupMenu columnsPopupMenu;
    private JMenuItem addColumnMenu;
    private JMenuItem delColumnMenu;

    private JPopupMenu pkPopupMenu;
    private JMenuItem addPkMenu;
    private JMenuItem delPkMenu;

    private JPopupMenu fkPopupMenu;
    private JMenuItem addFkMenu;
    private JMenuItem delFkMenu;

    private List<TableColumnView> columnViewList;
    private List<TablePrimaryKeyView> primaryKeyViewList;
    private List<TableForeignKeyView> foreignKeyViewList;

    public TableCreatorView(ClientController clientController) {
        super(WorkspaceType.TABLE_CREATOR);

        this.clientController = clientController;

        setLayout(new BorderLayout());
        add(contentPanel);
        columns.setLayout(new BoxLayout(columns, BoxLayout.Y_AXIS));
        primaryKeys.setLayout(new BoxLayout(primaryKeys, BoxLayout.Y_AXIS));
        foreignKeys.setLayout(new BoxLayout(foreignKeys, BoxLayout.Y_AXIS));

        columnViewList = new ArrayList<>();
        primaryKeyViewList = new ArrayList<>();
        foreignKeyViewList = new ArrayList<>();

        executeButton.addActionListener(clientController::onCreateTableButtonClicked);
        cancelButton.addActionListener(clientController::onCancelCreateTableButtonClicked);

        initPopupMenus();
    }

    public List<TableColumnView> getColumnViewList() {
        return columnViewList;
    }

    public List<TablePrimaryKeyView> getPrimaryKeyViewList() {
        return primaryKeyViewList;
    }

    public List<TableForeignKeyView> getForeignKeyViewList() {
        return foreignKeyViewList;
    }

    public String getTableName() {
        return tableNameField.getText();
    }

    private void initPopupMenus() {
        columnsPopupMenu = new JPopupMenu();

        addColumnMenu = new JMenuItem("Add column");
        addColumnMenu.addActionListener(this::onAddColumnMenuClicked);
        columnsPopupMenu.add(addColumnMenu);

        delColumnMenu = new JMenuItem("Delete column");
        delColumnMenu.addActionListener(this::onDelColumnMenuClicked);
        columnsPopupMenu.add(delColumnMenu);

        columns.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                onColumnsMouseClicked(mouseEvent);
            }
        });

        pkPopupMenu = new JPopupMenu();

        addPkMenu = new JMenuItem("Add column");
        addPkMenu.addActionListener(this::onAddPkMenuClicked);
        pkPopupMenu.add(addPkMenu);

        delPkMenu = new JMenuItem("Delete column");
        delPkMenu.addActionListener(this::onDelPkMenuClicked);
        pkPopupMenu.add(delPkMenu);

        primaryKeys.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                onPrimaryKeysMouseClicked(mouseEvent);
            }
        });

        fkPopupMenu = new JPopupMenu();

        addFkMenu = new JMenuItem("Add key");
        addFkMenu.addActionListener(this::onAddFkMenuClicked);
        fkPopupMenu.add(addFkMenu);

        delFkMenu = new JMenuItem("Delete key");
        delFkMenu.addActionListener(this::onDelFkMenuClicked);
        fkPopupMenu.add(delFkMenu);

        foreignKeys.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                onForeignKeysMouseClicked(mouseEvent);
            }
        });
    }

    private void onAddColumnMenuClicked(ActionEvent actionEvent) {
        TableColumnView tableColumnView = new TableColumnView();
        columnViewList.add(tableColumnView);
        columns.add(tableColumnView);

        columnsScrollPane.revalidate();
        columnsScrollPane.repaint();
    }

    private void onDelColumnMenuClicked(ActionEvent actionEvent) {
        if (selectedColumn == null) {
            return;
        }

        columnViewList.remove(selectedColumn);
        columns.remove(selectedColumn);

        columnsScrollPane.revalidate();
        columnsScrollPane.repaint();
    }

    private void onColumnsMouseClicked(MouseEvent mouseEvent) {
        if (!SwingUtilities.isRightMouseButton(mouseEvent)) {
            return;
        }

        Component c = columns.getComponentAt(mouseEvent.getPoint());
        if (c instanceof TableColumnView) {
            selectedColumn = (TableColumnView) c;
            delColumnMenu.setEnabled(true);
        } else {
            delColumnMenu.setEnabled(false);
        }

        columnsPopupMenu.show(columns, mouseEvent.getX(), mouseEvent.getY());
    }

    private void onAddPkMenuClicked(ActionEvent actionEvent) {
        TablePrimaryKeyView primaryKeyView = new TablePrimaryKeyView();
        primaryKeyViewList.add(primaryKeyView);
        primaryKeys.add(primaryKeyView);

        pkScrollPane.revalidate();
        pkScrollPane.repaint();
    }

    private void onDelPkMenuClicked(ActionEvent actionEvent) {
        if (selectedPk == null) {
            return;
        }

        primaryKeyViewList.remove(selectedPk);
        primaryKeys.remove(selectedPk);

        pkScrollPane.revalidate();
        pkScrollPane.repaint();
    }

    private void onPrimaryKeysMouseClicked(MouseEvent mouseEvent) {
        if (!SwingUtilities.isRightMouseButton(mouseEvent)) {
            return;
        }

        Component c = primaryKeys.getComponentAt(mouseEvent.getPoint());
        if (c instanceof TablePrimaryKeyView) {
            selectedPk = (TablePrimaryKeyView) c;
            delPkMenu.setEnabled(true);
        } else {
            delPkMenu.setEnabled(false);
        }

        pkPopupMenu.show(primaryKeys, mouseEvent.getX(), mouseEvent.getY());
    }

    private void onAddFkMenuClicked(ActionEvent actionEvent) {
        TableForeignKeyView foreignKeyView = new TableForeignKeyView();
        foreignKeyViewList.add(foreignKeyView);
        foreignKeys.add(foreignKeyView);

        fkScrollPane.revalidate();
        fkScrollPane.repaint();
    }

    private void onDelFkMenuClicked(ActionEvent actionEvent) {
        if (selectedFk == null) {
            return;
        }

        foreignKeyViewList.remove(selectedFk);
        foreignKeys.remove(selectedFk);

        fkScrollPane.revalidate();
        fkScrollPane.repaint();
    }

    private void onForeignKeysMouseClicked(MouseEvent mouseEvent) {
        if (!SwingUtilities.isRightMouseButton(mouseEvent)) {
            return;
        }

        Component c = foreignKeys.getComponentAt(mouseEvent.getPoint());
        if (c instanceof TableForeignKeyView) {
            selectedFk = (TableForeignKeyView) c;
            delFkMenu.setEnabled(true);
        } else {
            delFkMenu.setEnabled(false);
        }

        fkPopupMenu.show(foreignKeys, mouseEvent.getX(), mouseEvent.getY());
    }
}
