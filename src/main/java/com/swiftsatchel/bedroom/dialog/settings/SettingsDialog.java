package com.swiftsatchel.bedroom.dialog.settings;

import com.swiftsatchel.bedroom.dialog.alert.AlertDialog;
import com.swiftsatchel.bedroom.dialog.time.SelectTimeDialog;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener, WindowParent, KeyListener {

    private final SettingsUI sui;
    private final WindowParent parent;
    private boolean shifting = false;

    public SettingsDialog(WindowParent parent) {
        this.parent = parent;

        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(this);
        setTitle("Settings");
        setResizable(false);
        addKeyListener(this);
        sui = new SettingsUI(this);
        add(sui);
        pack();

        // Center on parent window
        int[] arr = parent.getXYWidthHeight();
        setLocation(arr[0] + ((arr[2] / 2) - (getWidth() / 2)), arr[1] + ((arr[3] / 2) - (getHeight() / 2)));

        setVisible(true);

    }

    WindowParent getWindowParent() {
        return parent;
    }

    public boolean isShifting() {
        return shifting;
    }

    public void save() {

        sui.updateValues();
        if (sui.changeCount > 2 && parent instanceof SelectTimeDialog)
            new AlertDialog(this, // If colors were changed, create an alert
                    """
                    Some colors may not change
                    until the select time
                    dialog is reopened.""");

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT -> shifting = true; // If shift is pressed, we are shifting
            case KeyEvent.VK_ESCAPE -> {
                save();  // Save changes
                dispose(); // If escape is pressed, close window
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) shifting = false;
    }

    @Override
    public void windowClosing(WindowEvent e) { // Save settings upon exiting
        save();
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
    public void setDisabled(boolean b) {
        setEnabled(!b);
    }

    @Override
    public void askForFocus() {
        requestFocus();
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

    @Override
    public void keyTyped(KeyEvent e) {} // KeyListener

}
