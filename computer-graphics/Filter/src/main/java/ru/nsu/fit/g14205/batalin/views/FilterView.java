package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.ImageModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Created by kir55rus on 11.03.17.
 */
public class FilterView extends JFrame {
    private FilterController filterController;
    private StatusBarView statusBarView;
    private WorkspaceView workspaceView;

    private JToggleButton selectButton;
    private JCheckBoxMenuItem editMenuSelect;

    private JCheckBoxMenuItem filtersVREmission;
    private JCheckBoxMenuItem filtersVRAbsorption;
    private JToggleButton vrAbsorptionButton;
    private JToggleButton vrEmissionButton;

    public FilterView(FilterController filterController, ImageModel aImageModel, ImageModel bImageModel, ImageModel cImageModel) {
        this.filterController = filterController;

        setMinimumSize(new Dimension(800, 800));
        setSize(1200, 600);
        setTitle("Filter");
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
//        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent we) {
//                lifeController.onCloseButtonClicked();
//            }
//        });

        setLayout(new BorderLayout());

        initMenu();

        initToolbar();

        initWorkSpace(filterController, aImageModel, bImageModel, cImageModel);

        initStatusBar();

        setVisible(true);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        initFileMenu(menuBar);
        initEditMenu(menuBar);
        initFiltersMenu(menuBar);
        initHelpMenu(menuBar);

        setJMenuBar(menuBar);
    }

    private void initFileMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem fileMenuNew = new JMenuItem("New");
        fileMenuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenuNew.setMnemonic(KeyEvent.VK_N);
        fileMenuNew.addActionListener(actionEvent -> filterController.onNewButtonClicked());
        fileMenu.add(fileMenuNew);

        JMenuItem fileMenuOpen = new JMenuItem("Open...");
        fileMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenuOpen.setMnemonic(KeyEvent.VK_O);
        fileMenuOpen.addActionListener(actionEvent -> filterController.onOpenButtonClicked());
        fileMenu.add(fileMenuOpen);

        JMenuItem fileMenuSaveAs = new JMenuItem("Save as...");
        fileMenuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenuSaveAs.setMnemonic(KeyEvent.VK_A);
        fileMenuSaveAs.addActionListener(actionEvent -> filterController.onSaveButtonClicked());
        fileMenu.add(fileMenuSaveAs);

        fileMenu.addSeparator();

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        fileMenuExit.setMnemonic(KeyEvent.VK_X);
        fileMenuExit.addActionListener(actionEvent -> filterController.onExitButtonClicked());
        fileMenu.add(fileMenuExit);
    }

    private void initEditMenu(JMenuBar menuBar) {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        editMenuSelect = new JCheckBoxMenuItem("Select");
        editMenuSelect.setMnemonic(KeyEvent.VK_S);
        editMenuSelect.addActionListener(actionEvent -> filterController.onSelectButtonClicked(editMenuSelect.isSelected()));
        editMenu.add(editMenuSelect);

        JMenuItem editMenuCopyBToC = new JMenuItem("Copy B to C");
        editMenuCopyBToC.setMnemonic(KeyEvent.VK_B);
        editMenuCopyBToC.addActionListener(actionEvent -> filterController.onCopyBToCButtonClicked());
        editMenu.add(editMenuCopyBToC);

        JMenuItem editMenuCopyCToB = new JMenuItem("Copy C to B");
        editMenuCopyCToB.setMnemonic(KeyEvent.VK_C);
        editMenuCopyCToB.addActionListener(actionEvent -> filterController.onCopyCToBButtonClicked());
        editMenu.add(editMenuCopyCToB);
    }

    private void initFiltersMenu(JMenuBar menuBar) {
        JMenu filtersMenu = new JMenu("Filters");
        filtersMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(filtersMenu);

        JMenuItem filtersMenuBlackWhite = new JMenuItem("Black and white");
        filtersMenuBlackWhite.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Black and white"));
        filtersMenu.add(filtersMenuBlackWhite);

        JMenuItem filtersMenuNegative = new JMenuItem("Negative");
        filtersMenuNegative.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Negative"));
        filtersMenu.add(filtersMenuNegative);

        JMenuItem filtersMenuFloyd = new JMenuItem("Floyd Steinberg");
        filtersMenuFloyd.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Floyd Steinberg"));
        filtersMenu.add(filtersMenuFloyd);

        JMenuItem filtersMenuOrdered = new JMenuItem("Ordered dither");
        filtersMenuOrdered.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Ordered dither"));
        filtersMenu.add(filtersMenuOrdered);

        JMenuItem filtersMenuRoberts = new JMenuItem("Roberts");
        filtersMenuRoberts.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Roberts"));
        filtersMenu.add(filtersMenuRoberts);

        JMenuItem filtersMenuSobel = new JMenuItem("Sobel");
        filtersMenuSobel.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Sobel"));
        filtersMenu.add(filtersMenuSobel);

        JMenuItem filtersMenuBlur = new JMenuItem("Blur");
        filtersMenuBlur.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Blur"));
        filtersMenu.add(filtersMenuBlur);

        JMenuItem filtersMenuSharp = new JMenuItem("Sharp");
        filtersMenuSharp.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Sharp"));
        filtersMenu.add(filtersMenuSharp);

        JMenuItem filtersMenuEmboss = new JMenuItem("Emboss");
        filtersMenuEmboss.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Emboss"));
        filtersMenu.add(filtersMenuEmboss);

        JMenuItem filtersMenuWatercolor = new JMenuItem("Watercolor");
        filtersMenuWatercolor.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Watercolor"));
        filtersMenu.add(filtersMenuWatercolor);

        JMenuItem filtersMenuRotation = new JMenuItem("Rotation");
        filtersMenuRotation.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Rotation"));
        filtersMenu.add(filtersMenuRotation);

        JMenuItem filtersMenuGamma = new JMenuItem("Gamma");
        filtersMenuGamma.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Gamma"));
        filtersMenu.add(filtersMenuGamma);

        JMenuItem filtersMenuZoom = new JMenuItem("Zoom");
        filtersMenuZoom.addActionListener(actionEvent -> filterController.onFilterButtonClicked("Zoom"));
        filtersMenu.add(filtersMenuZoom);

        JMenu filtersVR = new JMenu("VR");
        filtersMenu.add(filtersVR);

        JMenuItem filtersVRSettings = new JMenuItem("Settings");
        filtersVRSettings.addActionListener(actionEvent -> filterController.onVRSettingsButtonClicked());
        filtersVR.add(filtersVRSettings);

        filtersVREmission = new JCheckBoxMenuItem("Emission");
        filtersVREmission.addActionListener(actionEvent -> {
            vrEmissionButton.setSelected(filtersVREmission.isSelected());
        });
        filtersVR.add(filtersVREmission);

        filtersVRAbsorption = new JCheckBoxMenuItem("Absorption");
        filtersVRAbsorption.addActionListener(actionEvent -> {
            vrAbsorptionButton.setSelected(filtersVRAbsorption.isSelected());
        });
        filtersVR.add(filtersVRAbsorption);

        JMenuItem filtersVRStart = new JMenuItem("Start");
        filtersVRStart.addActionListener(actionEvent -> filterController.onVRStartButtonClicked());
        filtersVR.add(filtersVRStart);
    }

    public boolean isVREmissionSelected() {
        return filtersVREmission.isSelected();
    }

    public boolean isVRAbsorptionSelected() {
        return filtersVRAbsorption.isSelected();
    }

    private void initHelpMenu(JMenuBar menuBar) {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.setMnemonic(KeyEvent.VK_A);
        helpMenuAbout.addActionListener(actionEvent -> filterController.onAboutButtonClicked());
        helpMenu.add(helpMenuAbout);
    }

    private void initToolbar() {
        JToolBar toolBar = new JToolBar("Toolbar");
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);

        JButton newButton = new JButton();
        newButton.setToolTipText("Create new");
        Icon newButtonIcon = getButtonIcon("images/document_a4_new.png");
        if (newButtonIcon != null) {
            newButton.setIcon(newButtonIcon);
        }
        newButton.addActionListener(actionEvent -> filterController.onNewButtonClicked());
        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(newButton);

        JButton openButton = new JButton();
        openButton.setToolTipText("Open image");
        Icon openButtonIcon = getButtonIcon("images/folder_classic_up.png");
        if (openButtonIcon != null) {
            openButton.setIcon(openButtonIcon);
        }
        openButton.addActionListener(actionEvent -> filterController.onOpenButtonClicked());
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(openButton);

        JButton saveAsButton = new JButton();
        saveAsButton.setToolTipText("Save as...");
        Icon saveAsButtonIcon = getButtonIcon("images/folder_modernist_down.png");
        if (saveAsButtonIcon != null) {
            saveAsButton.setIcon(saveAsButtonIcon);
        }
        saveAsButton.addActionListener(actionEvent -> filterController.onSaveButtonClicked());
        saveAsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(saveAsButton);

        toolBar.addSeparator();

        selectButton = new JToggleButton();
        selectButton.setToolTipText("Select");
        Icon selectButtonIcon = getButtonIcon("images/image_modernist.png");
        if (selectButtonIcon != null) {
            selectButton.setIcon(selectButtonIcon);
        }
        selectButton.addActionListener(actionEvent -> filterController.onSelectButtonClicked(selectButton.isSelected()));
        selectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(selectButton);

        JButton copyBToCButton = new JButton();
        copyBToCButton.setToolTipText("Properties");
        Icon copyBToCButtonIcon = getButtonIcon("images/arrow_large_right.png");
        if (copyBToCButtonIcon != null) {
            copyBToCButton.setIcon(copyBToCButtonIcon);
        }
        copyBToCButton.addActionListener(actionEvent -> filterController.onCopyBToCButtonClicked());
        copyBToCButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(copyBToCButton);

        JButton copyCToBButton = new JButton();
        copyCToBButton.setToolTipText("Properties");
        Icon copyCToBButtonIcon = getButtonIcon("images/arrow_large_left.png");
        if (copyCToBButtonIcon != null) {
            copyCToBButton.setIcon(copyCToBButtonIcon);
        }
        copyCToBButton.addActionListener(actionEvent -> filterController.onCopyCToBButtonClicked());
        copyCToBButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(copyCToBButton);

        toolBar.addSeparator();

        //filters
        initFiltersToolbar(toolBar);

        toolBar.addSeparator();

        JButton aboutButton = new JButton();
        aboutButton.setToolTipText("About");
        Icon aboutButtonIcon = getButtonIcon("images/information.png");
        if (aboutButtonIcon != null) {
            aboutButton.setIcon(aboutButtonIcon);
        }
        aboutButton.addActionListener(actionEvent -> filterController.onAboutButtonClicked());
        aboutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(aboutButton);
    }

    private void initFiltersToolbar(JToolBar toolBar) {
        createFilterButton(toolBar, "Black and white", "two-circles-sign-one-black-other-white.png");
        createFilterButton(toolBar, "Negative", "negative-sign.png");
        createFilterButton(toolBar, "Floyd Steinberg", "font.png");
        createFilterButton(toolBar, "Ordered dither", "opera.png");
        createFilterButton(toolBar, "Roberts", "bones-typography-outline-of-letter-r.png");
        createFilterButton(toolBar, "Sobel", "letter-s-of-bones-outlined-typography.png");
        createFilterButton(toolBar, "Blur", "blur.png");
        createFilterButton(toolBar, "Sharp", "sharpener.png");
        createFilterButton(toolBar, "Emboss", "emboss.png");
        createFilterButton(toolBar, "Watercolor", "watercolor.png");
        createFilterButton(toolBar, "Rotation", "refresh-button.png");
        createFilterButton(toolBar, "Gamma", "letter-g-of-curved-bone-outlined-typography.png");
        createFilterButton(toolBar, "Zoom", "zoom-in.png");

        //VR
        toolBar.addSeparator();
        JButton vrSettingsButton = new JButton();
        vrSettingsButton.setToolTipText("Open config");
        Icon vrSettingsButtonIcon = getButtonIcon("images/settings.png");
        if (vrSettingsButtonIcon != null) {
            vrSettingsButton.setIcon(vrSettingsButtonIcon);
        }
        vrSettingsButton.addActionListener(actionEvent -> filterController.onVRSettingsButtonClicked());
        vrSettingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(vrSettingsButton);

        vrEmissionButton = new JToggleButton();
        vrEmissionButton.setToolTipText("Emission");
        Icon vrEmissionButtonIcon = getButtonIcon("images/letter-e-of-bones-outlined-typography-of-halloween.png");
        if (vrEmissionButtonIcon != null) {
            vrEmissionButton.setIcon(vrEmissionButtonIcon);
        }
        vrEmissionButton.addActionListener(actionEvent -> {
            filtersVREmission.setState(vrEmissionButton.isSelected());
        });
        vrEmissionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(vrEmissionButton);

        vrAbsorptionButton = new JToggleButton();
        vrAbsorptionButton.setToolTipText("Absorption");
        Icon vrAbsorptionButtonIcon = getButtonIcon("images/letter-a-of-halloween-bones-typography-outline.png");
        if (vrAbsorptionButtonIcon != null) {
            vrAbsorptionButton.setIcon(vrAbsorptionButtonIcon);
        }
        vrAbsorptionButton.addActionListener(actionEvent -> {
            filtersVRAbsorption.setState(vrAbsorptionButton.isSelected());
        });
        vrAbsorptionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(vrAbsorptionButton);

        JButton vrStartButton = new JButton();
        vrStartButton.setToolTipText("Start");
        Icon vrStartButtonIcon = getButtonIcon("images/play-button.png");
        if (vrStartButtonIcon != null) {
            vrStartButton.setIcon(vrStartButtonIcon);
        }
        vrStartButton.addActionListener(actionEvent -> filterController.onVRStartButtonClicked());
        vrStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(vrStartButton);
    }

    private void createFilterButton(JToolBar toolBar, String filterName, String icoName) {
        JButton filterButton = new JButton();
        filterButton.setToolTipText(filterName);
        Icon filterButtonIcon = getButtonIcon("images/" + icoName);
        if (filterButtonIcon != null) {
            filterButton.setIcon(filterButtonIcon);
        }
        filterButton.addActionListener(actionEvent -> filterController.onFilterButtonClicked(filterName));
        filterButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                filterController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                filterController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(filterButton);
    }

    private void initWorkSpace(FilterController filterController, ImageModel aImageModel, ImageModel bImageModel, ImageModel cImageModel) {
        workspaceView = new WorkspaceView(filterController, aImageModel, bImageModel, cImageModel);

        JScrollPane workspaceScrollPane = new JScrollPane(workspaceView);
        add(workspaceScrollPane, BorderLayout.CENTER);
    }

    public WorkspaceView getWorkspaceView() {
        return workspaceView;
    }

    public JToggleButton getSelectButton() {
        return selectButton;
    }

    public JCheckBoxMenuItem getEditMenuSelect() {
        return editMenuSelect;
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
