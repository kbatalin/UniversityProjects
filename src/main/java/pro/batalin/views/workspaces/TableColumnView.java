package pro.batalin.views.workspaces;

import javax.swing.*;
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

    public TableColumnView() {
        setLayout(new BorderLayout());
        add(contentPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMinimumSize().height));
    }
}
