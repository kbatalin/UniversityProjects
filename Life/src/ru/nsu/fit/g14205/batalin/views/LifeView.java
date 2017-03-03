package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.IFieldModel;
import ru.nsu.fit.g14205.batalin.models.IPropertiesModel;
import ru.nsu.fit.g14205.batalin.models.PropertiesModelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeView extends JFrame implements Observer {
    private LifeController lifeController;

    private JScrollPane scrollPane;
    private FieldView fieldView;

    private JCheckBoxMenuItem viewMenuImpact;
    private JToggleButton impactButton;

    private JRadioButtonMenuItem modeReplace;
    private JRadioButtonMenuItem modeXor;
    private JToggleButton replaceButton;
    private JToggleButton xorButton;
    private JToggleButton runButton;
    private JMenuItem actionMenuRun;

    public LifeView(LifeController lifeController, IFieldModel fieldModel, IPropertiesModel propertiesModel) {
        this.lifeController = lifeController;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMenu();

        setLayout(new BorderLayout());

        initToolbar();

        initField(lifeController, fieldModel, propertiesModel);

        propertiesModel.addObserver(PropertiesModelEvent.SIZE_CHANGED, () -> {
            fieldView.revalidate();
            scrollPane.repaint();
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        //todo
    }

    private void runButtonClicked(boolean isSelected) {
        if(!isSelected) {
            actionMenuRun.setText("Run");
            Icon runButtonIcon = getButtonIcon("images/media_controls_dark_play.png");
            if (runButtonIcon != null) {
                runButton.setIcon(runButtonIcon);
            }
            runButton.setSelected(false);
            lifeController.onRunButtonClicked(false);
            return;
        }

        actionMenuRun.setText("Stop");
        Icon runButtonIcon = getButtonIcon("images/media_controls_dark_pause.png");
        if (runButtonIcon != null) {
            runButton.setIcon(runButtonIcon);
        }
        runButton.setSelected(true);
        lifeController.onRunButtonClicked(true);
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
        editMenuClear.addActionListener(actionEvent -> lifeController.onClearButtonClicked());
        editMenu.add(editMenuClear);

        JMenu editMenuMode = new JMenu("Mode");
        editMenuMode.setMnemonic(KeyEvent.VK_M);
        editMenu.add(editMenuMode);

        ButtonGroup modeGroup = new ButtonGroup();

        modeReplace = new JRadioButtonMenuItem("Replace");
        modeReplace.setSelected(true);
        modeReplace.setMnemonic(KeyEvent.VK_R);
        modeReplace.addActionListener(actionEvent -> {
            replaceButton.setSelected(true);
            lifeController.onReplaceModeClicked();
        });
        modeGroup.add(modeReplace);
        editMenuMode.add(modeReplace);

        modeXor = new JRadioButtonMenuItem("XOR");
        modeXor.setMnemonic(KeyEvent.VK_X);
        modeXor.addActionListener(actionEvent -> {
            xorButton.setSelected(true);
            lifeController.onXorModeClicked();
        });
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

        actionMenuRun = new JMenuItem("Run");
        actionMenuRun.setMnemonic(KeyEvent.VK_R);
        actionMenuRun.addActionListener(actionEvent -> {
            boolean isSelected = runButton.isSelected();
            runButtonClicked(!isSelected);
        });
        actionMenu.add(actionMenuRun);

        JMenuItem actionMenuNext = new JMenuItem("Next");
        actionMenuNext.setMnemonic(KeyEvent.VK_N);
        actionMenuNext.addActionListener(actionEvent -> lifeController.onNextButtonClicked());
        actionMenu.add(actionMenuNext);
    }

    private void initViewMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu);

        JCheckBoxMenuItem viewMenuColors = new JCheckBoxMenuItem("Colors");
        viewMenuColors.setMnemonic(KeyEvent.VK_C);
        viewMenu.add(viewMenuColors);

        viewMenuImpact = new JCheckBoxMenuItem("Impact");
        viewMenuImpact.setMnemonic(KeyEvent.VK_I);
        viewMenuImpact.addActionListener(actionEvent -> {
            impactButton.setSelected(viewMenuImpact.getState());
            lifeController.onImpactButtonClicked(viewMenuImpact.getState());
        });
        viewMenu.add(viewMenuImpact);
    }

    private void initHelpMenu(JMenuBar menuBar) {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.setMnemonic(KeyEvent.VK_A);
        helpMenuAbout.addActionListener(actionEvent -> lifeController.onAboutButtonClicked());
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
        clearButton.addActionListener(actionEvent -> lifeController.onClearButtonClicked());
        toolBar.add(clearButton);

        ButtonGroup modeGroup = new ButtonGroup();
        replaceButton = new JToggleButton();
        replaceButton.setToolTipText("Replace cell");
        Icon replaceButtonIcon = getButtonIcon("images/add.png");
        if (replaceButtonIcon != null) {
            replaceButton.setIcon(replaceButtonIcon);
        }
        replaceButton.addActionListener(actionEvent -> {
            modeReplace.setSelected(true);
            lifeController.onReplaceModeClicked();
        });
        replaceButton.setSelected(true);
        modeGroup.add(replaceButton);
        toolBar.add(replaceButton);

        xorButton = new JToggleButton();
        xorButton.setToolTipText("Xor cell");
        Icon xorButtonIcon = getButtonIcon("images/add_outline.png");
        if (xorButtonIcon != null) {
            xorButton.setIcon(xorButtonIcon);
        }
        xorButton.addActionListener(actionEvent -> {
            modeXor.setSelected(true);
            lifeController.onXorModeClicked();
        });
        modeGroup.add(xorButton);
        toolBar.add(xorButton);


        JButton propertiesButton = new JButton();
        propertiesButton.setToolTipText("Properties");
        Icon propertiesButtonIcon = getButtonIcon("images/sprocket_dark.png");
        if (propertiesButtonIcon != null) {
            propertiesButton.setIcon(propertiesButtonIcon);
        }
        toolBar.add(propertiesButton);

        toolBar.addSeparator();

        runButton = new JToggleButton();
        runButton.setToolTipText("Run");
        Icon runButtonIcon = getButtonIcon("images/media_controls_dark_play.png");
        if (runButtonIcon != null) {
            runButton.setIcon(runButtonIcon);
        }
        runButton.addActionListener(actionEvent -> {
            runButtonClicked(runButton.isSelected());
        });
        toolBar.add(runButton);

        JButton nextButton = new JButton();
        nextButton.setToolTipText("Next step");
        Icon nextButtonIcon = getButtonIcon("images/pagination_1_last.png");
        if (nextButtonIcon != null) {
            nextButton.setIcon(nextButtonIcon);
        }
        nextButton.addActionListener(actionEvent -> lifeController.onNextButtonClicked());
        toolBar.add(nextButton);

        toolBar.addSeparator();

        JButton colorButton = new JButton();
        colorButton.setToolTipText("Color");
        Icon colorButtonIcon = getButtonIcon("images/rich_text_color.png");
        if (colorButtonIcon != null) {
            colorButton.setIcon(colorButtonIcon);
        }
        toolBar.add(colorButton);

        impactButton = new JToggleButton();
        impactButton.setToolTipText("Impact");
        Icon impactButtonIcon = getButtonIcon("images/rich_text_italics.png");
        if (impactButtonIcon != null) {
            impactButton.setIcon(impactButtonIcon);
        }
        impactButton.addActionListener(actionEvent -> {
            viewMenuImpact.setState(impactButton.isSelected());
            lifeController.onImpactButtonClicked(impactButton.isSelected());
        });
        toolBar.add(impactButton);

        toolBar.addSeparator();

        JButton aboutButton = new JButton();
        aboutButton.setToolTipText("About");
        Icon aboutButtonIcon = getButtonIcon("images/information.png");
        if (aboutButtonIcon != null) {
            aboutButton.setIcon(aboutButtonIcon);
        }
        aboutButton.addActionListener(actionEvent -> lifeController.onAboutButtonClicked());
        toolBar.add(aboutButton);
    }

    private void initField(LifeController lifeController, IFieldModel fieldModel, IPropertiesModel propertiesModel) {
        fieldView = new FieldView(lifeController, fieldModel, propertiesModel);
        scrollPane = new JScrollPane(fieldView);
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
