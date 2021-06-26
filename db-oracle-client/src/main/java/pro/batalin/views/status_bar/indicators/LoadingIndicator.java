package pro.batalin.views.status_bar.indicators;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class LoadingIndicator implements Indicator {
    private JLabel iconLabel;

    public LoadingIndicator() {
        try {
            Icon icon = new ImageIcon(getClass().getResource("/images/loading.gif"));
            iconLabel = new JLabel(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JComponent getComponent() {
        return iconLabel;
    }
}
