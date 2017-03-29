package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.event.*;

public class FloydSteinbergView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField redValue;
    private JTextField greenValue;
    private JTextField blueValue;
    private boolean result;

    public FloydSteinbergView() {
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

        redValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                redValue.setText(String.valueOf(getRed()));
            }
        });

        greenValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                greenValue.setText(String.valueOf(getGreen()));
            }
        });

        blueValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                blueValue.setText(String.valueOf(getBlue()));
            }
        });
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

    public int getRed() {
        return getValue(redValue);
    }

    public int getGreen() {
        return getValue(greenValue);
    }

    public int getBlue() {
        return getValue(blueValue);
    }

    private int getValue(JTextField textField) {
        try {
            int value = Integer.parseInt(textField.getText());
            return Math.max(2, Math.min(256, value));
        } catch (Exception e) {
            return 2;
        }
    }
}
