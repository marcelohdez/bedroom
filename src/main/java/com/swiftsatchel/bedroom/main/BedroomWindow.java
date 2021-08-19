package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BedroomWindow extends JFrame implements WindowParent, WindowListener {

    private final UI ui;

    public BedroomWindow() {

        setTitle("Bedroom " + Main.version);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        reloadAlwaysOnTop();
        addWindowListener(this);

        ui = new UI(this);
        add(ui);
        addKeyListener(ui);

        pack();
        ui.sizeButtons();
        pack();

        setLocationRelativeTo(null);

        setVisible(true);

    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    @Override
    public void reloadSettings() {

        ui.reloadColors();
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
    public void windowClosing(WindowEvent e) {

        // If we are currently in our shift:
        if (LocalDateTime.now().isAfter(Main.clockInTime) &&
                LocalDateTime.now().isBefore(Main.clockOutTime)) {

            // Clock out early
            if (Main.userPrefs.getBoolean("askBeforeEarlyClose", true)) {
                // If we have the option selected, show a dialog for confirmation
                new SelectTimeDialog(this, TimeWindowType.EARLY_CLOCK_OUT);

                // If we do not have the option selected, just clock out early at
                // the current time down to the minute
            } else Main.clockOut(LocalDateTime.parse((LocalDate.now() + "T" +
                    Ops.addZeroUnder10(LocalTime.now().getHour()) + ":" +
                    Ops.addZeroUnder10(LocalTime.now().getMinute()))));

        } else if (LocalDateTime.now().isBefore(Main.clockInTime)) { // If we have not clocked in:

            System.exit(0); // Just exit.

        } else Main.clockOut(Main.clockOutTime); // If our shift has ended, just clock out with original times.

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
