package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.IFieldModel;
import ru.nsu.fit.g14205.batalin.models.IPropertiesModel;
import ru.nsu.fit.g14205.batalin.models.PaintMode;
import ru.nsu.fit.g14205.batalin.models.PropertiesModelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Created by kir55rus on 10.02.17.
 */
public class LifeView extends JFrame {
    private LifeController lifeController;
    private IPropertiesModel propertiesModel;

    private JScrollPane scrollPane;
    private FieldView fieldView;
    private StatusBarView statusBarView;

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
        this.propertiesModel = propertiesModel;

        setMinimumSize(new Dimension(800, 600));
        setSize(800, 600);
        setTitle("Life");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                lifeController.onCloseButtonClicked();
            }
        });

        initMenu();

        setLayout(new BorderLayout());

        initToolbar();

        initField(lifeController, fieldModel, propertiesModel);

        initStatusBar();

        propertiesModel.addObserver(PropertiesModelEvent.HEX_SIZE_CHANGED, () -> {
            fieldView.revalidate();
            scrollPane.repaint();
        });

        propertiesModel.addObserver(PropertiesModelEvent.FIELD_SIZE_CHANGED, () -> {
            fieldView.revalidate();
            scrollPane.repaint();
        });

        propertiesModel.addObserver(PropertiesModelEvent.PAINTING_MODE_CHANGED, () -> {
            PaintMode mode = propertiesModel.getPaintMode();
            boolean isReplaceMode = mode == PaintMode.REPLACE;

            SwingUtilities.invokeLater(() -> {
                replaceButton.setSelected(isReplaceMode);
                modeReplace.setSelected(isReplaceMode);
                xorButton.setSelected(!isReplaceMode);
                modeXor.setSelected(!isReplaceMode);
            });
        });
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
        fileMenuNew.addActionListener(actionEvent -> lifeController.onNewFieldButtonClicked());
        fileMenu.add(fileMenuNew);

        JMenuItem fileMenuOpen = new JMenuItem("Open...");
        fileMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        fileMenuOpen.setMnemonic(KeyEvent.VK_O);
        fileMenuOpen.addActionListener(actionEvent -> lifeController.onOpenButtonClicked());
        fileMenu.add(fileMenuOpen);

        JMenuItem fileMenuSave = new JMenuItem("Save");
        fileMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        fileMenuSave.setMnemonic(KeyEvent.VK_S);
        fileMenuSave.addActionListener(actionEvent -> lifeController.onSaveButtonClicked());
        fileMenu.add(fileMenuSave);

        JMenuItem fileMenuSaveAs = new JMenuItem("Save as...");
        fileMenuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        fileMenuSaveAs.setMnemonic(KeyEvent.VK_A);
        fileMenuSaveAs.addActionListener(actionEvent -> lifeController.onSaveAsButtonClicked());
        fileMenu.add(fileMenuSaveAs);

        fileMenu.addSeparator();

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        fileMenuExit.setMnemonic(KeyEvent.VK_X);
        fileMenuExit.addActionListener(actionEvent -> lifeController.onCloseButtonClicked());
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
        modeReplace.setSelected(propertiesModel.getPaintMode() == PaintMode.REPLACE);
        modeReplace.setMnemonic(KeyEvent.VK_R);
        modeReplace.addActionListener(actionEvent -> {
            lifeController.onReplaceModeClicked();
        });
        modeGroup.add(modeReplace);
        editMenuMode.add(modeReplace);

        modeXor = new JRadioButtonMenuItem("XOR");
        modeXor.setMnemonic(KeyEvent.VK_X);
        modeXor.setSelected(propertiesModel.getPaintMode() == PaintMode.XOR);
        modeXor.addActionListener(actionEvent -> {
            lifeController.onXorModeClicked();
        });
        modeGroup.add(modeXor);
        editMenuMode.add(modeXor);

        editMenu.addSeparator();

        JMenuItem editMenuProperties = new JMenuItem("Properties");
        editMenuProperties.setMnemonic(KeyEvent.VK_P);
        editMenu.add(editMenuProperties);
        editMenuProperties.addActionListener(actionEvent -> lifeController.onPropertiesButtonClicked());
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

//        JCheckBoxMenuItem viewMenuColors = new JCheckBoxMenuItem("Colors");
//        viewMenuColors.setMnemonic(KeyEvent.VK_C);
//        viewMenu.add(viewMenuColors);

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
        newButton.addActionListener(actionEvent -> lifeController.onNewFieldButtonClicked());
        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(newButton);

        JButton openButton = new JButton();
        openButton.setToolTipText("Open saved field");
        Icon openButtonIcon = getButtonIcon("images/folder_classic_up.png");
        if (openButtonIcon != null) {
            openButton.setIcon(openButtonIcon);
        }
        openButton.addActionListener(actionEvent -> lifeController.onOpenButtonClicked());
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(openButton);

        JButton saveButton = new JButton();
        saveButton.setToolTipText("Save field");
        Icon saveButtonIcon = getButtonIcon("images/folder_classic_down.png");
        if (saveButtonIcon != null) {
            saveButton.setIcon(saveButtonIcon);
        }
        saveButton.addActionListener(actionEvent -> lifeController.onSaveButtonClicked());
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(saveButton);

        JButton saveAsButton = new JButton();
        saveAsButton.setToolTipText("Save field as...");
        Icon saveAsButtonIcon = getButtonIcon("images/folder_modernist_down.png");
        if (saveAsButtonIcon != null) {
            saveAsButton.setIcon(saveAsButtonIcon);
        }
        saveAsButton.addActionListener(actionEvent -> lifeController.onSaveAsButtonClicked());
        saveAsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(saveAsButton);

        JButton closeButton = new JButton();
        closeButton.setToolTipText("Exit");
        Icon closeButtonIcon = getButtonIcon("images/application_windows_remove.png");
        if (closeButtonIcon != null) {
            closeButton.setIcon(closeButtonIcon);
        }
        closeButton.addActionListener(actionEvent -> lifeController.onCloseButtonClicked());
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(closeButton);

        toolBar.addSeparator();

        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear field");
        Icon clearButtonIcon = getButtonIcon("images/document_a4_blank.png");
        if (clearButtonIcon != null) {
            clearButton.setIcon(clearButtonIcon);
        }
        clearButton.addActionListener(actionEvent -> lifeController.onClearButtonClicked());
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(clearButton);

        ButtonGroup modeGroup = new ButtonGroup();
        replaceButton = new JToggleButton();
        replaceButton.setToolTipText("Replace cell");
        Icon replaceButtonIcon = getButtonIcon("images/add.png");
        if (replaceButtonIcon != null) {
            replaceButton.setIcon(replaceButtonIcon);
        }
        replaceButton.addActionListener(actionEvent -> {
            lifeController.onReplaceModeClicked();
        });
        replaceButton.setSelected(propertiesModel.getPaintMode() == PaintMode.REPLACE);
        replaceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        modeGroup.add(replaceButton);
        toolBar.add(replaceButton);

        xorButton = new JToggleButton();
        xorButton.setToolTipText("Xor cell");
        Icon xorButtonIcon = getButtonIcon("images/add_outline.png");
        if (xorButtonIcon != null) {
            xorButton.setIcon(xorButtonIcon);
        }
        xorButton.addActionListener(actionEvent -> {
            lifeController.onXorModeClicked();
        });
        xorButton.setSelected(propertiesModel.getPaintMode() == PaintMode.XOR);
        xorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        modeGroup.add(xorButton);
        toolBar.add(xorButton);


        JButton propertiesButton = new JButton();
        propertiesButton.setToolTipText("Properties");
        Icon propertiesButtonIcon = getButtonIcon("images/sprocket_dark.png");
        if (propertiesButtonIcon != null) {
            propertiesButton.setIcon(propertiesButtonIcon);
        }
        propertiesButton.addActionListener(actionEvent -> lifeController.onPropertiesButtonClicked());
        propertiesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(propertiesButton);

        toolBar.addSeparator();

        runButton = new JToggleButton();
        runButton.setToolTipText("Run");
        Icon runButtonIcon = getButtonIcon("images/media_controls_dark_play.png");
        if (runButtonIcon != null) {
            runButton.setIcon(runButtonIcon);
        }
        runButton.addActionListener(actionEvent -> runButtonClicked(runButton.isSelected()));
        runButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(runButton);

        JButton nextButton = new JButton();
        nextButton.setToolTipText("Next step");
        Icon nextButtonIcon = getButtonIcon("images/pagination_1_last.png");
        if (nextButtonIcon != null) {
            nextButton.setIcon(nextButtonIcon);
        }
        nextButton.addActionListener(actionEvent -> lifeController.onNextButtonClicked());
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(nextButton);

        toolBar.addSeparator();

//        JButton colorButton = new JButton();
//        colorButton.setToolTipText("Color");
//        Icon colorButtonIcon = getButtonIcon("images/rich_text_color.png");
//        if (colorButtonIcon != null) {
//            colorButton.setIcon(colorButtonIcon);
//        }
//        colorButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//                lifeController.onEnterToolbarButton(mouseEvent);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//                lifeController.onExitToolbarButton(mouseEvent);
//            }
//        });
//        toolBar.add(colorButton);

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
        impactButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(impactButton);

        toolBar.addSeparator();

        JButton aboutButton = new JButton();
        aboutButton.setToolTipText("About");
        Icon aboutButtonIcon = getButtonIcon("images/information.png");
        if (aboutButtonIcon != null) {
            aboutButton.setIcon(aboutButtonIcon);
        }
        aboutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lifeController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lifeController.onExitToolbarButton(mouseEvent);
            }
        });
        aboutButton.addActionListener(actionEvent -> lifeController.onAboutButtonClicked());
        toolBar.add(aboutButton);
    }

    private void initField(LifeController lifeController, IFieldModel fieldModel, IPropertiesModel propertiesModel) {
        fieldView = new FieldView(lifeController, fieldModel, propertiesModel);
        scrollPane = new JScrollPane(fieldView);
        add(scrollPane, BorderLayout.CENTER);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private void initStatusBar() {
        statusBarView = new StatusBarView();
        add(statusBarView, BorderLayout.PAGE_END);
    }

    public StatusBarView getStatusBarView() {
        return statusBarView;
    }

    private Icon getButtonIcon(String imgPath) {
        URL imgUrl = this.getClass().getClassLoader().getResource(imgPath);
        if (imgUrl == null) {
            return null;
        }

        return new ImageIcon(imgUrl);
    }
}
