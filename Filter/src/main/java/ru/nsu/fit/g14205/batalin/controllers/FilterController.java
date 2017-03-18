package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.ImageModel;
import ru.nsu.fit.g14205.batalin.views.AboutView;
import ru.nsu.fit.g14205.batalin.views.FilterView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by kir55rus on 11.03.17.
 */
public class FilterController {
    private ImageModel aImageModel;
    private ImageModel bImageModel;
    private ImageModel cImageModel;

    private JFileChooser fileChooser;
    private FileFilter imageFileFilter;
    private FileFilter allFilesFilter;

    private FilterView filterView;

    private JDialog aboutDialog;
    private AboutView aboutView;

    public void run() {
        fileChooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "FIT_14205_Batalin_Kirill_Filter_Data");
        fileChooser.setCurrentDirectory(workingDirectory);
        allFilesFilter = fileChooser.getFileFilter();
        imageFileFilter = new FileNameExtensionFilter("Images (*.bmp, *.jpg, *.png)", "jpg", "bmp", "png");

        aImageModel = new ImageModel();
        bImageModel = new ImageModel();
        cImageModel = new ImageModel();

        filterView = new FilterView(this, aImageModel, bImageModel, cImageModel);
        filterView.setLocationRelativeTo(null);
    }

    public void onNewButtonClicked() {
        resetImages();
    }

    private void resetImages() {
        aImageModel.reset();
        bImageModel.reset();
        cImageModel.reset();
    }

    public void onOpenButtonClicked() {
        fileChooser.setFileFilter(imageFileFilter);

        int result = fileChooser.showOpenDialog(filterView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = fileChooser.getSelectedFile();
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IllegalArgumentException("Bad file");
            }
            resetImages();
            aImageModel.setImage(image);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(filterView,"Can't load image: " + e.getMessage(),"Open error", JOptionPane.ERROR_MESSAGE);
        }
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
