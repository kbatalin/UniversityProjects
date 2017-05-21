package pro.batalin.controllers;

import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.ApplicationPropertiesImpl;
import pro.batalin.models.properties.LoginProperties;
import pro.batalin.models.properties.LoginPropertiesImpl;
import pro.batalin.views.ClientGUI;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientController {
    private ApplicationProperties applicationProperties;

    private ClientGUI clientGUI;

    public void run() {
        try {
            applicationProperties = new ApplicationPropertiesImpl(new LoginPropertiesImpl());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        //todo: remove
        applicationProperties.getLoginProperties().setUsername("TEST_USER");
        applicationProperties.getLoginProperties().setPassword("TEST_PASS");

        LoginController loginController = new LoginController(applicationProperties);
        loginController.run();

        if (!loginController.isAuthorized()) {
            return;
        }

        clientGUI = new ClientGUI();
        clientGUI.pack();
        clientGUI.setLocationRelativeTo(null);
        clientGUI.setVisible(true);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }
}
