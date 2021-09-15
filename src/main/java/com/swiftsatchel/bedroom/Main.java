package com.swiftsatchel.bedroom;

import com.swiftsatchel.bedroom.dialog.alert.AlertDialog;
import com.swiftsatchel.bedroom.dialog.alert.ErrorDialog;
import com.swiftsatchel.bedroom.dialog.time.SelectTimeDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.main.BedroomWindow;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class Main {

    // ======= Global Variables =======
    public static final String VERSION = "3 (Beta 4)";
    public static final Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    public static BedroomWindow wnd; // Main window
    public static boolean timesChosen = false; // Have clock in/clock out times been chosen?

    // ======= Variables =======
    // Time variables
    private static int hr = 0;
    private static int min = 0;
    private static int sec = 0;
    private static long totalSecClockedIn = 0;
    private static long secondsTillClockIn = -1;
    private static long secondsTillLeaveBreak = -1;

    // Time values
    public static LocalDateTime clockInTime, clockOutTime, breakInTime, breakOutTime;

    // Shift stats
    private static int orders = 0;
    private static boolean inBreak = false;
    private static boolean clockInTimePassed = false;
    private static int target; // Target orders/hr
    private static int ordersNeeded = 0;

    // Shift performance history (key: shift end date, value: float of orders per hour)
    private static final TreeMap<LocalDate, Float> shiftHistory = Settings.loadShiftHistory();

    private static final DecimalFormat twoDecs = new DecimalFormat("#.00");

    public static void main(String[] args) {

        try { // Set cross-platform look and feel, fixes macOS buttons having a white background
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Also, if on Windows--IDK the property to any other OS--check for high contrast and enable if true
            if (System.getProperty("os.name").contains("Windows"))
                if (Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on").equals(Boolean.TRUE))
                    Settings.setHighContrastTo(true);
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

        for (String location : Settings.getWorkAppsList()) {

            if (!location.equals("")) {

                File workApp = new File(location);
                if (workApp.exists()) {

                    try {
                        Desktop.getDesktop().open(workApp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    new ErrorDialog(wnd, ErrorType.WORK_APP_DOES_NOT_EXIST);
                }

            }

        }

    }

    public static void update() {

        if (timesChosen) { // Have we chosen clock in and out times?
            // Has our clock in time passed?
            if (LocalDateTime.now().isAfter(clockInTime)) {
                clockInTimePassed = true;
                wnd.enableButtons(); // Default to enabled buttons

                if (LocalDateTime.now().isBefore(clockOutTime)) { // If we have not finished our shift:

                    if (breakOutTime != null) { // Have we chosen break times?
                        getBreakTime();
                    } else { // If not, set totalSecClocked to time from clock in to now
                        totalSecClockedIn = clockInTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
                    }

                } else getTotalShiftTime();

                getOrdersNeeded();
                sec = (int) (totalSecClockedIn % 60);
                min = (int) (totalSecClockedIn / 60) % 60;
                hr = (int) Math.floor(totalSecClockedIn / 60F / 60F);
                updateStats(); // Update stats and show on screen

            } else if (LocalDateTime.now().isBefore(clockInTime)) { // Else if it is before our shift starts:
                wnd.disableButtons(UI.Buttons.BOTH); // Disable buttons until we clock in
                // Get seconds left until we have to clock in
                secondsTillClockIn = LocalDateTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
                updateStats(); // Display it on screen
            }

            wnd.pack();

        }

    }

    public static void updateSettings() {

        Theme.reloadColors();
        wnd.reloadSettings();

    }

    public static void changeOrders(int amount) { // Change orders

        if (!inBreak) {
            orders += amount;
            if (orders < 0) orders = 0;
            updateStats();
        }

        wnd.pack(); // Pack the window in case of text changes.

    }

    public static void updateStats() {

        StringBuilder sb = new StringBuilder();
        if (clockInTimePassed) { // Get stats =======

            if (!inBreak) { // Show time clocked in
                sb.append("Time: ");
                Time.appendReadableTimeTo(sb, hr, min, sec);
                if (LocalDateTime.now().isAfter(clockOutTime)) sb.append(" (Done)");
                sb.append("\n") // Line break
                        .append(getStats());
            } else { // Show time left until our break ends =======
                sb.append("On break, ");
                Time.appendReadableTimeTo(sb, Time.shrinkTime(secondsTillLeaveBreak));
                sb.append(" left\n")
                        .append(getStats());
            }

        } else { // Show "Time until clocked in" =======

            sb.append("Time until clocked in:\n");
            Time.appendReadableTimeTo(sb, Time.shrinkTime(secondsTillClockIn));
            sb.append("\n");

        }

        UI.display(sb.toString()); // Show on UI

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

    private static void getBreakTime() {

        if (LocalDateTime.now().isAfter(breakInTime)) { // Has our break started?
            if (LocalDateTime.now().isAfter(breakOutTime)) { // Has our break ended?
                inBreak = false; // If so, we are not in break.
                // Set totalSecClocked to the seconds from clocking in to the break's start,
                // then from break end to the current time.
                totalSecClockedIn = (clockInTime.until(breakInTime, ChronoUnit.SECONDS) +
                        breakOutTime.until(LocalDateTime.now(), ChronoUnit.SECONDS));
            } else { // If our break has not ended:
                inBreak = true; // We are still in break
                // Set totalSecClocked to the seconds from clocking in to the break's start
                totalSecClockedIn = clockInTime.until(breakInTime, ChronoUnit.SECONDS);
                secondsTillLeaveBreak = // Seconds until our break ends
                        LocalDateTime.now().until(breakOutTime, ChronoUnit.SECONDS);
                wnd.disableButtons(UI.Buttons.ADD_ORDER); // Disable add order button during break
            }
            // If break has not started, set time clocked in from start to now.
        } else totalSecClockedIn = clockInTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);

    }

    private static void getOrdersNeeded() {

        if (breakOutTime == null) { // Check if we have not chosen break times
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
        if (breakOutTime != null) {
            totalSecClockedIn = clockInTime.until(clockOutTime, ChronoUnit.SECONDS) -
                    breakInTime.until(breakOutTime, ChronoUnit.SECONDS);
        } else totalSecClockedIn = clockInTime.until(clockOutTime, ChronoUnit.SECONDS);

    }

    public static TreeMap<LocalDate, Float> getShiftHistory() {
        return shiftHistory;
    }

    public static long getTotalSecClockedIn() {
        return totalSecClockedIn;
    }

    public static boolean isBreakTimesChosen() {
        return breakOutTime != null;
    }

    public static int getTarget() {
        return target;
    }

    public static void setTarget(int newTarget) {
        target = newTarget;
    }

    public static int getOrders() {
        return orders;
    }

    public static String getOrdersPerHour() {
        return twoDecs.format((float) (orders * 3600) / totalSecClockedIn) + "/hr";
    }

    public static void setOrders(int newVal) {
        orders = newVal;
    }

    public static void removeFromHistory(LocalDate date) {
        shiftHistory.remove(date);
    }

    public static void clockOut(LocalDateTime time) {

        // Store the current shift end date and the orders per hour within the chosen time.
        shiftHistory.put(LocalDate.now(), Float.valueOf(twoDecs.format((float) (orders*3600)/
                // If the selected time is the same as our clock in time (down to the minute, so if
                // we are only a few seconds clocked in) just do the math with the next minute.
                Main.clockInTime.until(time.equals(clockInTime) ? time.plusMinutes(1) : time, ChronoUnit.SECONDS))));

        userPrefs.put("shiftHistory", shiftHistory.toString());
        try {
            saveHistoryToFile();    // Relying on Preferences will be deprecated as of Beta 5 for
                                    // importing/exporting ability
        } catch (IOException e) { e.printStackTrace(); }

        System.exit(0);

    }

    /**
     * Saves shift history to a file at the default directory (usually Documents, home in linux)
     *
     * @throws IOException If unable to write file
     */
    private static void saveHistoryToFile() throws IOException {

        String dir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                File.separator + "bedroom-data"; // Get default directory plus bedroom-data folder
        File path = new File(dir); // Make the directory into a File

        if (path.exists() || path.mkdirs()) { // If the directory exists or if it can be made:

            createHistoryFileAt(dir); // Create the file

        } else { // If teh directory does not exist and cannot be made:
            new AlertDialog(wnd, "Unable to save shift history.");
            System.out.println("Error saving history to path.");
        }

    }

    /**
     * Creates a "shift.history" file storing all our shift history in text.
     *
     * @param path Where to create the file.
     */
    private static void createHistoryFileAt(String path) {

        // Create the file instance
        File shiftHistoryFile = new File(path + File.separator + "shift.history");

        try {
            if (shiftHistoryFile.createNewFile()) { // If the file does not exist attempt to make it:

                FileWriter writer = new FileWriter(shiftHistoryFile);
                writer.write(shiftHistory.toString()); // Write history
                writer.close();

            // Else if it exists, attempt to delete and remake it:
            } else if (shiftHistoryFile.delete()) saveHistoryToFile();
        } catch (IOException e) { e.printStackTrace(); }

    }

}
