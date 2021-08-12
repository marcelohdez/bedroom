package com.swiftsatchel.bedroom.settings;

import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener, WindowParent {

    private final SettingsUI sui;

    public SettingsDialog(WindowParent parent) {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        addWindowListener(this);
        setTitle("Settings");
        setResizable(false);

        sui = new SettingsUI(this);
        add(sui);

        pack();

        // Center on parent window
        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + ((xyWidthHeight[2]/2) - (this.getWidth()/2)),
                xyWidthHeight[1] + ((xyWidthHeight[3]/2) - (this.getHeight()/2)));

        setVisible(true);

    }

    @Override
    public void windowClosing(WindowEvent e) { // Save settings upon exiting

        sui.updateValues();

    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{this.getX(), this.getY(), this.getWidth(), this.getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) {}

}
