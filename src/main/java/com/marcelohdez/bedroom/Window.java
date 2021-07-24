package com.marcelohdez.bedroom;

import javax.swing.*;

public class Window extends JFrame {

    public Window(UI ui) {

        setTitle("Bedroom " + Main.version);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));

        ui.requestFocus();
        add(ui);
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

}
