package pro.batalin.views;

import pro.batalin.controllers.LoginController;
import pro.batalin.models.properties.LoginProperties;

import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonLogin;
    private JButton buttonCancel;
    private JTextField hostField;
    private JTextField portField;
    private JTextField sidField;
    private JTextField usernameField;
    private JTextField passwordField;

    public LoginDialog(LoginController loginController) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonLogin);

        buttonLogin.addActionListener(e -> loginController.onLoginButtonClicked());

        buttonCancel.addActionListener(e -> loginController.onCancelButtonClicked());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                loginController.onCancelButtonClicked();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> loginController.onCancelButtonClicked(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        LoginProperties loginProperties = loginController.getApplicationProperties().getLoginProperties();
        hostField.setText(loginProperties.getHostname());
        portField.setText(loginProperties.getPort());
        sidField.setText(loginProperties.getSid());
    }

    public String getHost() {
        return hostField.getText();
    }

    public String getPort() {
        return portField.getText();
    }

    public String getSID() {
        return sidField.getText();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
