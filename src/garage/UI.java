import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class UI extends JPanel implements ActionListener, KeyListener {

    // Program Variables
    private static DecimalFormat oph = new DecimalFormat("#.00");
    private static int key;

    // Time Variables
    private static int hr = 0, min = 0;
    private static long totalSecClocked = 0, sec = 0;
    private static long secondsTillCI = -1;
    public static boolean recheckTimeTill = false; // In case computer goes to sleep
    public static boolean recheckTime = false; // Increase time accuracy

    // Buttons
    private JButton clockInOut = new JButton("Enter Break");
    private JButton addOrder = new JButton("Add order");

    // Labels
    private static JTextArea stats = 
        new JTextArea("Time: 00:00:00\nOrders: 0 (.00/hr)\nNeeded: 0, 0 left");

    // Stats
    private static double orders = 0;
    public static boolean inBreak = true;
    public static boolean freeze = true;
    public static boolean clockInTimePassed = false;

    public static LocalTime clockInTime = LocalTime.parse("00:00");
    public static LocalTime clockOutTime = LocalTime.parse("00:00");
    public static int target = 0; // Target orders/hr
    private static long ordersNeeded = 0;

    // Colors
    public static Color myWhite = new Color(240, 240, 240);
    public static Color bg = new Color(70, 70, 70);
    public static Color myGray = new Color(95, 95, 95);

    public UI() {

        setFocusable(true);
        addKeyListener(this);

        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        addOrder.addActionListener(this);
        addOrder.setPreferredSize(new Dimension(100, 45));
        clockInOut.addActionListener(this);
        clockInOut.setPreferredSize(new Dimension(110, 45));

        // Set colors
        clockInOut.setBackground(myGray);
        clockInOut.setForeground(myWhite);
        addOrder.setBackground(myGray);
        addOrder.setForeground(myWhite);
        stats.setBackground(bg);
        stats.setForeground(myWhite);
 
        // Add components
        setBackground(bg);
        add(clockInOut);
        add(addOrder);
        add(stats);

        getStats();
        
    }

    public void actionPerformed(ActionEvent e) {

        this.requestFocus(); /* Get focus back on the UI panel every time an action is performed.
                                It's a workaround as buttons get the focus when clicked. */
        String bttn = e.getActionCommand();

        if (bttn == "Add order") {

            changeOrders(1);

        } else if (bttn == "Enter Break" || bttn == "Leave Break") {

            enterLeaveBreak();

        }
        
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
            if (hr < 10) sb.append("0" + hr);
            else sb.append(hr);
            sb.append(":");
            if (min < 10) sb.append("0" + min);
            else sb.append(min);
            sb.append(":");
            if (sec < 10) sb.append("0" + sec);
            else sb.append(sec);

            // Add other stats
            sb.append("\nOrders: ");
            sb.append((int)orders);
            sb.append(" (");
            sb.append(oph.format((orders*3600)/totalSecClocked));
            sb.append("/hr)\nNeeded: ");
            sb.append(ordersNeeded);
            sb.append(", ");
            

            stats.setText(sb.toString() + ", "
                        + (int)(ordersNeeded - orders) + " left");

        } else if (Window.coChosen) { // Get "Time till clock in" =======

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

            if (hours < 10) { sb.append("0" + hours); 
            } else sb.append(hours);
            sb.append(":");
            if (minutes < 10) { sb.append("0" + minutes); 
            } else sb.append(minutes);
            sb.append(":");
            if (seconds < 10) { sb.append("0" + seconds);
            } else sb.append(seconds);

            stats.setText("Time until clocked in:\n" + sb);

        }

    }

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {

        key = e.getKeyCode();

        // ======= Shortcuts =======
        if (key == 8 || key == 40) changeOrders(-1); // Remove orders with BckSpc & Down Arrow
        if (key == 48)  { // Enter/leave break with 0
            enterLeaveBreak();
        }
        if (key == 38) changeOrders(1); // Add orders with up arrow

        getStats();
		
	}

	public void keyReleased(KeyEvent e) {}

    private void enterLeaveBreak() { // Enter/Leave break

        if (!freeze) {

            inBreak = !inBreak;
            updateBttns();

        }

    }

    private void updateBttns() { // Update buttons

        if (!inBreak)  { 
            
            clockInOut.setText("Enter Break");

        } else clockInOut.setText("Leave Break");

        Window.packNow = true;

    }

    private void changeOrders(int amnt) { // Change orders

        if (!inBreak) {
            orders += amnt;
            if (orders < 0) orders = 0;
            getStats();
        }

        Window.packNow = true; // Call the Window to .pack()

    }

    public static void getTime() { // See if clock-in time has passed, if so get the difference
        
        if (clockInTime.compareTo(LocalTime.now()) <= 0 || recheckTime) {

            freeze = false;
            inBreak = false;
            clockInTimePassed = true;
            if (Window.isOSX) { totalSecClocked = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS);
            } else totalSecClocked = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) + 59;
            sec = totalSecClocked;
            min = 0;
            hr = 0;
            tick();
            ordersNeeded = clockInTime.until(clockOutTime, ChronoUnit.HOURS) * target;
            recheckTime = false;

        } else {

            if (secondsTillCI == -1 || recheckTimeTill) { // Set secondsTillCI to difference in time

                secondsTillCI = LocalTime.now().until(clockInTime, ChronoUnit.SECONDS) - 59;
                recheckTimeTill = false;

            }
            getStats();

        }

    }

}
