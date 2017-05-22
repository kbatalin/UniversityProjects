package pro.batalin.views;

import pro.batalin.controllers.ClientController;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.platforms.Platform;
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

    private ClientController clientController;

    public ClientGUI(ClientController clientController) {
        this.clientController = clientController;

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));

        workspacePanel.add(new EmptyWorkspace());

        initSchemasComboBox();
    }

    private void initSchemasComboBox() {
        schemasComboBox.removeAllItems();

        try {
            for (Schema schema : clientController.getApplicationProperties().getSchemas().getSchemas()) {
                schemasComboBox.addItem(schema);
            };

        } catch (Exception e) {
            e.printStackTrace();
        }

        schemasComboBox.addActionListener(clientController::onSchemasComboBoxSelected);
    }
}
