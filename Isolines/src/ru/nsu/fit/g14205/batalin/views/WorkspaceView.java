package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 30.03.17.
 */
public class WorkspaceView extends JComponent {
    private FunctionMapView functionMapView;
    private LegendView legendView;

    public WorkspaceView(IsolinesController isolinesController) {
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        functionMapView = new FunctionMapView(isolinesController);
        legendView = new LegendView(isolinesController);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 100;
        gbc.weighty = 90;
        add(functionMapView, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weighty = 10;
        gbc.insets = new Insets(5, 0, 0, 0);
        add(legendView, gbc);
    }

    public FunctionMapView getFunctionMapView() {
        return functionMapView;
    }

    public LegendView getLegendView() {
        return legendView;
    }
}
