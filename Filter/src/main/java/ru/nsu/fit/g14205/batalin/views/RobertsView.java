package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class RobertsView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField levelValue;
    private JSlider levelSlider;
    private boolean result;

    public RobertsView() {
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

        levelValue.addActionListener(actionEvent -> {

        });

        levelValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                updSlider();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                updSlider();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                updSlider();
            }

            private void updSlider() {
                try {
                    int value = Integer.parseInt(levelValue.getText());
                    if(value >= 1 && value <= 500) {
                        levelSlider.setValue(value);
                    }
                } catch (Exception e) {
                }
            }
        });

        levelValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                int value = getLevelValue();
                levelValue.setText(String.valueOf(value));
                levelSlider.setValue(value);
            }
        });

        levelSlider.addChangeListener(changeEvent -> {
            int value = levelSlider.getValue();
            levelValue.setText(String.valueOf(value));
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

    public int getLevelValue() {
        try {
            int value = Integer.parseInt(levelValue.getText());
            return Math.max(1, Math.min(500, value));
        } catch (Exception e) {
            return 20;
        }
    }
}
