package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.FieldModel;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.views.LifeView;

import java.awt.*;
import java.util.Map;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private FieldModel fieldModel;
    private PropertiesModel propertiesModel;

    public void run() {
        Dimension fieldSize = new Dimension(20, 100);

        propertiesModel = new PropertiesModel();
        fieldModel = new FieldModel(fieldSize);
        lifeView = new LifeView(this, fieldModel, 30);

        lifeView.setVisible(true);
    }

    public void onFieldClick(Point pos) {
        FieldModel.Field field = fieldModel.getField();

        Point hex = getHex(pos);
        if (!field.checkCrds(hex)) {
            System.out.println("Bad pos");
            return;
        }

        if (field.get(hex) == FieldModel.CellType.ALIVE) {
            field.set(hex, FieldModel.CellType.DEAD);
        } else {
            field.set(hex, FieldModel.CellType.ALIVE);
        }

        lifeView.repaint();
    }

    private Point getHex(Point pos) {
        System.out.println(pos);

        int hexSize = 30;
        int hexIncircle = (int)(hexSize * Math.sqrt(3) / 2);

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

        System.out.println(new Point(x, y));

        System.out.println(center1 + ": " + dist1);
        System.out.println(center2 + ": " + dist2);
        System.out.println(center3 + ": " + dist3);

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



//        int y = pos.y * 2 / 3 / hexSize;
//        int x = pos.x / hexIncircle;
//        int shiftedX = (y % 2 == 0) ? (x) : (x - 1);
//
//        System.out.println(new Point(x, y) + ", shifted: " + shiftedX);
//
//        double k = ((shiftedX >= 0 && (shiftedX % 2 == 0)) ? -1. : 1.) / Math.sqrt(3);
//
//        System.out.println("K: " + k);
//
//        int start = Math.min(x, y);
//        int offset = hexSize * (1 + 4 * start) / 2;
//        int offsetX = x - start;
//        int offsetY = y - start;
//        int b;
//        if (offsetX > 0) {
//            b = (offsetX % 2) * (-1) * (offsetX / 2 * hexSize + offset);
//        } else {
//            int evenCount = offsetY / 2;
//            int oddCount = evenCount + (offsetY % 2);
//            b = evenCount * 2 * hexSize + oddCount * hexSize + offset;
//        }
//
//        System.out.println("OffsetX: " + offsetX + ", offsetY: " + offsetY);
//        System.out.println("b: " + b);
//
////        if (y % 2 != 0) {
////            --x;
////        }
//
////        if (x < 0 || y < 0) {
////            System.out.println("Not #1");
////            return;
////        }
//
////        double k = 1. / Math.sqrt(3);
//////        double k = hexSize / 2. / hexIncircle;
////        if (x % 2 == 0) {
////            k *= -1.;
////        }
//
////        double b = y * 3 * hexSize / 2. - k * ((x / 2) * 2 * hexIncircle + hexIncircle * (y % 2 != 0 ? 2 : 1));
////        int b = y * hexSize;
////        b += (x % 2 == 0) ? (hexSize / 2) : (-hexSize / 2);
//
//        System.out.println(k * pos.x + b);
//        if (k * pos.x + b > pos.y) {
//            shiftedX += (y % 2 != 0) ? 1 : -1;
//            --y;
//        }
//
//        if (shiftedX < 0 || y < 0) {
//            return new Point(-1, -1);
//        }
//
//        shiftedX /= 2;
//
//        return new Point(shiftedX, y);
    }
}
