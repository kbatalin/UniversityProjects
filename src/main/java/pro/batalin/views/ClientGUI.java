package pro.batalin.views;

import pro.batalin.views.workspaces.EmptyWorkspace;

import javax.swing.*;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientGUI extends JFrame {
    private JPanel contentPane;
    private JPanel workspacePanel;

    public ClientGUI() {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        workspacePanel.add(new EmptyWorkspace());
    }
}
