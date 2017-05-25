package pro.batalin.controllers;

import pro.batalin.ddl4j.model.Schema;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.ApplicationPropertiesImpl;
import pro.batalin.models.properties.LoginPropertiesImpl;
import pro.batalin.views.ClientGUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ClientController {
    private ApplicationProperties applicationProperties;

    private ClientGUI clientGUI;

    public void run() {
        try {
            applicationProperties = new ApplicationPropertiesImpl(new LoginPropertiesImpl());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Startup error: " + e.getMessage(),"Startup error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        //todo: remove
        applicationProperties.getLoginProperties().setUsername("TEST_USER");
        applicationProperties.getLoginProperties().setPassword("TEST_PASS");

        LoginController loginController = new LoginController(applicationProperties);
        loginController.run();

        if (!loginController.isAuthorized()) {
            System.exit(0);
        }

        applicationProperties.getSchemas().update();

        clientGUI = new ClientGUI(this);
        clientGUI.pack();
        clientGUI.setLocationRelativeTo(null);
        clientGUI.setVisible(true);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public void onSchemasComboBoxSelected(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof JComboBox)) {
            return;
        }

        JComboBox comboBox = (JComboBox) source;
        Object selected = comboBox.getSelectedItem();
        if (!(selected instanceof Schema)) {
            return;
        }

        Schema schema = (Schema) selected;

        applicationProperties.getSchemas().setSelected(schema);
    }

    public void onTableSelected(ListSelectionEvent listSelectionEvent) {
        Object source = listSelectionEvent.getSource();
        if (!(source instanceof JList)) {
            return;
        }

        JList jList = (JList) source;

        Object selected = jList.getSelectedValue();
        if (!(selected instanceof String)) {
            return;
        }

        String table = (String) selected;
        applicationProperties.getTables().setSelectedTable(table);
    }
}
