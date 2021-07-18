package com.marcelohdez.settings;

import com.marcelohdez.bedroom.*;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsWindow extends JDialog implements WindowListener {

    private static SettingsUI sui;

    public SettingsWindow() {

        addWindowListener(this);
        setModalityType(ModalityType.APPLICATION_MODAL); // Do not allow user to do anything outside of this window
        setTitle("Settings");
        setResizable(false);

        sui = new SettingsUI();
        add(sui);

        // Center on main window
        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));

        pack();
        setVisible(true);

    }

    @Override
    public void windowClosing(WindowEvent e) { // Save settings upon exiting

        sui.updateValues();

    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) {}


}
