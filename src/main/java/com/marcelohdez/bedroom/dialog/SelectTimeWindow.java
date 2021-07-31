package com.marcelohdez.bedroom.dialog;

import com.marcelohdez.bedroom.main.*;
import com.marcelohdez.bedroom.enums.*;

import java.awt.event.*;
import javax.swing.*;

public class SelectTimeWindow extends JFrame implements WindowListener {

    private final SelectTimeUI ui;
    private final TimeWindowType type;

    public SelectTimeWindow(TimeWindowType type) {

        this.type = type;

        // Initial properties
        reloadAlwaysOnTop();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        ui = new SelectTimeUI(this, type); // Create ui based on window type
        addKeyListener(ui);                     // Add key listener for shortcuts
        add(ui);                                // Add the UI

        pack();

        // Set window title per window type
        switch (type) {
            case CLOCK_OUT -> setTitle("Clocking out:");
            case START_BREAK -> setTitle("Enter break:");
            case END_BREAK -> setTitle("Leave break:");
            case CLOCK_IN -> {
                setTitle("Clocking in:");
                setVisible(true); // Automatically show clock in window
                centerOnMainWindow();
            }
        }

    }

    public void centerOnMainWindow() {

        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));
        ui.requestFocus();

    }

    public void setUITime(SetTime type) {
        // Set ui's list boxes to a time
        ui.setListBoxIndexes(type);
    }

    public void reloadAlwaysOnTop() {
        this.setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    public void reloadSettings() {

        ui.colorSelf();
        reloadAlwaysOnTop();

    }

    @Override
    public void windowClosing(WindowEvent e) {

        switch (this.type) {
            case CLOCK_OUT -> { // Go back to clock in time window
                Main.clockOutWnd.dispose();
                Main.clockInWnd.setVisible(true);
            }
            case END_BREAK, START_BREAK -> this.dispose();   // Close window
        }

    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

}
