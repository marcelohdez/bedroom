package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

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
     * Returns an ArrayList from a String containing text separated by commas (ex: "[cat, dog, wolf]").
     *
     * @param str String to untangle
     * @return an ArrayList of the String's items
     */
    public static ArrayList<String> stringToList(String str) {

        ArrayList<String> list = new ArrayList<>();

        int start = 1; // Start 1 character ahead to avoid the beginning bracket
        int end = start;
        for (int i = 1; i < str.length() - 1; i++) { // -1 character from the end to avoid ending bracket

            if (str.charAt(i) != 44) { // If it is not a comma, extend end point
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
     * Returns a TreeMap<LocalDate, Float> of past shifts.
     *
     * @return A TreeMap<LocalDate, Float> from the string's values
     */
    public static TreeMap<LocalDate, Float> loadShiftHistory() {

        TreeMap<LocalDate, Float> hm = new TreeMap<>();
        String str = Main.userPrefs.get("shiftHistory", "{}");

        if (!str.equals("{}")) { // If the string is not an empty TreeMap: (to avoid null exceptions)

            int start = 1; // Start 1 character ahead to avoid the beginning bracket
            int end = start;
            String currentKey = "";
            for (int i = 1; i < str.length() - 1; i++) { // -1 character from the end to avoid ending bracket

                if (str.charAt(i) != 44) { // If it is not a comma then check:
                    if (str.charAt(i) != 61) { // If it is not a = then extend endpoint
                        end++;
                    } else { // If it is a =,
                        currentKey = str.substring(start, end); // Save this substring as the key
                        start = i + 1; // Go a characters ahead to start on float value.
                        end = i + 1;
                    }
                } else { // Else if it is a comma, set the key we got before the = to the value after the =.
                    hm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));
                    start = i + 2; // Go 2 characters ahead to avoid the space in between items.
                    end = i + 1;
                }

            }
            // Once loop is finished add last bit
            hm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));

        }

        return hm;

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

}
