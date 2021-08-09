package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.main.Main;

public class Settings {

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
     * Saves Misc. settings
     *
     * @param stayOnTop Make window always stay on top
     * @param gc Enable garbage collection
     */
    public static void saveMisc(boolean stayOnTop, boolean gc) {

        Main.userPrefs.putBoolean("alwaysOnTop", stayOnTop);
        Main.userPrefs.putBoolean("gc", gc);

    }

    /**
     * Saves a list of directories into Preferences
     *
     * @param apps The list of directories. In format: "[C:\User\Desktop\WorkApp1, C:\WorkApps\WorkApp2]"
     */
    public static void saveWorkApps(String apps) {

        Main.userPrefs.put("workApps", apps);

    }

}
