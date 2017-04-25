package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

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

    enum Event implements ObserveEvent {
        FIGURE_PROPERTIES_ADDED,
        FIGURE_PROPERTIES_REMOVED,
        AREA_CHANGED,
        SCENE_CHANGED,
    }
}
