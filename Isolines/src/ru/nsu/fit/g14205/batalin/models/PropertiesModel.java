package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObserveEvent;
import ru.nsu.fit.g14205.batalin.views.Painter;

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

    enum Event implements ObserveEvent {
        CELLS_COUNT_CHANGED,
        AREA_CHANGED,
        PAINTER_CHANGED,
        ISOLINES_SHOWN_CHANGED,
        GRID_SHOWN_CHANGED,
    }
}
