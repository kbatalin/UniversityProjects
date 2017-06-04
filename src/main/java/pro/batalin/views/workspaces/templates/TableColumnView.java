package pro.batalin.views.workspaces.templates;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableColumnView extends JPanel {
    private JPanel contentPanel;
    private JTextField columnNameField;
    private JTextField typeField;
    private JTextField defaultValueFiled;
    private JCheckBox notNullCheckBox;
    private JCheckBox uniqueCheckBox;
    private JTextField sizeTextField;
    private JCheckBox primaryKeyCheckBox;

    public TableColumnView() {
        setLayout(new BorderLayout());
        add(contentPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMinimumSize().height));

        primaryKeyCheckBox.addChangeListener(changeEvent -> {
            boolean selected = primaryKeyCheckBox.isSelected();
            notNullCheckBox.setEnabled(!selected);
            uniqueCheckBox.setEnabled(!selected);
        });
    }

    public String getColumnName() {
        return columnNameField.getText();
    }

    public String getType() {
        return typeField.getText();
    }

    public String getDefaultValue() {
        return defaultValueFiled.getText();
    }

    public Integer getTypeSize() {
        String sizeStr = sizeTextField.getText();
        if (sizeStr == null || sizeStr.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(sizeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isNotNull() {
        return notNullCheckBox.isEnabled() && notNullCheckBox.isSelected();
    }

    public boolean isUnique() {
        return uniqueCheckBox.isEnabled() && uniqueCheckBox.isSelected();
    }

    public boolean isPrimaryKey() {
        return primaryKeyCheckBox.isSelected();
    }
}
