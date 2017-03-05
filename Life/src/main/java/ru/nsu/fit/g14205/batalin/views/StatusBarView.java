package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Created by kir55rus on 06.03.17.
 */
public class StatusBarView extends JLabel {

    public StatusBarView() {
        Border border = BorderFactory.createEmptyBorder(2, 5, 5, 2);
        setBorder(border);

        setMessage("");

        setVisible(true);
    }

    public void setMessage(String str) {
        setText(" " + str);
    }
}
