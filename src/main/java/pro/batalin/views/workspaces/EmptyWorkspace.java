package pro.batalin.views.workspaces;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class EmptyWorkspace extends JPanel {
    private JPanel contentPane;

    public EmptyWorkspace() {
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }
}
