package pro.batalin.views.workspaces.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class DbRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col){
        if(value instanceof Timestamp){
            String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)value);
            setText(strDate);
            return this;
        } else {
            return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, col );
        }
    }
}
