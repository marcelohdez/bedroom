package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.enums.SetTime;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SelectTimeDialog extends JDialog implements WindowListener, WindowParent {

    private final SelectTimeUI ui;
    public final TimeWindowType type;
    private final WindowParent parent;

    public SelectTimeDialog(WindowParent parent, TimeWindowType type) {

        this.type = type;
        this.parent = parent;

        // Initial properties
        reloadAlwaysOnTop();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        ui = new SelectTimeUI(this); // Create ui based on window type
        addKeyListener(ui);                     // Add key listener for shortcuts
        add(ui);                                // Add the UI

        pack();

        // Set window title and time per type
        switch (type) {
            case CLOCK_IN -> setTitle("Clocking in:");
            case CLOCK_OUT -> {
                setTitle("Clocking out:");
                setUITime(SetTime.CLOCK_IN_PLUS_DEFAULT);
            }
            case START_BREAK -> setTitle("Enter break:");
            case END_BREAK -> {
                setTitle("Leave break:");
                setUITime(SetTime.BREAK_START_PLUS_30M);
            }
            case EARLY_CLOCK_OUT -> setTitle("Early clock out:");
        }

        centerOnParent();
        setVisible(true); // Show self

    }

    public void centerOnParent() {

        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + ((xyWidthHeight[2] /2) - (getWidth()/2)),
                xyWidthHeight[1]  + ((xyWidthHeight[3] /2) - (getHeight()/2)));

    }

    public void setUITime(SetTime type) {
        // Set UI's list boxes to a time
        ui.setListBoxIndexes(type);
    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    public void close() {
        switch (type) {
            case CLOCK_IN -> System.exit(0);
            case CLOCK_OUT, END_BREAK -> {  // Go back to previous window
                dispose();
                parent.makeVisible(true);
            }
            case START_BREAK, EARLY_CLOCK_OUT -> dispose();  // Close window
        }
    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{getX(), getY(), getWidth(), getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }

    @Override
    public void reloadSettings() {

        ui.colorSelf();
        reloadAlwaysOnTop();

    }

    @Override
    public void windowClosing(WindowEvent e) {
        close();
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
