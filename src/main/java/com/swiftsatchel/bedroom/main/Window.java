package com.swiftsatchel.bedroom.main;

import javax.swing.*;

public class Window extends JFrame {

    private final UI ui;

    public Window() {

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
        this.setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
    }

    public void reloadSettings() { // Pass through to UI

        ui.reloadColors();
        reloadAlwaysOnTop();

    }

}
