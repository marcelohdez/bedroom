package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.alert.AlertDialog;

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
    private static boolean showMoreShiftInfo = Main.userPrefs.getBoolean("showMoreShiftInfo", false);
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
                                boolean crashRecovery, int defTarget, boolean showMoreShiftInfo) {

        Main.userPrefs.putBoolean("alwaysOnTop", stayOnTop);
        Main.userPrefs.putBoolean("recoverFromCrashes", crashRecovery);
        Main.userPrefs.putBoolean("askBeforeEarlyClose", askBeforeEarlyClose);
        Main.userPrefs.putBoolean("showMoreShiftInfo", showMoreShiftInfo);
        Main.userPrefs.putInt("defaultShiftLength", defShiftLength);
        Main.userPrefs.putInt("defaultTarget", defTarget);

        alwaysOnTop = Main.userPrefs.getBoolean("alwaysOnTop", true);
        recoverFromCrashes = Main.userPrefs.getBoolean("recoverFromCrashes", true);
        Settings.askBeforeEarlyClose = Main.userPrefs.getBoolean("askBeforeEarlyClose", true);
        Settings.showMoreShiftInfo = Main.userPrefs.getBoolean("showMoreShiftInfo", false);
        Settings.defaultShiftLength = Main.userPrefs.getInt("defaultShiftLength", 4);
        defaultTarget = Main.userPrefs.getInt("defaultTarget", 9);

    }

    /**
     * Saves the list of startup item directories into Preferences
     *
     * @param items The list of directories. In format: "[C:\User\Desktop\Item1, C:\StartupItems\Item2]"
     */
    public static void saveStartupItems(String items) {
        // The Startup Items feature was originally called Work Apps,
        // hence, the preferences key is still workApps to not lose beta tester's data
        Main.userPrefs.put("workApps", items);
    }

    /**
     * @return Path to working directory (where non preference data should be stored) as a String
     */
    public static String getWorkingDir() {
        return workingDir;
    }

    /**
     * @return The value of always on top's user preference key
     */
    public static boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    /**
     * @return Whether crash recovery is enabled
     */
    public static boolean isCrashRecoveryEnabled() {
        return recoverFromCrashes;
    }

    /**
     * @return Value of ask before early close
     */
    public static boolean getAskBeforeEarlyClose() {
        return askBeforeEarlyClose;
    }

    /**
     * @return Whether user has enabled "Show more shift info" option
     */
    public static boolean showMoreShiftInfo() {
        return showMoreShiftInfo;
    }

    /**
     * @return Default amount of hours in a shift.
     */
    public static int getDefaultShiftLength() {
        return defaultShiftLength;
    }

    /**
     * @return Default target to set in clock in window.
     */
    public static int getDefaultTarget() {
        return defaultTarget;
    }

    public static boolean isDoneLoadingShiftHistory() {
        return isDoneLoadingShiftHistory;
    }

    /**
     * Returns an ArrayList<String> from the String of startup items saved in preferences.
     *
     * @return an ArrayList of the String's items
     */
    public static ArrayList<String> getStartupItemsList() {

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
            int fails = 0; // Keep track of failed attempts at parsing the dates read, if any.
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
                    if (canParseString(currentKey)) {
                        tm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));
                    } else fails++;
                    start = i + 2; // Go 2 characters ahead to avoid the space in between items.
                    end = i + 1;
                }

            }
            // Once loop is finished add last bit
            if (canParseString(currentKey)) {
                tm.put(LocalDate.parse(currentKey), Float.valueOf(str.substring(start, end)));
            } else fails++;

            if (fails > 0) new AlertDialog(null, """
                        Bedroom was unable to load
                        some dates from your past
                        shifts, it has recovered
                        what it could.""");

        }

        isDoneLoadingShiftHistory = true;
        return tm;

    }

    private static boolean canParseString(String string) {
        try {
            LocalDate.parse(string);
            return true;
        } catch (Exception e) {
            return false;
        }
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
