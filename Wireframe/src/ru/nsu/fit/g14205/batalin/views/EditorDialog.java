package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.EditorController;
import ru.nsu.fit.g14205.batalin.controllers.WireframeController;
import ru.nsu.fit.g14205.batalin.models.ApplicationProperties;
import ru.nsu.fit.g14205.batalin.models.EditorModel;
import ru.nsu.fit.g14205.batalin.models.LineProperties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class EditorDialog extends JDialog {
    private EditorController editorController;
    private JPanel contentPane;
//    private JButton buttonOK;
//    private JButton buttonCancel;
    private JPanel content;
    private JButton OKButton;
    private JButton applyButton;
    private JButton zoomPlusButton;
    private JSpinner nSpinner;
    private JSpinner aSpinner;
    private JSpinner znSpinner;
    private JSpinner mSpinner;
    private JSpinner bSpinner;
    private JSpinner zfSpinner;
    private JSpinner kSpinner;
    private JSpinner cSpinner;
    private JSpinner sqSpinner;
    private JSpinner numberSpinner;
    private JSpinner dSpinner;
    private JSpinner shSpinner;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;
    private JSlider zoomSlider;
    private JButton addButton;
    private JButton deleteButton;

    public EditorDialog(EditorController editorController) {
        this.editorController = editorController;

        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setTitle("Line editor");
        getRootPane().setDefaultButton(OKButton);

        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        content.setMinimumSize(new Dimension(600, 400));
        content.setSize(600, 400);

        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();

        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
        Color color = lineProperties.getColor();
        SpinnerNumberModel redSpinnerModel = new SpinnerNumberModel(color.getRed(), 0, 255, 1);
        redSpinner.setModel(redSpinnerModel);
        redSpinner.addChangeListener(changeEvent -> editorController.onRedSpinnerChanged(redSpinnerModel.getNumber().intValue()));
        SpinnerNumberModel greenSpinnerModel = new SpinnerNumberModel(color.getGreen(), 0, 255, 1);
        greenSpinner.setModel(greenSpinnerModel);
        greenSpinner.addChangeListener(changeEvent -> editorController.onGreenSpinnerChanged(greenSpinnerModel.getNumber().intValue()));
        SpinnerNumberModel blueSpinnerModel = new SpinnerNumberModel(color.getBlue(), 0, 255, 1);
        blueSpinner.setModel(blueSpinnerModel);
        blueSpinner.addChangeListener(changeEvent -> editorController.onBlueSpinnerChanged(blueSpinnerModel.getNumber().intValue()));

        zoomSlider.setMinimum(editorModel.getMinZoom());
        zoomSlider.setMaximum(editorModel.getMaxZoom());
        zoomSlider.setValue(editorModel.getZoom());
        zoomSlider.addChangeListener(changeEvent -> editorController.onZoomSliderChanged(zoomSlider.getValue()));

        addButton.addActionListener(actionEvent -> editorController.onAddButtonClicked());
        deleteButton.addActionListener(actionEvent -> editorController.onDeleteButtonClicked());

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, () -> {
            numberSpinner.setValue(editorModel.getCurrentLine());
        });

        updNumberSpinner();
        applicationProperties.addObserver(ApplicationProperties.Event.LINE_PROPERTIES_ADDED, this::updNumberSpinner);
        applicationProperties.addObserver(ApplicationProperties.Event.LINE_PROPERTIES_REMOVED, this::updNumberSpinner);
        numberSpinner.addChangeListener(changeEvent -> editorController.onNumberSpinnerChanged(((Number)numberSpinner.getValue()).intValue()));
    }

    private void updNumberSpinner() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();
        int maxValue = Math.max(0, applicationProperties.getLinePropertiesCount() - 1);
        SpinnerNumberModel numberSpinnerModel = new SpinnerNumberModel(editorModel.getCurrentLine(), 0, maxValue, 1);
        numberSpinner.setModel(numberSpinnerModel);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public LineEditorContentView getLineEditorContentView() {
        return (LineEditorContentView) content;
    }

    private void createUIComponents() {
        content = new LineEditorContentView(editorController);
    }
}
