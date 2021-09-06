package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

public final class Settings {

    /**
     * Check if highContrast is enabled in user preferences
     *
     * @return highContrast boolean value
     */
    public static boolean isContrastEnabled() {
        return Main.userPrefs.getBoolean("highContrast", false);
    }

    /**
     * Saves color values
     *
     * @param textRGB int[] of the text colors
     * @param buttonTextRGB int[] of the button text colors
     * @param buttonRGB int[] of the button colors
     * @param bgRGB int[] of the background colors
     */
    public static void saveColors(int[] textRGB, int[] buttonTextRGB, int[] buttonRGB, int[] bgRGB) {

        // text colors
        Main.userPrefs.putInt("textRed", textRGB[0]);
        Main.userPrefs.putInt("textGreen", textRGB[1]);
        Main.userPrefs.putInt("textBlue", textRGB[2]);

        // button text colors
        Main.userPrefs.putInt("buttonTextRed", buttonTextRGB[0]);
        Main.userPrefs.putInt("buttonTextGreen", buttonTextRGB[1]);
        Main.userPrefs.putInt("buttonTextBlue", buttonTextRGB[2]);

        // button colors
        Main.userPrefs.putInt("buttonRed", buttonRGB[0]);
        Main.userPrefs.putInt("buttonGreen", buttonRGB[1]);
        Main.userPrefs.putInt("buttonBlue", buttonRGB[2]);

        // background colors
        Main.userPrefs.putInt("bgRed", bgRGB[0]);
        Main.userPrefs.putInt("bgGreen", bgRGB[1]);
        Main.userPrefs.putInt("bgBlue", bgRGB[2]);

    }

    /**
     * Save a boolean to highContrast key
     *
     * @param highContrast new highContrast value
     */
    public static void setHighContrastTo(boolean highContrast) {
        Main.userPrefs.putBoolean("highContrast", highContrast); // Save new value
        if (highContrast) { // If it is true, set to high contrast theme
            int[] textRGB = new int[]{255, 255, 255};
            int[] buttonTextRGB = new int[]{255, 255, 255};
            int[] buttonRGB = new int[]{0, 0, 0};
            int[] bgRGB = new int[]{0, 0, 0};
            saveColors(textRGB, buttonTextRGB, buttonRGB, bgRGB);
        }
    }

    /**
     * Saves Misc. settings
     *
     * @param stayOnTop Make window always stay on top
     */
    public static void saveMisc(boolean stayOnTop, boolean askBeforeEarlyClose, int defShiftLength) {

        Main.userPrefs.putBoolean("alwaysOnTop", stayOnTop);
        Main.userPrefs.putBoolean("askBeforeEarlyClose", askBeforeEarlyClose);
        Main.userPrefs.putInt("defaultShiftLength", defShiftLength);

    }

    /**
     * Saves a list of directories into Preferences
     *
     * @param apps The list of directories. In format: "[C:\User\Desktop\WorkApp1, C:\WorkApps\WorkApp2]"
     */
    public static void saveWorkApps(String apps) {

        Main.userPrefs.put("workApps", apps);

    }

    /**
     * Get always on top value from user preferences.
     * This is preferred over writing the line over so that when changing the default value
     * we only have to change this method instead of digging through Bedroom to change all
     * occurrences.
     *
     * @return The value of always on top's user preference key
     */
    public static boolean getAlwaysOnTop() {
        return Main.userPrefs.getBoolean("alwaysOnTop", true);
    }

    /**
     * Returns an ArrayList<String> from the String of work apps saved in preferences.
     *
     * @return an ArrayList of the String's items
     */
    public static ArrayList<String> getWorkAppsList() {

        ArrayList<String> list = new ArrayList<>();
        String str = Main.userPrefs.get("workApps", "[]");

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

        TreeMap<LocalDate, Float> tm = new TreeMap<>();
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
                    tm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));
                    start = i + 2; // Go 2 characters ahead to avoid the space in between items.
                    end = i + 1;
                }

            }
            // Once loop is finished add last bit
            tm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));

        }

        return tm;

    }

}
