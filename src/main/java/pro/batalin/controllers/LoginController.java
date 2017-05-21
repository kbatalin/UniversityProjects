package pro.batalin.controllers;

import pro.batalin.models.properties.LoginProperties;
import pro.batalin.views.LoginFrame;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginController {
    private LoginProperties loginProperties;
    private boolean result;

    private LoginFrame loginFrame;

    public LoginController(LoginProperties loginProperties) {
        this.loginProperties = loginProperties;
        result = false;
    }

    public void run() {
        loginFrame = new LoginFrame(this);
    }
}
