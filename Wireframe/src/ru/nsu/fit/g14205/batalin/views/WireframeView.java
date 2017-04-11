package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.WireframeController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WireframeView extends JFrame {
    private WireframeController wireframeController;

    private WorkspaceView workspaceView;
    private StatusBarView statusBarView;

    private ButtonsManager buttonsManager;

    public WireframeView(WireframeController wireframeController) {
        this.wireframeController = wireframeController;

        setMinimumSize(new Dimension(800, 800));
        setSize(1200, 600);
        setTitle("Wireframe");
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        initWorkSpace();
        initStatusBar();
        initButtons();
    }

    private void initButtons() {
        buttonsManager = new ButtonsManager();

        buttonsManager.setMouseEnteredHandler(mouseEvent -> wireframeController.onEnterToolbarButton(mouseEvent));
        buttonsManager.setMouseExitedHandler(mouseEvent -> wireframeController.onExitToolbarButton(mouseEvent));

        initFileMenu();
        buttonsManager.addToolBarSeparator();

        initEditMenu();
        buttonsManager.addToolBarSeparator();

        initViewMenu();
        buttonsManager.addToolBarSeparator();

        initHelpMenu();

        setJMenuBar(buttonsManager.getMenuBar());
        add(buttonsManager.getToolBar(), BorderLayout.PAGE_START);
    }

    private void initFileMenu() {
        JMenu fileMenu = buttonsManager.addMenu(null, "File");

        buttonsManager.addItem(fileMenu, "New", "Create new", "document.png", null);
        buttonsManager.addItem(fileMenu, "Open", "Open", "folder.png", null);

        buttonsManager.addMenuSeparator(fileMenu);

        buttonsManager.addItem(fileMenu, "Exit", null, null, null);
    }

    private void initEditMenu() {
        JMenu editMenu = buttonsManager.addMenu(null, "Edit");
        buttonsManager.addItem(editMenu, "Line editor", null, null, actionEvent -> wireframeController.onLineEditButtonClicked());
    }

    private void initViewMenu() {
        JMenu viewMenu = buttonsManager.addMenu(null, "View");
    }

    private void initHelpMenu() {
        JMenu helpMenu = buttonsManager.addMenu(null, "Help");
        buttonsManager.addItem(helpMenu, "About", "About", "info.png", null);
    }

    private void initWorkSpace() {
        workspaceView = new WorkspaceView(wireframeController);

        JScrollPane workspaceScrollPane = new JScrollPane(workspaceView);
        add(workspaceScrollPane, BorderLayout.CENTER);
    }

    private void initStatusBar() {
        statusBarView = new StatusBarView();
        add(statusBarView, BorderLayout.PAGE_END);
    }

    public WorkspaceView getWorkspaceView() {
        return workspaceView;
    }

    public StatusBarView getStatusBarView() {
        return statusBarView;
    }
}
