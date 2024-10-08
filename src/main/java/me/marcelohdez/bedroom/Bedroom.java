package me.marcelohdez.bedroom;

import me.marcelohdez.bedroom.dialog.alert.AlertDialog;
import me.marcelohdez.bedroom.dialog.alert.YesNoDialog;
import me.marcelohdez.bedroom.main.BedroomWindow;
import me.marcelohdez.bedroom.util.Settings;
import me.marcelohdez.bedroom.util.Theme;
import me.marcelohdez.bedroom.util.Time;
import me.marcelohdez.bedroom.main.UI;

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
import java.util.Locale;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class Bedroom {

    // ======= Global Variables =======
    public static final String VERSION = "3.2-DEV";
    public static final Preferences userPrefs = Preferences.userRoot(); // User preferences directory

    // ======= Variables =======
    private static BedroomWindow wnd; // Main window

    // Time values
    private static LocalDateTime clockInTime, clockOutTime, breakInTime, breakOutTime;

    // Shift stats
    private static long lastOrderChange = 0; // Time of last order change, in milliseconds
    private static int orders = 0;
    private static int target; // Target orders/hr
    private static int ordersNeeded = 0;
    private static long secondsWorked = 0;
    private static boolean isOvernight = false;

    // Shift performance history (key: shift end date, value: float of orders per hour)
    private static TreeMap<LocalDate, Float> shiftHistory;

    private static final DecimalFormat twoDecs = new DecimalFormat("#.00");

    public static void main(String[] args) {

        doLAFStuff();
        SwingUtilities.invokeLater(Bedroom::openStartupItems);
        SwingUtilities.invokeLater(Bedroom::loadShiftHistory);
        init();

        // Create a timer to run every second, updating the time
        new Timer(1000, e -> update()).start();

    }

    private static void doLAFStuff() {
        if (userPrefs.getBoolean("firstLAFCheck", true)) {
            checkForSystemLAF();
        }

        try { // Set look and feel
            if (Settings.isSystemLAFEnabled()) {
                trySettingSystemLAF();
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            // If we have not checked before, check OS property and enable high contrast if true
            if (userPrefs.getBoolean("firstTimeHCCHeck", true)) setHighContrast();
        } catch(Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        Theme.setColors(); // Set extra color accents through UIManager
    }

    private static void checkForSystemLAF() {
        try {
            String systemLAF = UIManager.getSystemLookAndFeelClassName();
            if (systemLAF.equals(UIManager.getCrossPlatformLookAndFeelClassName())) {
                Settings.enableSystemLAF(false); // Disable system look and feel as it is not available
                System.out.println("No system look and feel found! Defaulting to cross-platform LAF.");
            }
        } catch (Exception e) { e.printStackTrace(); }

        userPrefs.putBoolean("firstLAFCheck", false);
    }

    private static void trySettingSystemLAF() throws Exception {
        String systemLAF = UIManager.getSystemLookAndFeelClassName();

        if (systemLAF.equals(UIManager.getCrossPlatformLookAndFeelClassName())) {
            new AlertDialog(null, "This system's theme is not supported!");
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } else {
            UIManager.setLookAndFeel(systemLAF);
        }
    }

    private static void setHighContrast() {
        if (System.getProperty("os.name").contains("Windows")) {
            if (Toolkit.getDefaultToolkit()
                    .getDesktopProperty("win.highContrast.on").equals(Boolean.TRUE)) {
                Settings.enableHighContrast(true);
            }
        }

        userPrefs.putBoolean("firstTimeHCCHeck", false);
    }

    private static void init() {
        if ((Settings.isCrashRecoveryEnabled() && isInSavedShift())) { // Recover from crash
            recoverShift();
            // Update now to not wait for next second as it is updated at the end of the update method
            secondsWorked = secondsWorkedBy(LocalDateTime.now());
        }

        wnd = new BedroomWindow(); // Create window
        update(); // Update stats etc
    }

    private static void recoverShift() {
        setShift(LocalDateTime.parse(userPrefs.get("shiftStart", "")),  // Set shift times to last saved
                LocalDateTime.parse(userPrefs.get("shiftEnd", "")));
        setTarget(userPrefs.getInt("target", Settings.getDefaultTarget()));   // Set target to saved value
        setOrders(userPrefs.getInt("orders", 0), false);   // Set orders to saved value

        if (lastSavedBreakIsInShift()) // If our last saved break is inside our shift:
            setBreak(LocalDateTime.parse(userPrefs.get("breakStart", "")),
                    LocalDateTime.parse(userPrefs.get("breakEnd", "")));
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

    private static void loadShiftHistory() {
        try { // Try to load shift history
            shiftHistory = Settings.loadShiftHistory();
        } catch (NumberFormatException e) { // If unable to load due to NumberFormatException show error:
            new AlertDialog(null, """
                    Bedroom was unable to load
                    your past shift history as
                    a character loaded was not
                    a number. Please check
                    your history file.""");
        }
    }

    private static void openStartupItems() {
        String[] list = Settings.getStartupItemsList();

        for (String location : list) {
            if (!location.isEmpty()) {
                openItem(location);
            }
        }
    }

    private static void openItem(String loc) {
        File workApp = new File(loc);
        if (!loc.toLowerCase(Locale.ROOT).endsWith(".jar") || new YesNoDialog(null, """
                        For safety reasons, Bedroom does not
                        automatically open .jar files so users
                        do not create an endless loop of
                        Bedroom processes, is this startup item
                        ok to run?:
                        
                        """ + workApp.getName()).accepted()) {

            if (workApp.exists()) {
                try {
                    Desktop.getDesktop().open(workApp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else new AlertDialog(wnd, """
                    One of your startup items was
                    not able to be started as it
                    no longer exists. Please go to
                    Settings > Manage Startup Items.""");
        }
    }

    public static void restart() {
        wnd.dispose();
        doLAFStuff();
        wnd = new BedroomWindow();
        update();
    }

    public static void updateSettings() {
        Theme.reloadColors();
        wnd.reloadSettings();
    }

    public static void update() {
        if (wnd != null && timesChosen()) { // Have we chosen clock in and out times?

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
                $u"""
                .replace("$o", String.valueOf(orders))
                .replace("$n", String.valueOf(ordersNeeded))
                .replace("$pH", getOrdersPerHour())
                .replace("$u", getUntilTargetText());

    }

    private static String getUntilTargetText() {
        int ordersNeeded = getOrdersLeftForTarget();

        if (ordersNeeded > 0) {
            return "$u until target of $t/hr"
                .replace("$u", String.valueOf(ordersNeeded))
                .replace("$t", String.valueOf(target));
        } else {
            return "You are on target.";
        }
    }

    /** Returns how many orders user has left to reach their target */
    public static int getOrdersLeftForTarget() {

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

    public static void setOrders(int newVal, boolean changeLastOrderTime) {
        if (clockInTimePassed() && !isInBreak()) {
            orders = newVal;
            userPrefs.putInt("orders", newVal);
            if (changeLastOrderTime) lastOrderChange = System.currentTimeMillis();
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
        // Save break times in preferences for crash recovery
        userPrefs.put("breakStart", start.toString());
        userPrefs.put("breakEnd", end.toString());
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
        // Save shift times in preferences for crash recovery
        userPrefs.put("shiftStart", start.toString());
        userPrefs.put("shiftEnd", end.toString());
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
            new AlertDialog(wnd, "Unable to save shift history");
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
            if (Settings.isDoneLoadingShiftHistory()) {
                createHistoryFileAt(path.toPath()); // Create history file with data
            }
        } else { // If the directory does not exist and cannot be made:
            new AlertDialog(wnd, "Unable to save shift history");
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

            if (shiftHistoryFile.createNewFile()) { // If the file does not exist attempt to make it
                try (FileWriter writer = new FileWriter(shiftHistoryFile)) {
                    if (shiftHistory != null) {

                        writer.write(shiftHistory.toString()); // Write history

                    } else writer.write("{}"); // If history is null, just write empty brackets
                }
            } else if (shiftHistoryFile.delete()) saveHistoryToFile(); // delete and remake it if it does exists

        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog(wnd, "Unable to save history to file.");
        }

    }

}
