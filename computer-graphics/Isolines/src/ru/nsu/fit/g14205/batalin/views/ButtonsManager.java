package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ButtonsManager {
    private JToolBar toolBar;
    private JMenuBar menuBar;

    private Consumer<MouseEvent> mouseEnteredHandler;
    private Consumer<MouseEvent> mouseExitedHandler;

    public ButtonsManager() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        menuBar = new JMenuBar();
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void setMouseEnteredHandler(Consumer<MouseEvent> mouseEnteredHandler) {
        this.mouseEnteredHandler = mouseEnteredHandler;
    }

    public void setMouseExitedHandler(Consumer<MouseEvent> mouseExitedHandler) {
        this.mouseExitedHandler = mouseExitedHandler;
    }

    public JMenu addMenu(JMenuItem parent, String name) {
        JMenu menu = new JMenu(name);
        addMenuItem(parent, menu);
        return menu;
    }

    public void addMenuSeparator(JMenu menu) {
        menu.addSeparator();
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }

    public void addSeparator(JMenu menu) {
        menu.addSeparator();
        toolBar.addSeparator();
    }

    public void addToggleItem(JMenuItem menuParent, String name, String toolTip, String icoName,
                              boolean isSelected, ActionListener actionListener) {
        JCheckBoxMenuItem menuItem = createCheckBoxMenuItem(name, actionListener);
        addMenuItem(menuParent, menuItem);
        menuItem.setSelected(isSelected);

        if (icoName == null) {
            return;
        }

        JToggleButton button = createToolbarToggleButton(toolTip, icoName, actionListener);
        toolBar.add(button);
        button.setSelected(isSelected);

        menuItem.addActionListener(actionEvent -> button.setSelected(menuItem.isSelected()));
        button.addActionListener(actionEvent -> menuItem.setSelected(button.isSelected()));
    }

    public void addRadioItem(JMenuItem menuParent, String name, ButtonGroup menuGroup, String toolTip, String icoName,
                             ButtonGroup toolbarGroup, boolean isSelected, ActionListener actionListener) {
        JRadioButtonMenuItem menuItem = createRadioButtonMenuItem(name, actionListener);
        addMenuItem(menuParent, menuItem);
        menuGroup.add(menuItem);
        menuItem.setSelected(isSelected);

        if (icoName == null) {
            return;
        }

        JToggleButton button = createToolbarToggleButton(toolTip, icoName, actionListener);
        toolBar.add(button);
        toolbarGroup.add(button);
        button.setSelected(isSelected);

        menuItem.addActionListener(actionEvent -> button.setSelected(menuItem.isSelected()));
        button.addActionListener(actionEvent -> menuItem.setSelected(button.isSelected()));
    }

    public void addItem(JMenuItem menuParent, String name, String toolTip, String icoName, ActionListener actionListener) {
        JMenuItem menuItem = createMenuItem(name, actionListener);
        addMenuItem(menuParent, menuItem);

        if (icoName == null) {
            return;
        }

        JButton button = createToolbarButton(toolTip, icoName, actionListener);
        toolBar.add(button);
    }

    private void addMenuItem(JMenuItem parent, JMenuItem item) {
        if (parent != null) {
            parent.add(item);
        } else {
            menuBar.add(item);
        }
    }

    private JRadioButtonMenuItem createRadioButtonMenuItem(String name, ActionListener actionListener) {
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
        initMenuItem(menuItem, actionListener);

        return menuItem;
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(String name, ActionListener actionListener) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);
        initMenuItem(menuItem, actionListener);

        return menuItem;
    }

    private JMenuItem createMenuItem(String name, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(name);
        initMenuItem(menuItem, actionListener);

        return menuItem;
    }

    private void initMenuItem(JMenuItem item, ActionListener actionListener) {
        if (actionListener != null) {
            item.addActionListener(actionListener);
        }
    }

    private JButton createToolbarButton(String toolTip, String icoName, ActionListener actionListener) {
        JButton button = new JButton();
        initToolbarButton(button, toolTip, icoName, actionListener);

        return button;
    }

//    private JRadioButton createToolbarRadioButton(String toolTip, String icoName, ActionListener actionListener) {
//        JRadioButton button = new JRadioButton();
//        initToolbarButton(button, toolTip, icoName, actionListener);
//
//        return button;
//    }

    private JToggleButton createToolbarToggleButton(String toolTip, String icoName, ActionListener actionListener) {
        JToggleButton button = new JToggleButton();
        initToolbarButton(button, toolTip, icoName, actionListener);

        return button;
    }

    private void initToolbarButton(AbstractButton button, String toolTip, String icoName, ActionListener actionListener) {
        button.setToolTipText(toolTip);
        Icon aboutButtonIcon = getButtonIcon("images/" + icoName);
        if (aboutButtonIcon != null) {
            button.setIcon(aboutButtonIcon);
        }
        button.addActionListener(actionListener);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                mouseEnteredHandler.accept(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                mouseExitedHandler.accept(mouseEvent);
            }
        });
    }

    private Icon getButtonIcon(String imgPath) {
        URL imgUrl = this.getClass().getClassLoader().getResource("resources" + File.separator + imgPath);
        if (imgUrl == null) {
            return null;
        }

        return new ImageIcon(imgUrl);
    }
}
