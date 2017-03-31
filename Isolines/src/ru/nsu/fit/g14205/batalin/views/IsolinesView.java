package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesView extends JFrame {
    private IsolinesController isolinesController;

    private WorkspaceView workspaceView;
    private StatusBarView statusBarView;

    private JMenuBar menuBar;
    private JToolBar toolBar;

    public IsolinesView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;

        setMinimumSize(new Dimension(800, 800));
        setSize(1200, 600);
        setTitle("Filter");
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        initMenu();

        initToolbar();

        initWorkSpace();

        initStatusBar();

        setVisible(true);
    }

    private void initMenu() {
        menuBar = new JMenuBar();

        initFileMenu();
        initEditMenu();
        initViewMenu();
        initHelpMenu();

        setJMenuBar(menuBar);
    }

    private void initFileMenu() {
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

        JMenuItem fileMenuSave = new JMenuItem("Save");
        fileMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenuSave.setMnemonic(KeyEvent.VK_S);
        fileMenu.add(fileMenuSave);

        JMenuItem fileMenuSaveAs = new JMenuItem("Save as...");
        fileMenuSaveAs.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(fileMenuSaveAs);

        fileMenu.addSeparator();

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        fileMenuExit.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(fileMenuExit);
    }

    private void initEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        JMenuItem editMenuClear = new JMenuItem("Clear isolines");
        editMenuClear.setMnemonic(KeyEvent.VK_B);
        editMenu.add(editMenuClear);
    }

    private void initViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu);

        JCheckBoxMenuItem viewMenuBlackWhite = new JCheckBoxMenuItem("Black/white");
        viewMenu.add(viewMenuBlackWhite);

        JCheckBoxMenuItem viewMenuBuildType = new JCheckBoxMenuItem("Build type");
        viewMenu.add(viewMenuBuildType);

        JCheckBoxMenuItem viewMenuMesh = new JCheckBoxMenuItem("Mesh");
        viewMenu.add(viewMenuMesh);

        JCheckBoxMenuItem viewMenuMap = new JCheckBoxMenuItem("Map");
        viewMenu.add(viewMenuMap);

        JCheckBoxMenuItem viewMenuIsolines = new JCheckBoxMenuItem("Isolines");
        viewMenu.add(viewMenuIsolines);

        viewMenu.addSeparator();

        JMenuItem viewMenuZoomPlus = new JMenuItem("Zoom+");
        viewMenu.add(viewMenuZoomPlus);

        JMenuItem viewMenuZoomMinus = new JMenuItem("Zoom-");
        viewMenu.add(viewMenuZoomMinus);

        viewMenu.addSeparator();

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem viewMenuTriangleBarycentric = new JRadioButtonMenuItem("Triangle barycentric");
        buttonGroup.add(viewMenuTriangleBarycentric);
        viewMenu.add(viewMenuTriangleBarycentric);

        JRadioButtonMenuItem viewMenuQuadBilinear = new JRadioButtonMenuItem("Quad bilinear");
        buttonGroup.add(viewMenuQuadBilinear);
        viewMenu.add(viewMenuQuadBilinear);

        JRadioButtonMenuItem viewMenuTriangleBilinear = new JRadioButtonMenuItem("Triangle bilinear");
        buttonGroup.add(viewMenuTriangleBilinear);
        viewMenu.add(viewMenuTriangleBilinear);
    }

    private void initHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(helpMenuAbout);
    }

    private void initToolbar() {
        toolBar = new JToolBar("Toolbar");
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);

        addToolbarButton("Create new", "document.png", null);
        addToolbarButton("Open", "folder.png", null);
        addToolbarButton("Save", "save.png", null);
        addToolbarButton("Save as...", "save_as.png", null);

        toolBar.addSeparator();

        addToolbarButton("Clear isolines", "trash.png", null);

        toolBar.addSeparator();

        addToolbarToggleButton("Black/white", "map.png", null);

    }

    private JButton addToolbarButton(String name, String icoName, ActionListener listener) {
        JButton button = new JButton();
        addToolbarButton(button, name, icoName, listener);
        return button;
    }

    private JRadioButton addToolbarRadioButton(String name, String icoName, ButtonGroup buttonGroup, ActionListener listener) {
        JRadioButton button = new JRadioButton();
        buttonGroup.add(button);
        addToolbarButton(button, name, icoName, listener);
        return button;
    }

    private JToggleButton addToolbarToggleButton(String name, String icoName, ActionListener listener) {
        JToggleButton button = new JToggleButton();
        addToolbarButton(button, name, icoName, listener);
        return button;
    }

    private void addToolbarButton(AbstractButton button, String name, String icoName, ActionListener listener) {
        button.setName(name);
        button.setToolTipText(name);
        Icon aboutButtonIcon = getButtonIcon("images/" + icoName);
        if (aboutButtonIcon != null) {
            button.setIcon(aboutButtonIcon);
        }
        button.addActionListener(listener);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                isolinesController.onEnterToolbarButton(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                isolinesController.onExitToolbarButton(mouseEvent);
            }
        });
        toolBar.add(button);
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

    public StatusBarView getStatusBarView() {
        return statusBarView;
    }

    private Icon getButtonIcon(String imgPath) {
        URL imgUrl = this.getClass().getClassLoader().getResource("resources" + File.separator + imgPath);
        if (imgUrl == null) {
            return null;
        }

        return new ImageIcon(imgUrl);
    }
}
