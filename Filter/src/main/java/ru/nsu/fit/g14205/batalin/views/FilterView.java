package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;

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

    public FilterView(FilterController filterController) {
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

        initWorkSpace(filterController);

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
        fileMenu.add(fileMenuNew);

        JMenuItem fileMenuOpen = new JMenuItem("Open...");
        fileMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenuOpen.setMnemonic(KeyEvent.VK_O);
        fileMenu.add(fileMenuOpen);

        JMenuItem fileMenuSaveAs = new JMenuItem("Save as...");
        fileMenuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenuSaveAs.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(fileMenuSaveAs);

        fileMenu.addSeparator();

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        fileMenuExit.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(fileMenuExit);
    }

    private void initEditMenu(JMenuBar menuBar) {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        JMenuItem editMenuClear = new JMenuItem("Select");
        editMenuClear.setMnemonic(KeyEvent.VK_S);
        editMenu.add(editMenuClear);

        JMenuItem editMenuCopyBToC = new JMenuItem("Copy B to C");
        editMenuCopyBToC.setMnemonic(KeyEvent.VK_B);
        editMenu.add(editMenuCopyBToC);

        JMenuItem editMenuCopyCToB = new JMenuItem("Copy C to B");
        editMenuCopyCToB.setMnemonic(KeyEvent.VK_C);
        editMenu.add(editMenuCopyCToB);
    }

    private void initFiltersMenu(JMenuBar menuBar) {
        JMenu actionMenu = new JMenu("Filters");
        actionMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(actionMenu);

        JMenuItem actionMenuNext = new JMenuItem("Filter #1");
        actionMenuNext.setMnemonic(KeyEvent.VK_N);
        actionMenu.add(actionMenuNext);
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

        JButton selectButton = new JButton();
        selectButton.setToolTipText("Select");
        Icon selectButtonIcon = getButtonIcon("images/image_modernist.png");
        if (selectButtonIcon != null) {
            selectButton.setIcon(selectButtonIcon);
        }
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

    private void initWorkSpace(FilterController filterController) {
        WorkspaceView workspaceView = new WorkspaceView(filterController);

        JScrollPane workspaceScrollPane = new JScrollPane(workspaceView);
        add(workspaceScrollPane, BorderLayout.CENTER);
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
