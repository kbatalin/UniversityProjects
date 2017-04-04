package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 02.04.17.
 */
public interface PropertiesModel extends Observable {
    int getHorizontalCellsCount();
    void setHorizontalCellsCount(int val);

    int getVerticalCellsCount();
    void setVerticalCellsCount(int val);

    Area getArea();
    void setArea(Area area);

    Painter getPainter();
    void setPainter(Painter painter);

    boolean isIsolinesShown();
    void setIsolinesShown(boolean val);

    boolean isGridShown();
    void setGridShown(boolean val);

    boolean isCreatingIsolines();
    void setCreatingIsolines(boolean val);

    boolean isDynamicIsolines();
    void setDynamicIsolines(boolean val);

    Color getIsolinesColor();
    void setIsolinesColor(Color color);

    ArrayList<Double> getIsolinesValues();
    void setIsolinesValues(ArrayList<Double> isolinesValues);

    DoubleBinaryOperator getMainFunction();
    DoubleBinaryOperator getLegendFunction();
    void setMainFunction(DoubleBinaryOperator function);

    double[] getValues();
    double getMinValue();
    double getMaxValue();

    Color getValueColor(double value);
    Color[] getValuesColors();
    void setValuesColors(Color[] colors);

    enum Event implements ObserveEvent {
        CELLS_COUNT_CHANGED,
        AREA_CHANGED,
        PAINTER_CHANGED,
        ISOLINES_SHOWN_CHANGED,
        GRID_SHOWN_CHANGED,
        FUNCTION_CHANGED,
        COLORS_CHANGED,
        ISOLINES_VALUES_CHANGED,
    }
}
