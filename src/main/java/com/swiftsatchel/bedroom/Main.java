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
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class Main {

    // ======= Global Variables =======
    public static final String VERSION = "3 (Beta 6) RC";
    public static final Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    // ======= Variables =======
    private static BedroomWindow wnd; // Main window

    // Time variables
    private static int hr = 0;
    private static int min = 0;
    private static int sec = 0;

    // Time values
    private static LocalDateTime clockInTime, clockOutTime, breakInTime, breakOutTime;

    // Shift stats
    private static int orders = 0;
    private static int target; // Target orders/hr
    private static int ordersNeeded = 0;
    private static long secondsWorked = 0;

    // Shift performance history (key: shift end date, value: float of orders per hour)
    private static TreeMap<LocalDate, Float> shiftHistory;

    private static final DecimalFormat twoDecs = new DecimalFormat("#.00");

    public static void main(String[] args) {

        try { // Set cross-platform look and feel, fixes macOS buttons having a white background
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Also, if on Windows--IDK the property to any other OS--check for high contrast and enable if true
            if (System.getProperty("os.name").contains("Windows"))
                if (Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on").equals(Boolean.TRUE))
                    Settings.setHighContrastTo(true);
        } catch(Exception e) { e.printStackTrace(); }

        Theme.setColors(); // Set extra color accents through UIManager
        init();
        SwingUtilities.invokeLater(() -> {

            Main.openStartupItems();

            try { // Try to load shift history
                shiftHistory = Settings.loadShiftHistory();
            } catch (NumberFormatException e) { // If unable to load due to NumberFormatException show error:
                new ErrorDialog(null, ErrorType.FAILED_TO_LOAD_SHIFT_HISTORY);
            }

        });

        // Create a timer to run every second, updating the time
        Timer t = new Timer(1000, e -> update());
        t.setRepeats(true);
        t.start(); // Start timer

    }

    private static void init() {

        wnd = new BedroomWindow(); // Set main window

        if (Settings.isCrashRecoveryEnabled() && isInSavedShift()) {

            setShift(LocalDateTime.parse(userPrefs.get("shiftStart", "")),  // Set shift times to last saved
                    LocalDateTime.parse(userPrefs.get("shiftEnd", "")));    // times
            setTarget(userPrefs.getInt("target", Settings.getDefaultTarget()));   // Set target to saved value
            setOrders(userPrefs.getInt("orders", 0));   // Set orders to saved value

            if (lastSavedBreakIsInShift()) // If our last saved break is inside our shift:
                setBreak(LocalDateTime.parse(userPrefs.get("breakStart", "")),
                    LocalDateTime.parse(userPrefs.get("breakEnd", "")));

            update(); // Update stats etc

        } else new SelectTimeDialog(wnd, TimeWindowType.CLOCK_IN); // Create clock in window

    }

    private static boolean isInSavedShift() {

        try { // Try to parse:

            // Return if we are within the last saved shift start and end values
            return LocalDateTime.now().isAfter(LocalDateTime.parse(userPrefs.get("shiftStart", ""))) &&
                    LocalDateTime.now().isBefore(LocalDateTime.parse(userPrefs.get("shiftEnd", "")));

        } catch (DateTimeParseException e) {
            // If unable to parse just return false
            return false;
        }

    }

    private static boolean lastSavedBreakIsInShift() {

        try { // Try to parse:

            // Return if last break set was inside our shift
            return clockInTime.isBefore(LocalDateTime.parse(userPrefs.get("breakStart", ""))) &&
                    clockOutTime.isAfter(LocalDateTime.parse(userPrefs.get("breakEnd", "")));

        } catch (DateTimeParseException e) {
            // If unable to parse just return false
            return false;
        }

    }

    private static void openStartupItems() {

        for (String location : Settings.getStartupItemsList()) {

            if (!location.equals("")) {

                File workApp = new File(location);
                if (workApp.exists()) {

                    try {
                        Desktop.getDesktop().open(workApp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    new ErrorDialog(wnd, ErrorType.STARTUP_ITEM_NONEXISTENT);
                }

            }

        }

    }

    public static void updateSettings() {

        Theme.reloadColors();
        wnd.reloadSettings();

    }

    public static void update() {

        if (timesChosen()) { // Have we chosen clock in and out times?
            long seconds;

            // Has our clock in time passed?
            if (LocalDateTime.now().isAfter(clockInTime)) {
                wnd.enableButtons(); // Default to enabled buttons

                if (LocalDateTime.now().isBefore(clockOutTime)) { // If we have not finished our shift:

                    if (isInBreak()) { // If we are in our break, get time left until break ends:
                        seconds = LocalDateTime.now().until(breakOutTime, ChronoUnit.SECONDS);
                        wnd.disableButtons(UI.Buttons.ADD_ORDER); // Disable add order button during break
                    } else { // If not, get time worked
                        seconds = timeWorkedTill(LocalDateTime.now(), ChronoUnit.SECONDS);
                    }

                // If we have passed our clock out time get total time worked:
                } else seconds = timeWorkedTill(clockOutTime, ChronoUnit.SECONDS);

                ordersNeeded = Math.round(target * (timeWorkedTill(clockOutTime, ChronoUnit.MINUTES) / 60F));

            } else { // Else if it is before our shift starts:

                wnd.disableButtons(UI.Buttons.BOTH); // Disable buttons until we clock in
                // Get seconds left until we have to clock in
                seconds = LocalDateTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;

            }

            sec = (int) (seconds % 60);
            min = (int) (seconds / 60) % 60;
            hr = (int) Math.floor(seconds / 60F / 60F);
            secondsWorked = timeWorkedTill(LocalDateTime.now(), ChronoUnit.SECONDS);

            updateStats(); // Update stats and show on screen
            wnd.pack();

        }

    }

    public static boolean timesChosen() {
        return clockOutTime != null;
    }

    public static void changeOrders(int amount) { // Change orders

        if (!isInBreak()) {
            orders += amount;
            if (orders < 0) orders = 0;
            updateStats();
        }

        userPrefs.putInt("orders", orders);
        wnd.pack(); // Pack the window in case of text changes.

    }

    public static void updateStats() {

        StringBuilder sb = new StringBuilder();
        if (clockInTimePassed()) { // Get stats =======

            if (!isInBreak()) { // Show time clocked in
                sb.append("Time: ");
                Time.appendReadableTimeTo(sb, hr, min, sec);
                if (LocalDateTime.now().isAfter(clockOutTime)) sb.append(" (Done)");
                sb.append("\n") // Line break
                        .append(getStats());
            } else { // Show time left until our break ends =======
                sb.append("On break, ");
                Time.appendReadableTimeTo(sb, hr, min, sec);
                sb.append(" left\n")
                        .append(getStats());
            }

        } else { // Show "Time until clocked in" =======

            sb.append("Time until clocked in:\n");
            Time.appendReadableTimeTo(sb, hr, min, sec);
            sb.append("\n");

        }

        UI.display(sb.toString()); // Show on UI

    }

    private static String getStats() {

        return """
                Orders: $orders ($perHr)
                Needed: $needed, $left left"""
                .replace("$orders", String.valueOf(orders))
                .replace("$perHr", getOrdersPerHour())
                .replace("$needed", String.valueOf(ordersNeeded))
                .replace("$left", (orders < ordersNeeded) ?
                        String.valueOf(ordersNeeded - orders) : "0");

    }

    public static boolean isInBreak() {
        return breakTimesChosen() && LocalDateTime.now().isAfter(breakInTime) &&
                LocalDateTime.now().isBefore(breakOutTime);
    }

    public static boolean clockInTimePassed() {
        return timesChosen() && LocalDateTime.now().isAfter(clockInTime);
    }

    public static TreeMap<LocalDate, Float> getShiftHistory() {
        return shiftHistory;
    }

    public static long getTotalSecClockedIn() {
        return secondsWorked;
    }

    public static boolean breakTimesChosen() {
        return breakOutTime != null;
    }

    public static int getTarget() {
        return target;
    }

    public static void setTarget(int newTarget) {
        target = newTarget;
        userPrefs.putInt("target", newTarget);
    }

    public static int getOrders() {
        return orders;
    }

    public static String getOrdersPerHour() {
        return twoDecs.format((float) (orders * 3600) / secondsWorked) + "/hr";
    }

    public static void setOrders(int newVal) {
        if (clockInTimePassed() && !isInBreak()) {
            orders = newVal;
            userPrefs.putInt("orders", newVal);
            update();
        }
    }

    public static LocalDateTime getBreakStart() {
        return breakInTime;
    }

    public static LocalDateTime getBreakEnd() {
        return breakOutTime;
    }

    public static void setBreak(LocalDateTime start, LocalDateTime end) {
        breakInTime = start;
        breakOutTime = end;
        if (Settings.isCrashRecoveryEnabled()) {
            userPrefs.put("breakStart", start.toString());
            userPrefs.put("breakEnd", end.toString());
        }
    }

    public static LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public static LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public static void setShift(LocalDateTime start, LocalDateTime end) {
        clockInTime = start;
        clockOutTime = end;
        if (Settings.isCrashRecoveryEnabled()) {
            userPrefs.put("shiftStart", start.toString());
            userPrefs.put("shiftEnd", end.toString());
        }
    }

    public static void removeFromHistory(LocalDate date) {
        shiftHistory.remove(date);
    }

    public static void clockOut(LocalDateTime time) {

        // Store the current shift end date and the orders per hour within the chosen time.
        shiftHistory.put(LocalDate.now(),
                Float.valueOf(twoDecs.format((float) (orders * 3600) /
                        timeWorkedTill(time, ChronoUnit.SECONDS))));
        userPrefs.put("shiftEnd", time.toString()); // Save clocked out time

        exit();

    }

    /**
     * Get time worked from clock in time to a specified time, in the unit chosen.
     *
     * @param till Time to calculate off of
     * @param unit Unit of time to use
     * @return The amount of *unit* worked from clock in time to chosen *till* time
     */
    private static long timeWorkedTill(LocalDateTime till, ChronoUnit unit) {

        if (breakOutTime == null) { // If there is no break:
            return clockInTime.until(till, unit); // Get seconds until the time chosen
        } else {
            if (till.isBefore(breakInTime)) { // If we have not started our break:
                return clockInTime.until(till, unit); // Get seconds until the time chosen
            } else if (till.isBefore(breakOutTime)) { // If we are in our break:
                return clockInTime.until(breakInTime, unit);
            } else { // If we have passed our break:
                // Return the time between clocking in and wanted time minus the break length
                return clockInTime.until(till, unit) - breakInTime.until(breakOutTime, unit);
            }
        }

    }

    /**
     * Get things wrapped up before exiting
     */
    public static void exit() {

        // Save shitHistory to file
        try {

            saveHistoryToFile();

        } catch (IOException e) {

            new ErrorDialog(wnd, ErrorType.SAVING_HISTORY_FAILED);
            e.printStackTrace();

        }

        System.exit(0);

    }

    /**
     * Saves shift history to a file at the default directory (usually Documents, home in linux)
     *
     * @throws IOException If unable to write file
     */
    private static void saveHistoryToFile() throws IOException {

        File path = new File(Settings.getWorkingDir()); // Make the directory into a File

        if (path.exists() || path.mkdirs()) { // If the directory exists or if it can be made:

            createHistoryFileAt(path.toPath()); // Create the file

        } else { // If teh directory does not exist and cannot be made:
            new ErrorDialog(null, ErrorType.SAVING_HISTORY_FAILED);
            System.out.println("Error saving history to path.");
        }

    }

    /**
     * Creates a "shift.history" file storing all our shift history in text.
     *
     * @param path Where to create the file.
     */
    private static void createHistoryFileAt(Path path) {

        // Create the file instance
        File shiftHistoryFile = new File(path + File.separator + "shift.history");

        try {

            if (shiftHistoryFile.createNewFile()) { // If the file does not exist attempt to make it:

                FileWriter writer = new FileWriter(shiftHistoryFile);

                if (shiftHistory != null) {

                    writer.write(shiftHistory.toString()); // Write history

                } else writer.write("{}"); // If history is null, just write empty brackets

                writer.close();

            // Else if it exists, attempt to delete and remake it:
            } else if (shiftHistoryFile.delete()) saveHistoryToFile();

        } catch (Exception e) {

            e.printStackTrace();
            new AlertDialog(null, """
                    Unable to save history to file.""");

        }

    }

}
