package com.marcelohdez.bedroom;

import com.marcelohdez.dialog.*;
import com.marcelohdez.enums.TimeWindowType;

import javax.swing.*;
import java.util.prefs.Preferences;

public class Main {

    public static String version = "3 (Beta 1)";

    public static boolean isOSX = System.getProperty("os.name").contains("Mac OS X"); // Check if OS is MacOS

    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds

    public static Window wnd; // This window
    public static UI ui; // UI of main window
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    public static Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    public static void main(String[] args) {
        try { // Set cross-platform look and feel, fixes MacOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        // Create main window
        ui = new UI();
        wnd = new Window(ui);
        // Create clock in/out windows
        clockInWnd = new SelectTimeWindow(TimeWindowType.CLOCK_IN_TYPE);
        clockInWnd.centerOnMainWindow(); // Center clock in window on main window
        clockOutWnd = new SelectTimeWindow(TimeWindowType.CLOCK_OUT_TYPE);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(TimeWindowType.START_BREAK_TYPE);
        leaveBreakWnd = new SelectTimeWindow(TimeWindowType.END_BREAK_TYPE);

        // Create a timer to run every second
        Timer t = new Timer(1000, e -> {

            if (UI.clockInTimePassed && !UI.inBreak) { UI.tick();
            } else UI.getTime(); // To get time until clock in

            secCount++;

            if (secCount > 59) { // Run every minute
                System.gc(); // Garbage collect

                UI.getTime(); // Recheck time clocked in, in case of computer sleep and for accuracy

                secCount = 0;
            }

            wnd.pack();

        });

        t.setRepeats(true);
        t.start(); // Start timer
    }

    public static void updateColors() {

        ui.reloadColors();
        clockInWnd.reloadColors();
        clockOutWnd.reloadColors();
        enterBreakWnd.reloadColors();
        leaveBreakWnd.reloadColors();

    }

}
