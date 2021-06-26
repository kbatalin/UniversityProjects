package pro.batalin.views.status_bar;

import pro.batalin.views.status_bar.indicators.Indicator;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class StatusBar extends JPanel {
    private JPanel contentPanel;
    private JPanel indicatorsPanel;
    private JLabel messageLabel;
    private Map<String, Indicator> indicators = new ConcurrentHashMap<>();

    public StatusBar() {
        setLayout(new BorderLayout());
        add(contentPanel);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public boolean addIndicator(String name, Indicator indicator) {
        return addIndicator(name, indicator, true);
    }

    public boolean addIndicator(String name, Indicator indicator, boolean visible) {
        if (indicators.containsKey(name)) {
            return false;
        }
        indicators.put(name, indicator);
        indicatorsPanel.add(indicator.getComponent());
        indicator.getComponent().setVisible(visible);
        return true;
    }

    public void removeIndicator(String name) {
        Indicator indicator = indicators.get(name);

        if (indicator != null) {
            indicatorsPanel.remove(indicator.getComponent());
        }
    }

    public void setIndicatorVisible(String name, boolean visible) {
        Indicator indicator = indicators.get(name);

        if (indicator == null) {
            return;
        }

        indicator.getComponent().setVisible(visible);
    }
}
