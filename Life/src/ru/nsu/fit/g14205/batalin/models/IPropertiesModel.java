package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IObservable;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IPropertiesModel extends IObservable {
    int getHexIncircle();
    int getHexSize();
    int getMinHexSize();
    int getMaxHexSize();
    void setHexSize(int hexSize);
    Dimension getFieldSize();
    void setFieldSize(Dimension fieldSize);

    double getFirstImpact();
    double getSecondImpact();
    double getLiveBegin();
    double getLiveEnd();
    double getBirthBegin();
    double getBirthEnd();
    void setFirstImpact(double val);
    void setSecondImpact(double val);
    void setLiveBegin(double val);
    void setLiveEnd(double val);
    void setBirthBegin(double val);
    void setBirthEnd(double val);

    boolean isImpactVisible();
    void setImpactVisible(boolean impactVisible);

    int getImpactFontSize();
}
