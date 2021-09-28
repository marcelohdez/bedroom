package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.Main;

import javax.swing.*;
import java.awt.*;

/**
 * A class for some coloring/theming methods as well as holding the current theme's color values
 */
public final class Theme {

    // ======= Public reusable colors & fonts =======
    // Fonts:
    private static final Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font chartFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    // UI colors:
    private static Color textColor = loadColorOf("text", 240);
    private static Color buttonTextColor = loadColorOf("buttonText", 240);
    private static Color buttonColor = loadColorOf("button", 80);
    private static Color bg = loadColorOf("bg", 64);

    /**
     * Colors the given JComponents' background and foreground after being created.
     * Used for UIs that can have their colors changed after initializing.
     *
     * @param comps Components to color
     */
    public static void color(JComponent... comps) {

        for (JComponent c : comps) {
            if (c instanceof JButton || c instanceof JList || c instanceof JComboBox) {
                c.setBackground(getButtonColor());
                c.setForeground(getButtonTextColor());
            } else {
                c.setBackground(getBgColor());
                c.setForeground(getTextColor());
            }
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
     * Returns a darkened or lightened shade of the given color depending on brightness.
     *
     * @param c Color to contrast with
     * @param amount Amount to lighten/darken by
     * @return New color
     */
    public static Color contrastWithShade(Color c, int amount) {

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
     * Also takes into account high contrast, if it is enabled accents will be much
     * more exaggerated, and certain colors will change.
     */
    public static void setColors() {

        Color c = contrastWithShade(buttonColor, Settings.isContrastEnabled() ? 240 : 30);
        UIManager.put("Button.select", c);
        UIManager.put("Button.background", getButtonColor());
        UIManager.put("Button.foreground", getButtonTextColor());
        UIManager.put("Panel.background", getBgColor());
        UIManager.put("Label.foreground", getTextColor());
        UIManager.put("ComboBox.background", getButtonColor());
        UIManager.put("ComboBox.foreground", getButtonTextColor());
        UIManager.put("Button.focus", c);
        UIManager.put("ComboBox.selectionBackground", c);
        UIManager.put("ComboBox.selectionForeground", Settings.isContrastEnabled() ? contrastWithBnW(c) : buttonTextColor);
        UIManager.put("List.background", getBgColor());
        UIManager.put("List.foreground", getTextColor());
        UIManager.put("CheckBox.background", getBgColor());
        UIManager.put("CheckBox.foreground", getTextColor());
        UIManager.put("Slider.background", getBgColor());
        UIManager.put("List.selectionBackground", c);
        UIManager.put("List.selectionForeground", Settings.isContrastEnabled() ? contrastWithBnW(c) : buttonTextColor);
        UIManager.put("ScrollBar.background", bg);
        UIManager.put("ToolTip.background", Color.WHITE);

    }

    /**
     * Gets the default bold font from theme.
     *
     * @return The bold font.
     */
    public static Font getBoldFont() {
        return boldFont;
    }

    /**
     * Gets the default text used for the shift history chart from theme.
     *
     * @return The chart font
     */
    public static Font getChartFont() {
        return chartFont;
    }

    /**
     * Gets the text color of current theme
     *
     * @return The text color
     */
    public static Color getTextColor() {
        return textColor;
    }

    /**
     * Gets the button text color of current theme
     *
     * @return The button text color
     */
    public static Color getButtonTextColor() {
        return buttonTextColor;
    }

    /**
     * Gets the button color of current theme
     *
     * @return The button color
     */
    public static Color getButtonColor() {
        return buttonColor;
    }

    /**
     * Gets the background color of current theme
     *
     * @return The bg color
     */
    public static Color getBgColor() {
        return bg;
    }

    /**
     * Reloads the colors from preferences.
     */
    public static void reloadColors() {

        textColor = loadColorOf("text", 240);
        buttonTextColor = loadColorOf("buttonText", 240);
        buttonColor = loadColorOf("button", 80);
        bg = loadColorOf("bg", 64);
        setColors();

    }

    /**
     * Loads the RGB values of the specified color's key from preferences.
     * ex: for buttonTextColor the red value is stored in buttonTextRed,
     * so this method iterates through the given "buttonText" key and adds
     * "Red" "Green" and "Blue" at the end as need.
     *
     * @param colorKey String of component ex: "button"
     * @param def Default value
     * @return The color value of that key from preferences.
     */
    private static Color loadColorOf(String colorKey, int def) {

        return new Color(Main.userPrefs.getInt(colorKey + "Red", def),
                Main.userPrefs.getInt(colorKey + "Green", def),
                Main.userPrefs.getInt(colorKey + "Blue", def));

    }

}
