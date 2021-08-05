package com.marcelohdez.bedroom.util;

import com.marcelohdez.bedroom.main.UI;

import javax.swing.*;
import java.awt.*;

public class Theme {

    /**
     * Colors a JComponent's background and foreground.
     *
     * @param c Component to color
     */
    public static void colorThis(JComponent c) {

        if (c instanceof JButton || c instanceof JList || c instanceof JComboBox) {
            c.setBackground(UI.buttonColor);
            c.setForeground(UI.buttonTextColor);
        } else {
            c.setBackground(UI.bg);
            c.setForeground(UI.textColor);
        }

    }

    /**
     * Colors a list of components' background and foreground.
     *
     * @param c Components to color
     */
    public static void colorThese(JComponent[] c) {

        for (JComponent comp : c) {
            if (comp instanceof JButton || comp instanceof JList || comp instanceof JComboBox) {
                comp.setBackground(UI.buttonColor);
                comp.setForeground(UI.buttonTextColor);
            } else {
                comp.setBackground(UI.bg);
                comp.setForeground(UI.textColor);
            }
        }

    }

    /**
     * Returns white or black depending on how bright the color given is, in order to create contrast.
     *
     * @param c Color to calculate off of
     * @return White or black
     */
    public static Color contrastTo(Color c) {

        if (c.getRed() + c.getGreen() + c.getBlue() > 525) return Color.BLACK;

        return Color.WHITE;

    }

    /**
     * Darkens the given color by the given amount
     *
     * @param c Color to darken
     * @param amount Amount to darken by
     * @return Darkened color
     */
    public static Color darken(Color c, int amount) {

        int r = c.getRed() - amount;
        int g = c.getGreen() - amount;
        int b = c.getBlue() - amount;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;

        return new Color(r, g, b);

    }

}
