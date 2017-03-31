package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;

import javax.swing.*;

/**
 * Created by kir55rus on 30.03.17.
 */
public class WorkspaceView extends JComponent {
    private FunctionMapView functionMapView;
    private LegendView legendView;

    public WorkspaceView(IsolinesController isolinesController) {
        functionMapView = new FunctionMapView(isolinesController);
        legendView = new LegendView(isolinesController);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(functionMapView);
        add(Box.createHorizontalStrut(20));
        add(legendView);
    }
}
