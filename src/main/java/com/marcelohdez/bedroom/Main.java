package com.marcelohdez.bedroom;

import com.marcelohdez.dialog.*;
import javax.swing.*;

public class Main {

    public static String version = "2.1";

    public static boolean isOSX = System.getProperty("os.name").contains("Mac OS X"); // Check if OS is MacOS

    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds

    public static Window wnd; // This window
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    public enum TIME_WINDOW { // Type of select time windows
        CLOCK_IN_TYPE,
        CLOCK_OUT_TYPE,
        START_BREAK_TYPE,
        END_BREAK_TYPE
    }

    public enum ERROR { // Types of user errors we can catch
        BREAK_OUT_OF_SHIFT,
        NEGATIVE_BREAK_TIME,
        NEGATIVE_SHIFT_TIME
    }

    public static void main(String[] args) {
        try { // Set cross-platform look and feel, fixes MacOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        // Open main window
        wnd = new Window();
        // Create clock in/out windows
        clockInWnd = new SelectTimeWindow(TIME_WINDOW.CLOCK_IN_TYPE);
        clockInWnd.centerOnMainWindow(); // Center clock in window on main window
        clockOutWnd = new SelectTimeWindow(TIME_WINDOW.CLOCK_OUT_TYPE);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(TIME_WINDOW.START_BREAK_TYPE);
        leaveBreakWnd = new SelectTimeWindow(TIME_WINDOW.END_BREAK_TYPE);

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

}
