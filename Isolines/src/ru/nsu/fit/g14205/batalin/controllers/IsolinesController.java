package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.ParaboloidFunction;
import ru.nsu.fit.g14205.batalin.models.Properties;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.views.IsolinesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private PropertiesModel propertiesModel;

    private IsolinesView isolinesView;

    public void run() {
        propertiesModel = new Properties();
        propertiesModel.setFunction(new ParaboloidFunction());
        propertiesModel.setArea(new Area(-5, -5, 5, 5));
        propertiesModel.setValuesCount(5);
        propertiesModel.setValuesColors(new Color[]{
                new Color(221, 211, 0),
                new Color(94, 98, 63),
                new Color(144, 148, 100),
                new Color(67, 178, 71),
                new Color(30, 96, 178),
                new Color(211, 112, 36)
        });

        isolinesView = new IsolinesView(this);
    }

    public PropertiesModel getPropertiesModel() {
        return propertiesModel;
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        isolinesView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        isolinesView.getStatusBarView().setMessage("");
    }
}
