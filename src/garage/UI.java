import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class UI extends JPanel implements ActionListener, KeyListener {

    // Program Variables
    private String bttn;
    private static double orders = 0;
    private static long totalSecClocked = 0;
    public static boolean clockedIn = false;
    private static String ln = "\n";
    private static DecimalFormat oph = new DecimalFormat("#.00");
    private static int key;
    private Color bg = Color.LIGHT_GRAY;

    // Time Variables
    private static int hr = 0, min = 0, sec = 0;
    private static StringBuilder shr, smin, ssec;

    // Buttons
    private JButton clockInOut = new JButton("Clock in");
    private JButton addOrder = new JButton("Add order");
    private static JTextArea stats = new JTextArea();

    public UI() {

        setFocusable(true);
        addKeyListener(this);
        setLayout(new BorderLayout());

        clockInOut.addActionListener(this);
        addOrder.addActionListener(this);
        stats.setBackground(bg);
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
 
        setBackground(bg);
        add(clockInOut, BorderLayout.WEST);
        add(addOrder, BorderLayout.CENTER);
        add(stats, BorderLayout.EAST);

        getStats();
        
    }

    public void actionPerformed(ActionEvent e) {

        this.requestFocus(); /* Get focus back on the UI panel every time an action is performed.
                                It's a workaround as buttons get the focus when clicked. */
        bttn = e.getActionCommand();

        if (bttn == "Add order") {

            changeOrders(1);

        } else if (bttn == "Clock in" || bttn == "Clock out") {

            clockedIn = !clockedIn;
            updateBttns();

        }
        
    }

    public static void tick() {

        totalSecClocked++;
        sec++;

        if (sec > 59) {

            min++;
            sec -= 60;

        }

        if (min > 59) {

            hr++;
            min -= 60;

        }

        getStats();

    }

    private static void getStats() {

        shr = new StringBuilder();
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
        System.out.println(key);

        // ============ Shortcuts ============
        if (key == 8) if (orders > 0) changeOrders(-1); // Remove an order with backspace
        if (key == 48)  { // Clock in/out with 0
            
            clockedIn = !clockedIn;
            updateBttns();

        }
        if (key == 38) changeOrders(1); // Add orders with up arrow
        if (key == 40) changeOrders(-1); // Remove orders with down arrow (again, i know)

        getStats();
		
	}

	public void keyReleased(KeyEvent e) {}

    private void updateBttns() {

        if (clockedIn)  { 
            
            clockInOut.setText("Clock out");

        } else clockInOut.setText("Clock in");

    }

    private static void changeOrders(int amnt) {

        if (clockedIn) {

            orders += amnt;
            if (orders < 0) orders = 0;

            getStats();

        }

    }

}
