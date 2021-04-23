package window;

import java.awt.Color;
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
    private String bttnPressed;
    private static double orders = 0;
    private static long totalSecClocked = 0;
    public static boolean clockedIn = false;
    private Color bg = Color.LIGHT_GRAY;
    private static String ln = "\n";
    private static DecimalFormat oph = new DecimalFormat("#.00");

    // Time Variables
    private static int hr = 0, min = 0, sec = 0;

    // Buttons
    private JButton clockInOut = new JButton("Clock in");
    private JButton addOrder = new JButton("Add order");
    private static JTextArea stats = new JTextArea("Time: 00:00:00" + ln
                                    + "Orders: " + orders + " (" + "" + ".00/hr)");

    public UI() {

        setFocusable(true);
        addKeyListener(this);
        setLayout(new BorderLayout());

        clockInOut.addActionListener(this);
        addOrder.addActionListener(this);
        stats.setBackground(bg);
        stats.setEditable(false);
 
        setBackground(bg);
        add(clockInOut, BorderLayout.WEST);
        add(addOrder, BorderLayout.CENTER);
        add(stats, BorderLayout.EAST);
        
    }

    public void actionPerformed(ActionEvent e) {

        this.requestFocus(); /* Get focus back on the UI panel every time an action is performed
                                It is a workaround. */
        bttnPressed = e.getActionCommand();

        if (bttnPressed == "Add order") {

            if (clockedIn)  { 
                
                orders++;
                tick(false);

            }

        } else if (bttnPressed == "Clock in" || bttnPressed == "Clock out") {

            clockedIn = !clockedIn;

            if (clockedIn)  { clockInOut.setText("Clock out");
            } else clockInOut.setText("Clock in");

        }
        
    }

    public static void tick(boolean timeChange) {

        if (timeChange) { 
            
            totalSecClocked++;
            sec++;

        }

        if (sec > 59) {

            min++;
            sec -= 60;

        }

        if (min > 59) {

            hr++;
            min -= 60;

        }

        stats.setText("Time: " + getHr() + ":" + getMin() + ":" + getSec() + ln
                    + "Orders: " + (int)orders + " (" + oph.format((orders*3600)/totalSecClocked) + "/hr)");

    }

    private static String getSec() {

        if (sec < 10) return "0" + sec;

        return "" + sec;

    }
    
    private static String getMin() {

        if (min < 10) return "0" + min;

        return "" + min;

    }

    private static String getHr() {

        if (hr < 10) return "0" + hr;

        return "" + hr;

    }

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {

        // Remove an order with backspace
        if (e.getKeyCode() == 8) if (orders > 0) orders--;

        tick(false);
		
	}

	public void keyReleased(KeyEvent e) {}

}
