package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.painters.ColorMapPainter;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.util.List;
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
    private ArrayList<Double> isolinesValues = new ArrayList<>();
    private boolean creatingIsolines;
    private boolean dynamicIsolines;
    private boolean entryPointsShown;

    public static ApplicationProperties createDefault() {
        ApplicationProperties properties = new ApplicationProperties();
        properties.initDefault();

        return properties;
    }

    @Override
    public void initDefault() {
        setArea(new Area(-5, -5, 5, 5));
        setHorizontalCellsCount(10);
        setVerticalCellsCount(10);
        setGridShown(false);
        setIsolinesShown(false);
        setPainter(new ColorMapPainter());
        setDynamicIsolines(false);
        setCreatingIsolines(false);
        setMainFunction(new SinCosFunction());
        setValuesColors(new Color[]{
                new Color(255, 0, 0),
                new Color(255, 0, 255),
                new Color(0, 0, 255),
                new Color(0, 255, 255),
        });
        setIsolinesColor(Color.WHITE);
    }

    @Override
    public void load(File file) throws IOException {
        try (Scanner scanner = new Scanner(file).useLocale(Locale.US)) {
            String[] strData = nextData(scanner);
            int k = Integer.parseInt(strData[0]);
            int m = Integer.parseInt(strData[1]);
            strData = nextData(scanner);

            int n = Integer.parseInt(strData[0]);
            strData = nextData(scanner);

            Color[] valuesColors = new Color[n];
            for(int i = 0; i < n; ++i) {
                valuesColors[i] = parseColor(strData);
                strData = nextData(scanner);
            }

            Color isolinesColor = parseColor(strData);

            setVerticalCellsCount(k);
            setHorizontalCellsCount(m);
            setValuesColors(valuesColors);
            setIsolinesColor(isolinesColor);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static Color parseColor(String[] data) {
        int r = Integer.parseInt(data[0]);
        int g = Integer.parseInt(data[1]);
        int b = Integer.parseInt(data[2]);

        return new Color(r, g, b);
    }

    private static String removeComment(String line) {
        int index = line.indexOf("//");
        if (index == -1) {
            return line;
        }

        return line.substring(0, index);
    }

    private static String[] nextData(Scanner scanner) {
        String line = "";
        while (line.isEmpty()) {
            line = removeComment(scanner.nextLine()).trim();
        }

        return line.split(" ");
    }

    @Override
    public boolean isEntryPointsShown() {
        return entryPointsShown;
    }

    @Override
    public void setEntryPointsShown(boolean entryPointsShown) {
        this.entryPointsShown = entryPointsShown;
        notifyObservers(Event.ENTRY_POINTS_SHOWN_CHANGED);
    }

    @Override
    public boolean isCreatingIsolines() {
        return creatingIsolines;
    }

    @Override
    public void setCreatingIsolines(boolean creatingIsolines) {
        this.creatingIsolines = creatingIsolines;
    }

    @Override
    public boolean isDynamicIsolines() {
        return dynamicIsolines;
    }

    @Override
    public void setDynamicIsolines(boolean dynamicIsolines) {
        this.dynamicIsolines = dynamicIsolines;
    }

    @Override
    public ArrayList<Double> getIsolinesValues() {
        return isolinesValues;
    }

    @Override
    public void setIsolinesValues(ArrayList<Double> isolinesValues) {
        this.isolinesValues = isolinesValues;
        notifyObservers(Event.ISOLINES_VALUES_CHANGED);
    }

    @Override
    public int getHorizontalCellsCount() {
        return horizontalCellsCount;
    }

    @Override
    public void setHorizontalCellsCount(int horizontalCellsCount) {
        if (horizontalCellsCount < 2) {
            throw new IllegalArgumentException("count must be > 2");
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
        if (verticalCellsCount < 2) {
            throw new IllegalArgumentException("count must be > 2");
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
