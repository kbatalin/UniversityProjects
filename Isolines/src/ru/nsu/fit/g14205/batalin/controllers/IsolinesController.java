package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.*;
import ru.nsu.fit.g14205.batalin.views.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private PropertiesModel mapProperties;
    private PropertiesModel legendProperties;

    private Painter painter;
    private IsolinesView isolinesView;

    public void run() {
        mapProperties = new MapProperties();
        mapProperties.setFunction(new ParaboloidFunction());
        mapProperties.setArea(new Area(-5, -5, 5, 5));
        mapProperties.setValuesCount(5);
        mapProperties.setValuesColors(new Color[]{
                new Color(221, 211, 0),
                new Color(94, 98, 63),
                new Color(144, 148, 100),
                new Color(67, 178, 71),
                new Color(30, 96, 178),
                new Color(211, 112, 36)
        });

        legendProperties = new LegendProperties(mapProperties);

        painter = new StrictPainter();
        isolinesView = new IsolinesView(this);
    }

    public void onGradientButtonClicked(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof AbstractButton)) {
            return;
        }
        AbstractButton button = ((AbstractButton) source);
        boolean isSelected = button.isSelected();

        if (isSelected) {
            painter = new GradientPainter();
        } else {
            painter = new StrictPainter();
        }

        isolinesView.repaint();
    }

    public Painter getPainter() {
        return painter;
    }

    public PropertiesModel getMapProperties() {
        return mapProperties;
    }

    public PropertiesModel getLegendProperties() {
        return legendProperties;
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
