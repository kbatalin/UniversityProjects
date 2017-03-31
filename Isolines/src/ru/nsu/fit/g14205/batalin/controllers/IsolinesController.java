package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.IsolinesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private IsolinesView isolinesView;

    public void run() {
        isolinesView = new IsolinesView(this);
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        isolinesView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        isolinesView.getStatusBarView().setMessage("");
    }
}
