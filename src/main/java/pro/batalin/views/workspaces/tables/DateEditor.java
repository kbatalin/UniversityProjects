package pro.batalin.views.workspaces.tables;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class DateEditor extends DefaultCellEditor {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");;

    public DateEditor() {
        super(new JTextField());

        editorComponent.setBorder(null);
    }

    @Override
    public Object getCellEditorValue() {
        String dateStr = ((JTextField) editorComponent).getText();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            Date parsedDate = dateFormat.parse(dateStr);
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Component getTableCellEditorComponent(JTable table,  Object value, boolean isSelected, int row, int col) {
        super.getTableCellEditorComponent(table, value, isSelected, row, col);

        if (value instanceof Timestamp) {
            ((JTextField) editorComponent).setText(dateFormat.format((Timestamp)value));
        }

        return editorComponent;
    }

}
