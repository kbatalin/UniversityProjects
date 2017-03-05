package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public class PropertiesModel extends Observable implements IPropertiesModel {
    private static int DEFAULT_HEX_SIZE = 30;
    private static Dimension DEFAULT_MIN_FIELD_SIZE = new Dimension(1, 1);
    private static Dimension DEFAULT_MAX_FIELD_SIZE = new Dimension(5000, 5000);
    private static Dimension DEFAULT_FIELD_SIZE = new Dimension(20, 100);
    private static int DEFAULT_MIN_HEX_SIZE = 4;
    private static int DEFAULT_MAX_HEX_SIZE = 50;
    private static double DEFAULT_FIRST_IMPACT = 1.0;
    private static double DEFAULT_SECOND_IMPACT = 0.3;
    private static double DEFAULT_LIVE_BEGIN = 2.0;
    private static double DEFAULT_LIVE_END = 3.3;
    private static double DEFAULT_BIRTH_BEGIN = 2.3;
    private static double DEFAULT_BIRTH_END = 2.9;
    private static int DEFAULT_IMPACT_FONT_SIZE = 12;
    private static PaintMode DEFAULT_PAINT_MODE = PaintMode.REPLACE;
    private static long DEFAULT_TIMER = 1000;
    private static int DEFAULT_LINE_THICKNESS = 1;
    private static int DEFAULT_MIN_LINE_THICKNESS = 1;
    private static int DEFAULT_MAX_LINE_THICKNESS = 20;

    private int hexSize;
    private int hexIncircle;
    private Dimension fieldSize;
    private int lineThickness;
    private double firstImpact;
    private double secondImpact;
    private double liveBegin;
    private double liveEnd;
    private double birthBegin;
    private double birthEnd;
    private boolean isImpactVisible;
    private PaintMode paintMode;
    private long timer;

    public static PropertiesModel createDefault() {
        PropertiesModel propertiesModel = new PropertiesModel();
        propertiesModel.setFieldSize(DEFAULT_FIELD_SIZE);
        propertiesModel.setHexSize(DEFAULT_HEX_SIZE);
        propertiesModel.setFirstImpact(DEFAULT_FIRST_IMPACT);
        propertiesModel.setSecondImpact(DEFAULT_SECOND_IMPACT);
        propertiesModel.setLiveEnd(DEFAULT_LIVE_END);
        propertiesModel.setBirthEnd(DEFAULT_BIRTH_END);
        propertiesModel.setBirthBegin(DEFAULT_BIRTH_BEGIN);
        propertiesModel.setLiveBegin(DEFAULT_LIVE_BEGIN);
        propertiesModel.setPaintMode(DEFAULT_PAINT_MODE);
        propertiesModel.setTimer(DEFAULT_TIMER);
        propertiesModel.setLineThickness(DEFAULT_LINE_THICKNESS);

        return propertiesModel;
    }

    private PropertiesModel() {
    }

    @Override
    public int getHexIncircle() {
        return hexIncircle;
    }

    @Override
    public int getHexSize() {
        return hexSize;
    }

    @Override
    public void setHexSize(int hexSize) {
        if (hexSize <= 0) {
            throw new IllegalArgumentException("Size <= 0");
        }
        this.hexSize = hexSize;
        this.hexIncircle = (int)(hexSize * Math.sqrt(3) / 2);

        notifyObservers(PropertiesModelEvent.SIZE_CHANGED);
    }

    @Override
    public Dimension getFieldSize() {
        return fieldSize;
    }

    @Override
    public void setFieldSize(Dimension fieldSize) {
        if (fieldSize.height <= 0 || fieldSize.width <= 0) {
            throw new IllegalArgumentException("fieldSize <= 0");
        }
        this.fieldSize = fieldSize;

        notifyObservers(PropertiesModelEvent.SIZE_CHANGED);
    }

    @Override
    public int getMinHexSize() {
        return DEFAULT_MIN_HEX_SIZE;
    }

    @Override
    public int getMaxHexSize() {
        return DEFAULT_MAX_HEX_SIZE;
    }

    @Override
    public double getFirstImpact() {
        return firstImpact;
    }

    @Override
    public double getSecondImpact() {
        return secondImpact;
    }

    @Override
    public double getLiveBegin() {
        return liveBegin;
    }

    @Override
    public double getLiveEnd() {
        return liveEnd;
    }

    @Override
    public double getBirthBegin() {
        return birthBegin;
    }

    @Override
    public double getBirthEnd() {
        return birthEnd;
    }

    @Override
    public void setFirstImpact(double firstImpact) {
        if(firstImpact < 0.) {
            throw new IllegalArgumentException("First impact must be >= 0");
        }
        this.firstImpact = firstImpact;
    }

    @Override
    public void setSecondImpact(double secondImpact) {
        if(secondImpact < 0.) {
            throw new IllegalArgumentException("Second impact must be >= 0");
        }
        this.secondImpact = secondImpact;
    }

    @Override
    public void setLiveBegin(double liveBegin) {
        if (liveBegin > birthBegin) {
            throw new IllegalArgumentException("liveBegin > birthBegin");
        }
        this.liveBegin = liveBegin;
    }

    @Override
    public void setLiveEnd(double liveEnd) {
        if (liveEnd < birthEnd) {
            throw new IllegalArgumentException("liveEnd < birthEnd");
        }
        this.liveEnd = liveEnd;
    }

    @Override
    public void setBirthBegin(double birthBegin) {
        if (birthBegin < liveBegin || birthBegin > birthEnd) {
            throw new IllegalArgumentException("birthBegin < liveBegin || birthBegin > birthEnd");
        }
        this.birthBegin = birthBegin;
    }

    @Override
    public void setBirthEnd(double birthEnd) {
        if (birthEnd > liveEnd || birthEnd < birthBegin) {
            throw new IllegalArgumentException("birthEnd > liveEnd || birthEnd < birthBegin");
        }
        this.birthEnd = birthEnd;
    }

    @Override
    public boolean isImpactVisible() {
        return isImpactVisible;
    }

    @Override
    public void setImpactVisible(boolean impactVisible) {
        isImpactVisible = impactVisible;
        notifyObservers(PropertiesModelEvent.IMPACT_VISIBLE_CHANGED);
    }

    @Override
    public int getImpactFontSize() {
        return DEFAULT_IMPACT_FONT_SIZE;
    }

    @Override
    public PaintMode getPaintMode() {
        return paintMode;
    }

    @Override
    public void setPaintMode(PaintMode paintMode) {
        this.paintMode = paintMode;
    }

    @Override
    public long getTimer() {
        return timer;
    }

    @Override
    public void setTimer(long timer) {
        this.timer = timer;
    }

    @Override
    public Dimension getMinFieldSize() {
        return DEFAULT_MIN_FIELD_SIZE;
    }

    @Override
    public Dimension getMaxFieldSize() {
        return DEFAULT_MAX_FIELD_SIZE;
    }

    @Override
    public int getLineThickness() {
        return lineThickness;
    }

    @Override
    public void setLineThickness(int lineThickness) {
        if(lineThickness < 0)
        this.lineThickness = lineThickness;
    }

    @Override
    public int getMinLineThickness() {
        return DEFAULT_MIN_LINE_THICKNESS;
    }

    @Override
    public int getMaxLineThickness() {
        return DEFAULT_MAX_LINE_THICKNESS;
    }
}
