package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.AboutView;
import ru.nsu.fit.g14205.batalin.views.LifeView;
import ru.nsu.fit.g14205.batalin.views.NewFieldView;
import ru.nsu.fit.g14205.batalin.views.PropertiesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Timer;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private JDialog aboutDialog;
    private JDialog newFieldDialog;
    private JDialog saveOnCloseDialog;
    private JDialog propertiesDialog;
    private PropertiesView propertiesView;
    private IPropertiesModel propertiesModel;
    private IFieldModel fieldModel;
    private Point prevCell;
    private Timer timer;
    private JFileChooser fileChooser;
    private boolean isSaved;

    public void run() {
        fileChooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "FIT_14205_Batalin_Kirill_Life_Data");
        fileChooser.setCurrentDirectory(workingDirectory);

        createDefaultField();
    }

    private void createDefaultField() {
        reset();
        propertiesModel = PropertiesModel.createDefault();
        fieldModel = new FieldModel(propertiesModel);
        lifeView = new LifeView(this, fieldModel, propertiesModel);
        fieldModel.addObserver(FieldModelEvent.CELL_STATE_CHANGED, () -> setSaved(false));

        SwingUtilities.invokeLater(() -> {
            lifeView.setLocationRelativeTo(null);
            lifeView.setVisible(true);
        });

    }

    public void onPropertiesButtonClicked() {
        if (propertiesDialog != null) {
            return;
        }

        propertiesView = new PropertiesView(this, propertiesModel);
        propertiesView.setLocationRelativeTo(lifeView);
        propertiesDialog = new JDialog(propertiesView, "Properties", Dialog.ModalityType.DOCUMENT_MODAL);
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        lifeView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        lifeView.getStatusBarView().setMessage("");
    }

    public void onCloseButtonClicked() {
        if (isSaved) {
            System.exit(0);
            return;
        }

        JOptionPane optionPane = new JOptionPane(
                "The field is not saved. Do you want to save?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);

        saveOnCloseDialog = new JDialog(lifeView,"Save...",true);
        saveOnCloseDialog.setContentPane(optionPane);

        optionPane.addPropertyChangeListener(e -> {
            String prop = e.getPropertyName();

            if (saveOnCloseDialog.isVisible()
                    && (e.getSource() == optionPane)
                    && (JOptionPane.VALUE_PROPERTY.equals(prop))) {
                saveOnCloseDialog.setVisible(false);
            }
        });
        saveOnCloseDialog.pack();
        saveOnCloseDialog.setLocationRelativeTo(lifeView);
        saveOnCloseDialog.setVisible(true);

        Object valueObj = optionPane.getValue();
        if (!(valueObj instanceof Number)) {
            return;
        }

        int value = ((Number)valueObj).intValue();
        if (value != JOptionPane.YES_OPTION) {
            System.exit(0);
            return;
        }

        if(save()) {
            System.exit(0);
        }
    }

    private void setSaved(boolean isSaved) {
        this.isSaved = isSaved;

        SwingUtilities.invokeLater(() -> {
            if(lifeView != null) {
                lifeView.setTitle("Life" + (isSaved ? "" : " *"));
            }
        });
    }

    public void onOpenButtonClicked() {
        int result = fileChooser.showOpenDialog(lifeView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try {
            ILoader loader = new FileLoader(file.toPath());
            loader.load();

            reset();

            propertiesModel = loader.getPropertiesModel();
            fieldModel = loader.getFieldModel();
            fieldModel.addObserver(FieldModelEvent.CELL_STATE_CHANGED, () -> setSaved(false));

            lifeView = new LifeView(this, fieldModel, propertiesModel);
            lifeView.setLocationRelativeTo(null);
            lifeView.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(lifeView,"Can't load field: " + e.getMessage(),"Open error", JOptionPane.ERROR_MESSAGE);
        }

        setSaved(true);
    }

    public void onSaveAsButtonClicked() {
        saveAs();
    }

    private boolean saveAs() {
        return chooseSaveFile() && save();
    }

    public void onSaveButtonClicked() {
        save();
    }

    private boolean chooseSaveFile() {
        int result = fileChooser.showSaveDialog(lifeView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = fileChooser.getSelectedFile();
        propertiesModel.setSavePath(file.toPath());
        return true;
    }

    private boolean save() {
        if (propertiesModel.getSavePath() == null && !chooseSaveFile()) {
            return false;
        }

        try {
            ISaver saver = new FileSaver(propertiesModel, fieldModel);
            saver.save();
            setSaved(true);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(lifeView,"Can't save field","Save error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void reset() {
        setSaved(true);

        if (aboutDialog != null) {
            Window window = SwingUtilities.getWindowAncestor(aboutDialog);
            window.dispose();
            aboutDialog = null;
        }

        if (propertiesDialog != null) {
            Window window = SwingUtilities.getWindowAncestor(propertiesDialog);
            window.dispose();
            propertiesDialog = null;
        }

        if (newFieldDialog != null) {
            Window window = SwingUtilities.getWindowAncestor(newFieldDialog);
            window.dispose();
            newFieldDialog = null;
        }

        if (lifeView != null) {
            lifeView.setVisible(false);
            lifeView = null;
        }

        fieldModel = null;
        propertiesModel = null;
        prevCell = null;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (fileChooser != null) {
            fileChooser.cancelSelection();
        }
    }

    public void onCreateNewFieldDialogOk(Dimension size) {
        reset();

        propertiesModel = PropertiesModel.createDefault();
        propertiesModel.setFieldSize(size);

        fieldModel = new FieldModel(propertiesModel);
        fieldModel.addObserver(FieldModelEvent.CELL_STATE_CHANGED, () -> setSaved(false));

        lifeView = new LifeView(this, fieldModel, propertiesModel);
        lifeView.setLocationRelativeTo(null);
        lifeView.setVisible(true);
    }

    public void onRunButtonClicked(boolean isSelected) {
        if (!isSelected) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            return;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fieldModel.step();
            }
        }, 0, propertiesModel.getTimer());
    }

    public void onClearButtonClicked() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }

        fieldModel.clear();
    }

    public void onAboutButtonClicked() {
        if (aboutDialog != null) {
            return;
        }

        AboutView aboutView = new AboutView(this);
        aboutView.setLocationRelativeTo(lifeView);
        aboutDialog = new JDialog(aboutView, "About", Dialog.ModalityType.DOCUMENT_MODAL);
    }

    public void onPropertiesDialogClosing() {
        propertiesDialog = null;
    }

    public void onPropertiesDialogCancelButtonClicked() {
        propertiesDialog = null;
    }

    public void onPropertiesDialogOkButtonClicked() {
        if (propertiesDialog == null) {
            return;
        }

        propertiesModel.setPaintMode(propertiesView.getPaintMode());

        int fieldWidth = propertiesView.getWidthSlider().getValue();
        int fieldHeight = propertiesView.getHeightSlider().getValue();
        propertiesModel.setFieldSize(new Dimension(fieldWidth, fieldHeight));

        propertiesModel.setHexSize(propertiesView.getHexSizeSlider().getValue());

        propertiesModel.setLineThickness(propertiesView.getLineThicknessSlider().getValue());

        String firstImpactStr = propertiesView.getFirstImpactText().getText();
        double firstImpact = Double.parseDouble(firstImpactStr);
        propertiesModel.setFirstImpact(firstImpact);

        String secondImpactStr = propertiesView.getSecondImpactText().getText();
        double secondImpact = Double.parseDouble(secondImpactStr);
        propertiesModel.setSecondImpact(secondImpact);

        String birthBeginStr = propertiesView.getBirthBeginText().getText();
        String birthEndStr = propertiesView.getBirthEndText().getText();
        String liveEndStr = propertiesView.getLiveBeginText().getText();
        String liveBeginStr = propertiesView.getLiveBeginText().getText();
        double liveBegin = Double.parseDouble(liveBeginStr);
        double liveEnd = Double.parseDouble(liveEndStr);
        double birthBegin = Double.parseDouble(birthBeginStr);
        double birthEnd = Double.parseDouble(birthEndStr);
        propertiesModel.setLifeRules(liveBegin, birthBegin, birthEnd, liveEnd);

        propertiesDialog = null;
    }

    public void onPropertiesDialogFstImpactTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String firstImpactStr = propertiesView.getFirstImpactText().getText();
                double firstImpact = Double.parseDouble(firstImpactStr);
                if(Double.compare(firstImpact, 0.) < 0) {
                    propertiesView.getFirstImpactText().setText(String.valueOf(propertiesModel.getFirstImpact()));
                }
            } catch (Exception e) {
                propertiesView.getFirstImpactText().setText(String.valueOf(propertiesModel.getFirstImpact()));
            }
        });
    }

    public void onPropertiesDialogSndImpactTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String secondImpactStr = propertiesView.getSecondImpactText().getText();
                double secondImpact = Double.parseDouble(secondImpactStr);
                if(Double.compare(secondImpact, 0.) < 0) {
                    propertiesView.getSecondImpactText().setText(String.valueOf(propertiesModel.getSecondImpact()));
                }
            } catch (Exception e) {
                propertiesView.getSecondImpactText().setText(String.valueOf(propertiesModel.getSecondImpact()));
            }
        });
    }

    public void onPropertiesDialogLiveBeginTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String liveBeginStr = propertiesView.getLiveBeginText().getText();
                String birthBeginStr = propertiesView.getBirthBeginText().getText();
                double liveBegin = Double.parseDouble(liveBeginStr);
                double birthBegin = Double.parseDouble(birthBeginStr);
                if(Double.compare(liveBegin, birthBegin) > 0) {
                    propertiesView.getLiveBeginText().setText(String.valueOf(propertiesModel.getLiveBegin()));
                }
            } catch (Exception e) {
                propertiesView.getLiveBeginText().setText(String.valueOf(propertiesModel.getLiveBegin()));
            }
        });
    }

    public void onPropertiesDialogLiveEndTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String liveEndStr = propertiesView.getLiveBeginText().getText();
                String birthEndStr = propertiesView.getBirthEndText().getText();
                double liveEnd = Double.parseDouble(liveEndStr);
                double birthEnd = Double.parseDouble(birthEndStr);
                if(Double.compare(liveEnd, birthEnd) < 0) {
                    propertiesView.getLiveEndText().setText(String.valueOf(propertiesModel.getLiveEnd()));
                }
            } catch (Exception e) {
                propertiesView.getLiveEndText().setText(String.valueOf(propertiesModel.getLiveEnd()));
            }
        });
    }

    public void onPropertiesDialogBirthBeginTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String liveBeginStr = propertiesView.getLiveBeginText().getText();
                String birthBeginStr = propertiesView.getBirthBeginText().getText();
                String birthEndStr = propertiesView.getBirthEndText().getText();
                double liveBegin = Double.parseDouble(liveBeginStr);
                double birthBegin = Double.parseDouble(birthBeginStr);
                double birthEnd = Double.parseDouble(birthEndStr);
                if(Double.compare(birthBegin, liveBegin) < 0 || Double.compare(birthBegin, birthEnd) > 0) {
                    propertiesView.getBirthBeginText().setText(String.valueOf(propertiesModel.getBirthBegin()));
                }
            } catch (Exception e) {
                propertiesView.getBirthBeginText().setText(String.valueOf(propertiesModel.getBirthBegin()));
            }
        });
    }

    public void onPropertiesDialogBirthEndTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String birthBeginStr = propertiesView.getBirthBeginText().getText();
                String birthEndStr = propertiesView.getBirthEndText().getText();
                String liveEndStr = propertiesView.getLiveBeginText().getText();
                double liveEnd = Double.parseDouble(liveEndStr);
                double birthBegin = Double.parseDouble(birthBeginStr);
                double birthEnd = Double.parseDouble(birthEndStr);
                if(Double.compare(birthEnd, liveEnd) > 0 || Double.compare(birthEnd, birthBegin) < 0) {
                    propertiesView.getBirthEndText().setText(String.valueOf(propertiesModel.getBirthEnd()));
                }
            } catch (Exception e) {
                propertiesView.getBirthEndText().setText(String.valueOf(propertiesModel.getBirthEnd()));
            }
        });
    }

    public void onPropertiesDialogLineThicknessTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String lineThicknessStr = propertiesView.getLineThicknessText().getText();
                int lineThickness = Integer.parseInt(lineThicknessStr);
                if (lineThickness < propertiesModel.getMinLineThickness()) {
                    lineThickness = propertiesModel.getMinLineThickness();
                }
                if (lineThickness > propertiesModel.getMaxLineThickness()) {
                    lineThickness = propertiesModel.getMaxLineThickness();
                }
                propertiesView.setLineThickness(lineThickness);
            } catch (Exception e) {
                propertiesView.setLineThickness(propertiesModel.getLineThickness());
            }
        });
    }

    public void onPropertiesDialogLineThicknessSliderChanged() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            int lineThickness = propertiesView.getLineThicknessSlider().getValue();
            if (lineThickness < propertiesModel.getMinLineThickness()) {
                lineThickness = propertiesModel.getMinLineThickness();
            }
            if (lineThickness > propertiesModel.getMaxLineThickness()) {
                lineThickness = propertiesModel.getMaxLineThickness();
            }
            propertiesView.setLineThickness(lineThickness);
        });
    }

    public void onPropertiesDialogHexSizeTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String hexSizeStr = propertiesView.getHexSizeText().getText();
                int hexSize = Integer.parseInt(hexSizeStr);
                if (hexSize < propertiesModel.getMinHexSize()) {
                    hexSize = propertiesModel.getMinHexSize();
                }
                if (hexSize > propertiesModel.getMaxHexSize()) {
                    hexSize = propertiesModel.getMaxHexSize();
                }
                propertiesView.setHexSize(hexSize);
            } catch (Exception e) {
                propertiesView.setHexSize(propertiesModel.getHexSize());
            }
        });
    }

    public void onPropertiesDialogHexSizeSliderChanged() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            int hexSize = propertiesView.getHexSizeSlider().getValue();
            if (hexSize < propertiesModel.getMinHexSize()) {
                hexSize = propertiesModel.getMinHexSize();
            }
            if (hexSize > propertiesModel.getMaxHexSize()) {
                hexSize = propertiesModel.getMaxHexSize();
            }
            propertiesView.setHexSize(hexSize);
        });
    }

    public void onPropertiesDialogWidthTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String widthStr = propertiesView.getWidthText().getText();
                int width = Integer.parseInt(widthStr);
                if (width < propertiesModel.getMinFieldSize().width) {
                    width = propertiesModel.getMinFieldSize().width;
                }
                if (width > propertiesModel.getMaxFieldSize().width) {
                    width = propertiesModel.getMaxFieldSize().width;
                }
                propertiesView.setWidth(width);
            } catch (Exception e) {
                propertiesView.setWidth(propertiesModel.getFieldSize().width);
            }
        });
    }

    public void onPropertiesDialogWidthSliderChanged() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            int width = propertiesView.getWidthSlider().getValue();
            if (width < propertiesModel.getMinFieldSize().width) {
                width = propertiesModel.getMinFieldSize().width;
            }
            if (width > propertiesModel.getMaxFieldSize().width) {
                width = propertiesModel.getMaxFieldSize().width;
            }
            propertiesView.setWidth(width);
        });
    }

    public void onPropertiesDialogHeightTextFocusLost() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            try {
                String heightStr = propertiesView.getHeightText().getText();
                int height = Integer.parseInt(heightStr);
                if (height < propertiesModel.getMinFieldSize().height) {
                    height = propertiesModel.getMinFieldSize().height;
                }
                if (height > propertiesModel.getMaxFieldSize().height) {
                    height = propertiesModel.getMaxFieldSize().height;
                }
                propertiesView.setHeight(height);
            } catch (Exception e) {
                propertiesView.setHeight(propertiesModel.getFieldSize().height);
            }
        });
    }

    public void onPropertiesDialogHeightSliderChanged() {
        SwingUtilities.invokeLater(() -> {
            if (propertiesDialog == null) {
                return;
            }

            int height = propertiesView.getHeightSlider().getValue();
            if (height < propertiesModel.getMinFieldSize().height) {
                height = propertiesModel.getMinFieldSize().height;
            }
            if (height > propertiesModel.getMaxFieldSize().height) {
                height = propertiesModel.getMaxFieldSize().height;
            }
            propertiesView.setHeight(height);
        });
    }

    public void onNewFieldDialogClosing() {
        newFieldDialog = null;
    }

    public void onNewFieldButtonClicked() {
        if (newFieldDialog != null) {
            return;
        }

        NewFieldView newFieldView = new NewFieldView(this, propertiesModel);
        newFieldView.setLocationRelativeTo( lifeView );

        newFieldDialog = new JDialog(newFieldView, "New field", Dialog.ModalityType.DOCUMENT_MODAL);
    }

    public void onAboutDialogClosing() {
        aboutDialog = null;
    }

    public void onReplaceModeClicked() {
        propertiesModel.setPaintMode(PaintMode.REPLACE);
    }

    public void onXorModeClicked() {
        propertiesModel.setPaintMode(PaintMode.XOR);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        prevCell = getHex(mouseEvent.getPoint());
        changeCellState(prevCell);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        IField field = fieldModel.getActiveField();
        Point nextCell = getHex(mouseEvent.getPoint());

        if (prevCell.equals(nextCell)) {
            return;
        }

        prevCell = nextCell;

        if (!field.checkCrds(nextCell)) {
            return;
        }

        changeCellState(nextCell);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        prevCell = new Point(-1, -1);
    }

    private void changeCellState(Point pos) {
        IField field = fieldModel.getActiveField();
        if (!field.checkCrds(pos)) {
            return;
        }

        //Replace
        if (propertiesModel.getPaintMode() == PaintMode.REPLACE) {
            field.set(pos, CellState.ALIVE);
            lifeView.repaint();
            return;
        }

        //XOR
        if (field.get(pos) == CellState.ALIVE) {
            field.set(pos, CellState.DEAD);
        } else {
            field.set(pos, CellState.ALIVE);
        }
    }

    public void onNextButtonClicked() {
        fieldModel.step();
    }

    public void onImpactButtonClicked(boolean isSelected) {
        propertiesModel.setImpactVisible(isSelected);
    }

    public void onMouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
//        System.out.println(mouseWheelEvent.getPoint());
//
//        lifeView.getScrollPane().getVerticalScrollBar().setValue(lifeView.getScrollPane().getVerticalScrollBar().getMaximum());

//        System.out.println(lifeView.getScrollPane().getVerticalScrollBar().getMaximum());

        int newHexSize = propertiesModel.getHexSize();
        newHexSize -= newHexSize % 2;
        newHexSize -= 2 * mouseWheelEvent.getWheelRotation();
        newHexSize = Math.min(propertiesModel.getMaxHexSize(), newHexSize);
        newHexSize = Math.max(propertiesModel.getMinHexSize(), newHexSize);
//        System.out.println("New hex size: " + newHexSize);

//        double k = (double) newHexSize / propertiesModel.getHexSize();
//        int dx = (int)((k - 1) * mouseWheelEvent.getPoint().x);
//        int dy = (int)((k - 1) * mouseWheelEvent.getPoint().y);
//        SwingUtilities.invokeLater(() -> {
//            JScrollPane scrollPane = lifeView.getScrollPane();
//            scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + dx);
//            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + dy);
//        });

        propertiesModel.setHexSize(newHexSize);
    }

    private Point getHex(Point pos) {
//        System.out.println(pos);

        int hexSize = propertiesModel.getHexSize();
        int hexIncircle = propertiesModel.getHexIncircle();
        int halfHexSize = hexSize / 2;

        int y = pos.y / 3 / halfHexSize;
        int offset = (y % 2 != 0) ? hexIncircle : 0;
        int x = (pos.x - offset) / 2 / hexIncircle;
//        int shiftedX = (y % 2 == 0) ? (x) : (x - 1);

        Point center1 = new Point((x * 2 + 1) * hexIncircle + offset, y * 3 * halfHexSize + hexSize);
        Point center2 = new Point(center1.x - hexIncircle , center1.y - 3 * halfHexSize);
        Point center3 = new Point(center1.x + hexIncircle , center2.y);

        double dist1 = pos.distanceSq(center1);
        double dist2 = pos.distanceSq(center2);
        double dist3 = pos.distanceSq(center3);

//        System.out.println(new Point(x, y));

//        System.out.println(center1 + ": " + dist1);
//        System.out.println(center2 + ": " + dist2);
//        System.out.println(center3 + ": " + dist3);

        if (dist1 < dist2) {
            if(dist1 < dist3) {
                return new Point(x, y);
            } else {
                return new Point(x + (y % 2 != 0 ? 1 : 0), y - 1);
            }
        }

        if (dist2 < dist3) {
            return new Point(x + (y % 2 != 0 ? 0 : -1), y - 1);
        }
        return new Point(x + (y % 2 != 0 ? 1 : 0), y - 1);
    }
}
