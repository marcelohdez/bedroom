package com.marcelohdez.bedroom;

import com.marcelohdez.dialog.SelectTimeUI;
import com.marcelohdez.settings.SettingsWindow;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import javax.swing.*;

public class UI extends JPanel implements ActionListener, KeyListener {

    // Decimal format
    private static final DecimalFormat oph = new DecimalFormat("#.00"); // orders/hr

    // Time Variables
    private static int hr = 0, min = 0;
    private static long totalSecClockedIn = 0, sec = 0;
    private static long secondsTillClockIn = -1;
    private static long secondsTillLeaveBreak = -1;

    // Components used outside of constructor
    private static final JTextArea stats = new JTextArea("Please clock in.\n\n");
    private static final JButton breakButton = new JButton("Set Break"),
            addOrder = new JButton("Add Order"); // Add Order button;

    // Stats
    private static long orders = 0;
    public static boolean inBreak = false;
    public static boolean freeze = true; // Ignore entering/leaving break and changing orders
    public static boolean clockInTimePassed = false;
    public static int target = 0; // Target orders/hr
    private static long ordersNeeded = 0;
    private static double percentOfShift = 0;   // How much of our shift have we done (in decimal,
                                                // ex: 80% is 0.8)

    // Time values
    public static LocalTime clockInTime, clockOutTime,
            breakInTime, breakOutTime;
    public static boolean breakTimesChosen = false;

    // Public reusable colors & fonts
    public static Font boldText = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    public static Color textColor = // Get color for text from user's prefs, default to 240 if not found
            new Color(Main.userPrefs.getInt("textRed", 240),
                    Main.userPrefs.getInt("textGreen", 240),
                    Main.userPrefs.getInt("textBlue", 240)),
            // Get color of buttons
            buttonColor = new Color(Main.userPrefs.getInt("buttonRed", 80),
                    Main.userPrefs.getInt("buttonGreen", 80),
                    Main.userPrefs.getInt("buttonBlue", 80)),
            // Get color of background
            bg = new Color(Main.userPrefs.getInt("bgRed", 64),
                    Main.userPrefs.getInt("bgGreen", 64),
                    Main.userPrefs.getInt("bgBlue", 64));

    public UI() { // Set UI's properties

        Dimension buttonSize = new Dimension(110, 55);

        setFocusable(true);
        addKeyListener(this);

        // Set components' properties
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        addOrder.addActionListener(this);
        addOrder.setPreferredSize(buttonSize);
        breakButton.addActionListener(this);
        breakButton.setPreferredSize(buttonSize);
        breakButton.setToolTipText("<html><b>Currently no break is set</b></html>"); // Default tooltip

        // Set colors
        breakButton.setBackground(buttonColor);
        breakButton.setForeground(textColor);
        addOrder.setBackground(buttonColor);
        addOrder.setForeground(textColor);
        stats.setBackground(bg);
        stats.setForeground(textColor);
 
        // Add components
        setBackground(bg);
        add(breakButton);
        add(addOrder);
        add(stats);

        getStats();
        
    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Add Order" -> changeOrders(1);
            case "Set Break" -> enterBreak();
        }

        this.requestFocus(); /* Get focus back on the UI panel every time an action is performed,
                                it's a workaround as buttons get the focus when clicked. */
        
    }

    public static void tick() { // Change time values

        totalSecClockedIn++;
        sec++;

        while (sec > 59) {
            min++;
            sec -= 60;
        }

        while (min > 59) {
            hr++;
            min -= 60;
        }

        percentOfShift = ((double) totalSecClockedIn / // Set percent of shift done
                clockInTime.until(clockOutTime, ChronoUnit.SECONDS));

        getStats();

    }

    private static void getStats() {

        StringBuilder sb = new StringBuilder();

        if (clockInTimePassed) { // Get stats =======

            if (!inBreak) { // Get time clocked in
                stats.setText(
                        sb.append("Time: ")
                        .append(makeTimeHumanReadable(hr, min, (int) sec))
                        // Add other stats
                        .append(makeStatsIntoString()).toString());
            } else { // Get time left until our break ends =======
                stats.setText(
                        sb.append("On break, ")
                        .append(shrinkTime(secondsTillLeaveBreak))
                        .append(" left")
                        // Add current stats
                        .append(makeStatsIntoString()).toString());
            }

            setTooltips();

        } else if (Main.timesChosen) { // Get "Time till clock in" =======
            stats.setText(
                    sb.append("Time until clocked in:\n")
                    .append(shrinkTime(secondsTillClockIn))
                    .append("\n").toString());
        }

    }

    private static String shrinkTime(long seconds) { // Convert big number of seconds into time

        int hours = 0;
        int minutes = 0;

        while (seconds > 59) {
            minutes++;
            seconds -= 60;
        }
        while (minutes > 59) {
            hours++;
            minutes -= 60;
        }

        return makeTimeHumanReadable(hours, minutes, (int) seconds);
    }

    private static String makeTimeHumanReadable(int h, int m, int s) { // Set time to "00:00:00" format
        StringBuilder sb = new StringBuilder();

        if (h < 10) sb.append("0");
        sb.append(h).append(":");
        if (m < 10) sb.append("0");
        sb.append(m).append(":");
        if (s < 10) sb.append("0");
        sb.append(s);

        return sb.toString();
    }

    private static String makeStatsIntoString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nOrders: ").append((int)orders).append(" (")
                .append(oph.format((double) (orders*3600)/ totalSecClockedIn))
                .append("/hr)\nNeeded: ");
        if (ordersNeeded > 0) {
            sb.append(ordersNeeded);
        } else sb.append("0");
        sb.append(", ");
        if (orders < ordersNeeded) { sb.append((int) (ordersNeeded - orders));
        } else sb.append("0");
        sb.append(" left");

        return sb.toString();
    }

    private void enterBreak() {
        if (!freeze) {
            Main.enterBreakWnd.centerOnMainWindow(); // Set to current center of main window
            Main.enterBreakWnd.setUITime(SelectTimeUI.GET_TIME.CURRENT);
            Main.enterBreakWnd.setVisible(true);
        }
    }

	public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
        // ======= Shortcuts =======
        switch (e.getKeyCode()) {
            case 8, 40 -> changeOrders(-1); // Remove orders with BckSpc & Down Arrow
            case 48 -> enterBreak();                // Set break times with 0
            case 38 -> changeOrders(1);     // Add orders with up arrow
            case 27, 127 -> new SettingsWindow();  // Open settings with Del or Esc keys
        }
	}

    private void changeOrders(int amount) { // Change orders

        if (!freeze && !inBreak) {
            orders += amount;
            if (orders < 0) orders = 0;
            getStats();
        }

        Main.wnd.pack(); // Call the Window to pack itself

    }

    public static void getTime() {

        if (Main.timesChosen) // Have we chosen clock in and out times?
            // Has our clock in time passed?
            if (clockInTime.compareTo(LocalTime.now()) <= 0) {

                freeze = false;
                clockInTimePassed = true;

                if (breakTimesChosen) { // Have we chosen break times?
                    getBreakTime();
                } else { // If not, set totalSecClocked to time from clock in to now
                    totalSecClockedIn = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) - 1;
                }

                getOrdersNeeded();
                sec = totalSecClockedIn;
                min = 0;
                hr = 0;
                tick(); // Update time and show on screen

            } else {
                // Get seconds left until we have to clock in
                secondsTillClockIn = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
                getStats(); // Display it on screen
            }

        Main.wnd.pack();

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

    private static void setTooltips() {

        double neededForTarget = (double) totalSecClockedIn/3600 * target;
        if (neededForTarget > orders) { // Tell us how many orders we need to reach our target

            StringBuilder sb = new StringBuilder();
            int amountMissing = (int) Math.round(Math.ceil(neededForTarget - orders));
            addOrder.setToolTipText(sb.append("<html><b>You are ")
                    .append(amountMissing)
                    .append(" order")
                    .append(isPlural(amountMissing))
                    .append(" behind your hourly target</b></html>").toString());

        } else addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");

        if (breakTimesChosen) { // If we have chosen break times, change the tooltip to them.

            StringBuilder sb = new StringBuilder();
            breakButton.setToolTipText(sb.append("Current: ")
                    .append(makeTime12Hour(breakInTime))
                    .append("-")
                    .append(makeTime12Hour(breakOutTime))
                    .toString());

        }

    }

    private static String makeTime12Hour(LocalTime time) { // Convert from 24hr to 12hr (ex: 16:00 -> 4:00pm)

        StringBuilder sb = new StringBuilder();
        int hour = time.getHour();
        int minute = time.getMinute();
        String amPM;

        if (hour >= 12) { // Set correct hour and am/pm value
            amPM = "PM";
            if (hour != 12) {       // Set hour to 1-11pm
                sb.append(hour-12);
            } else sb.append(12);   // Set to 12pm
        } else {
            amPM = "AM";
            if (hour != 0) {        // Set hour to 1-11am
                sb.append(hour);
            } else sb.append(12);   // Set to 12am
        }

        if (minute < 10) sb.append("0"); // Add 0 first if minute is less than 10 (ex: 4:1pm -> 4:01pm)
        sb.append(":").append(minute).append(amPM);

        return sb.toString();

    }

    private static String isPlural(int number) { // Return "s" if there is more than 1 of number
        if (number > 1) return "s";
        return "";
    }

    public static void reloadColors() {

        // Get color for text from user's prefs, default to 240 if not found
        textColor = new Color(Main.userPrefs.getInt("textRed", 240),
                Main.userPrefs.getInt("textGreen", 240),
                Main.userPrefs.getInt("textBlue", 240));
        // Get color of buttons
        buttonColor = new Color(Main.userPrefs.getInt("buttonRed", 80),
                Main.userPrefs.getInt("buttonGreen", 80),
                Main.userPrefs.getInt("buttonBlue", 80));
        // Get color of background
        bg = new Color(Main.userPrefs.getInt("bgRed", 64),
                Main.userPrefs.getInt("bgGreen", 64),
                Main.userPrefs.getInt("bgBlue", 64));

    }

}
