package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.AboutView;
import ru.nsu.fit.g14205.batalin.views.LifeView;
import ru.nsu.fit.g14205.batalin.views.NewFieldView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private JDialog aboutDialog;
    private JDialog newFieldDialog;
    private IFieldModel fieldModel;
    private IPropertiesModel propertiesModel;
    private Point prevCell;
    private Timer timer;
    private JFileChooser fileChooser;

    public void run() {
        propertiesModel = PropertiesModel.createDefault();
        fieldModel = new FieldModel(propertiesModel);
        lifeView = new LifeView(this, fieldModel, propertiesModel);

        fileChooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "FIT_14205_Batalin_Kirill_Life_Data");
        fileChooser.setCurrentDirectory(workingDirectory);

        SwingUtilities.invokeLater(() -> {
            lifeView.setLocationRelativeTo(null);
            lifeView.setVisible(true);
        });
    }

    public void onSaveAsButtonClicked() {
        saveAs();
    }

    private void saveAs() {
        int result = fileChooser.showSaveDialog(lifeView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        propertiesModel.setSavePath(file.toPath());

        save();
    }

    public void onSaveButtonClicked() {
        if (propertiesModel.getSavePath() == null) {
            saveAs();
            return;
        }

        save();
    }

    private void save() {
        try {
            ISaver saver = new FileSaver(propertiesModel, fieldModel);
            saver.save();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(lifeView,"Can't save field","Save error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reset() {
        if (aboutDialog != null) {
            aboutDialog.setVisible(false);
            aboutDialog = null;
        }

        if (newFieldDialog != null) {
            newFieldDialog.setVisible(false);
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
        if (lifeView != null) {
            lifeView.setVisible(false);
        }
        reset();

        propertiesModel = PropertiesModel.createDefault();
        propertiesModel.setFieldSize(size);

        fieldModel = new FieldModel(propertiesModel);

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

        lifeView.repaint();
    }

    public void onNextButtonClicked() {
        fieldModel.step();
    }

    public void onImpactButtonClicked(boolean isSelected) {
        propertiesModel.setImpactVisible(isSelected);
    }

    public void onMouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        int newHexSize = propertiesModel.getHexSize();
        newHexSize -= newHexSize % 2;
        newHexSize -= 2 * mouseWheelEvent.getWheelRotation();
        newHexSize = Math.min(propertiesModel.getMaxHexSize(), newHexSize);
        newHexSize = Math.max(propertiesModel.getMinHexSize(), newHexSize);
        System.out.println("New hex size: " + newHexSize);
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
