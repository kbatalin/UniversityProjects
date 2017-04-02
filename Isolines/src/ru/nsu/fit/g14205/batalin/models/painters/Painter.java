package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.FunctionProperties;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface Painter {
    BufferedImage draw(FunctionProperties functionProperties, Dimension size);
}
