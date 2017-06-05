package pro.batalin.views.workspaces.templates;

import pro.batalin.ddl4j.model.Column;
import pro.batalin.views.workspaces.TableCreatorView;

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

    private Column oldColumn;

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

    public TableColumnView(Column column) {
        this();

        try {
            oldColumn = column.clone();
            setColumnName(column.getName());
            setType(column.getType().getType());
            setTypeSeize(column.getSize());
            setDefaultValue(column.getDefaultValue());
            setNotNull(column.isRequired());
            setUnique(column.isUnique());
            setPrimaryKey(column.isPrimaryKey());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    public String getColumnName() {
        return columnNameField.getText();
    }

    public void setColumnName(String value) {
        columnNameField.setText(value);
    }

    public String getType() {
        return typeField.getText();
    }

    public void setType(String value) {
        typeField.setText(value);
    }

    public String getDefaultValue() {
        String text = defaultValueFiled.getText();
        return text == null || text.isEmpty() ? null : text;
    }

    public void setDefaultValue(String value) {
        defaultValueFiled.setText(value);
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

    public void setTypeSeize(Integer value) {
        if (value == null) {
            return;
        }

        sizeTextField.setText(String.valueOf(value));
    }

    public boolean isNotNull() {
        return notNullCheckBox.isSelected();
    }

    public void setNotNull(boolean value) {
        notNullCheckBox.setSelected(value);
    }

    public boolean isUnique() {
        return uniqueCheckBox.isEnabled() && uniqueCheckBox.isSelected();
    }

    public void setUnique(boolean value) {
        uniqueCheckBox.setSelected(value);
    }

    public boolean isPrimaryKey() {
        return primaryKeyCheckBox.isSelected();
    }

    public void setPrimaryKey(boolean value) {
        primaryKeyCheckBox.setSelected(value);
    }
}
