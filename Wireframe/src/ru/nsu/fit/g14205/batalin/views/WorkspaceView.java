package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.WireframeController;
import ru.nsu.fit.g14205.batalin.models.ApplicationProperties;
import ru.nsu.fit.g14205.batalin.models.ViewPyramid;
import ru.nsu.fit.g14205.batalin.models.ViewPyramidProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WorkspaceView extends JComponent {
    private WireframeController wireframeController;
    private int margins = 10;

    public WorkspaceView(WireframeController wireframeController) {
        this.wireframeController = wireframeController;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension componentSize = getSize();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, componentSize.width, componentSize.height);

        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        ViewPyramidProperties viewPyramidProperties = applicationProperties.getViewPyramidProperties();

        double viewPortSizeRatio = Math.min((componentSize.getWidth() - 2*margins) / viewPyramidProperties.getFrontPlaneWidth(),
                (componentSize.getHeight() - 2* margins) / viewPyramidProperties.getFrontPlaneHeight());
        int viewPortWidth = (int)(viewPyramidProperties.getFrontPlaneWidth() * viewPortSizeRatio);
        int viewPortHeight = (int)(viewPyramidProperties.getFrontPlaneHeight() * viewPortSizeRatio);
        Rectangle viewPort = new Rectangle((componentSize.width - viewPortWidth) / 2, (componentSize.height - viewPortHeight) / 2, viewPortWidth, viewPortHeight);

        graphics.setColor(Color.BLUE);
        graphics.drawRect(viewPort.x, viewPort.y, viewPort.width, viewPort.height);


    }
}
