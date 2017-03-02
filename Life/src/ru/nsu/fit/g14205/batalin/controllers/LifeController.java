package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.LifeView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private IFieldModel fieldModel;
    private IPropertiesModel propertiesModel;
    private Point prevCell;
    private CellState replaceModeNewState;

    public void run() {
        propertiesModel = PropertiesModel.createDefault();
        fieldModel = new FieldModel(propertiesModel);
        lifeView = new LifeView(this, fieldModel, propertiesModel);

        lifeView.setVisible(true);
    }

    public void onReplaceModeClicked() {
        propertiesModel.setPaintMode(PaintMode.REPLACE);
    }

    public void onXorModeClicked() {
        propertiesModel.setPaintMode(PaintMode.XOR);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        IField field = fieldModel.getActiveField();
        prevCell = getHex(mouseEvent.getPoint());

        if (!field.checkCrds(prevCell)) {
            replaceModeNewState = CellState.ALIVE;
            return;
        }

        replaceModeNewState = CellState.opposite(field.get(prevCell));

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
            field.set(pos, replaceModeNewState);
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
