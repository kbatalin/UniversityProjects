package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.event.*;

public class VRView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner nxSpinner;
    private JSpinner nySpinner;
    private JSpinner nzSpinner;
    private boolean result;

    public VRView() {
        result = false;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

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

        SpinnerNumberModel nxSpinnerModel = new SpinnerNumberModel(350, 1, 350, 1);
        nxSpinner.setModel(nxSpinnerModel);

        SpinnerNumberModel nySpinnerModel = new SpinnerNumberModel(350, 1, 350, 1);
        nySpinner.setModel(nySpinnerModel);

        SpinnerNumberModel nzSpinnerModel = new SpinnerNumberModel(350, 1, 350, 1);
        nzSpinner.setModel(nzSpinnerModel);
    }

    private void onOK() {
        result = true;
        dispose();
    }

    private void onCancel() {
        result = false;
        dispose();
    }

    public boolean getResult() {
        return result;
    }

    public int getNxValue() {
        return getValue(nxSpinner);
    }

    public int getNyValue() {
        return getValue(nySpinner);
    }

    public int getNzValue() {
        return getValue(nzSpinner);
    }

    private int getValue(JSpinner spinner) {
        Object objValue = spinner.getValue();
        if (objValue instanceof Number) {
            Number number = ((Number) objValue);
            return number.intValue();
        }

        return 1;
    }
}
