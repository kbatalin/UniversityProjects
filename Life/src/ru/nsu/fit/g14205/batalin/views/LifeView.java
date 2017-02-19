package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeView extends JFrame {
    private LifeController lifeController;

    public LifeView(LifeController lifeController, Dimension fieldSize, int hexSize) {
        this.lifeController = lifeController;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMenu();

        setLayout(new BorderLayout());

        initToolbar();

        initField(fieldSize, hexSize);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        initFileMenu(menuBar);
        initEditMenu(menuBar);
        initActionMenu(menuBar);
        initViewMenu(menuBar);
        initHelpMenu(menuBar);

        setJMenuBar(menuBar);
    }

    private void initFileMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem fileMenuNew = new JMenuItem("New");
        fileMenuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        fileMenuNew.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(fileMenuNew);

        JMenuItem fileMenuOpen = new JMenuItem("Open...");
        fileMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        fileMenuOpen.setMnemonic(KeyEvent.VK_O);
        fileMenu.add(fileMenuOpen);

        JMenuItem fileMenuSave = new JMenuItem("Save");
        fileMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        fileMenuSave.setMnemonic(KeyEvent.VK_S);
        fileMenu.add(fileMenuSave);

        JMenuItem fileMenuSaveAs = new JMenuItem("Save as...");
        fileMenuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        fileMenuSaveAs.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(fileMenuSaveAs);

        fileMenu.addSeparator();

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        fileMenuExit.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(fileMenuExit);
    }

    private void initEditMenu(JMenuBar menuBar) {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        JMenuItem editMenuClear = new JMenuItem("Clear");
        editMenuClear.setMnemonic(KeyEvent.VK_C);
        editMenu.add(editMenuClear);

        JMenu editMenuMode = new JMenu("Mode");
        editMenuMode.setMnemonic(KeyEvent.VK_M);
        editMenu.add(editMenuMode);

        ButtonGroup modeGroup = new ButtonGroup();

        JRadioButtonMenuItem modeReplace = new JRadioButtonMenuItem("Replace");
        modeReplace.setSelected(true);
        modeReplace.setMnemonic(KeyEvent.VK_R);
        modeGroup.add(modeReplace);
        editMenuMode.add(modeReplace);

        JRadioButtonMenuItem modeXor = new JRadioButtonMenuItem("XOR");
        modeXor.setMnemonic(KeyEvent.VK_X);
        modeGroup.add(modeXor);
        editMenuMode.add(modeXor);

        editMenu.addSeparator();

        JMenuItem editMenuProperties = new JMenuItem("Properties");
        editMenuProperties.setMnemonic(KeyEvent.VK_P);
        editMenu.add(editMenuProperties);
    }

    private void initActionMenu(JMenuBar menuBar) {
        JMenu actionMenu = new JMenu("Action");
        actionMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(actionMenu);

        JMenuItem actionMenuRun = new JMenuItem("Run");
        actionMenuRun.setMnemonic(KeyEvent.VK_R);
        actionMenu.add(actionMenuRun);

        JMenuItem actionMenuNext = new JMenuItem("Next");
        actionMenuNext.setMnemonic(KeyEvent.VK_N);
        actionMenu.add(actionMenuNext);
    }

    private void initViewMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu);

        JCheckBoxMenuItem viewMenuColors = new JCheckBoxMenuItem("Colors");
        viewMenuColors.setMnemonic(KeyEvent.VK_C);
        viewMenu.add(viewMenuColors);

        JCheckBoxMenuItem viewMenuImpact = new JCheckBoxMenuItem("Impact");
        viewMenuImpact.setMnemonic(KeyEvent.VK_I);
        viewMenu.add(viewMenuImpact);
    }

    private void initHelpMenu(JMenuBar menuBar) {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(helpMenuAbout);
    }

    private void initToolbar() {
        JToolBar toolBar = new JToolBar("Toolbar");
        add(toolBar, BorderLayout.PAGE_START);

        JButton newButton = new JButton();
        newButton.setToolTipText("Create new field");
        Icon newButtonIcon = getButtonIcon("images/document_a4_new.png");
        if (newButtonIcon != null) {
            newButton.setIcon(newButtonIcon);
        }
        toolBar.add(newButton);

        JButton openButton = new JButton();
        openButton.setToolTipText("Open saved field");
        Icon openButtonIcon = getButtonIcon("images/folder_classic_up.png");
        if (openButtonIcon != null) {
            openButton.setIcon(openButtonIcon);
        }
        toolBar.add(openButton);

        JButton saveButton = new JButton();
        saveButton.setToolTipText("Save field");
        Icon saveButtonIcon = getButtonIcon("images/folder_classic_down.png");
        if (saveButtonIcon != null) {
            saveButton.setIcon(saveButtonIcon);
        }
        toolBar.add(saveButton);

        JButton saveAsButton = new JButton();
        saveAsButton.setToolTipText("Save field as...");
        Icon saveAsButtonIcon = getButtonIcon("images/folder_modernist_down.png");
        if (saveAsButtonIcon != null) {
            saveAsButton.setIcon(saveAsButtonIcon);
        }
        toolBar.add(saveAsButton);

        JButton closeButton = new JButton();
        closeButton.setToolTipText("Exit");
        Icon closeButtonIcon = getButtonIcon("images/application_windows_remove.png");
        if (closeButtonIcon != null) {
            closeButton.setIcon(closeButtonIcon);
        }
        toolBar.add(closeButton);

        toolBar.addSeparator();

        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear field");
        Icon clearButtonIcon = getButtonIcon("images/document_a4_blank.png");
        if (clearButtonIcon != null) {
            clearButton.setIcon(clearButtonIcon);
        }
        toolBar.add(clearButton);

        JButton replaceButton = new JButton();
        replaceButton.setToolTipText("Replace cell");
        Icon replaceButtonIcon = getButtonIcon("images/add.png");
        if (replaceButtonIcon != null) {
            replaceButton.setIcon(replaceButtonIcon);
        }
        toolBar.add(replaceButton);

        JButton xorButton = new JButton();
        xorButton.setToolTipText("Xor cell");
        Icon xorButtonIcon = getButtonIcon("images/add_outline.png");
        if (xorButtonIcon != null) {
            xorButton.setIcon(xorButtonIcon);
        }
        toolBar.add(xorButton);


        JButton propertiesButton = new JButton();
        propertiesButton.setToolTipText("Properties");
        Icon propertiesButtonIcon = getButtonIcon("images/sprocket_dark.png");
        if (propertiesButtonIcon != null) {
            propertiesButton.setIcon(propertiesButtonIcon);
        }
        toolBar.add(propertiesButton);

        toolBar.addSeparator();

        JButton runButton = new JButton();
        runButton.setToolTipText("Run");
        Icon runButtonIcon = getButtonIcon("images/media_controls_dark_play.png");
        if (runButtonIcon != null) {
            runButton.setIcon(runButtonIcon);
        }
        toolBar.add(runButton);

        JButton nextButton = new JButton();
        nextButton.setToolTipText("Next step");
        Icon nextButtonIcon = getButtonIcon("images/pagination_1_last.png");
        if (nextButtonIcon != null) {
            nextButton.setIcon(nextButtonIcon);
        }
        toolBar.add(nextButton);

        toolBar.addSeparator();

        JButton colorButton = new JButton();
        colorButton.setToolTipText("Color");
        Icon colorButtonIcon = getButtonIcon("images/rich_text_color.png");
        if (colorButtonIcon != null) {
            colorButton.setIcon(colorButtonIcon);
        }
        toolBar.add(colorButton);

        JButton impactButton = new JButton();
        impactButton.setToolTipText("Impact");
        Icon impactButtonIcon = getButtonIcon("images/rich_text_italics.png");
        if (impactButtonIcon != null) {
            impactButton.setIcon(impactButtonIcon);
        }
        toolBar.add(impactButton);

        toolBar.addSeparator();

        JButton aboutButton = new JButton();
        aboutButton.setToolTipText("About");
        Icon aboutButtonIcon = getButtonIcon("images/information.png");
        if (aboutButtonIcon != null) {
            aboutButton.setIcon(aboutButtonIcon);
        }
        toolBar.add(aboutButton);
    }

    private void initField(Dimension fieldSize, int hexSize) {
        FieldView fieldView = new FieldView(fieldSize, hexSize);
        JScrollPane scrollPane = new JScrollPane(fieldView);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Icon getButtonIcon(String imgPath) {
        URL imgUrl = this.getClass().getClassLoader().getResource(imgPath);
        if (imgUrl == null) {
            return null;
        }

        return new ImageIcon(imgUrl);
    }
}
