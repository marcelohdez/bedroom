package me.soggysandwich.bedroom.dialog.settings;

import me.soggysandwich.bedroom.dialog.time.SelectTimeDialog;
import me.soggysandwich.bedroom.util.Reloadable;
import me.soggysandwich.bedroom.dialog.alert.AlertDialog;
import me.soggysandwich.bedroom.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener, KeyListener {

    private final SettingsUI sui;
    private final Component parent;
    private boolean shifting = false;

    public SettingsDialog(Component parent) {
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
        int[] arr = new int[]{parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight()};
        setLocation(arr[0] + ((arr[2] / 2) - (getWidth() / 2)), arr[1] + ((arr[3] / 2) - (getHeight() / 2)));

        setVisible(true);

    }

    Reloadable getReloadableComponent() {
        return (Reloadable) parent;
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

    // ======= Currently unused interface methods =======
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
