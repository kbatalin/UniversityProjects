package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.EditorDialog;
import ru.nsu.fit.g14205.batalin.views.WireframeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WireframeController {
    private WireframeView wireframeView;

    public void run() {
        wireframeView = new WireframeView(this);
        wireframeView.setVisible(true);
    }

    public void onLineEditButtonClicked() {
        EditorDialog dialog = new EditorDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(wireframeView);
        dialog.setVisible(true);
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        wireframeView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        wireframeView.getStatusBarView().setMessage("");
    }
}
