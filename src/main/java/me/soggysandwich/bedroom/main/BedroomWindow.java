package me.soggysandwich.bedroom.main;

import me.soggysandwich.bedroom.Bedroom;
import me.soggysandwich.bedroom.dialog.history.ShiftHistoryWindow;
import me.soggysandwich.bedroom.dialog.settings.SettingsDialog;
import me.soggysandwich.bedroom.dialog.time.SelectTimeDialog;
import me.soggysandwich.bedroom.util.TimeWindowType;
import me.soggysandwich.bedroom.util.Settings;
import me.soggysandwich.bedroom.util.Reloadable;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;

public class BedroomWindow extends JFrame implements Reloadable, WindowListener, KeyListener {

    private final UI ui = new UI(this);
    private SelectTimeDialog clockInDialog;

    public BedroomWindow() {

        setTitle("Bedroom " + Bedroom.VERSION);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        reloadAlwaysOnTop();
        addWindowListener(this);
        addKeyListener(this);

        add(ui);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        if (!Bedroom.timesChosen()) {
            SwingUtilities.invokeLater(() -> {
                clockInDialog = new SelectTimeDialog(this, TimeWindowType.CLOCK_IN);
                clockInDialog.showSelf();
            });
        }

    }

    public void display(String text) {
        ui.display(text);
    }

    public void reloadAlwaysOnTop() {
        setAlwaysOnTop(Settings.getAlwaysOnTop());
    }

    public void enterBreak() {
        new SelectTimeDialog(this, TimeWindowType.START_BREAK).showSelf();
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
    public void keyPressed(KeyEvent e) {
        // ======= Shortcuts =======
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN -> Bedroom.setOrders(Bedroom.getOrders() - 1, true); // Remove orders
            case KeyEvent.VK_0 -> enterBreak();             // Set break times
            case KeyEvent.VK_UP -> Bedroom.setOrders(Bedroom.getOrders() + 1, true); // Add orders
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(this);  // Open settings with Delete or Backspace keys
            case KeyEvent.VK_BACK_SLASH -> new ShiftHistoryWindow(this);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {

        // If we are currently in our shift:
        if (LocalDateTime.now().isAfter(Bedroom.getClockInTime()) &&
                LocalDateTime.now().isBefore(Bedroom.getClockOutTime())) {

            // Clock out early
            if (Settings.getAskBeforeEarlyClose()) {
                // If we have the option selected, show a dialog for confirmation
                new SelectTimeDialog(this, TimeWindowType.EARLY_CLOCK_OUT).showSelf();

                // If we do not have the option selected, just clock out early at
                // the current time down to the minute
            } else Bedroom.clockOut(LocalDateTime.parse(LocalDateTime.now().toString().substring(0, 16)));

        } else if (LocalDateTime.now().isBefore(Bedroom.getClockInTime())) { // If we have not clocked in:

            Bedroom.exit(); // Just exit

        } else Bedroom.clockOut(Bedroom.getClockOutTime()); // If our shift has ended, just clock out with original times.

    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (clockInDialog != null) {
            clockInDialog.dispose();
            clockInDialog = null;
        }
    }

    // Unused
    @Override
    public void windowOpened(WindowEvent e) {}
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
