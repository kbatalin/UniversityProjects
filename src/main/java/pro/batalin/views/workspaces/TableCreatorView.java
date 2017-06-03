package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;
import pro.batalin.views.workspaces.templates.TableColumnView;

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
    private ClientController clientController;

    private TableColumnView selectedColumn = null;

    private JPopupMenu columnsPopupMenu;
    private JMenuItem addColumnMenu;
    private JMenuItem delColumnMenu;

    private List<TableColumnView> columnViewList;

    public TableCreatorView(ClientController clientController) {
        super(WorkspaceType.TABLE_CREATOR);

        this.clientController = clientController;

        setLayout(new BorderLayout());
        add(contentPanel);
        getRootPane().setDefaultButton(executeButton);
        columns.setLayout(new BoxLayout(columns, BoxLayout.Y_AXIS));

        columnViewList = new ArrayList<>();

        initPopupMenus();
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
    }

    private void onExecuteButtonClicked(ActionEvent actionEvent) {

    }

    private void onCancelButtonClicked(ActionEvent actionEvent) {

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
}
