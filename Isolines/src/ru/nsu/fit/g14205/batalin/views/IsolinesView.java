package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesView extends JFrame {
    private IsolinesController isolinesController;

    private WorkspaceView workspaceView;
    private StatusBarView statusBarView;

    private ButtonsManager buttonsManager;

    public IsolinesView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;

        setMinimumSize(new Dimension(800, 800));
        setSize(1200, 600);
        setTitle("Filter");
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        initWorkSpace();
        initStatusBar();
        initButtons();

        setVisible(true);
    }

    private void initButtons() {
        buttonsManager = new ButtonsManager();

        buttonsManager.setMouseEnteredHandler(mouseEvent -> isolinesController.onEnterToolbarButton(mouseEvent));
        buttonsManager.setMouseExitedHandler(mouseEvent -> isolinesController.onExitToolbarButton(mouseEvent));

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

        buttonsManager.addItem(fileMenu, "New", "Create new", "document.png", actionEvent -> isolinesController.onNewButtonClicked(actionEvent));
        buttonsManager.addItem(fileMenu, "Open", "Open", "folder.png", actionEvent -> isolinesController.onOpenButtonClicked(actionEvent));

        buttonsManager.addMenuSeparator(fileMenu);

        buttonsManager.addItem(fileMenu, "Exit", null, null, actionEvent -> isolinesController.onExitButtonClicked(actionEvent));
    }

    private void initEditMenu() {
        JMenu editMenu = buttonsManager.addMenu(null, "Edit");

        buttonsManager.addToggleItem(editMenu, "Create singles", "Create singles",
                "create_isoline.png", false, actionEvent -> isolinesController.onCreateIsolineButtonClicked(actionEvent));

        buttonsManager.addToggleItem(editMenu, "Dynamic isolines", "Dynamic isolines",
                "dynamic_isoline.png", false, actionEvent -> isolinesController.onDynamicIsolineButtonClicked(actionEvent));
        buttonsManager.addItem(editMenu, "Clear isolines", "Clear isolines",
                "trash.png", actionEvent -> isolinesController.onClearIsolinesButtonClicked(actionEvent));

        buttonsManager.addSeparator(editMenu);

        buttonsManager.addItem(editMenu, "Settings", "Settings",
                "settings.png", actionEvent -> isolinesController.onSettingsButtonClicked(actionEvent));
    }

    private void initViewMenu() {
        JMenu viewMenu = buttonsManager.addMenu(null, "View");

        ButtonGroup viewModeMenuGroup = new ButtonGroup();
        ButtonGroup viewModeToolbarGroup = new ButtonGroup();
        buttonsManager.addRadioItem(viewMenu, "Color map", viewModeMenuGroup, "Color map",
                "color_map.png", viewModeToolbarGroup, true, actionEvent -> isolinesController.onColorMapButtonClicked(actionEvent));
        buttonsManager.addRadioItem(viewMenu, "Interpolation", viewModeMenuGroup, "Interpolation",
                "interpolation.png", viewModeToolbarGroup, false, actionEvent -> isolinesController.onInterpolationButtonClicked(actionEvent));
        buttonsManager.addSeparator(viewMenu);

        buttonsManager.addToggleItem(viewMenu, "Show grid", "Show grid",
                "grid.png", false, actionEvent -> isolinesController.onGridButtonClicked(actionEvent));
        buttonsManager.addToggleItem(viewMenu, "Show isolines", "Show isolines",
                "isolines.png", false, actionEvent -> isolinesController.onIsolinesShowButtonClicked(actionEvent));
        buttonsManager.addToggleItem(viewMenu, "Show entry points", "Show entry points",
                "target.png", false, actionEvent -> isolinesController.onShowEntryPointsButtonClicked(actionEvent));
    }

    private void initHelpMenu() {
        JMenu helpMenu = buttonsManager.addMenu(null, "Help");
        buttonsManager.addItem(helpMenu, "About", "About", "info.png", actionEvent -> isolinesController.onAboutDialogClicked(actionEvent));
    }

    private void initWorkSpace() {
        workspaceView = new WorkspaceView(isolinesController);

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
