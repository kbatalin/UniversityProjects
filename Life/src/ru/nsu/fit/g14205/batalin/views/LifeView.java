package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeView extends JFrame {
    private LifeController lifeController;

    public LifeView(LifeController lifeController) {
        this.lifeController = lifeController;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMenu();

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
}
