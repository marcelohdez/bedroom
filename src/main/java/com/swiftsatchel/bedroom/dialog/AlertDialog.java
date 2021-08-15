package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlertDialog extends JDialog implements ActionListener {

    public AlertDialog(WindowParent parent, String alert) {

        // Create components
        JPanel topUI = new JPanel();
        JPanel botUI = new JPanel();
        JTextArea message = new JTextArea(alert);
        JButton ok = new JButton("OK");

        // Customize components
        Theme.colorThese(new JComponent[]{topUI, botUI, message, ok});
        message.setFont(UI.boldText);
        message.setEditable(false);
        ok.addActionListener(this);

        // Add components
        topUI.add(message);
        botUI.add(ok);
        add(topUI, BorderLayout.PAGE_START);
        add(botUI, BorderLayout.PAGE_END);

        // Set window properties
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Retain input from other windows
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Alert");
        pack();
        ok.setPreferredSize(new Dimension(getWidth() - 5, 40));
        pack();

        // Center on summoner
        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + (xyWidthHeight[2]/2 - (getWidth()/2)),
                xyWidthHeight[1] + (xyWidthHeight[3]/2 - (getHeight()/2)));

        // Show
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) { // Do sum when OK is pressed
        dispose();
    }

}
