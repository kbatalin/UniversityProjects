package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface Painter {
    BufferedImage draw(DoubleBinaryOperator function, PropertiesModel properties, Dimension displaySize);
}
