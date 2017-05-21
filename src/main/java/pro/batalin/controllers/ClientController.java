package pro.batalin.controllers;

import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.ApplicationPropertiesImpl;
import pro.batalin.models.properties.LoginProperties;
import pro.batalin.models.properties.LoginPropertiesImpl;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientController {
    private ApplicationProperties applicationProperties;

    public void run() {
        applicationProperties = new ApplicationPropertiesImpl(new LoginPropertiesImpl());

        LoginController loginController = new LoginController();
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }
}
