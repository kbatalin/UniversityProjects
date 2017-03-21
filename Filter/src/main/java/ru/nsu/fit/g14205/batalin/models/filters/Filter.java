package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 22.03.17.
 */
public interface Filter {
    BufferedImage process(BufferedImage image);
}
