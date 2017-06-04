package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.model.constraints.ForeignKey;
import pro.batalin.ddl4j.model.constraints.PrimaryKey;
import pro.batalin.ddl4j.model.constraints.Unique;
import pro.batalin.views.workspaces.templates.TableColumnView;
import pro.batalin.views.workspaces.templates.TableForeignKeyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private JScrollPane fkScrollPane;
    private JPanel foreignKeys;
    private ClientController clientController;

    private TableColumnView selectedColumn = null;
    private TableForeignKeyView selectedFk = null;

    private JPopupMenu columnsPopupMenu;
    private JMenuItem addColumnMenu;
    private JMenuItem delColumnMenu;

    private JPopupMenu fkPopupMenu;
    private JMenuItem addFkMenu;
    private JMenuItem delFkMenu;

    private List<TableColumnView> columnViewList;
    private List<TableForeignKeyView> foreignKeyViewList;

    public TableCreatorView(ClientController clientController) {
        super(WorkspaceType.TABLE_CREATOR);

        init(clientController);

        executeButton.addActionListener(clientController::onCreateTableButtonClicked);
        cancelButton.addActionListener(clientController::onCancelCreateTableButtonClicked);
    }

    public TableCreatorView(ClientController clientController, Table table, PrimaryKey primaryKey, List<Unique> uniques, List<ForeignKey> foreignKeys) {
        super(WorkspaceType.TABLE_CREATOR);

        init(clientController);

        tableNameField.setText(table.getName());
        Set<String> pkNames = primaryKey != null
                ? primaryKey.getColumns().stream()
                .map(Column::getName)
                .collect(Collectors.toSet())
                : new HashSet<>();
        Set<String> unNames = uniques.stream()
                .map(e -> e.getColumn().getName())
                .collect(Collectors.toSet());

        for (Column column : table.getColumns()) {
            TableColumnView columnView = new TableColumnView();
            columnView.setColumnName(column.getName());
            columnView.setType(column.getType().getType());
            columnView.setDefaultValue(column.getDefaultValue());
            columnView.setPrimaryKey(pkNames.contains(column.getName()));
            columnView.setNotNull(column.isRequired());
            columnView.setUnique(unNames.contains(column.getName()));

            columnViewList.add(columnView);
            columns.add(columnView);
        }

        for (ForeignKey foreignKey : foreignKeys) {
            TableForeignKeyView foreignKeyView = new TableForeignKeyView();
            foreignKeyView.setFromColumn(foreignKey.getFirstColumn().getName());
            foreignKeyView.setToTable(foreignKey.getSecondTable().getName());
            foreignKeyView.setToColumn(foreignKey.getSecondColumn().getName());

            foreignKeyViewList.add(foreignKeyView);
            this.foreignKeys.add(foreignKeyView);
        }

        executeButton.addActionListener(clientController::onUpdateTableButtonClicked);
        cancelButton.addActionListener(clientController::onCancelCreateTableButtonClicked);
    }

    private void init(ClientController clientController) {
        this.clientController = clientController;

        setLayout(new BorderLayout());
        add(contentPanel);
        columns.setLayout(new BoxLayout(columns, BoxLayout.Y_AXIS));
        foreignKeys.setLayout(new BoxLayout(foreignKeys, BoxLayout.Y_AXIS));

        columnViewList = new ArrayList<>();
        foreignKeyViewList = new ArrayList<>();

        initPopupMenus();
    }

    public List<TableColumnView> getColumnViewList() {
        return columnViewList;
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
