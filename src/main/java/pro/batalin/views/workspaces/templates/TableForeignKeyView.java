package pro.batalin.views.workspaces.templates;

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

    public TableForeignKeyView() {
        setLayout(new BorderLayout());
        add(contentPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMinimumSize().height));
    }

    public String getFromColumn() {
        return fromColumnField.getText();
    }

    public String getToColumn() {
        return toColumnField.getText();
    }

    public String getToTable() {
        return toTableField.getText();
    }
}
