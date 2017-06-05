package pro.batalin.views.workspaces;

import pro.batalin.controllers.ClientController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class PlsqlView extends WorkspaceBase {
    private JPanel contentPanel;
    private JTextArea plsqlTextArea;
    private JPanel reportPanel;
    private JButton executeButton;
    private JButton cancelButton;
    private JTextArea reportTextArea;
    private JSplitPane reportScrollPane;

    private ClientController clientController;

    public PlsqlView(ClientController clientController) {
        super(WorkspaceType.PLSQL);

        this.clientController = clientController;

        setLayout(new BorderLayout());
        add(contentPanel);

        reportTextArea.setAutoscrolls(true);

        executeButton.addActionListener(clientController::onExecutePlsqlButtonClicked);
        cancelButton.addActionListener(clientController::onCancelPlsqlButtonClicked);
    }

    public String getPlsql() {
        return plsqlTextArea.getText();
    }

    public void addLog(String log) {
        reportTextArea.append(log);
    }
}
