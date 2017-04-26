package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.awt.*;
import java.util.List;

/**
 * Created by kir55rus on 12.04.17.
 */
public interface ApplicationProperties extends Observable, Cloneable {
    double getControlPointRadius();

    List<FigureProperties> getFigureProperties();
    int getFigurePropertiesCount();
    void addFigureProperties(FigureProperties figureProperties);
    void delFigureProperties(int index);

    Area getArea();
    void setArea(Area area);

    CameraProperties getCameraProperties();
    ViewPyramidProperties getViewPyramidProperties();

    Grid getGrid();

    PaintedFigure getScene();
    void setScene(PaintedFigure scene);

    ApplicationProperties clone() throws CloneNotSupportedException;
    void apply(ApplicationProperties applicationProperties);

    Color getBackgroundColor();
    void setBackgroundColor(Color color);

    boolean isClippingEnabled();
    void setClippingEnabled(boolean enable);

    boolean isOutboardBoxShown();
    void setOutboardBoxShown(boolean visible);

    enum Event implements ObserveEvent {
        FIGURE_PROPERTIES_ADDED,
        FIGURE_PROPERTIES_REMOVED,
        AREA_CHANGED,
        SCENE_CHANGED,
        BACKGROUND_COLOR_CHANGED,
        CLIPPING_ENABLED_CHANGED,
        OUTBOARD_BOX_SHOWN_CHANGED,
    }
}
