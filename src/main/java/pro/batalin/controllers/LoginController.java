package pro.batalin.controllers;

import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.LoginProperties;
import pro.batalin.views.LoginDialog;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginController {
    private ApplicationProperties applicationProperties;
    private boolean isAuthorized;

    private LoginDialog loginDialog;

    public LoginController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        isAuthorized = false;
    }

    public void run() {
        loginDialog = new LoginDialog(this);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void onLoginButtonClicked() {
        LoginProperties loginProperties = applicationProperties.getLoginProperties();
        loginProperties.setHostname(loginDialog.getHost());
        loginProperties.setPort(loginDialog.getPort());
        loginProperties.setSid(loginDialog.getSID());
        loginProperties.setUsername(loginDialog.getUsername());
        loginProperties.setPassword(loginDialog.getPassword());

        try {
            Connection connection = applicationProperties.getConnection();

            if(connection != null) {
                isAuthorized = true;
                loginDialog.dispose();
                return;
            }

            JOptionPane.showMessageDialog(loginDialog,"Can't connect","Connection error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(loginDialog,"Connection error: " + e.getMessage(),"Connection error", JOptionPane.ERROR_MESSAGE);
        }

        isAuthorized = false;
    }

    public void onCancelButtonClicked() {
        isAuthorized = false;
        loginDialog.dispose();
    }
}
