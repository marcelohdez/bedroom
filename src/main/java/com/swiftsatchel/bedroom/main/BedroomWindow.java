package com.swiftsatchel.bedroom.main;

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
}
