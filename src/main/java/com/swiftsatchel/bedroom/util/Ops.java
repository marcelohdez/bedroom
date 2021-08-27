package com.swiftsatchel.bedroom.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public final class Ops { // Operations

    /**
     * Creates a String[] of numbers with optional added text to each.
     *
     * @param start Starting number
     * @param end Ending number
     * @param extraText Optional added text, can be null
     * @return A String[] of numbers with optional added text to each.
     */
    public static String[] createNumberList(boolean addZeroUnder10, int start, int end, String extraText) {

        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb;
        for (int i = start; i <= end; i++) {
            sb = new StringBuilder();
            sb.append(i < 10 && addZeroUnder10 ? "0" + i : i);
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

    /**
     * Returns a 0 + the number if the number is under 10
     *
     * @param number The number to check
     * @return A String containing a 0 and the number ex: "04"
     */
    public static String addZeroUnder10(int number) {
        return (number < 10) ? "0" + number : String.valueOf(number);
    }

    /**
     * Sets hand cursor on the needed components in the given JPanel
     */
    public static void setHandCursorOnCompsFrom(JPanel pnl) {

        for (Component c : pnl.getComponents()) { // Go through the component list in this JPanel

            // If it is of a desired class, set the cursor
            if (c instanceof JButton || c instanceof JComboBox || c instanceof JSlider || c instanceof JCheckBox) {

                c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            } else if (c instanceof JPanel) { // If it is another JPanel, use recursion to get those components as well.

                setHandCursorOnCompsFrom((JPanel) c);

            }

        }

    }

}
