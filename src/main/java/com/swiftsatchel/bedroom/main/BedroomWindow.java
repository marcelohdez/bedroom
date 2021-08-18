package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;

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

        if (LocalDateTime.now().isBefore(Main.clockOutTime)) {

            new SelectTimeDialog(this, TimeWindowType.EARLY_CLOCK_OUT);

        } else Main.clockOut(Main.clockOutTime);

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
