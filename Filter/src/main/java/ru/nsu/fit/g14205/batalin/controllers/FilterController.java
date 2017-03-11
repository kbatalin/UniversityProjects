package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.views.AboutView;
import ru.nsu.fit.g14205.batalin.views.FilterView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 11.03.17.
 */
public class FilterController {
    private FilterView filterView;

    private JDialog aboutDialog;
    private AboutView aboutView;

    public void run() {
        filterView = new FilterView(this);
        filterView.setLocationRelativeTo(null);
    }

    public void onAboutButtonClicked() {
        if (aboutDialog != null) {
            return;
        }

        aboutView = new AboutView(this);
        aboutView.setLocationRelativeTo(filterView);
        aboutDialog = new JDialog(aboutView, "About", Dialog.ModalityType.DOCUMENT_MODAL);
    }

    public void onAboutDialogClosing() {
        if (aboutView != null) {
            aboutView.setVisible(false);
            aboutView = null;
        }

        aboutDialog = null;
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
