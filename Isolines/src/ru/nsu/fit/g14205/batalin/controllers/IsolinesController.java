package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.IsolinesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private PropertiesModel mapProperties;
    private PropertiesModel legendProperties;

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

//        Function function = legendProperties.getFunction();
//        double prev = 0;
//        for(int i = -1000; i < 1000; ++i) {
//            double val = function.calc(i, 0);
//            if (Double.compare(val, prev) == 0) {
//                continue;
//            }
//
//            System.out.println(i + " " + val);
//            prev = val;
//        }

//        System.out.println(legendProperties.getValueColor(-5));
//        System.out.println(legendProperties.getValueColor(0));
//        System.out.println(legendProperties.getValueColor(2));
//        System.out.println(legendProperties.getValueColor(120));
//
//        System.out.println("---");
//
//        System.out.println(mapProperties.getValueColor(-5));
//        System.out.println(mapProperties.getValueColor(0));
//        System.out.println(mapProperties.getValueColor(2));
//        System.out.println(mapProperties.getValueColor(120));

        isolinesView = new IsolinesView(this);
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
