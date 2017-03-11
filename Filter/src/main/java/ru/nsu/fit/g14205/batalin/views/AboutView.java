package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 * Created by kir55rus on 02.03.17.
 */
public class AboutView extends JFrame {
    public AboutView(FilterController filterController) {

        setTitle("About FIT_14205_Batalin_Kirill_Filter");

        setLayout(new FlowLayout(FlowLayout.LEADING));
        URL imgUrl = this.getClass().getClassLoader().getResource("images/about.jpg");
        if (imgUrl != null) {
            JLabel photo = new JLabel(new ImageIcon(imgUrl));
            photo.setMaximumSize(new Dimension(100, 128));
            photo.setSize(100, 128);
            add(photo);
        }

        add(Box.createHorizontalStrut(20));


        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
        add(textPanel);

        textPanel.add(new JLabel("Author: Batalin Kirill"));
        textPanel.add(new JLabel("Group: 14205, NSU"));
        textPanel.add(new JLabel("Task: Filter"));

        textPanel.add(Box.createVerticalStrut(20));

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(actionEvent -> {
            filterController.onAboutDialogClosing();
        });
        textPanel.add(okButton);

        add(Box.createHorizontalStrut(20));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                filterController.onAboutDialogClosing();
            }
        });

        pack();
        setVisible(true);
    }
}
