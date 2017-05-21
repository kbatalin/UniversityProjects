package pro.batalin.controllers;

import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.LoginProperties;
import pro.batalin.views.LoginFrame;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginController {
    private ApplicationProperties applicationProperties;
    private boolean result;

    private LoginFrame loginFrame;

    public LoginController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        result = false;
    }

    public void run() {
        loginFrame = new LoginFrame(this);
    }
}
