package me.marcelohdez.bedroom.dialog.settings;

import me.marcelohdez.bedroom.Bedroom;
import me.marcelohdez.bedroom.dialog.time.SelectTimeDialog;
import me.marcelohdez.bedroom.util.Reloadable;
import me.marcelohdez.bedroom.dialog.alert.AlertDialog;
import me.marcelohdez.bedroom.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SettingsDialog extends JDialog implements WindowListener, KeyListener {

    private final SettingsUI sui;
    private final Reloadable summoner;
    private boolean shifting = false;
    private final boolean isSystemLAFEnabled = Settings.isSystemLAFEnabled(); // Check for change upon closing

    public SettingsDialog(Reloadable summoner) {
        this.summoner = summoner;

        setTitle("Settings");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(this);
        setResizable(false);
        addKeyListener(this);
        sui = new SettingsUI(this);
        add(sui);

        pack();
        if (summoner instanceof Component) {
            setLocationRelativeTo((Component) summoner); // Center on parent window
        }
        setVisible(true); // Show

    }

    Reloadable getSummoner() {
        return summoner;
    }

    public boolean isShifting() {
        return shifting;
    }

    public void saveChanges() {
        sui.updateValues();
        Settings.enableSystemLAF(sui.isSystemLAFChosen());
        alertSelectTimeDialogColorChange();
        alertLAFChange();
    }

    private void alertSelectTimeDialogColorChange() {
        if (sui.changeCount > 2 && summoner instanceof SelectTimeDialog) {
            // If colors were changed, create an alert
            new AlertDialog(this,
                    """
                    Some colors may not change
                    until the select time
                    dialog is reopened.""");
        }
    }

    private void alertLAFChange() {
        if (isSystemLAFEnabled != Settings.isSystemLAFEnabled()) {
            if (!Settings.isSystemLAFEnabled()) {
                Bedroom.restart();
            } else {
                new AlertDialog(this, """
                        Changing from the system's theme
                        to a colored one requires a restart
                        so please reopen me as I will now
                        close.""");
                System.exit(0);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT -> shifting = true; // If shift is pressed, we are shifting
            case KeyEvent.VK_ESCAPE -> {
                saveChanges();  // Save changes
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
        saveChanges();
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
