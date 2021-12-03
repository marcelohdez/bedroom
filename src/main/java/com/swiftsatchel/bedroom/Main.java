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
    public static final String VERSION = "3.1-DEV";
    public static final Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    // ======= Variables =======
    private static BedroomWindow wnd; // Main window

    // Time values
    private static LocalDateTime clockInTime, clockOutTime, breakInTime, breakOutTime;

    // Shift stats
    private static long lastOrderChange; // Time of last order change, in milliseconds
    private static int orders = 0;
    private static int target; // Target orders/hr
    private static int ordersNeeded = 0;
    private static long secondsWorked = 0;
    private static boolean isOvernight = false;

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
        new Timer(1000, e -> update()).start();

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

            // Has our clock in time passed?
            if (LocalDateTime.now().isAfter(clockInTime)) {
                wnd.enableButtons(); // Default to enabled buttons

                if (isInBreak()) wnd.disableButtons(UI.Buttons.ADD_ORDER); // Disable add order button during break
                ordersNeeded = Math.round(target * (secondsWorkedBy(clockOutTime) / 3600f));

            } else wnd.disableButtons(UI.Buttons.BOTH); // Disable buttons until we clock in

            updateStatsText(); // Update stats and show on screen
            wnd.pack();

        }

    }

    public static boolean timesChosen() {
        return clockOutTime != null;
    }

    public static void updateStatsText() {

        StringBuilder sb = new StringBuilder();
        if (clockInTimePassed()) { // Get stats =======

            if (!isInBreak()) { // Show time clocked in
                sb.append("Time: $t $e\n"
                        .replace("$t", Time.secondsToTime(secondsWorked))
                        .replace("$e", getPercentDone()));
            } else { // Show time left until our break ends =======
                sb.append("On break, $t left\n"
                        .replace("$t",
                                Time.secondsToTime(LocalDateTime.now().until(breakOutTime, ChronoUnit.SECONDS))));
            }

            sb.append(getStats()); // Add stats at the end

        } else { // Show "Time until clocked in" =======
            sb.append("""
                    Time until clocked in:
                    $t
                    """
                    .replace("$t",
                            Time.secondsToTime(LocalDateTime.now().until(clockInTime, ChronoUnit.SECONDS))));
        }

        secondsWorked = secondsWorkedBy(LocalDateTime.now());
        wnd.display(sb.toString()); // Show on UI

    }

    public static String getPercentDone() {
        float percent = (secondsWorked / (float) secondsWorkedBy(clockOutTime)) * 100f;

        if (Settings.showMoreShiftInfo()) {
            if (percent < 100f) {
                return "(p%)".replace("p", new DecimalFormat("#.0").format(percent));
            } else return "(Done)";
        } else if (percent >= 100f) return "(Done)";

        return "";
    }

    private static String getStats() {

        if (!Settings.showMoreShiftInfo()) {
            return """
                    Orders: $o ($pH)
                    Needed: $n, $l left"""
                    .replace("$o", String.valueOf(orders))
                    .replace("$pH", getOrdersPerHour())
                    .replace("$n", String.valueOf(ordersNeeded))
                    .replace("$l", (orders < ordersNeeded) ?
                            String.valueOf(ordersNeeded - orders) : "0");
        } else return """
                Orders: $o/$n @ $pH,
                $u until target of $t/hr"""
                .replace("$o", String.valueOf(orders))
                .replace("$n", String.valueOf(ordersNeeded))
                .replace("$pH", getOrdersPerHour())
                .replace("$u", String.valueOf(getOrdersNeededForTarget()))
                .replace("$t", String.valueOf(target));

    }

    // Tell us how many orders we need to reach our target
    public static int getOrdersNeededForTarget() {

        double neededForTarget = (double) secondsWorked/3600 * target;
        if (neededForTarget > orders) {

            return (int) Math.ceil(neededForTarget - orders);

        } else return 0;

    }

    public static boolean isInBreak() {
        return breakTimesChosen() && LocalDateTime.now().isAfter(breakInTime) &&
                LocalDateTime.now().isBefore(breakOutTime);
    }

    public static boolean isOvernightShift() {
        return isOvernight;
    }

    public static boolean clockInTimePassed() {
        return timesChosen() && LocalDateTime.now().isAfter(clockInTime);
    }

    public static TreeMap<LocalDate, Float> getShiftHistory() {
        return shiftHistory;
    }

    public static boolean breakTimesChosen() {
        return breakOutTime != null;
    }

    public static void setTarget(int newTarget) {
        target = newTarget;
        userPrefs.putInt("target", newTarget);
    }

    public static long getLastOrderChange() {
        return lastOrderChange;
    }

    public static int getOrders() {
        return orders;
    }

    public static int getOrdersNeeded() {
        return ordersNeeded;
    }

    public static String getOrdersPerHour() {
        return twoDecs.format(orders * 3600f / secondsWorked) + "/hr";
    }

    public static void setOrders(int newVal) {
        if (clockInTimePassed() && !isInBreak()) {
            orders = newVal;
            userPrefs.putInt("orders", newVal);
            lastOrderChange = System.currentTimeMillis();
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
        // If clock in time is 1 day before clock out time, then it is an overnight shift
        isOvernight = (start.getDayOfWeek().plus(1) == end.getDayOfWeek());

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
                Float.valueOf(twoDecs.format(orders * 3600f / secondsWorkedBy(time))));
        userPrefs.put("shiftEnd", time.toString()); // Save clocked out time

        exit();

    }

    /**
     * Get time worked from clock in time to the specified time, in seconds.
     *
     * @param till Time to calculate off of
     * @return The amount of seconds worked from clock in time to chosen time
     */
    private static long secondsWorkedBy(LocalDateTime till) {

        if (till.isAfter(clockOutTime)) { // If time is beyond clock out time, only measure up to clock out time:
            return secondsWorkedBy(clockOutTime);
        }

        if (breakOutTime == null) { // If there is no break:
            return clockInTime.until(till, ChronoUnit.SECONDS); // Get seconds until the time chosen
        } else {
            if (till.isBefore(breakInTime)) { // If we have not started our break:
                return clockInTime.until(till, ChronoUnit.SECONDS); // Get seconds until the time chosen
            } else if (till.isBefore(breakOutTime)) { // If we are in our break:
                return clockInTime.until(breakInTime, ChronoUnit.SECONDS);
            } else { // If we have passed our break:
                // Return the time between clocking in and wanted time minus the break length
                return clockInTime.until(till, ChronoUnit.SECONDS) -
                        breakInTime.until(breakOutTime, ChronoUnit.SECONDS);
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
