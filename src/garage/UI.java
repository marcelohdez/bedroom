import org.jetbrains.annotations.NotNull;

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
    private static long totalSecClocked = 0, sec = 0;
    private static long secondsTillClockIn = -1;
    private static long secondsTillLeaveBreak = -1;
    public static boolean recheckTime = false; // Increase time accuracy

    // Components used outside of constructor
    private static final JButton breakButton = new JButton("Enter Break");
    private static final JTextArea stats =
        new JTextArea("Please clock in.\n\n");

    // Stats
    private static double orders = 0;
    public static boolean inBreak = false;
    public static boolean freeze = true; // Ignore entering/leaving break and changing orders
    public static boolean clockInTimePassed = false;
    public static int target = 0; // Target orders/hr
    private static long ordersNeeded = 0;

    // Time values
    public static LocalTime clockInTime = LocalTime.parse("00:00"),  clockOutTime = LocalTime.parse("00:00"),
            breakInTime, breakOutTime;
    public static boolean breakTimeChosen = false;
    public static boolean clockOutSkipped = false;

    // Public reusable colors & fonts
    public static Color textColor = new Color(240, 240, 240),
            buttonColor = new Color(80, 80, 80),
            bg = new Color(64, 64, 64);

    public UI() { // Set UI's properties

        JButton addOrder = new JButton("Add Order"); // Add Order button
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
            case "Enter Break" -> enterBreak();
        }

        this.requestFocus(); /* Get focus back on the UI panel every time an action is performed,
                                it's a workaround as buttons get the focus when clicked. */
        
    }

    public static void tick() { // Change time values

        totalSecClocked++;
        sec++;

        while (sec > 59) {
            min++;
            sec -= 60;
        }

        while (min > 59) {
            hr++;
            min -= 60;
        }

        getStats();

    }

    private static void getStats() {

        StringBuilder sb = new StringBuilder();

        if (clockInTimePassed) { // Get stats =======

            if (!inBreak) {
                sb.append("Time: ")
                        .append(makeTimeHumanReadable(hr, min, sec))
                // Add other stats
                .append(makeStatsIntoString());

            } else { // Get time left until our break ends =======
                long seconds = secondsTillLeaveBreak;
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

                sb.append("On break, ")
                        // Get time left to be human readable
                        .append(makeTimeHumanReadable(hours, minutes, seconds))
                        .append(" left")
                        // Add current stats
                        .append(makeStatsIntoString());
            }

            stats.setText(sb.toString());

        } else if (Main.coChosen) { // Get "Time till clock in" =======
            secondsTillClockIn -= 1;
            long seconds = secondsTillClockIn;
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

            sb.append("Time until clocked in:\n")
                    .append(makeTimeHumanReadable(hours, minutes, seconds))
                    .append("\n");

            stats.setText(sb.toString());
        }

    }

    @NotNull
    private static String makeTimeHumanReadable(int h, int m, long s) {
        StringBuilder sb = new StringBuilder();

        if (h < 10) sb.append("0");
        sb.append(h).append(":");
        if (m < 10) sb.append("0");
        sb.append(m).append(":");
        if (s < 10) sb.append("0");
        sb.append(s);

        return sb.toString();
    }

    @NotNull
    private static String makeStatsIntoString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nOrders: ").append((int)orders).append(" (")
                .append(oph.format((orders*3600)/totalSecClocked))
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
            Main.enterBreakWnd.setUIToCurrentTime();
            Main.enterBreakWnd.setVisible(true);
        }
    }

	public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
        // ======= Shortcuts =======
        switch (e.getKeyCode()) {
            case 8, 40 -> changeOrders(-1); // Remove orders with BckSpc & Down Arrow
            case 48 -> enterBreak();                // Enter break with 0
            case 38 -> changeOrders(1);     // Add orders with up arrow
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

        // Has our clock in time passed?
        if (clockInTime.compareTo(LocalTime.now()) <= 0 || recheckTime) {

            freeze = false;
            clockInTimePassed = true;
            if (breakTimeChosen) { // Have we chosen break times?
                if (breakInTime.compareTo(LocalTime.now()) <= 0) { // Has our break started?
                    if (breakOutTime.compareTo(LocalTime.now()) <= 0) { // Has our break ended?
                        inBreak = false; // If so, we are not in break.
                        // Set totalSecClocked to the seconds from clocking in to the break's start,
                        // then from break end to the current time.
                        totalSecClocked = (clockInTime.until(breakInTime, ChronoUnit.SECONDS) +
                                breakOutTime.until(LocalTime.now(), ChronoUnit.SECONDS) - 1);
                    } else { // If our break has not ended:
                        inBreak = true; // We are still in break
                        // Set totalSecClocked to the seconds from clocking in to the break's start
                        totalSecClocked = clockInTime.until(breakInTime, ChronoUnit.SECONDS) - 1;
                        secondsTillLeaveBreak = // Seconds until our break ends
                                LocalTime.now().until(breakOutTime, ChronoUnit.SECONDS);
                    }
                }
            } else { // If not, set totalSecClocked to time from clock in to now
                totalSecClocked = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) - 1;
            }

            sec = totalSecClocked;
            min = 0;
            hr = 0;
            tick(); // Update time and show on screen
            if (!clockOutSkipped) // If we did not skip clock out times:
                if (!breakTimeChosen) { // Check if we have not chosen break times
                    ordersNeeded = Math.round(target *
                            // If so, get ordersNeeded with clock in and out times
                            ((double) clockInTime.until(clockOutTime, ChronoUnit.MINUTES) / 60));
                } else ordersNeeded = Math.round(target *
                        // If we did choose break times, then get ordersNeeded from clock in
                        // and clock out times minus the difference of our break's start and end times
                        (((double) clockInTime.until(clockOutTime, ChronoUnit.MINUTES) -
                                (double) breakInTime.until(breakOutTime, ChronoUnit.MINUTES)) / 60));
            recheckTime = false;

        } else {
            // Get seconds till we have to clock in
            secondsTillClockIn = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
            getStats(); // Display it on screen
        }

        Main.wnd.pack();

    }

}
