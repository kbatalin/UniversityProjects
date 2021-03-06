package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.ImageModel;
import ru.nsu.fit.g14205.batalin.models.VRLoader;
import ru.nsu.fit.g14205.batalin.models.filters.*;
import ru.nsu.fit.g14205.batalin.views.*;

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

    private VRFilter vrFilter;

    private FilterFactory filterFactory;

    private JFileChooser fileOpenChooser;
    private FileFilter imageFileFilter;
    private FileFilter settingsFileFilter;
    private FileFilter allFilesFilter;
    private JFileChooser fileSaveChooser;
    private FileNameExtensionFilter bmpFilter;
    private FileNameExtensionFilter pngFilter;
    private FileNameExtensionFilter jpgFilter;

    private FilterView filterView;

    private JDialog aboutDialog;
    private AboutView aboutView;

    public void run() {
        fileOpenChooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "FIT_14205_Batalin_Kirill_Filter_Data");
        fileOpenChooser.setCurrentDirectory(workingDirectory);
        allFilesFilter = fileOpenChooser.getFileFilter();
        imageFileFilter = new FileNameExtensionFilter("Images (*.bmp, *.jpg, *.png)", "jpg", "bmp", "png");
        settingsFileFilter = new FileNameExtensionFilter("Text (*.txt)", "txt");

        fileSaveChooser = new JFileChooser();
        fileSaveChooser.setCurrentDirectory(workingDirectory);
        bmpFilter = new FileNameExtensionFilter("bmp images (*.bmp)", "bmp");
        fileSaveChooser.addChoosableFileFilter(bmpFilter);
        fileSaveChooser.setFileFilter(bmpFilter);
        pngFilter = new FileNameExtensionFilter("png images (*.png)", "png");
        fileSaveChooser.addChoosableFileFilter(pngFilter);
        fileSaveChooser.setFileFilter(pngFilter);
        jpgFilter = new FileNameExtensionFilter("jpg images (*.jpg)", "jpg");
        fileSaveChooser.addChoosableFileFilter(jpgFilter);
        fileSaveChooser.setFileFilter(jpgFilter);

        aImageModel = new ImageModel();
        bImageModel = new ImageModel();
        cImageModel = new ImageModel();

        filterView = new FilterView(this, aImageModel, bImageModel, cImageModel);
        filterView.setLocationRelativeTo(null);

        initFilterFactory();
    }

    private void initFilterFactory() {
        filterFactory = new FilterFactory();

        filterFactory.add("Black and white", BlackWhiteFilter::new);
        filterFactory.add("Negative", NegativeFilter::new);
        filterFactory.add("Floyd Steinberg", () -> {
            FloydSteinbergView dialog = new FloydSteinbergView();
            dialog.pack();
            dialog.setLocationRelativeTo(filterView);
            dialog.setVisible(true);

            if (!dialog.getResult()) {
                return null;
            }

            return new FloydSteinbergFilter(dialog.getRed(), dialog.getGreen(), dialog.getBlue());
        });
        filterFactory.add("Ordered dither", OrderedDitherFilter::new);
        filterFactory.add("Roberts", () -> {
            RobertsSobelView dialog = new RobertsSobelView();
            dialog.pack();
            dialog.setLocationRelativeTo(filterView);
            dialog.setVisible(true);

            if (!dialog.getResult()) {
                return null;
            }

            return new RobertsFilter(dialog.getLevelValue());
        });
        filterFactory.add("Sobel", () -> {
            RobertsSobelView dialog = new RobertsSobelView();
            dialog.pack();
            dialog.setLocationRelativeTo(filterView);
            dialog.setVisible(true);

            if (!dialog.getResult()) {
                return null;
            }

            return new SobelFilter(dialog.getLevelValue());
        });
        filterFactory.add("Blur", BlurFilter::new);
        filterFactory.add("Sharp", SharpFilter::new);
        filterFactory.add("Emboss", EmbossFilter::new);
        filterFactory.add("Watercolor", WatercolorFilter::new);
        filterFactory.add("Rotation", () -> {
            RotationView dialog = new RotationView();
            dialog.pack();
            dialog.setLocationRelativeTo(filterView);
            dialog.setVisible(true);

            if (!dialog.getResult()) {
                return null;
            }

            return new RotationFilter(dialog.getAngle());
        });
        filterFactory.add("Gamma", () -> {
            GammaView dialog = new GammaView();
            dialog.pack();
            dialog.setLocationRelativeTo(filterView);
            dialog.setVisible(true);

            if (!dialog.getResult()) {
                return null;
            }

            return new GammaFilter(dialog.getGamma());
        });
        filterFactory.add("Zoom", ZoomFilter::new);
    }

    public void onVRSettingsButtonClicked() {
        fileOpenChooser.setFileFilter(settingsFileFilter);

        int result = fileOpenChooser.showOpenDialog(filterView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = fileOpenChooser.getSelectedFile();
            VRLoader loader = new VRLoader();
            loader.load(file);

            vrFilter = new VRFilter(loader.getAbsorptionModel(), loader.getEmissionModel(), loader.getChargeModel());

            filterView.getWorkspaceView().getAbsorptionView().setAbsorptionModel(loader.getAbsorptionModel());
            filterView.getWorkspaceView().getAbsorptionView().repaint();

            filterView.getWorkspaceView().getEmissionView().setEmissionModel(loader.getEmissionModel());
            filterView.getWorkspaceView().getEmissionView().repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(filterView,"Can't load config: " + e.getMessage(),"Open error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onVRStartButtonClicked() {
        if (vrFilter == null) {
            return;
        }

        BufferedImage srcImage = bImageModel.getImage();
        if (srcImage == null) {
            return;
        }

        VRView dialog = new VRView();
        dialog.pack();
        dialog.setLocationRelativeTo(filterView);
        dialog.setVisible(true);

        if (!dialog.getResult()) {
            return;
        }

        BufferedImage image = vrFilter.process(
                srcImage,
                filterView.isVRAbsorptionSelected(),
                filterView.isVREmissionSelected(),
                dialog.getNxValue(),
                dialog.getNyValue(),
                dialog.getNzValue()
        );
        cImageModel.setImage(image);
    }

    public void onNewButtonClicked() {
        resetImages();
    }

    public void onFilterButtonClicked(String filterName) {
        BufferedImage srcImage = bImageModel.getImage();
        if (srcImage == null) {
            return;
        }

        Filter filter = filterFactory.get(filterName);
        if (filter == null) {
            return;
        }

        BufferedImage image = filter.process(srcImage);
        cImageModel.setImage(image);
    }

    public void onExitButtonClicked() {
        filterView.setVisible(false);
        System.exit(0);
    }

    public void onSaveButtonClicked() {
        saveImage();
    }

    private boolean saveImage() {
        BufferedImage image = cImageModel.getImage();
        if (image == null) {
            return false;
        }

        int result = fileSaveChooser.showSaveDialog(filterView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = fileSaveChooser.getSelectedFile();
        try {
            String extension;
            FileFilter activeFilter = fileSaveChooser.getFileFilter();
            if(pngFilter.equals(activeFilter)) {
                extension = "png";
            } else if (bmpFilter.equals(activeFilter)) {
                extension = "bmp";
            } else if (jpgFilter.equals(activeFilter)) {
                extension = "jpg";
            } else {
                throw new IllegalArgumentException("Bad file name");
            }

            if (!file.getName().endsWith("." + extension)) {
                file = new File(file.getAbsolutePath() + "." + extension);
            }

            return ImageIO.write(image, extension, file);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(filterView,"Can't save image: " + e.getMessage(),"Save error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void onSelectButtonClicked(boolean isSelected) {
        filterView.getSelectButton().setSelected(isSelected);
        filterView.getEditMenuSelect().setState(isSelected);
    }

    public void onCopyBToCButtonClicked() {
        cImageModel.setImage(bImageModel.getImage());
    }

    public void onCopyCToBButtonClicked() {
        bImageModel.setImage(cImageModel.getImage());
    }

    public void onMousePressed(ImageView imageView, MouseEvent mouseEvent) {
        onMouseDragged(imageView, mouseEvent);
    }

    public void onMouseDragged(ImageView imageView, MouseEvent mouseEvent) {
        boolean isSelectButtonPushed = filterView.getSelectButton().isSelected();
        if (!isSelectButtonPushed) {
            return;
        }

        if (imageView != filterView.getWorkspaceView().getAImage()) {
            return;
        }

        Point pos = mouseEvent.getPoint();
        selectImageArea(pos);
    }

    private void selectImageArea(Point pos) {
        BufferedImage originalImage = aImageModel.getImage();
        if (originalImage == null) {
            bImageModel.setImage(null);
            return;
        }
        int imageSize = Math.max(originalImage.getHeight(), originalImage.getWidth());
        double sizeRatio = imageSize / 350.;
        int selectedAreaSize = Math.min(350, (int) (350 / sizeRatio));

        pos.x -= Math.max(0, pos.x + selectedAreaSize / 2 - 350);
        pos.y -= Math.max(0, pos.y + selectedAreaSize / 2 - 350);

        pos.x = Math.max(0, pos.x - selectedAreaSize / 2);
        pos.y = Math.max(0, pos.y - selectedAreaSize / 2);
        Rectangle selectedAreaRect = new Rectangle(pos.x, pos.y, selectedAreaSize, selectedAreaSize);

        ImageView aImage = filterView.getWorkspaceView().getAImage();
        aImage.setSelectedArea(selectedAreaRect);
        aImage.repaint();

        Rectangle selectedArea = new Rectangle((int)(selectedAreaRect.x * sizeRatio), (int)(selectedAreaRect.y * sizeRatio), 350, 350);

        selectedArea.setSize(
                Math.max(0, 350 - Math.max(0, (selectedArea.x + 350 - originalImage.getWidth()))),
                Math.max(0, 350 - Math.max(0, (selectedArea.y + 350 - originalImage.getHeight())))
                );
        if (selectedArea.width == 0 || selectedArea.height == 0) {
            bImageModel.setImage(null);
            return;
        }

        BufferedImage selectedImage = aImageModel.getImage(selectedArea);
        bImageModel.setImage(selectedImage);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        boolean isSelectButtonPushed = filterView.getSelectButton().isSelected();
        if (!isSelectButtonPushed) {
            return;
        }

        ImageView aImage = filterView.getWorkspaceView().getAImage();
        aImage.setSelectedArea(null);
        aImage.repaint();
    }

    private void resetImages() {
        aImageModel.reset();
        bImageModel.reset();
        cImageModel.reset();
    }

    public void onOpenButtonClicked() {
        fileOpenChooser.setFileFilter(imageFileFilter);

        int result = fileOpenChooser.showOpenDialog(filterView);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = fileOpenChooser.getSelectedFile();
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
