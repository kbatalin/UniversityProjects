package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

/**
 * Created by kir55rus on 02.04.17.
 */
public class ApplicationProperties extends ObservableBase implements PropertiesModel {
    private int horizontalCellsCount;
    private int verticalCellsCount;
    private Area area;
    private Painter painter;
    private boolean isIsolinesShown;
    private boolean isGridShown;

    @Override
    public int getHorizontalCellsCount() {
        return horizontalCellsCount;
    }

    @Override
    public void setHorizontalCellsCount(int horizontalCellsCount) {
        if (horizontalCellsCount < 1) {
            throw new IllegalArgumentException("count must be > 1");
        }

        this.horizontalCellsCount = horizontalCellsCount;
        notifyObservers(Event.CELLS_COUNT_CHANGED);
    }

    @Override
    public int getVerticalCellsCount() {
        return verticalCellsCount;
    }

    @Override
    public void setVerticalCellsCount(int verticalCellsCount) {
        if (verticalCellsCount < 1) {
            throw new IllegalArgumentException("count must be > 1");
        }

        this.verticalCellsCount = verticalCellsCount;
        notifyObservers(Event.CELLS_COUNT_CHANGED);
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void setArea(Area area) {
        this.area = area;
        notifyObservers(Event.AREA_CHANGED);
    }

    @Override
    public Painter getPainter() {
        return painter;
    }

    @Override
    public void setPainter(Painter painter) {
        this.painter = painter;
        notifyObservers(Event.PAINTER_CHANGED);
    }

    @Override
    public boolean isIsolinesShown() {
        return isIsolinesShown;
    }

    @Override
    public void setIsolinesShown(boolean isolinesShown) {
        isIsolinesShown = isolinesShown;
        notifyObservers(Event.ISOLINES_SHOWN_CHANGED);
    }

    @Override
    public boolean isGridShown() {
        return isGridShown;
    }

    @Override
    public void setGridShown(boolean gridShown) {
        isGridShown = gridShown;
        notifyObservers(Event.GRID_SHOWN_CHANGED);
    }
}
