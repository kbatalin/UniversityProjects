package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.FilterView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 11.03.17.
 */
public class FilterController {
    private FilterView filterView;

    public void run() {
        filterView = new FilterView(this);
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        filterView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        filterView.getStatusBarView().setMessage("");
    }
}
