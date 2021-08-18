package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.dialog.AlertDialog;
import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class Main {

    // ======= Global Variables =======
    public static String version = "3 (Beta 2)";
    public static Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    public static BedroomWindow wnd; // Main window
    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    // ======= Debugging =======
    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds

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
    private static int ordersNeeded = 0;

    // Shift performance history (key: shift end date, value: float of orders per hour)
    public static TreeMap<LocalDate, Float> shiftHistory = Ops.loadShiftHistory();

    private static final DecimalFormat twoDecs = new DecimalFormat("#.00");

    public static void main(String[] args) {

        try { // Set cross-platform look and feel, fixes macOS buttons having a white background
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        Theme.setAccents(); // Set extra color accents through UIManager
        init();
        openWorkApps(); // Open any work apps

        // Create a timer to run every second, updating the time
        Timer t = new Timer(1000, e -> update());
        t.setRepeats(true);
        t.start(); // Start timer

    }

    private static void init() {

        wnd = new BedroomWindow(); // Set main window
        new SelectTimeDialog(wnd, TimeWindowType.CLOCK_IN); // Create clock in window

    }

    private static void openWorkApps() {

        for (String app : Ops.stringToList(userPrefs.get("workApps", "[]"))) {

            if (!app.equals("")) {

                File workApp = new File(app);
                if (workApp.exists()) {

                    try {
                        Desktop.getDesktop().open(workApp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    new AlertDialog(wnd, ErrorType.WORK_APP_DOES_NOT_EXIST);
                }

            }

        }

    }

    private static void update() {

        updateTime(); // To get time until clock in

        secCount++;
        if (secCount > 59) { // Run every minute
            updateTime(); // Recheck time clocked in, in case of computer sleep and for accuracy
            secCount = 0;
        }

        wnd.pack();

    }

    public static void updateSettings() {

        Theme.setAccents();
        wnd.reloadSettings();

    }

    static void changeOrders(int amount) { // Change orders

        if (!inBreak) {
            orders += amount;
            if (orders < 0) orders = 0;
            updateStats();
        }

        wnd.pack(); // Pack the window in case of text changes.

    }

    static void updateStats() {

        StringBuilder sb = new StringBuilder();
        if (clockInTimePassed) { // Get stats =======

            if (!inBreak) { // Show time clocked in
                sb.append("Time: ");
                Time.appendReadableTimeTo(sb, hr, min, sec);
                if (LocalTime.now().isAfter(clockOutTime)) sb.append(" (Done)");
                sb.append("\n") // Line break
                        .append(getStats());
            } else { // Show time left until our break ends =======
                sb.append("On break, ");
                Time.appendReadableTimeTo(sb, Time.shrinkTime(secondsTillLeaveBreak));
                sb.append(" left\n")
                        .append(getStats());
            }
            UI.display(sb.toString()); // Show on UI

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
                        twoDecs.format((float) (orders*3600)/totalSecClockedIn))
                .replace("$needed", String.valueOf(ordersNeeded))
                .replace("$left", (orders < ordersNeeded) ?
                        String.valueOf(ordersNeeded - orders) : "0");

    }

    public static void updateTime() {

        if (Main.timesChosen) // Have we chosen clock in and out times?
            // Has our clock in time passed?
            if (LocalTime.now().isAfter(clockInTime)) {
                clockInTimePassed = true;

                if (LocalTime.now().isBefore(clockOutTime)) { // If we have not finished our shift:

                    if (breakTimesChosen) { // Have we chosen break times?
                        getBreakTime();
                    } else { // If not, set totalSecClocked to time from clock in to now
                        totalSecClockedIn = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS);
                    }

                } else getTotalShiftTime();

                getOrdersNeeded();
                sec = (int) (totalSecClockedIn % 60);
                min = (int) (totalSecClockedIn / 60) % 60;
                hr = (int) Math.floor(totalSecClockedIn / 60F / 60F);
                updateStats(); // Update stats and show on screen

            } else if (LocalTime.now().isBefore(clockInTime)) { // Else if it is before our shift starts:
                // Get seconds left until we have to clock in
                secondsTillClockIn = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
                updateStats(); // Display it on screen
            }

        wnd.pack();

    }

    private static void getBreakTime() {

        if (LocalTime.now().isAfter(breakInTime)) { // Has our break started?
            if (LocalTime.now().isAfter(breakOutTime)) { // Has our break ended?
                inBreak = false; // If so, we are not in break.
                // Set totalSecClocked to the seconds from clocking in to the break's start,
                // then from break end to the current time.
                totalSecClockedIn = (clockInTime.until(breakInTime, ChronoUnit.SECONDS) +
                        breakOutTime.until(LocalTime.now(), ChronoUnit.SECONDS));
            } else { // If our break has not ended:
                inBreak = true; // We are still in break
                // Set totalSecClocked to the seconds from clocking in to the break's start
                totalSecClockedIn = clockInTime.until(breakInTime, ChronoUnit.SECONDS);
                secondsTillLeaveBreak = // Seconds until our break ends
                        LocalTime.now().until(breakOutTime, ChronoUnit.SECONDS);
            }
            // If break has not started, set time clocked in from start to now.
        } else totalSecClockedIn = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS);

    }

    private static void getOrdersNeeded() {

        if (!breakTimesChosen) { // Check if we have not chosen break times
            ordersNeeded = Math.round(target *
                    // If so, get ordersNeeded with clock in and out times
                    (clockInTime.until(clockOutTime, ChronoUnit.MINUTES) / 60F));
        } else ordersNeeded = Math.round(target *
                // If we did choose break times, then get ordersNeeded from minutes we work
                // minus the break length.
                ((clockInTime.until(clockOutTime, ChronoUnit.MINUTES) -
                        breakInTime.until(breakOutTime, ChronoUnit.MINUTES)) / 60F));

    }

    private static void getTotalShiftTime() {
        // Get shift length, used once our shift has ended as to not keep updating time,
        // If we have chosen breaks, get shift length minus break length, for worked time.
        if (breakTimesChosen) {
            totalSecClockedIn = clockInTime.until(clockOutTime, ChronoUnit.SECONDS) -
                    breakInTime.until(breakOutTime, ChronoUnit.SECONDS);
        } else totalSecClockedIn = clockInTime.until(clockOutTime, ChronoUnit.SECONDS);

    }

    public static void clockOut(LocalTime time) {

        // Store the current shift end date and the orders per hour within the chosen time.
        shiftHistory.put(LocalDate.now(), Float.valueOf(twoDecs.format((float) (orders*3600)/
                time.until(time, ChronoUnit.SECONDS))));

        userPrefs.put("shiftHistory", shiftHistory.toString());

        System.exit(0);

    }

}
