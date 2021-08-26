package com.swiftsatchel.bedroom.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * The default Bedroom button which extends JButton, makes it easier to have the hand cursor,
 * an action listener and a key listener set on all buttons upon creation instead of having to
 * use more code lines.
 */
public class BedroomButton extends JButton {

    /**
     * Creates a JButton with a hand cursor and its text, an action listener, and key listener set
     *
     * @param text The text
     * @param actionListener The action listener
     * @param keyListener The key listener
     */
    public BedroomButton(String text, ActionListener actionListener, KeyListener keyListener) {
        init(text, actionListener, keyListener);
    }

    /**
     * Creates a JButton with a hand cursor and its text plus an action listener set
     *
     * @param text The text
     * @param actionListener The action listener
     */
    public BedroomButton(String text, ActionListener actionListener) {
        init(text, actionListener, null);
    }

    /**
     * Creates a JButton with a hand cursor and its text already set
     *
     * @param text The text
     */
    public BedroomButton(String text) {
        init(text, null, null);
    }

    private void init(String text, ActionListener al, KeyListener kl) {

        // Defaults
        setText(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Optionals
        if (al != null) addActionListener(al);
        if (kl != null) addKeyListener(kl);

    }

}
