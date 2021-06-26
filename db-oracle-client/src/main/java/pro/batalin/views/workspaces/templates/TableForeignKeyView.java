package pro.batalin.views.workspaces.templates;

import pro.batalin.ddl4j.model.constraints.ForeignKey;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TableForeignKeyView  extends JPanel {
    private JPanel contentPanel;
    private JTextField toColumnField;
    private JTextField fromColumnField;
    private JTextField toTableField;

    private ForeignKey oldForeignKey;

    public TableForeignKeyView() {
        setLayout(new BorderLayout());
        add(contentPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMinimumSize().height));
    }

    public TableForeignKeyView(ForeignKey foreignKey) {
        this();

        this.oldForeignKey = foreignKey;

        setFromColumn(foreignKey.getFirstColumn().getName());
        setToTable(foreignKey.getSecondTable().getName());
        setToColumn(foreignKey.getSecondColumn().getName());
    }

    public ForeignKey getOldForeignKey() {
        return oldForeignKey;
    }

    public String getFromColumn() {
        return fromColumnField.getText();
    }

    public void setFromColumn(String value) {
        fromColumnField.setText(value);
    }

    public String getToColumn() {
        return toColumnField.getText();
    }

    public void setToColumn(String value) {
        toColumnField.setText(value);
    }

    public String getToTable() {
        return toTableField.getText();
    }

    public void setToTable(String value) {
        toTableField.setText(value);
    }
}
