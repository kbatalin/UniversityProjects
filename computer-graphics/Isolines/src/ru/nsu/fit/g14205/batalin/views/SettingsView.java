package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class SettingsView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner colsSpinner;
    private JSlider colsSlider;
    private JSpinner rowsSpinner;
    private JSlider rowsSlider;
    private JTextField aValue;
    private JTextField cValue;
    private JTextField bValue;
    private JTextField dValue;
    private boolean result;

    public SettingsView(IsolinesController isolinesController) {
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

        PropertiesModel propertiesModel = isolinesController.getApplicationProperties();
        SpinnerNumberModel colsSpinnerModel = new SpinnerNumberModel(propertiesModel.getVerticalCellsCount(), 2, 50, 1);
        colsSpinner.setModel(colsSpinnerModel);
        colsSpinner.addChangeListener(changeEvent -> {
            int value = colsSpinnerModel.getNumber().intValue();
            colsSlider.setValue(value);
        });

        colsSlider.setValue(propertiesModel.getVerticalCellsCount());
        colsSlider.setMinimum(2);
        colsSlider.setMaximum(50);
        colsSlider.addChangeListener(changeEvent -> {
            int value = colsSlider.getValue();
            colsSpinner.setValue(value);
        });

        SpinnerNumberModel rowsSpinnerModel = new SpinnerNumberModel(propertiesModel.getHorizontalCellsCount(), 2, 50, 1);
        rowsSpinner.setModel(rowsSpinnerModel);
        rowsSpinner.addChangeListener(changeEvent -> {
            int value = rowsSpinnerModel.getNumber().intValue();
            rowsSlider.setValue(value);
        });

        rowsSlider.setValue(propertiesModel.getHorizontalCellsCount());
        rowsSlider.setMinimum(2);
        rowsSlider.setMaximum(50);
        rowsSlider.addChangeListener(changeEvent -> {
            int value = rowsSlider.getValue();
            rowsSpinner.setValue(value);
        });

        Area area = propertiesModel.getArea();
        aValue.setText(String.valueOf(area.first.getX()));
        aValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                validateBoundary(aValue, bValue);
            }
        });

        bValue.setText(String.valueOf(area.second.getX()));
        bValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                validateBoundary(aValue, bValue);
            }
        });

        cValue.setText(String.valueOf(area.first.getY()));
        cValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                validateBoundary(cValue, dValue);
            }
        });

        dValue.setText(String.valueOf(area.second.getY()));
        dValue.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                validateBoundary(cValue, dValue);
            }
        });
    }

    private void validateBoundary(JTextField firstField, JTextField secondField) {
        double first = getValue(firstField);
        double second = getValue(secondField);

        if(Double.compare(first, second) < 0) {
            firstField.setText(String.valueOf(first));
            return;
        }

        if (Double.compare(second, -100.) <= 0) {
            first = second;
            second += 0.1;
        } else {
            first = second - 0.1;
        }

        firstField.setText(String.valueOf(first));
        secondField.setText(String.valueOf(second));
    }

    public boolean getResult() {
        return result;
    }

    private void onOK() {
        result = true;
        dispose();
    }

    private void onCancel() {
        result = false;
        dispose();
    }

    public int getRows() {
        return getValue(rowsSpinner);
    }

    public int getCols() {
        return getValue(colsSpinner);
    }

    public double getA() {
        return getValue(aValue);
    }

    public double getB() {
        return getValue(bValue);
    }

    public double getC() {
        return getValue(cValue);
    }

    public double getD() {
        return getValue(dValue);
    }

    private int getValue(JSpinner spinner) {
        Object objValue = spinner.getValue();
        if (objValue instanceof Number) {
            Number number = ((Number) objValue);
            return number.intValue();
        }

        return 10;
    }

    private double getValue(JTextField textField) {
        try {
            double value = Double.parseDouble(textField.getText());
            return Math.max(-100., Math.min(100., value));
        } catch (Exception e) {
            return 0.;
        }
    }
}
