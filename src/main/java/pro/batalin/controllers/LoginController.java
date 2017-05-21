package pro.batalin.controllers;

import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.views.LoginDialog;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginController {
    private ApplicationProperties applicationProperties;
    private boolean result;

    private LoginDialog loginDialog;

    public LoginController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        result = false;
    }

    public void run() {
        loginDialog = new LoginDialog();
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }
}
