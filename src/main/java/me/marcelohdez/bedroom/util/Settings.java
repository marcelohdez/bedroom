package me.marcelohdez.bedroom.util;

import me.marcelohdez.bedroom.Bedroom;
import me.marcelohdez.bedroom.dialog.alert.AlertDialog;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public final class Settings {

    private static boolean isDoneLoadingShiftHistory = false;

    // Settings variables, to return when called on methods.
    private static boolean useSystemLAF = Bedroom.userPrefs.getBoolean("useSystemLAF", true);

    private static boolean alwaysOnTop = Bedroom.userPrefs.getBoolean("alwaysOnTop", true);
    private static boolean recoverFromCrashes = Bedroom.userPrefs.getBoolean("recoverFromCrashes", true);
    private static boolean askBeforeEarlyClose = Bedroom.userPrefs.getBoolean("askBeforeEarlyClose", true);
    private static boolean showMoreShiftInfo = Bedroom.userPrefs.getBoolean("showMoreShiftInfo", false);
    private static int defaultShiftLength = Bedroom.userPrefs.getInt("defaultShiftLength", 4);
    private static int defaultTarget = Bedroom.userPrefs.getInt("defaultTarget", 9);

    // Current working directory, to store shift history files etc.
    private static final String workingDir =
            FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "bedroom-data";

    /**
     * Check if highContrast is enabled in user preferences
     *
     * @return highContrast boolean value
     */
    public static boolean isContrastEnabled() {
        return Bedroom.userPrefs.getBoolean("highContrast", false);
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
        Bedroom.userPrefs.putInt("textRed", textRGB[0]);
        Bedroom.userPrefs.putInt("textGreen", textRGB[1]);
        Bedroom.userPrefs.putInt("textBlue", textRGB[2]);

        // button text colors
        Bedroom.userPrefs.putInt("buttonTextRed", buttonTextRGB[0]);
        Bedroom.userPrefs.putInt("buttonTextGreen", buttonTextRGB[1]);
        Bedroom.userPrefs.putInt("buttonTextBlue", buttonTextRGB[2]);

        // button colors
        Bedroom.userPrefs.putInt("buttonRed", buttonRGB[0]);
        Bedroom.userPrefs.putInt("buttonGreen", buttonRGB[1]);
        Bedroom.userPrefs.putInt("buttonBlue", buttonRGB[2]);

        // background colors
        Bedroom.userPrefs.putInt("bgRed", bgRGB[0]);
        Bedroom.userPrefs.putInt("bgGreen", bgRGB[1]);
        Bedroom.userPrefs.putInt("bgBlue", bgRGB[2]);

    }

    public static void enableHighContrast(boolean enable) {
        if (enable) { // Set theme to high contrast values
            useSystemLAF = false; // Disable System LAF to not clash with colors
            int[] textRGB = new int[]{255, 255, 255};
            int[] buttonTextRGB = new int[]{255, 255, 255};
            int[] buttonRGB = new int[]{0, 0, 0};
            int[] bgRGB = new int[]{0, 0, 0};
            saveColors(textRGB, buttonTextRGB, buttonRGB, bgRGB);
        }
        Bedroom.userPrefs.putBoolean("highContrast", enable); // Save new value
    }

    public static void setAlwaysOnTop(boolean alwaysOnTop) {
        Settings.alwaysOnTop = alwaysOnTop;
        Bedroom.userPrefs.putBoolean("askBeforeEarlyClose", alwaysOnTop);
    }

    public static void setAskBeforeEarlyClose(boolean ask) {
        askBeforeEarlyClose = ask;
        Bedroom.userPrefs.putBoolean("alwaysOnTop", ask);
    }

    public static void setDefaultShiftLength(int newDefault) {
        defaultShiftLength = newDefault;
        Bedroom.userPrefs.putInt("defaultShiftLength", newDefault);
    }

    public static void setDefaultTarget(int newDefault) {
        defaultTarget = newDefault;
        Bedroom.userPrefs.putInt("defaultTarget", newDefault);
    }

    public static void enableCrashRecovery(boolean enable) {
        recoverFromCrashes = enable;
        Bedroom.userPrefs.putBoolean("recoverFromCrashes", enable);
    }

    public static void enableExtraShiftInfo(boolean enable) {
        showMoreShiftInfo = enable;
        Bedroom.userPrefs.putBoolean("showMoreShiftInfo", enable);
    }

    public static void enableSystemLAF(boolean enable) {
        useSystemLAF = enable;
        Bedroom.userPrefs.putBoolean("useSystemLAF", enable);
        if (enable) {
            removeColors();
        }
    }

    private static void removeColors() {
        // text colors
        Bedroom.userPrefs.remove("textRed");
        Bedroom.userPrefs.remove("textGreen");
        Bedroom.userPrefs.remove("textBlue");

        // button text colors
        Bedroom.userPrefs.remove("buttonTextRed");
        Bedroom.userPrefs.remove("buttonTextGreen");
        Bedroom.userPrefs.remove("buttonTextBlue");

        // button colors
        Bedroom.userPrefs.remove("buttonRed");
        Bedroom.userPrefs.remove("buttonGreen");
        Bedroom.userPrefs.remove("buttonBlue");

        // background colors
        Bedroom.userPrefs.remove("bgRed");
        Bedroom.userPrefs.remove("bgGreen");
        Bedroom.userPrefs.remove("bgBlue");
    }

    /**
     * @return Path to working directory (where non preference data should be stored) as a String
     */
    public static String getWorkingDir() {
        return workingDir;
    }

    public static boolean isSystemLAFEnabled() {
        return useSystemLAF;
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
            try (Scanner reader = new Scanner(file)) {

                if (reader.hasNextLine()) // Read the next line (we only save history in a single line)
                    return reader.nextLine();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "{}"; // Return this if file does not exist or is empty

    }

}
