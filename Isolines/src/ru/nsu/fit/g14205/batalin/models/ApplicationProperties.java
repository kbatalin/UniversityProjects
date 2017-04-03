package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import java.awt.*;
import java.util.function.DoubleBinaryOperator;

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
    private DoubleBinaryOperator mainFunction;
    private DoubleBinaryOperator legendFunction;
    private double minValue;
    private double maxValue;
    private double[] values;
    private Color[] valuesColors;
    private Color isolinesColor;
    private double[] isolinesValues;

    @Override
    public double[] getIsolinesValues() {
        return new double[] {-1.3};
//        return isolinesValues;
    }

    @Override
    public void setIsolinesValues(double[] isolinesValues) {
        this.isolinesValues = isolinesValues;
    }

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
        updFunctionInfo();
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

    @Override
    public DoubleBinaryOperator getMainFunction() {
        return mainFunction;
    }

    @Override
    public DoubleBinaryOperator getLegendFunction() {
        return legendFunction;
    }

    @Override
    public void setMainFunction(DoubleBinaryOperator function) {
        this.mainFunction = function;

        updFunctionInfo();
        notifyObservers(Event.FUNCTION_CHANGED);
    }

    private void updFunctionInfo() {
        if (mainFunction == null || area == null || valuesColors == null) {
            return;
        }

        double delta = 0.1;
        minValue = mainFunction.applyAsDouble(area.first.getX(), area.first.getY());
        maxValue = minValue;
        for(double x = area.first.getX(); Double.compare(x, area.second.getX()) < 0; x += delta) {
            for(double y = area.first.getY(); Double.compare(y, area.second.getY()) < 0; y += delta) {
                double value = mainFunction.applyAsDouble(x, y);
                if (Double.compare(value, minValue) < 0) {
                    minValue = value;
                }
                if (Double.compare(maxValue, value) < 0) {
                    maxValue = value;
                }
            }
        }

        values = new double[valuesColors.length - 1];
        double len = (maxValue - minValue) / (valuesColors.length);
        for(int i = 0; i < valuesColors.length - 1; ++i) {
            values[i] = minValue + (i + 1) * len;
        }

        updLegendFunction();
    }

    private void updLegendFunction() {
        Area area = getArea();
        double x0 = area.first.getX();
        double y0 = getMinValue();
        double x1 = area.second.getX();
        double y1 = getMaxValue();
        double k = (y1 - y0) / (x1 - x0);
        double b = y0 - k * x0;

        legendFunction = (x, y) -> x * k + b;
    }

    @Override
    public double[] getValues() {
        return values;
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public Color getValueColor(double value) {
        double[] values = getValues();
        Color[] colors = getValuesColors();
        if (values == null || colors == null) {
            return Color.BLACK;
        }

        for(int i = 0; i < values.length; ++i) {
            if (Double.compare(value, values[i]) < 0) {
                return colors[i];
            }
        }

        return colors[colors.length - 1];

    }

    @Override
    public Color[] getValuesColors() {
        return valuesColors;
    }

    @Override
    public void setValuesColors(Color[] colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("Count of colors must be grater than 1");
        }

        valuesColors = colors;
        updFunctionInfo();
        notifyObservers(Event.COLORS_CHANGED);
    }

    @Override
    public Color getIsolinesColor() {
        return isolinesColor;
    }

    @Override
    public void setIsolinesColor(Color isolinesColor) {
        this.isolinesColor = isolinesColor;
    }
}
