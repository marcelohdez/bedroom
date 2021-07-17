package com.marcelohdez.dialog;

import com.marcelohdez.bedroom.*;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

public class SelectTimeWindow extends JFrame implements WindowListener {

    private final SelectTimeUI ui;

    public SelectTimeWindow(Main.TIME_WINDOW type) {

        // Initial properties
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        ui = new SelectTimeUI(type);            // Create ui based on window type
        add(ui);                                // Add the UI

        pack();

        // Set window title per window type
        switch (type) {
            case CLOCK_OUT_TYPE -> setTitle("Clocking out:");
            case START_BREAK_TYPE -> setTitle("Enter break:");
            case END_BREAK_TYPE -> setTitle("Leave break:");
            case CLOCK_IN_TYPE -> {
                setTitle("Clocking in:");
                setVisible(true); // Automatically show clock in window
            }
        }

    }

    public void centerOnMainWindow() {

        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));

    }

    public void setUITime(SelectTimeUI.GET_TIME type) { // Pass through to UI
        // Set to current time, for windows opened up after program start up like break start window
        ui.setListBoxIndexes(type);
    }

    // Cancel setting break times if user tries to close window
    @Override
    public void windowClosing(WindowEvent e) {

        if ( Main.enterBreakWnd.isVisible()) { // If setting break start time:

            Main.enterBreakWnd.dispose();   // Close the window

        } else if (Main.leaveBreakWnd.isVisible()) { // If setting break end time:

            UI.breakInTime = null;          // Delete break start time that was set
            Main.leaveBreakWnd.dispose();   // Close the window

        }

    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

}
