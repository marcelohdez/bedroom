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
     * @param isButton Use button colors
     */
    public static void colorThis(JComponent c, boolean isButton) {

        if (isButton) {
            c.setBackground(UI.buttonColor);
            c.setForeground(UI.buttonTextColor);
        } else {
            c.setBackground(UI.bg);
            c.setForeground(UI.textColor);
        }

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
