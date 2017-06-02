package pro.batalin.views.workspaces.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class DateRenderer extends DefaultTableCellRenderer {
    private SimpleDateFormat dateFormat;

    public DateRenderer() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col){
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if(value instanceof Timestamp){
            String dateStr = dateFormat.format((Timestamp) value);
            setText(dateStr);
        }

        return this;
    }
}
