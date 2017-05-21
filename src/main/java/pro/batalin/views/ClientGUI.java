package pro.batalin.views;

import pro.batalin.views.workspaces.EmptyWorkspace;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientGUI extends JFrame {
    private JPanel contentPane;
    private JPanel workspacePanel;
    private JComboBox schemasComboBox;

    public ClientGUI() {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));

        workspacePanel.add(new EmptyWorkspace());
    }
}
