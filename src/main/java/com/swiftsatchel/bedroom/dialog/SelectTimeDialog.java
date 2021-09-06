package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.enums.SetTime;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.settings.SettingsDialog;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SelectTimeDialog extends JDialog implements WindowListener, WindowParent, KeyListener {

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
        addKeyListener(this);                   // Add key listener for shortcuts
        add(ui);                                // Add the UI

        pack();
        setSize((int)(getWidth()*1.4), (int)(getHeight()*1.2));

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

    private void centerOnParent() {

        setLocation(parent.getXYWidthHeight()[0] + ((parent.getXYWidthHeight()[2] / 2) - (getWidth() / 2)),
                parent.getXYWidthHeight()[1] + ((parent.getXYWidthHeight()[3] / 2) - (getHeight() / 2)));

    }

    public void setUITime(SetTime type) {
        // Set UI's list boxes to a time
        ui.setListBoxIndexes(type);
    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Settings.getAlwaysOnTop());
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
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER, 13 -> ui.selectTime(); // Select time with Enter (return on macOS, which is 13)
            case KeyEvent.VK_ESCAPE -> close();
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(this);  // Open settings with Delete or Backspace keys
        }
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
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

}
