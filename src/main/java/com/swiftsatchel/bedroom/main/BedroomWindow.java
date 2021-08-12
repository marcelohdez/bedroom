package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.dialog.SelectTimeWindow;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;

public class BedroomWindow extends JFrame implements WindowParent {

    private final UI ui;

    public BedroomWindow() {

        setTitle("Bedroom " + Main.version);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        reloadAlwaysOnTop();


        ui = new UI(this);
        add(ui);
        addKeyListener(ui);

        pack();
        ui.sizeButtons();
        pack();

        setLocationRelativeTo(null);

        setVisible(true);
        new SelectTimeWindow(this, TimeWindowType.CLOCK_IN); // Create clock in window

    }

    public void reloadAlwaysOnTop() {
        this.setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    public void reloadSettings() { // Pass through to UI

        ui.reloadColors();
        reloadAlwaysOnTop();

    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{this.getX(), this.getY(), this.getWidth(), this.getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }
}
