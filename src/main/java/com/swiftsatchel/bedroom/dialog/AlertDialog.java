package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.util.UIBuilder;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlertDialog extends JDialog implements ActionListener {

    public AlertDialog(WindowParent parent, String alert) {

        UIBuilder.createDialog(this, this, "Alert", alert);

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
