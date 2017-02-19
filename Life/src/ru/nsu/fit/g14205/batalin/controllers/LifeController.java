package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.LifeView;

import java.awt.*;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;

    public void run() {
        lifeView = new LifeView(this, new Dimension(20, 100), 30);

        lifeView.setVisible(true);
    }
}
