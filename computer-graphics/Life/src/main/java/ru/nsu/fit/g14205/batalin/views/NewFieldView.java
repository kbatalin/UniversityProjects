package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.IPropertiesModel;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import sun.font.TextLabel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel(30,
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

        SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel(30,
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
        okButton.addActionListener(actionEvent -> {
            int width = (int) widthSpinnerModel.getNumber().intValue();
            int height = heightSpinnerModel.getNumber().intValue();
            lifeController.onCreateNewFieldDialogOk(new Dimension(width, height));
        });
        buttonPanels.add(okButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                lifeController.onNewFieldDialogClosing();
            }
        });

        pack();
        setResizable(false);
        setVisible(true);
    }
}
