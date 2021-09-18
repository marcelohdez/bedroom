package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.time.SelectTimeDialog;
import com.swiftsatchel.bedroom.dialog.ShiftHistoryWindow;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.dialog.settings.SettingsDialog;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BedroomWindow extends JFrame implements WindowParent, WindowListener, KeyListener {

    private final UI ui;

    public BedroomWindow() {

        setTitle("Bedroom " + Main.VERSION);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        reloadAlwaysOnTop();
        addWindowListener(this);
        addKeyListener(this);

        ui = new UI(this);
        add(ui);

        pack();
        ui.sizeButtons();
        pack();

        setLocationRelativeTo(null);

        setVisible(true);

    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Settings.getAlwaysOnTop());
    }

    public void enterBreak() {
        new SelectTimeDialog(this, TimeWindowType.START_BREAK);
    }

    public void enableButtons() {
        ui.enableButtons();
    }

    public void disableButtons(UI.Buttons b) {
        ui.disableButtons(b);
    }

    @Override
    public void reloadSettings() {

        ui.colorComponents();
        reloadAlwaysOnTop();

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

    @Override
    public void keyPressed(KeyEvent e) {
        // ======= Shortcuts =======
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN ->
                    Main.changeOrders(-1); // Remove orders with BckSpc & Down Arrow
            case KeyEvent.VK_0 -> enterBreak();             // Set break times with 0
            case KeyEvent.VK_UP -> Main.changeOrders(1); // Add orders with up arrow
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(this);  // Open settings with Delete or Backspace keys
            case KeyEvent.VK_BACK_SLASH -> new ShiftHistoryWindow(this);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {

        // If we are currently in our shift:
        if (LocalDateTime.now().isAfter(Main.getClockInTime()) &&
                LocalDateTime.now().isBefore(Main.getClockOutTime())) {

            // Clock out early
            if (Settings.getAskBeforeEarlyClose()) {
                // If we have the option selected, show a dialog for confirmation
                new SelectTimeDialog(this, TimeWindowType.EARLY_CLOCK_OUT);

                // If we do not have the option selected, just clock out early at
                // the current time down to the minute
            } else Main.clockOut(LocalDateTime.parse((LocalDate.now() + "T" +
                    Ops.addZeroUnder10(LocalTime.now().getHour()) + ":" +
                    Ops.addZeroUnder10(LocalTime.now().getMinute()))));

        } else if (LocalDateTime.now().isBefore(Main.getClockInTime())) { // If we have not clocked in:

            Main.exit(); // Just exit

        } else Main.clockOut(Main.getClockOutTime()); // If our shift has ended, just clock out with original times.

    }

    // Unused
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
