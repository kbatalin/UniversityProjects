package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.IPropertiesModel;
import ru.nsu.fit.g14205.batalin.models.PaintMode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by kir55rus on 08.03.17.
 */
public class PropertiesView extends JFrame {
    private LifeController lifeController;

    private JRadioButton replaceRadio;
    private JRadioButton xorRadio;

    private JTextField widthText;
    private JSlider widthSlider;
    private JTextField heightText;
    private JSlider heightSlider;

    private JTextField hexSizeText;
    private JSlider hexSizeSlider;
    private JTextField lineThicknessText;
    private JSlider lineThicknessSlider;

    private JTextField firstImpactText;
    private JTextField secondImpactText;
    private JTextField liveBeginText;
    private JTextField liveEndText;
    private JTextField birthBeginText;
    private JTextField birthEndText;

    public PropertiesView(LifeController lifeController, IPropertiesModel propertiesModel) {
        this.lifeController = lifeController;

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(content);

        JPanel first = new JPanel();
        first.setLayout(new BoxLayout(first, BoxLayout.X_AXIS));
        content.add(first);

        initPaintMode(propertiesModel, first);

        initFieldSize(propertiesModel, first);

        initCellProperties(propertiesModel, first);


        JPanel second = new JPanel();
        second.setLayout(new BoxLayout(second, BoxLayout.X_AXIS));
        content.add(second);

        initGameProperties(propertiesModel, second);

        initButtonsPanel(second);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                lifeController.onPropertiesDialogClosing();
            }
        });

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void initButtonsPanel(JPanel parent) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(7,5,5,5));
        parent.add(buttonsPanel);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(actionEvent -> {
            setVisible(false);
            lifeController.onPropertiesDialogOkButtonClicked();
        });
        buttonsPanel.add(okButton);

        buttonsPanel.add(Box.createVerticalStrut(5));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(actionEvent -> {
            setVisible(false);
            lifeController.onPropertiesDialogCancelButtonClicked();
        });
        buttonsPanel.add(cancelButton);
    }

    private void initGameProperties(IPropertiesModel propertiesModel, JPanel parent) {
        JPanel gameProperties = new JPanel();
        gameProperties.setLayout(new BoxLayout(gameProperties, BoxLayout.X_AXIS));
        gameProperties.setBorder(BorderFactory.createTitledBorder("Game properties:"));
        parent.add(gameProperties);

        JPanel firstImpactPanel = new JPanel();
        firstImpactPanel.setLayout(new BoxLayout(firstImpactPanel, BoxLayout.Y_AXIS));
        gameProperties.add(firstImpactPanel);

        firstImpactPanel.add(new JLabel("FST_IMPACT:"));
        firstImpactPanel.add(Box.createVerticalStrut(5));
        firstImpactText = new JTextField(3);
        firstImpactText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogFstImpactTextFocusLost();
            }
        });
        firstImpactText.setText(String.valueOf(propertiesModel.getFirstImpact()));
        firstImpactPanel.add(firstImpactText);

        gameProperties.add(Box.createHorizontalStrut(7));

        JPanel secondImpactPanel = new JPanel();
        secondImpactPanel.setLayout(new BoxLayout(secondImpactPanel, BoxLayout.Y_AXIS));
        gameProperties.add(secondImpactPanel);


        secondImpactPanel.add(new JLabel("SND_IMPACT:"));
        secondImpactPanel.add(Box.createVerticalStrut(5));
        secondImpactText = new JTextField(3);
        secondImpactText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogSndImpactTextFocusLost();
            }
        });
        secondImpactText.setText(String.valueOf(propertiesModel.getSecondImpact()));
        secondImpactPanel.add(secondImpactText);

        gameProperties.add(Box.createHorizontalStrut(7));

        JPanel liveBeginPanel = new JPanel();
        liveBeginPanel.setLayout(new BoxLayout(liveBeginPanel, BoxLayout.Y_AXIS));
        gameProperties.add(liveBeginPanel);

        liveBeginPanel.add(new JLabel("LIVE_BEGIN:"));
        liveBeginPanel.add(Box.createVerticalStrut(5));
        liveBeginText = new JTextField(3);
        liveBeginText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogLiveBeginTextFocusLost();
            }
        });
        liveBeginText.setText(String.valueOf(propertiesModel.getLiveBegin()));
        liveBeginPanel.add(liveBeginText);

        gameProperties.add(Box.createHorizontalStrut(7));

        JPanel liveEndPanel = new JPanel();
        liveEndPanel.setLayout(new BoxLayout(liveEndPanel, BoxLayout.Y_AXIS));
        gameProperties.add(liveEndPanel);

        liveEndPanel.add(new JLabel("LIVE_END:"));
        liveEndPanel.add(Box.createVerticalStrut(5));
        liveEndText = new JTextField(3);
        liveEndText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogLiveEndTextFocusLost();
            }
        });
        liveEndText.setText(String.valueOf(propertiesModel.getLiveEnd()));
        liveEndPanel.add(liveEndText);

        gameProperties.add(Box.createHorizontalStrut(7));

        JPanel birthBeginPanel = new JPanel();
        birthBeginPanel.setLayout(new BoxLayout(birthBeginPanel, BoxLayout.Y_AXIS));
        gameProperties.add(birthBeginPanel);

        birthBeginPanel.add(new JLabel("BIRTH_BEGIN:"));
        birthBeginPanel.add(Box.createVerticalStrut(5));
        birthBeginText = new JTextField(3);
        birthBeginText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogBirthBeginTextFocusLost();
            }
        });
        birthBeginText.setText(String.valueOf(propertiesModel.getBirthBegin()));
        birthBeginPanel.add(birthBeginText);

        gameProperties.add(Box.createHorizontalStrut(7));

        JPanel birthEndPanel = new JPanel();
        birthEndPanel.setLayout(new BoxLayout(birthEndPanel, BoxLayout.Y_AXIS));
        gameProperties.add(birthEndPanel);

        birthEndPanel.add(new JLabel("BIRTH_END:"));
        birthEndPanel.add(Box.createVerticalStrut(5));
        birthEndText = new JTextField(3);
        birthEndText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogBirthEndTextFocusLost();
            }
        });
        birthEndText.setText(String.valueOf(propertiesModel.getBirthEnd()));
        birthEndPanel.add(birthEndText);
    }

    public JTextField getFirstImpactText() {
        return firstImpactText;
    }

    public JTextField getSecondImpactText() {
        return secondImpactText;
    }

    public JTextField getLiveBeginText() {
        return liveBeginText;
    }

    public JTextField getLiveEndText() {
        return liveEndText;
    }

    public JTextField getBirthBeginText() {
        return birthBeginText;
    }

    public JTextField getBirthEndText() {
        return birthEndText;
    }

    private void initCellProperties(IPropertiesModel propertiesModel, JPanel parent) {
        JPanel cellProperties = new JPanel();
        cellProperties.setLayout(new BoxLayout(cellProperties, BoxLayout.Y_AXIS));
        cellProperties.setBorder(BorderFactory.createTitledBorder("Cell properties"));
        parent.add(cellProperties);

        JPanel hexSizeLine = new JPanel();
        hexSizeLine.setLayout(new BoxLayout(hexSizeLine, BoxLayout.X_AXIS));
        cellProperties.add(hexSizeLine);

        int minHexSize = propertiesModel.getMinHexSize();
        int maxHexSize = propertiesModel.getMaxHexSize();
        int currentHexSize = propertiesModel.getHexSize();
        JLabel hexSizeLabel = new JLabel("Hex size:");
        hexSizeLine.add(hexSizeLabel);
        hexSizeLine.add(Box.createHorizontalStrut(7));
        hexSizeText = new JTextField(5);
        hexSizeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogHexSizeTextFocusLost();
            }
        });
        hexSizeText.setText(String.valueOf(currentHexSize));
        hexSizeLine.add(hexSizeText);
        hexSizeLine.add(Box.createHorizontalStrut(7));
        hexSizeSlider = new JSlider(SwingConstants.HORIZONTAL, minHexSize, maxHexSize, currentHexSize);
        hexSizeSlider.addChangeListener(changeEvent -> lifeController.onPropertiesDialogHexSizeSliderChanged());
        hexSizeLine.add(hexSizeSlider);

        cellProperties.add(Box.createVerticalStrut(5));

        JPanel lineThicknessLine = new JPanel();
        lineThicknessLine.setLayout(new BoxLayout(lineThicknessLine, BoxLayout.X_AXIS));
        cellProperties.add(lineThicknessLine);

        int minLineThickness = propertiesModel.getMinLineThickness();
        int maxLineThickness = propertiesModel.getMaxLineThickness();
        int currentLineThickness = propertiesModel.getLineThickness();
        lineThicknessLine.add(new JLabel("Line thickness:"));
        lineThicknessLine.add(Box.createHorizontalStrut(7));
        lineThicknessText = new JTextField(5);
        lineThicknessText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogLineThicknessTextFocusLost();
            }
        });
        lineThicknessText.setText(String.valueOf(currentLineThickness));
        lineThicknessLine.add(lineThicknessText);
        lineThicknessLine.add(Box.createHorizontalStrut(7));
        lineThicknessSlider = new JSlider(SwingConstants.HORIZONTAL, minLineThickness, maxLineThickness, currentLineThickness);
        lineThicknessSlider.addChangeListener(changeEvent -> lifeController.onPropertiesDialogLineThicknessSliderChanged());
        lineThicknessLine.add(lineThicknessSlider);
    }

    public void setHexSize(int hexSize) {
        hexSizeText.setText(String.valueOf(hexSize));
        hexSizeSlider.setValue(hexSize);
    }

    public JTextField getHexSizeText() {
        return hexSizeText;
    }

    public JSlider getHexSizeSlider() {
        return hexSizeSlider;
    }

    public void setLineThickness(int lineThickness) {
        lineThicknessText.setText(String.valueOf(lineThickness));
        lineThicknessSlider.setValue(lineThickness);
    }

    public JTextField getLineThicknessText() {
        return lineThicknessText;
    }

    public JSlider getLineThicknessSlider() {
        return lineThicknessSlider;
    }

    private void initFieldSize(IPropertiesModel propertiesModel, JPanel parent) {
        JPanel sizeProperties = new JPanel();
        sizeProperties.setLayout(new BoxLayout(sizeProperties, BoxLayout.Y_AXIS));
        Border sizeGroupBorder = BorderFactory.createTitledBorder("Field size");
        sizeProperties.setBorder(sizeGroupBorder);
        parent.add(sizeProperties);

        Dimension minFieldSize = propertiesModel.getMinFieldSize();
        Dimension maxFieldSize = propertiesModel.getMaxFieldSize();
        Dimension currentFieldSize = propertiesModel.getFieldSize();

        JPanel widthLine = new JPanel();
        widthLine.setLayout(new BoxLayout(widthLine, BoxLayout.X_AXIS));
        sizeProperties.add(widthLine);

        JLabel widthLabel = new JLabel("Width:");
        widthLine.add(widthLabel);
        widthLine.add(Box.createHorizontalStrut(7));
        widthText = new JTextField(5);
        widthText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogWidthTextFocusLost();
            }
        });
        widthText.setText(String.valueOf(currentFieldSize.width));
        widthLine.add(widthText);
        widthLine.add(Box.createHorizontalStrut(7));
        widthSlider = new JSlider(SwingConstants.HORIZONTAL, minFieldSize.width, maxFieldSize.width, currentFieldSize.width);
        widthSlider.addChangeListener(changeEvent -> lifeController.onPropertiesDialogWidthSliderChanged());
        widthLine.add(widthSlider);

        sizeProperties.add(Box.createVerticalStrut(5));

        JPanel heightLine = new JPanel();
        heightLine.setLayout(new BoxLayout(heightLine, BoxLayout.X_AXIS));
        sizeProperties.add(heightLine);

        heightLine.add(new JLabel("Height:"));
        heightLine.add(Box.createHorizontalStrut(7));
        heightText = new JTextField(5);
        heightText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lifeController.onPropertiesDialogHeightTextFocusLost();
            }
        });
        heightText.setText(String.valueOf(currentFieldSize.height));
        heightLine.add(heightText);
        heightLine.add(Box.createHorizontalStrut(7));
        heightSlider = new JSlider(SwingConstants.HORIZONTAL, minFieldSize.height, maxFieldSize.height, currentFieldSize.height);
        heightSlider.addChangeListener(changeEvent -> lifeController.onPropertiesDialogHeightSliderChanged());
        heightLine.add(heightSlider);
    }

    public void setWidth(int width) {
        widthText.setText(String.valueOf(width));
        widthSlider.setValue(width);
    }

    public JTextField getWidthText() {
        return widthText;
    }

    public JSlider getWidthSlider() {
        return widthSlider;
    }

    public void setHeight(int height) {
        heightText.setText(String.valueOf(height));
        heightSlider.setValue(height);
    }

    public JTextField getHeightText() {
        return heightText;
    }

    public JSlider getHeightSlider() {
        return heightSlider;
    }

    private void initPaintMode(IPropertiesModel propertiesModel, JPanel parent) {
        JPanel modeProperties = new JPanel();
        modeProperties.setLayout(new BoxLayout(modeProperties, BoxLayout.Y_AXIS));
        Border modeGroupBorder = BorderFactory.createTitledBorder("Mode");
        modeProperties.setBorder(modeGroupBorder);
        parent.add(modeProperties);

        ButtonGroup modeGroup = new ButtonGroup();
        replaceRadio = new JRadioButton("Replace");
        modeGroup.add(replaceRadio);
        replaceRadio.setSelected(propertiesModel.getPaintMode() == PaintMode.REPLACE);
        modeProperties.add(replaceRadio);

        modeProperties.add(Box.createVerticalStrut(5));

        xorRadio = new JRadioButton("XOR");
        modeGroup.add(xorRadio);
        xorRadio.setSelected(propertiesModel.getPaintMode() == PaintMode.XOR);
        modeProperties.add(xorRadio);
    }

    public PaintMode getPaintMode() {
        return replaceRadio.isSelected() ? PaintMode.REPLACE : PaintMode.XOR;
    }
}
