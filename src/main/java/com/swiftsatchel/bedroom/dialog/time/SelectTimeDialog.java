package com.swiftsatchel.bedroom.dialog.time;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.history.ShiftHistoryWindow;
import com.swiftsatchel.bedroom.dialog.settings.SettingsDialog;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.main.BedroomWindow;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;

public class SelectTimeDialog extends JDialog implements WindowListener, WindowParent, KeyListener {

    private final SelectTimeUI ui;
    public final TimeWindowType type;
    private final BedroomWindow initParent;
    private final SelectTimeDialog lastDialog; // Used in cases where two select time dialogs are needed
    private boolean shifting = false;

    public SelectTimeDialog(BedroomWindow parent, TimeWindowType type) {
        parent.setDisabled(true);
        this.type = type;
        this.initParent = parent;
        lastDialog = null;
        ui = new SelectTimeUI(this);
        init();
    }

    // Creates a continued select time dialog , given a parent and window type, plus the last selected time
    // and the original parent of this group of select time dialogs
    public SelectTimeDialog(SelectTimeDialog parent, TimeWindowType type, LocalDateTime lastTime) {
        this.type = type;
        initParent = parent.getInitParent();
        lastDialog = parent;
        ui = new SelectTimeUI(this, lastTime); // Create continuation UI
        init();
    }

    private void init() {

        // Set window title per type
        switch (type) {
            case CLOCK_IN -> setTitle("Clocking in:");
            case CLOCK_OUT -> setTitle("Clocking out:");
            case START_BREAK -> setTitle("Enter break:");
            case END_BREAK -> setTitle("Leave break:");
            case EARLY_CLOCK_OUT -> setTitle("Early clock out:");
        }

        // Initial properties
        reloadAlwaysOnTop();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        addKeyListener(this);                   // Add key listener for shortcuts
        add(ui);                                // Add the UI
        pack();
        setMinimumSize(new Dimension((int)(getWidth()*1.4), (int)(getHeight()*1.2)));
        // Center on parent
        int[] arr = initParent.getXYWidthHeight();
        setLocation(arr[0] + ((arr[2] / 2) - (getWidth() / 2)), arr[1] + ((arr[3] / 2) - (getHeight() / 2)));
        setVisible(true); // Show self

    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Settings.getAlwaysOnTop());
    }

    public void close() {
        switch (type) {
            case CLOCK_IN -> Main.exit();
            case CLOCK_OUT, END_BREAK -> {  // Go back to previous window
                dispose();
                lastDialog.makeVisible(true);
            }
            case START_BREAK, EARLY_CLOCK_OUT -> dispose();  // Close window
        }
        if (lastDialog != null) {
            lastDialog.setDisabled(false);
        } else initParent.setDisabled(false);
    }

    /**
     * Dispose this window and its parent, to finish this set and clear up memory
     */
    protected void finish() {
        if (lastDialog != null) lastDialog.dispose();
        dispose();
    }

    public BedroomWindow getInitParent() {
        return initParent;
    }

    public boolean isShifting() {
        return shifting;
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

        ui.reColorComps();
        reloadAlwaysOnTop();

    }

    @Override
    public void setDisabled(boolean b) {
        setEnabled(!b);
    }

    @Override
    public void askForFocus() {
        requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER, 13 -> ui.selectTime(); // Select time with Enter (return on macOS, which is 13)
            case KeyEvent.VK_ESCAPE -> close();
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(this);  // Open settings with Delete or Backspace keys
            case KeyEvent.VK_BACK_SLASH -> new ShiftHistoryWindow(this);
            case KeyEvent.VK_SHIFT -> shifting = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) shifting = false;
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

}
