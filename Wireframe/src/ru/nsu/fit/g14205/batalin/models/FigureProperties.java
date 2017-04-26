package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by Kirill Batalin (kir55rus) on 26.04.17.
 */
public interface FigureProperties extends Cloneable {
    LineProperties getLineProperties();
    CoordinateSystem getCoordinateSystem();

    FigureProperties clone() throws CloneNotSupportedException;
}
