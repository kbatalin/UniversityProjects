package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class AboutView extends JDialog {
    private JPanel contentPane;
    private JButton okButton;
    private JLabel authorPhoto;

    public AboutView() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okButton);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        URL imgUrl = this.getClass().getClassLoader().getResource("resources/images/about.jpg");
        if (imgUrl != null) {
            authorPhoto = new JLabel(new ImageIcon(imgUrl));
            authorPhoto.setMaximumSize(new Dimension(100, 128));
            authorPhoto.setSize(100, 128);
        }
    }
}
