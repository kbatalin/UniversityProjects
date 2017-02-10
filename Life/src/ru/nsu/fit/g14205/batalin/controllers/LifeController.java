package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.LifeView;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;

    public void run() {
        lifeView = new LifeView(this);

        lifeView.setVisible(true);
    }
}
