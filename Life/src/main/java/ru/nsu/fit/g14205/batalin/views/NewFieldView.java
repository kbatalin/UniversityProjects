package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.IPropertiesModel;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import sun.font.TextLabel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by kir55rus on 05.03.17.
 */
public class NewFieldView extends JFrame {
    public NewFieldView(LifeController lifeController, IPropertiesModel propertiesModel) {
        Dimension minFieldSize = propertiesModel.getMinFieldSize();
        Dimension maxFieldSize = propertiesModel.getMaxFieldSize();

        setTitle("New field");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        panel.setBorder(padding);
        add(panel);

        //width
        JPanel widthPanel = new JPanel();
        widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
        panel.add(widthPanel);

        JLabel widthLabel = new JLabel("Width:");
        widthPanel.add(widthLabel);
        widthPanel.add(Box.createHorizontalStrut(10));

        SpinnerModel widthSpinnerModel = new SpinnerNumberModel(30,
                minFieldSize.getWidth(), //min
                maxFieldSize.getWidth(), //max
                1); //step

        JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
        widthLabel.setLabelFor(widthSpinner);
        widthPanel.add(widthSpinner);

        panel.add(Box.createVerticalStrut(10));

        //height
        JPanel heightPanel = new JPanel();
        heightPanel.setLayout(new BoxLayout(heightPanel, BoxLayout.X_AXIS));
        panel.add(heightPanel);

        JLabel heightLabel = new JLabel("Height:");
        heightPanel.add(heightLabel);
        heightPanel.add(Box.createHorizontalStrut(10));

        SpinnerModel heightSpinnerModel = new SpinnerNumberModel(30,
                minFieldSize.getHeight(), //min
                maxFieldSize.getHeight(), //max
                1); //step

        JSpinner heightSpinner = new JSpinner(heightSpinnerModel);
        heightLabel.setLabelFor(heightSpinner);
        heightPanel.add(heightSpinner);

        panel.add(Box.createVerticalStrut(10));

        //buttons
        JPanel buttonPanels = new JPanel();
        buttonPanels.setLayout(new BoxLayout(buttonPanels, BoxLayout.X_AXIS));
        buttonPanels.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(buttonPanels);

        JButton okButton = new JButton("Create");
        buttonPanels.add(okButton);

        pack();
        setResizable(false);
        setVisible(true);
    }
}
