package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.FieldModel;
import ru.nsu.fit.g14205.batalin.views.LifeView;

import java.awt.*;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeController {
    private LifeView lifeView;
    private FieldModel fieldModel;

    public void run() {
        Dimension fieldSize = new Dimension(20, 100);

        fieldModel = new FieldModel(fieldSize);
        lifeView = new LifeView(this, fieldModel, 30);

        lifeView.setVisible(true);
    }
}
