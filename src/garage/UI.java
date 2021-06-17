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
    private static long secondsTillCI = -1;
    public static boolean recheckTimeTill = false; // In case computer goes to sleep
    public static boolean recheckTime = false; // Increase time accuracy

    // Components
    private static final JTextArea stats =
        new JTextArea("Time: 00:00:00\nOrders: 0 (.00/hr)\nNeeded: 0, 0 left");

    private static final JButton breakButton = new JButton("Enter Break");

    // Stats
    private static double orders = 0;
    public static boolean inBreak = false;
    public static boolean freeze = true; // Ignore entering/leaving break and changing orders
    public static boolean clockInTimePassed = false;
    public static int target = 0; // Target orders/hr
    private static long ordersNeeded = 0;

    // Keep track of times
    public static LocalTime clockInTime = LocalTime.parse("00:00"),  clockOutTime = LocalTime.parse("00:00"),
            breakInTime, breakOutTime;
    public static boolean breakTimeChosen = false;
    public static boolean clockOutSkipped = false;

    // Colors
    public static Color textColor = new Color(240, 240, 240),
            buttonColor = new Color(80, 80, 80),
            bg = new Color(64, 64, 64);

    public UI() { // Set UI's properties

        JButton addOrder = new JButton("Add order"); // Add Order button
        Dimension buttonSize = new Dimension(110, 55);

        setFocusable(true);
        addKeyListener(this);

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

        String b = e.getActionCommand();

        if (b.equals("Add order")) {
            changeOrders(1);
        } else if (b.equals("Enter Break")) {
            enterBreak();
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

        if (clockInTimePassed) { // Get stats =======

            StringBuilder sb = new StringBuilder();

            sb.append("Time: ");
            // Get time into human readable format
            if (hr < 10) sb.append("0");
            sb.append(hr).append(":");
            if (min < 10) sb.append("0");
            sb.append(min).append(":");
            if (sec < 10) sb.append("0");
            sb.append(sec);

            // Add other stats
            sb.append("\nOrders: ").append((int)orders).append(" (")
                    .append(oph.format((orders*3600)/totalSecClocked))
                    .append("/hr)\nNeeded: ").append(ordersNeeded).append(", ");
            if (orders < ordersNeeded) { sb.append((int) (ordersNeeded - orders));
            } else sb.append("0");
            sb.append(" left");

            stats.setText(sb.toString());

        } else if (Main.coChosen) { // Get "Time till clock in" =======

            secondsTillCI -= 1;
            long seconds = secondsTillCI;
            int hours = 0;
            int minutes = 0;

            StringBuilder sb = new StringBuilder();

            while (seconds > 59) {
                minutes++;
                seconds -= 60;
            }
            while (minutes > 59) {
                hours++;
                minutes -= 60;
            }

            if (hours < 10) sb.append("0");
            sb.append(hours).append(":");
            if (minutes < 10) sb.append("0");
            sb.append(minutes).append(":");
            if (seconds < 10) sb.append("0");
            sb.append(seconds);

            stats.setText("Time until clocked in:\n" + sb);

        }

    }

    private void enterBreak() {
        if (!freeze) {
            Main.enterBreakWnd.setUIToCurrentTime();
            Main.enterBreakWnd.setVisible(true);
        }
    }

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        // ======= Shortcuts =======
        if (key == 8 || key == 40) changeOrders(-1); // Remove orders with BckSpc & Down Arrow
        if (key == 48)  { // Enter break with 0
            enterBreak();
        }
        if (key == 38) changeOrders(1); // Add orders with up arrow
		
	}

	public void keyReleased(KeyEvent e) {}

    private void changeOrders(int amount) { // Change orders

        if (!freeze) {
            orders += amount;
            if (orders < 0) orders = 0;
            getStats();
        }

        Main.wnd.pack(); // Call the Window to pack itself

    }

    public static void getTime() { // See if clock-in time has passed, if so get the difference
        
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
                    }
                }
            } else // If not, set totalSecClocked to time from clock in to now
                totalSecClocked = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) - 1;
            sec = totalSecClocked;
            min = 0;
            hr = 0;
            tick();
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

            if (secondsTillCI == -1 || recheckTimeTill) { // Set secondsTillCI to difference in time
                secondsTillCI = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) + 1;
                recheckTimeTill = false;
            }
            getStats();

        }

        Main.wnd.pack();

    }

}
