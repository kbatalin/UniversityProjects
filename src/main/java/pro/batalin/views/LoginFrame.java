package pro.batalin.views;

import pro.batalin.controllers.LoginController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginFrame extends JFrame {
    private JPanel rootPanel;
    private JTextField textField1;
    private JTextField textField2;

    public LoginFrame(LoginController loginController) {
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);
    }
}
