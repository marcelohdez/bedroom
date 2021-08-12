package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.dialog.ErrorDialog;
import com.swiftsatchel.bedroom.dialog.SelectTimeWindow;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.prefs.Preferences;

public class Main {

    // ======= Global Variables =======
    public static String version = "3 (Beta 2)";
    public static Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    // Windows
    public static BedroomWindow wnd;
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd;

    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    // ======= Debugging =======
    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds
    private static boolean gc = userPrefs.getBoolean("gc", false);

    // ======= Variables =======
    // Time variables
    private static int hr = 0;
    private static int min = 0;
    private static int sec = 0;
    public static long totalSecClockedIn = 0;
    public static long secondsTillClockIn = -1;
    public static long secondsTillLeaveBreak = -1;

    // Time values
    public static LocalTime clockInTime, clockOutTime, breakInTime, breakOutTime;
    public static boolean breakTimesChosen = false;

    // Shift stats
    public static long orders = 0;
    public static boolean inBreak = false;
    public static boolean clockInTimePassed = false;
    public static int target = 0; // Target orders/hr
    private static long ordersNeeded = 0;

    private static final DecimalFormat twoDecs = new DecimalFormat("#.00");

    public static void main(String[] args) {

        try { // Set cross-platform look and feel, fixes macOS buttons having a white background
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        Theme.setAccents();
        initWindows();
        openWorkApps();

        // Create a timer to run every second, updating the time
        Timer t = new Timer(1000, e -> update());

        t.setRepeats(true);
        t.start(); // Start timer

    }

    private static void initWindows() {

        // Create main window
        wnd = new BedroomWindow();
        // Create clock in/out windows
        clockInWnd = new SelectTimeWindow(TimeWindowType.CLOCK_IN);
        clockOutWnd = new SelectTimeWindow(TimeWindowType.CLOCK_OUT);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(TimeWindowType.START_BREAK);
        leaveBreakWnd = new SelectTimeWindow(TimeWindowType.END_BREAK);

    }

    private static void openWorkApps() {

        for (String app : Ops.untangleString(userPrefs.get("workApps", "[]"))) {

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

    private static void update() {

        if (clockInTimePassed && !inBreak) { tick(); // Add a second to clocked in time
        } else updateTime(); // To get time until clock in

        secCount++;

        if (secCount > 59) { // Run every minute
            if (gc) System.gc();
            updateTime(); // Recheck time clocked in, in case of computer sleep and for accuracy
            secCount = 0;
        }

        wnd.pack();

    }

    public static void updateSettings() {

        gc = userPrefs.getBoolean("gc", false);

        Theme.setAccents();
        wnd.reloadSettings();
        clockInWnd.reloadSettings();
        clockOutWnd.reloadSettings();
        enterBreakWnd.reloadSettings();
        leaveBreakWnd.reloadSettings();

    }

    static void changeOrders(int amount) { // Change orders

        if (!UI.freeze && !inBreak) {
            orders += amount;
            if (orders < 0) orders = 0;
            updateStats();
        }

        wnd.pack(); // Pack the window in case of text changes.

    }

    private static void tick() { // Change time values

        while (sec > 59) {
            sec -= 60;
            min++;
            if (min > 59) {
                min -= 60;
                hr++;
            }
        }
        totalSecClockedIn++;
        sec++;

        updateStats();

    }

    static void updateStats() {

        StringBuilder sb = new StringBuilder();
        if (clockInTimePassed) { // Get stats =======

            if (!inBreak) { // Show time clocked in
                sb.append("Time: ");
                Time.appendReadableTimeTo(sb, hr, min, sec);
                sb.append("\n") // Line break
                        .append(getStats());
            } else { // Show time left until our break ends =======
                sb.append("On break, ");
                Time.appendReadableTimeTo(sb, Time.shrinkTime(secondsTillLeaveBreak));
                sb.append(" left\n");
            }
            UI.display(sb.toString()); // Show on-screen

        } else if (Main.timesChosen) { // Show "Time till clock in" =======

            sb.append("Time until clocked in:\n");
            Time.appendReadableTimeTo(sb, Time.shrinkTime(secondsTillClockIn));
            UI.display(sb.toString()); // Show on-screen

        }

    }

    private static String getStats() {

        return """
                Orders: $orders ($perHr/hr)
                Needed: $needed, $left left"""
                .replace("$orders", String.valueOf(orders))
                .replace("$perHr",
                        String.valueOf(twoDecs.format((double) (orders*3600)/ totalSecClockedIn)))
                .replace("$needed", String.valueOf(ordersNeeded))
                .replace("$left", (orders < ordersNeeded) ?
                        String.valueOf(ordersNeeded - orders) : "0");

    }

    public static void updateTime() {

        if (Main.timesChosen) // Have we chosen clock in and out times?
            // Has our clock in time passed?
            if (clockInTime.compareTo(LocalTime.now()) <= 0) {

                UI.freeze = false;
                clockInTimePassed = true;

                if (breakTimesChosen) { // Have we chosen break times?
                    getBreakTime();
                } else { // If not, set totalSecClocked to time from clock in to now
                    totalSecClockedIn = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS);
                }

                getOrdersNeeded();
                sec = (int) (totalSecClockedIn % 60);
                min = (int) (totalSecClockedIn / 60) % 60;
                hr = (int) Math.floor(totalSecClockedIn/60F/60F);
                tick(); // Update time and show on screen

            } else {
                // Get seconds left until we have to clock in
                secondsTillClockIn = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
                updateStats(); // Display it on screen
            }

        wnd.pack();

    }

    private static void getBreakTime() {

        if (breakInTime.compareTo(LocalTime.now()) <= 0) { // Has our break started?
            if (breakOutTime.compareTo(LocalTime.now()) <= 0) { // Has our break ended?
                inBreak = false; // If so, we are not in break.
                // Set totalSecClocked to the seconds from clocking in to the break's start,
                // then from break end to the current time.
                totalSecClockedIn = (clockInTime.until(breakInTime, ChronoUnit.SECONDS) +
                        breakOutTime.until(LocalTime.now(), ChronoUnit.SECONDS) - 1);
            } else { // If our break has not ended:
                inBreak = true; // We are still in break
                // Set totalSecClocked to the seconds from clocking in to the break's start
                totalSecClockedIn = clockInTime.until(breakInTime, ChronoUnit.SECONDS) - 1;
                secondsTillLeaveBreak = // Seconds until our break ends
                        LocalTime.now().until(breakOutTime, ChronoUnit.SECONDS);
            }
        }

    }

    private static void getOrdersNeeded() {

        if (!breakTimesChosen) { // Check if we have not chosen break times
            ordersNeeded = Math.round(target *
                    // If so, get ordersNeeded with clock in and out times
                    ((double) clockInTime.until(clockOutTime, ChronoUnit.MINUTES) / 60));
        } else ordersNeeded = Math.round(target *
                // If we did choose break times, then get ordersNeeded from clock in
                // and clock out times minus the difference of our break's start and end times
                (((double) clockInTime.until(clockOutTime, ChronoUnit.MINUTES) -
                        (double) breakInTime.until(breakOutTime, ChronoUnit.MINUTES)) / 60));

    }

}
