package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface Painter {
    Image draw(PropertiesModel propertiesModel, Dimension size);
}
