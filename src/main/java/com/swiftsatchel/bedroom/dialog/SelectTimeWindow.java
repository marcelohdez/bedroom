package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.enums.SetTime;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.util.WindowParent;

import java.awt.event.*;
import javax.swing.*;

public class SelectTimeWindow extends JFrame implements WindowListener, WindowParent {

    private final SelectTimeUI ui;
    public final TimeWindowType type;
    private final WindowParent parent;

    public SelectTimeWindow(WindowParent parent, TimeWindowType type) {

        this.type = type;
        this.parent = parent;

        // Initial properties
        reloadAlwaysOnTop();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        ui = new SelectTimeUI(this); // Create ui based on window type
        addKeyListener(ui);                     // Add key listener for shortcuts
        add(ui);                                // Add the UI

        pack();

        // Set window title per window type
        switch (type) {
            case CLOCK_OUT -> setTitle("Clocking out:");
            case START_BREAK -> setTitle("Enter break:");
            case END_BREAK -> setTitle("Leave break:");
            case CLOCK_IN -> setTitle("Clocking in:");
        }

        centerOnParent();
        setVisible(true); // Show self

    }

    public void centerOnParent() {

        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + ((xyWidthHeight[2] /2) - (this.getWidth()/2)),
                xyWidthHeight[1]  + ((xyWidthHeight[3] /2) - (this.getHeight()/2)));

    }

    public void setUITime(SetTime type) {
        // Set UI's list boxes to a time
        ui.setListBoxIndexes(type);
    }

    public void reloadAlwaysOnTop() {
        this.setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    @Override
    public void windowClosing(WindowEvent e) {

        switch (this.type) {
            case CLOCK_OUT, END_BREAK -> { // Go back to clock in time window
                parent.makeVisible(true);
                this.dispose();
            }
            case START_BREAK -> this.dispose();   // Close window
        }

    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{this.getX(), this.getY(), this.getWidth(), this.getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }

    @Override
    public void windowOpened(WindowEvent e) {}
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
