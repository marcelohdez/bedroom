package com.marcelohdez.bedroom.util;

import com.marcelohdez.bedroom.main.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Creates a floating JSpinner with the chosen start, min, and max values
 */
public class FloatingSpinner extends JDialog implements ChangeListener {

    private final JSpinner js;

    public FloatingSpinner(int startingValue, int min, int max) {

        setUndecorated(true);
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        setModalityType(ModalityType.APPLICATION_MODAL);

        js = new JSpinner(new SpinnerNumberModel(startingValue, min, max, 1));
        js.addChangeListener(this);
        add(js);

        pack();

    }

    /**
     * Shows self at mouse pointer's position. This freezes the application until
     * a new value is entered and returned once setVisible(false) is called.
     *
     * @return The newly entered value.
     */
    public int showSelf() {

        setLocation(MouseInfo.getPointerInfo().getLocation());
        requestFocus();
        setVisible(true);

        return (int) js.getValue();

    }

    @Override
    public void stateChanged(ChangeEvent e) {

        setVisible(false);
        dispose();

    }
}
