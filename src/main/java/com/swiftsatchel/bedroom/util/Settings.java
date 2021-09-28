package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.Main;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public final class Settings {

    private static final String workingDir =  // Current working directory, to store shift history files etc.
            FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "bedroom-data";

    private static boolean isDoneLoadingShiftHistory = false;

    // Settings variables, to return when called on methods, instead of doing storage reads every time.
    private static boolean alwaysOnTop = Main.userPrefs.getBoolean("alwaysOnTop", true);
    private static boolean recoverFromCrashes = Main.userPrefs.getBoolean("recoverFromCrashes", true);
    private static boolean askBeforeEarlyClose = Main.userPrefs.getBoolean("askBeforeEarlyClose", true);
    private static int defaultShiftLength = Main.userPrefs.getInt("defaultShiftLength", 4);
    private static int defaultTarget = Main.userPrefs.getInt("defaultTarget", 9);

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
    public static void saveMisc(boolean stayOnTop, boolean askBeforeEarlyClose, int defShiftLength,
                                boolean crashRecovery, int defTarget) {

        Main.userPrefs.putBoolean("alwaysOnTop", stayOnTop);
        Main.userPrefs.putBoolean("recoverFromCrashes", crashRecovery);
        Main.userPrefs.putBoolean("askBeforeEarlyClose", askBeforeEarlyClose);
        Main.userPrefs.putInt("defaultShiftLength", defShiftLength);
        Main.userPrefs.putInt("defaultTarget", defTarget);

        alwaysOnTop = Main.userPrefs.getBoolean("alwaysOnTop", true);
        recoverFromCrashes = Main.userPrefs.getBoolean("recoverFromCrashes", true);
        Settings.askBeforeEarlyClose = Main.userPrefs.getBoolean("askBeforeEarlyClose", true);
        Settings.defaultShiftLength = Main.userPrefs.getInt("defaultShiftLength", 4);
        defaultTarget = Main.userPrefs.getInt("defaultTarget", 9);

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
     * Get working directory, where shift history and other data files are stored.
     *
     * @return Path to folder as a String
     */
    public static String getWorkingDir() {
        return workingDir;
    }

    /**
     * Get always on top value from user preferences.
     *
     * @return The value of always on top's user preference key
     */
    public static boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    /**
     * Get whether the crash recovery setting is enabled
     *
     * @return Whether crash recovery is enabled
     */
    public static boolean isCrashRecoveryEnabled() {
        return recoverFromCrashes;
    }

    /**
     * Get ask before early close boolean from user preferences.
     *
     * @return Value of ask before early close
     */
    public static boolean getAskBeforeEarlyClose() {
        return askBeforeEarlyClose;
    }

    /**
     * Get default shift length from user preferences
     *
     * @return Default amount of hours in a shift.
     */
    public static int getDefaultShiftLength() {
        return defaultShiftLength;
    }

    /**
     * Get default target value from user preferences
     *
     * @return Default target to set in clock in window.
     */
    public static int getDefaultTarget() {
        return defaultTarget;
    }

    public static boolean isDoneLoadingShiftHistory() {
        return isDoneLoadingShiftHistory;
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
        String str = readShiftHistory();

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

        isDoneLoadingShiftHistory = true;
        return tm;

    }

    /**
     * Read the line of past history data from its file if available
     *
     * @return The string of past history data.
     */
    private static String readShiftHistory() {

        File file = new File(Settings.getWorkingDir() + File.separator + "shift.history"); // Get file

        if (file.exists()) {
            try {

                Scanner reader = new Scanner(file); // Make a new scanner
                if (reader.hasNextLine()) // If there is a line to read:
                    return reader.nextLine(); // Read line of data

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "{}"; // Return this if file does not exist or is empty

    }

}
