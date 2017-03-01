package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.LifeView;

import java.awt.*;
import java.awt.event.MouseWheelEvent;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private IFieldModel fieldModel;
    private IPropertiesModel propertiesModel;

    public void run() {
        propertiesModel = PropertiesModel.createDefault();
        fieldModel = new FieldModel(propertiesModel);
        lifeView = new LifeView(this, fieldModel, propertiesModel);

        lifeView.setVisible(true);
    }

    public void onNextButtonClicked() {
        fieldModel.step();
    }

    public void onMouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        int newHexSize = propertiesModel.getHexSize() - mouseWheelEvent.getWheelRotation();
        newHexSize = Math.min(propertiesModel.getMaxHexSize(), newHexSize);
        newHexSize = Math.max(propertiesModel.getMinHexSize(), newHexSize);
        System.out.println("New hex size: " + newHexSize);
        propertiesModel.setHexSize(newHexSize);
    }

    public void onFieldClick(Point pos) {
        IField field = fieldModel.getActiveField();

        Point hex = getHex(pos);
        if (!field.checkCrds(hex)) {
            System.out.println("Bad pos");
            return;
        }

        if (field.get(hex) == CellState.ALIVE) {
            field.set(hex, CellState.DEAD);
        } else {
            field.set(hex, CellState.ALIVE);
        }

        lifeView.repaint();
    }

    private Point getHex(Point pos) {
//        System.out.println(pos);

        int hexSize = propertiesModel.getHexSize();
        int hexIncircle = propertiesModel.getHexIncircle();

        int y = pos.y * 2 / 3 / hexSize;
        int offset = (y % 2 != 0) ? hexIncircle : 0;
        int x = (pos.x - offset) / 2 / hexIncircle;
//        int shiftedX = (y % 2 == 0) ? (x) : (x - 1);

        Point center1 = new Point((x * 2 + 1) * hexIncircle + offset, y * 3 * hexSize / 2 + hexSize);
        Point center2 = new Point(center1.x - hexIncircle , center1.y - 3 * hexSize / 2);
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
