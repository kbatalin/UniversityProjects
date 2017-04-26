package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.EditorController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
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
    private JSpinner zfSpinner;
    private JSpinner mSpinner;
    private JSpinner bSpinner;
    private JSpinner zbSpinner;
    private JSpinner kSpinner;
    private JSpinner cSpinner;
    private JSpinner swSpinner;
    private JSpinner numberSpinner;
    private JSpinner dSpinner;
    private JSpinner shSpinner;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;
    private JSlider zoomSlider;
    private JButton addButton;
    private JButton deleteButton;
    private JSpinner cXSpinner;
    private JSpinner alphaSpinner;
    private JSpinner cYSpinner;
    private JSpinner betaSpinner;
    private JSpinner cZSpinner;
    private JSpinner thetaSpinner;
    private JCheckBox showRisksCheckBox;
    private JCheckBox showControlPointsCheckBox;
    private boolean result;

    public EditorDialog(EditorController editorController) {
        this.editorController = editorController;

        result = false;

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

        applyButton.addActionListener(actionEvent -> editorController.onApplyButtonClicked());

        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
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

        Grid grid = applicationProperties.getGrid();
        SpinnerNumberModel nSpinnerModel = new SpinnerNumberModel(grid.getCols(), 1, 50, 1);
        nSpinner.setModel(nSpinnerModel);
        nSpinner.addChangeListener(changeEvent -> editorController.onNSpinnerChanged(nSpinnerModel.getNumber().intValue()));
        SpinnerNumberModel mSpinnerModel = new SpinnerNumberModel(grid.getRows(), 1, 50, 1);
        mSpinner.setModel(mSpinnerModel);
        mSpinner.addChangeListener(changeEvent -> editorController.onMSpinnerChanged(mSpinnerModel.getNumber().intValue()));
        SpinnerNumberModel kSpinnerModel = new SpinnerNumberModel(grid.getSegmentSplitting(), 1, 50, 1);
        kSpinner.setModel(kSpinnerModel);
        kSpinner.addChangeListener(changeEvent -> editorController.onKSpinnerChanged(kSpinnerModel.getNumber().intValue()));

        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        SpinnerNumberModel zfSpinnerModel = new SpinnerNumberModel(viewPyramid.getFrontPlaneDistance(), 1, 50, 1);
        zfSpinner.setModel(zfSpinnerModel);
        zfSpinner.addChangeListener(changeEvent -> editorController.onZfSpinnerChanged(zfSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel zbSpinnerModel = new SpinnerNumberModel(viewPyramid.getBackPlaneDistance(), 1, 50, 1);
        zbSpinner.setModel(zbSpinnerModel);
        zbSpinner.addChangeListener(changeEvent -> editorController.onZbSpinnerChanged(zbSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel swSpinnerModel = new SpinnerNumberModel(viewPyramid.getFrontPlaneWidth(), 1, 20, 1);
        swSpinner.setModel(swSpinnerModel);
        swSpinner.addChangeListener(changeEvent -> editorController.onSwSpinnerChanged(swSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel shSpinnerModel = new SpinnerNumberModel(viewPyramid.getFrontPlaneHeight(), 1, 20, 1);
        shSpinner.setModel(shSpinnerModel);
        shSpinner.addChangeListener(changeEvent -> editorController.onShSpinnerChanged(shSpinnerModel.getNumber().doubleValue()));

        zoomSlider.setMinimum(editorModel.getMinZoom());
        zoomSlider.setMaximum(editorModel.getMaxZoom());
        zoomSlider.setValue(editorModel.getZoom());
        zoomSlider.addChangeListener(changeEvent -> editorController.onZoomSliderChanged(zoomSlider.getValue()));

        addButton.addActionListener(actionEvent -> editorController.onAddButtonClicked());
        deleteButton.addActionListener(actionEvent -> editorController.onDeleteButtonClicked());

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, this::onLineChanged);

        updNumberSpinner();
        applicationProperties.addObserver(ApplicationProperties.Event.FIGURE_PROPERTIES_ADDED, this::updNumberSpinner);
        applicationProperties.addObserver(ApplicationProperties.Event.FIGURE_PROPERTIES_REMOVED, this::updNumberSpinner);
        numberSpinner.addChangeListener(changeEvent -> editorController.onNumberSpinnerChanged(((Number)numberSpinner.getValue()).intValue()));

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, this::updColor);

        initAreaSpinners();
        initCoordinateSystemSpinners();
    }

    private void onLineChanged() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();

        numberSpinner.setValue(editorModel.getCurrentFigure());

        System.out.println(editorModel.getCurrentFigure());

        Point3D center = coordinateSystem.getCenter();
        cXSpinner.setValue(center.getX());
        cYSpinner.setValue(center.getY());
        cZSpinner.setValue(center.getZ());

        alphaSpinner.setValue(coordinateSystem.getAlphaAngle() * 180 / Math.PI);
        betaSpinner.setValue(coordinateSystem.getBetaAngle() * 180 / Math.PI);
        thetaSpinner.setValue(coordinateSystem.getThetaAngle() * 180 / Math.PI);
    }

    private void initCoordinateSystemSpinners() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();

        Point3D center = coordinateSystem.getCenter();

        double minCrd = -200;
        double maxCrd = 200;
        SpinnerNumberModel cXSpinnerModel = new SpinnerNumberModel(center.getX(), minCrd, maxCrd, 1);
        cXSpinner.setModel(cXSpinnerModel);
        cXSpinner.addChangeListener(changeEvent -> editorController.onCXSpinnerChanged(cXSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel cYSpinnerModel = new SpinnerNumberModel(center.getY(), minCrd, maxCrd, 1);
        cYSpinner.setModel(cYSpinnerModel);
        cYSpinner.addChangeListener(changeEvent -> editorController.onCYSpinnerChanged(cYSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel cZSpinnerModel = new SpinnerNumberModel(center.getZ(), minCrd, maxCrd, 1);
        cZSpinner.setModel(cZSpinnerModel);
        cZSpinner.addChangeListener(changeEvent -> editorController.onCZSpinnerChanged(cZSpinnerModel.getNumber().doubleValue()));


        SpinnerNumberModel alphaSpinnerModel = new SpinnerNumberModel(coordinateSystem.getAlphaAngle() * 180 / Math.PI, 0., 360., 0.5);
        alphaSpinner.setModel(alphaSpinnerModel);
        alphaSpinner.addChangeListener(changeEvent -> editorController.onAlphaSpinnerChanged(alphaSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel betaSpinnerModel = new SpinnerNumberModel(coordinateSystem.getBetaAngle() * 180 / Math.PI, 0., 360., 0.5);
        betaSpinner.setModel(betaSpinnerModel);
        betaSpinner.addChangeListener(changeEvent -> editorController.onBetaSpinnerChanged(betaSpinnerModel.getNumber().doubleValue()));

        SpinnerNumberModel thetaSpinnerModel = new SpinnerNumberModel(coordinateSystem.getThetaAngle() * 180 / Math.PI, 0., 360., 0.5);
        thetaSpinner.setModel(thetaSpinnerModel);
        thetaSpinner.addChangeListener(changeEvent -> editorController.onThetaSpinnerChanged(thetaSpinnerModel.getNumber().doubleValue()));

        showRisksCheckBox.addActionListener(editorController::onShowRisksCheckBoxChanged);
        showControlPointsCheckBox.addActionListener(editorController::onShowControlPointsCheckBoxChanged);
    }

    private void initAreaSpinners() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        Area area = applicationProperties.getArea();
        SpinnerNumberModel aSpinnerModel = new SpinnerNumberModel(area.first.getX(), 0., 1., 0.01);
        aSpinner.setModel(aSpinnerModel);
        aSpinner.addChangeListener(changeEvent -> {
            Area currentArea = applicationProperties.getArea();
            double oldValue = currentArea.first.getX();
            double newValue = ((Number) aSpinner.getValue()).doubleValue();
            double bValue = ((Number) bSpinner.getValue()).doubleValue();
            if(Double.compare(oldValue, newValue) >= 0) {
                applicationProperties.setArea(new Area(newValue, currentArea.first.getY(), currentArea.second.getX(), currentArea.second.getY()));
                return;
            }
            if (Double.compare(newValue, bValue) > 0) {
                bSpinner.setValue(newValue);
                bValue = newValue;
            }

            applicationProperties.setArea(new Area(newValue, currentArea.first.getY(), bValue, currentArea.second.getY()));
        });

        SpinnerNumberModel bSpinnerModel = new SpinnerNumberModel(area.second.getX(), 0., 1., 0.01);
        bSpinner.setModel(bSpinnerModel);
        bSpinner.addChangeListener(changeEvent -> {
            Area currentArea = applicationProperties.getArea();
            double oldValue = currentArea.second.getX();
            double newValue = ((Number) bSpinner.getValue()).doubleValue();
            double aValue = ((Number) aSpinner.getValue()).doubleValue();
            if(Double.compare(oldValue, newValue) <= 0) {
                applicationProperties.setArea(new Area(currentArea.first.getX(), currentArea.first.getY(), newValue, currentArea.second.getY()));
                return;
            }
            if (Double.compare(newValue, aValue) < 0) {
                aSpinner.setValue(newValue);
                aValue = newValue;
            }

            applicationProperties.setArea(new Area(aValue, currentArea.first.getY(), newValue, currentArea.second.getY()));
        });

        SpinnerNumberModel cSpinnerModel = new SpinnerNumberModel(area.first.getY(), 0., 2 * Math.PI, 0.01);
        cSpinner.setModel(cSpinnerModel);
        cSpinner.addChangeListener(changeEvent -> {
            Area currentArea = applicationProperties.getArea();
            double oldValue = currentArea.first.getY();
            double newValue = ((Number) cSpinner.getValue()).doubleValue();
            double dValue = ((Number) dSpinner.getValue()).doubleValue();
            if(Double.compare(oldValue, newValue) >= 0) {
                applicationProperties.setArea(new Area(currentArea.first.getX(), newValue, currentArea.second.getX(), currentArea.second.getY()));
                return;
            }
            if (Double.compare(newValue, dValue) > 0) {
                dSpinner.setValue(newValue);
                dValue = newValue;
            }

            applicationProperties.setArea(new Area(currentArea.first.getX(), newValue, currentArea.second.getX(), dValue));
        });

        SpinnerNumberModel dSpinnerModel = new SpinnerNumberModel(area.second.getY(), 0., 2 * Math.PI, 0.01);
        dSpinner.setModel(dSpinnerModel);
        dSpinner.addChangeListener(changeEvent -> {
            Area currentArea = applicationProperties.getArea();
            double oldValue = currentArea.second.getY();
            double newValue = ((Number) dSpinner.getValue()).doubleValue();
            double cValue = ((Number) cSpinner.getValue()).doubleValue();
            if(Double.compare(oldValue, newValue) <= 0) {
                applicationProperties.setArea(new Area(currentArea.first.getX(), currentArea.first.getY(), currentArea.second.getX(), newValue));
                return;
            }
            if (Double.compare(newValue, cValue) < 0) {
                cSpinner.setValue(newValue);
                cValue = newValue;
            }

            applicationProperties.setArea(new Area(currentArea.first.getX(), cValue, currentArea.second.getX(), newValue));
        });
    }

    private void updColor() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Color color = lineProperties.getColor();
        redSpinner.setValue(color.getRed());
        greenSpinner.setValue(color.getGreen());
        blueSpinner.setValue(color.getBlue());
    }

    private void updNumberSpinner() {
        ApplicationProperties applicationProperties = editorController.getApplicationProperties();
        EditorModel editorModel = editorController.getEditorModel();
        int maxValue = Math.max(0, applicationProperties.getFigurePropertiesCount() - 1);
        SpinnerNumberModel numberSpinnerModel = new SpinnerNumberModel(editorModel.getCurrentFigure(), 0, maxValue, 1);
        numberSpinner.setModel(numberSpinnerModel);
    }

    private void onOK() {
        result = true;
        dispose();
    }

    private void onCancel() {
        result = false;
        dispose();
    }

    public boolean getResult() {
        return result;
    }

    public LineEditorContentView getLineEditorContentView() {
        return (LineEditorContentView) content;
    }

    private void createUIComponents() {
        content = new LineEditorContentView(editorController);
    }
}
