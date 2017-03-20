package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by kir55rus on 17.03.17.
 */
public class ImageModel extends ObservableBase implements Observable {
    private BufferedImage image;

    public ImageModel() {

    }

    public ImageModel(BufferedImage image) {
        this.image = image;
    }

    public ImageModel(File file) throws IOException {
        image = ImageIO.read(file);
    }

    public void reset() {
        setImage(null);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        notifyObservers(ImageModelEvent.IMAGE_UPDATED);
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getImage(Rectangle area) {
        return image.getSubimage(area.x, area.y, area.width, area.height);
    }
}
