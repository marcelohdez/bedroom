package com.swiftsatchel.bedroom.util;

import java.util.ArrayList;

public class Ops { // Operations

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
     * Makes an ArrayList from a String that used to be an ArrayList (ex: "[cat, dog, wolf]").
     * Used for a list that is saved in Preferences (since it is only allowed to save Strings)
     * as ArrayList.toString(); and then getting the list back from Preferences.
     *
     * @param str String to untangle
     * @return an ArrayList of the String's items
     */
    public static ArrayList<String> untangleString(String str) {

        ArrayList<String> list = new ArrayList<>();

        // Get a string[] from a string ex: "[monkey, car, shoe]" -> new String[]{"monkey", "car", "shoe"};
        int start = 1; // Start 1 character ahead to avoid the beginning bracket
        int end = start;
        for (int i = 1; i < str.length() - 1; i++) { // -1 character from the end to avoid ending bracket

            if (str.charAt(i) != 44) { // If it is not the comma, extend end point
                end++;
            } else { // Else return the string we have
                list.add(str.substring(start, end));
                start = i+2; // Go 2 characters ahead to avoid the space in between items.
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
