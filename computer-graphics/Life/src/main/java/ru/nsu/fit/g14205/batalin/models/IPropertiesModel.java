package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IObservable;

import java.awt.*;
import java.nio.file.Path;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IPropertiesModel extends IObservable {
    int getHexIncircle();
    int getHexSize();
    int getMinHexSize();
    int getMaxHexSize();
    void setHexSize(int hexSize);
    Dimension getMinFieldSize();
    Dimension getMaxFieldSize();
    Dimension getFieldSize();
    void setFieldSize(Dimension fieldSize);
    int getLineThickness();
    void setLineThickness(int thickness);
    int getMinLineThickness();
    int getMaxLineThickness();

    double getFirstImpact();
    void setFirstImpact(double val);
    double getSecondImpact();
    void setSecondImpact(double val);
    double getLiveBegin();
    void setLiveBegin(double val);
    double getLiveEnd();
    void setLiveEnd(double val);
    double getBirthBegin();
    void setBirthBegin(double val);
    double getBirthEnd();
    void setBirthEnd(double val);
    void setLifeRules(double liveBegin, double birthBegin, double birthEnd, double liveEnd);

    boolean isImpactVisible();
    void setImpactVisible(boolean impactVisible);

    int getImpactFontSize();

    PaintMode getPaintMode();
    void setPaintMode(PaintMode mode);

    long getTimer();
    void setTimer(long timer);

    Path getSavePath();
    void setSavePath(Path path);
}
