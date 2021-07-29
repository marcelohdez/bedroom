package com.marcelohdez.bedroom.dialog;

import com.marcelohdez.bedroom.main.*;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener {

    private final SettingsUI sui;

    public SettingsDialog() {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        addWindowListener(this);
        setTitle("Settings");
        setResizable(false);

        sui = new SettingsUI();
        add(sui);

        pack();

        // Center on main window
        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));

        setVisible(true);

    }

    @Override
    public void windowClosing(WindowEvent e) { // Save settings upon exiting

        this.sui.updateValues();
        Main.ui.requestFocus();

    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) {}


}
