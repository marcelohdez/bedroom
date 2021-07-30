package com.marcelohdez.bedroom.main;

import javax.swing.*;

public class Window extends JFrame {

    private final UI ui;

    public Window() {

        setTitle("Bedroom " + Main.version);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));

        ui = new UI(this);
        ui.requestFocus();
        add(ui);
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

    public void reloadColors() { // Pass through to UI

        ui.reloadColors();

    }

}
