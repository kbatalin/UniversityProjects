package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;

import javax.swing.*;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeView extends JFrame {
    private LifeController lifeController;

    public LifeView(LifeController lifeController) {
        this.lifeController = lifeController;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
    }
}
