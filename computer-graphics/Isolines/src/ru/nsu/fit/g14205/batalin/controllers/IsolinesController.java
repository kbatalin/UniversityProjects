package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.models.painters.ColorMapPainter;
import ru.nsu.fit.g14205.batalin.models.painters.InterpolationPainter;
import ru.nsu.fit.g14205.batalin.views.*;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private PropertiesModel applicationProperties;
    private int dynamicIsolineIndex = -1;
    private JFileChooser fileOpenChooser;

    private IsolinesView isolinesView;

    public void run() {
        fileOpenChooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "FIT_14205_Batalin_Kirill_Isolines_Data");
        fileOpenChooser.setCurrentDirectory(workingDirectory);
        FileNameExtensionFilter settingsFileFilter = new FileNameExtensionFilter("Text (*.txt)", "txt");
        fileOpenChooser.setFileFilter(settingsFileFilter);

        applicationProperties = ApplicationProperties.createDefault();

        isolinesView = new IsolinesView(this);
    }

    public void onNewButtonClicked(ActionEvent actionEvent) {
        applicationProperties.initDefault();
    }

    public void onExitButtonClicked(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void onSettingsButtonClicked(ActionEvent actionEvent) {
        SettingsView dialog = new SettingsView(this);
        dialog.pack();
        dialog.setLocationRelativeTo(isolinesView);
        dialog.setVisible(true);

        if (!dialog.getResult()) {
            return;
        }

        Area area = new Area(dialog.getA(), dialog.getC(), dialog.getB(), dialog.getD());
        applicationProperties.setArea(area);
        applicationProperties.setVerticalCellsCount(dialog.getCols());
        applicationProperties.setHorizontalCellsCount(dialog.getRows());
    }

    public void onAboutDialogClicked(ActionEvent actionEvent) {
        AboutView dialog = new AboutView();
        dialog.pack();
        dialog.setLocationRelativeTo(isolinesView);
        dialog.setVisible(true);
    }

    public void onOpenButtonClicked(ActionEvent actionEvent) {
        int result = fileOpenChooser.showOpenDialog(isolinesView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = fileOpenChooser.getSelectedFile();
            applicationProperties.load(file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(isolinesView,"Can't load config: " + e.getMessage(),"Open error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        showFunctionOnToolbar(pos);
    }

    private void showFunctionOnToolbar(Point2D pos) {
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesView.getStatusBarView().setMessage(String.format("F(%.1f, %.1f) = %.1f", pos.getX(), pos.getY(), f));
    }

    private Point2D pixel2Area(Point pos) {
        Dimension mapSize = isolinesView.getWorkspaceView().getFunctionMapView().getSize();
        Area area = applicationProperties.getArea();
        Dimension areaSize = area.toDimension();
        double x = pos.x / mapSize.getWidth() * areaSize.width + area.first.getX();
        double y = (mapSize.getHeight() - 1 - pos.y) / mapSize.getHeight() * areaSize.height + area.first.getY();
        return new Point2D.Double(x, y);
    }

    public void onClearIsolinesButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setIsolinesValues(new ArrayList<>());
    }

    public void onShowEntryPointsButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setEntryPointsShown(button.isSelected());
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        showFunctionOnToolbar(pos);
        if (!applicationProperties.isDynamicIsolines()) {
            return;
        }

        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        if(dynamicIsolineIndex != -1) {
            isolinesValues.remove(dynamicIsolineIndex);
            dynamicIsolineIndex = -1;
        }
        isolinesValues.add(f);
        dynamicIsolineIndex = isolinesValues.size() - 1;
        dynamicIsolineIndex = isolinesValues.size() - 1;
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        if (!applicationProperties.isDynamicIsolines()) {
            return;
        }

        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesValues.add(f);
        dynamicIsolineIndex = isolinesValues.size() - 1;
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();

        if (applicationProperties.isDynamicIsolines() && dynamicIsolineIndex != -1) {
            isolinesValues.remove(dynamicIsolineIndex);
            applicationProperties.setIsolinesValues(isolinesValues);
            dynamicIsolineIndex = -1;
        }

        if (!applicationProperties.isCreatingIsolines()) {
            return;
        }

        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesValues.add(f);
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onCreateIsolineButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setCreatingIsolines(button.isSelected());
    }

    public void onDynamicIsolineButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setDynamicIsolines(button.isSelected());
    }

    public void onIsolinesShowButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setIsolinesShown(button.isSelected());
    }

    public void onGridButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setGridShown(button.isSelected());
    }

    public void onInterpolationButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setPainter(new InterpolationPainter());
    }

    public void onColorMapButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setPainter(new ColorMapPainter());
    }

    public PropertiesModel getApplicationProperties() {
        return applicationProperties;
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
