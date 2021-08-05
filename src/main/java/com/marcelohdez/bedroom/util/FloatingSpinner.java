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

    public int showSelf(Point location) {

        setLocation(location);
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
