package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.main.UI;

import javax.swing.*;
import java.awt.*;

public final class Theme {

    /**
     * Colors a JComponent's background and foreground.
     *
     * @param c Component to color
     */
    public static void colorThis(JComponent c) {

        if (c instanceof JButton || c instanceof JList || c instanceof JComboBox) {
            c.setBackground(UI.getButtonColor());
            c.setForeground(UI.getButtonTextColor());
        } else {
            c.setBackground(UI.getBgColor());
            c.setForeground(UI.getTextColor());
        }

    }

    /**
     * Colors a list of components' background and foreground.
     *
     * @param comps Components to color
     */
    public static void colorThese(JComponent[] comps) {

        for (JComponent c : comps) {
            colorThis(c);
        }

    }

    /**
     * Returns white or black depending on how bright the color given is, in order to create contrast.
     *
     * @param c Color to contrast with
     * @return White or black
     */
    public static Color contrastWithBnW(Color c) {

        // If colors add up to 525+ return black, else return white
        return (c.getRed() + c.getGreen() + c.getBlue() > 525) ? Color.BLACK : Color.WHITE;

    }

    /**
     * Returns a darkened or lightened version of given color depending on brightness.
     *
     * @param c Color to contrast with
     * @param amount Amount to lighten/darken by
     * @return New color
     */
    public static Color contrastWithColor(Color c, int amount) {

        // If colors add up to 525+ return black, else return white
        return (c.getRed() + c.getGreen() + c.getBlue() > 525) ? darkenBy(c, amount) :
                darkenBy(c, -amount);

    }

    /**
     * Darkens the given color by the given amount, or lightens if amount is negative
     *
     * @param c Color to darken
     * @param amount Amount to darken by
     * @return Darkened color
     */
    public static Color darkenBy(Color c, int amount) {

        int r = c.getRed() - amount;
        int g = c.getGreen() - amount;
        int b = c.getBlue() - amount;
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;

        return new Color(r, g, b);

    }

    /**
     * Sets color accents not accessible through setForeground or setBackground.
     * Hence, this only works before creating the components.
     */
    public static void setAccents() {

        Color c = contrastWithColor(UI.getButtonColor(), 30);
        UIManager.put("Button.select", c);
        UIManager.put("Button.focus", c);
        UIManager.put("ComboBox.selectionBackground", c);
        UIManager.put("ComboBox.selectionForeground", UI.getButtonTextColor());
        UIManager.put("List.selectionBackground", c);
        UIManager.put("List.selectionForeground", UI.getButtonTextColor());
        UIManager.put("ScrollBar.background", UI.getBgColor());
        UIManager.put("ToolTip.background", Color.WHITE);

    }

}
