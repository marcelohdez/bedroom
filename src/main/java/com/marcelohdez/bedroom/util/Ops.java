package com.marcelohdez.bedroom.util;

import com.marcelohdez.bedroom.main.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Ops { // Operations

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
     * Creates a String[] of numbers with optional added text to each.
     *
     * @param start Starting number
     * @param end Ending number
     * @param extraText Optional added text, can be null
     * @return A String[] of numbers with optional added text to each.
     */
    public static String[] createNumberList(int start, int end, String extraText) {

        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb;
        for (int i = start; i <= end; i++) {
            sb = new StringBuilder();
            if (i < 10) sb.append(0);
            sb.append(i);
            if (extraText != null) sb.append(extraText);
            list.add(sb.toString());
        }

        // This line was gotten from Floern and Bozho's response on StackOverflow:
        // https://stackoverflow.com/questions/4042434/converting-arrayliststring-to-string-in-java
        return list.toArray(new String[0]);

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

    /**
     * Makes an ArrayList from a String that used to be an ArrayList (ex: "[cat, dog, wolf]").
     * Used for a list that is saved in Preferences (since it is only allowed to save Strings)
     * as ArrayList.toString(); and then getting the list back from Preferences.
     *
     * @param str String to detangle
     * @return an ArrayList of the String's items
     */
    public static ArrayList<String> detangleString(String str) {

        ArrayList<String> list = new ArrayList<>();

        // Detangle string
        int start = 1;
        int end = start;
        for (int i = 1; i < str.length() - 1; i++) { // -1 character from start and end to ignore []

            if (str.charAt(i) != 44) { // If it is not the comma, extend end point
                end++;
            } else {
                list.add(str.substring(start, end));
                start = i+2;
                end = i+1;
            }

        }
        list.add(str.substring(start, end));

        return list;

    }

    /**
     * Returns an "s" if number is greater than 1
     *
     * @param number Number to check
     * @return An "s" if number is greater than 1, else ""
     */
    public static String isPlural(int number) { // Return "s" if there is more than 1 of number
        if (number > 1) return "s";
        return "";
    }

}
