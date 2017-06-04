package pro.batalin.views.workspaces.templates;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class TablePrimaryKeyView extends JPanel {
    private JPanel contentPanel;
    private JTextField columnField;

    public TablePrimaryKeyView() {
        setLayout(new BorderLayout());
        add(contentPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMinimumSize().height));
    }

    public String getColumnName() {
        return columnField.getText();
    }
}
