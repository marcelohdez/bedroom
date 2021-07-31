package com.marcelohdez.bedroom.main;

import com.marcelohdez.bedroom.dialog.*;
import com.marcelohdez.bedroom.enums.ErrorType;
import com.marcelohdez.bedroom.enums.TimeWindowType;
import com.marcelohdez.bedroom.util.Ops;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Main {

    public static String version = "3 (Beta 1)";

    public static Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds
    private static final boolean gc = userPrefs.getBoolean("gc", false);

    public static Window wnd; // This window
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    public static void main(String[] args) {

        try { // Set cross-platform look and feel, fixes macOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        initWindows();
        openWorkApps();

        // Create a timer to run every second
        Timer t = new Timer(1000, e -> {

            if (UI.clockInTimePassed && !UI.inBreak) { UI.tick();
            } else UI.getTime(); // To get time until clock in

            secCount++;

            if (secCount > 59) { // Run every minute
                if (gc) System.gc();
                UI.getTime(); // Recheck time clocked in, in case of computer sleep and for accuracy
                secCount = 0;
            }

            wnd.pack();

        });

        t.setRepeats(true);
        t.start(); // Start timer

        System.out.println(UIManager.getLookAndFeel());

    }

    private static void initWindows() {

        // Create main window
        wnd = new Window();
        // Create clock in/out windows
        clockInWnd = new SelectTimeWindow(TimeWindowType.CLOCK_IN);
        clockOutWnd = new SelectTimeWindow(TimeWindowType.CLOCK_OUT);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(TimeWindowType.START_BREAK);
        leaveBreakWnd = new SelectTimeWindow(TimeWindowType.END_BREAK);

    }

    private static void openWorkApps() {

        for (String app : Ops.detangleString(userPrefs.get("workApps", "[]"))) {

            if (!app.equals("")) {

                File workApp = new File(app);
                if (workApp.exists()) {

                    try {
                        Desktop.getDesktop().open(workApp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    new ErrorDialog(new int[]{wnd.getX(), wnd.getY(), wnd.getWidth(), wnd.getHeight()},
                            ErrorType.WORK_APP_DOES_NOT_EXIST);
                }

            }

        }

    }

    public static void updateSettings() {

        wnd.reloadSettings();
        clockInWnd.reloadSettings();
        clockOutWnd.reloadSettings();
        enterBreakWnd.reloadSettings();
        leaveBreakWnd.reloadSettings();

    }

}
