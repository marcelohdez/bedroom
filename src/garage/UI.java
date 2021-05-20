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
    private static String ln = "\n";
    private static DecimalFormat oph = new DecimalFormat("#.00");
    private static int key;
    private Color bg = Color.LIGHT_GRAY;

    // Time Variables
    private static int hr = 0, min = 0;
    private static StringBuilder shr, smin, ssec;
    private static long totalSecClocked = 0, sec = 0;

    // Buttons
    private JButton clockInOut = new JButton("Enter Break");
    private JButton addOrder = new JButton("Add order");
    private static JTextArea stats = new JTextArea();

    // Stats
    private static double orders = 0;
    public static boolean clockedIn = false;
    public static boolean freeze = false;
    public static boolean clockInTimePassed = false;

    public static LocalTime clockInTime = LocalTime.parse("00:00");
    public static LocalTime clockOutTime = LocalTime.parse("00:00");

    public UI() {

        setFocusable(true);
        addKeyListener(this);

        clockInOut.addActionListener(this);
        clockInOut.setPreferredSize(new Dimension(110, 45));
        addOrder.addActionListener(this);
        addOrder.setPreferredSize(new Dimension(110, 45));
        stats.setBackground(bg);
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
 
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

        shr = new StringBuilder(); // Reset values
        smin = new StringBuilder();
        ssec = new StringBuilder();

        if (sec < 10) ssec.append("0" + sec);
        else ssec.append(sec);

        if (min < 10) smin.append("0" + min);
        else smin.append(min);

        if (hr < 10) shr.append("0" + hr);
        else shr.append(hr);

        stats.setText("Time: " + shr + ":" + smin + ":" + ssec + ln
                    + "Orders: " + (int)orders + " (" 
                    + oph.format((orders*3600)/totalSecClocked) + "/hr)");

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

            clockedIn = !clockedIn;
            updateBttns();

        }

    }

    private void updateBttns() { // Update buttons

        if (clockedIn)  { 
            
            clockInOut.setText("Enter Break");

        } else clockInOut.setText("Leave Break");

        Window.packNow = true;

    }

    private void changeOrders(int amnt) { // Change orders

        if (clockedIn) {
            orders += amnt;
            if (orders < 0) orders = 0;
            getStats();
        }

        Window.packNow = true; // Call the Window to .pack()

    }

    public static void getTime() { // See if clock-in time has passed, if so get the difference

        if (clockInTime.compareTo(LocalTime.now()) <= 0) {

            freeze = false;
            clockedIn = true;
            clockInTimePassed = true;
            totalSecClocked = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) + 59;
            sec = clockInTime.until(LocalTime.now(), ChronoUnit.SECONDS) + 59;

        }

    }

}
