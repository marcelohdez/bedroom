package com.swiftsatchel.bedroom.settings;

import com.swiftsatchel.bedroom.dialog.AlertDialog;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener, WindowParent {

    private final SettingsUI sui;
    private final WindowParent parent;

    public SettingsDialog(WindowParent parent) {

        this.parent = parent;

        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        addWindowListener(this);
        setTitle("Settings");
        setResizable(false);

        sui = new SettingsUI(this);
        add(sui);

        pack();

        // Center on parent window
        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + ((xyWidthHeight[2]/2) - (getWidth()/2)),
                xyWidthHeight[1] + ((xyWidthHeight[3]/2) - (getHeight()/2)));

        setVisible(true);

    }

    WindowParent getWindowParent() {
        return parent;
    }

    @Override
    public void windowClosing(WindowEvent e) { // Save settings upon exiting

        sui.updateValues();
        if (sui.changeCount > 2 && parent instanceof SelectTimeDialog)
            new AlertDialog(this, // If colors were changed, create an alert
                """
                Some colors may not change
                until the select time
                dialog is reopened.""");

    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{getX(), getY(), getWidth(), getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }

    // ======= Currently unused interface methods =======
    @Override
    public void reloadSettings() {} // WindowParent

    @Override
    public void windowOpened(WindowEvent e) {} // WindowListener
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}

}
