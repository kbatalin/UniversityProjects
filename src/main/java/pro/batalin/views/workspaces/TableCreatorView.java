package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableCreatorView extends WorkspaceBase {
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JTextField tableNameField;
    private JPanel columns;
    private ClientController clientController;

    public TableCreatorView(ClientController clientController) {
        super(WorkspaceType.TABLE_CREATOR);

        this.clientController = clientController;

        setLayout(new BorderLayout());
        add(contentPanel);

        columns.setLayout(new BoxLayout(columns, BoxLayout.Y_AXIS));

        columns.add(new TableColumnView());
        columns.add(new TableColumnView());
        columns.add(new TableColumnView());
    }
}
