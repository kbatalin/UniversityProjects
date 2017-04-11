package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditorDialog extends JDialog {
    private JPanel contentPane;
//    private JButton buttonOK;
//    private JButton buttonCancel;
    private JPanel content;
    private JButton OKButton;
    private JButton applyButton;
    private JButton zoomButton;
    private JButton zoomButton1;
    private JButton addButton;
    private JButton delButton;
    private JCheckBox interactiveCheckBox;
    private JCheckBox autoscaleCheckBox;
    private JSpinner nSpinner;
    private JSpinner aSpinner;
    private JSpinner znSpinner;
    private JSpinner mSpinner;
    private JSpinner bSpinner;
    private JSpinner zfSpinner;
    private JSpinner kSpinner;
    private JSpinner cSpinner;
    private JSpinner sqSpinner;
    private JSpinner numberSpinner;
    private JSpinner dSpinner;
    private JSpinner shSpinner;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;

    public EditorDialog() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setTitle("Line editor");
        getRootPane().setDefaultButton(OKButton);

        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        content.setMinimumSize(new Dimension(600, 400));
        content.setSize(600, 400);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        EditorDialog dialog = new EditorDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
