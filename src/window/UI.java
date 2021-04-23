package window;

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
    private String bttnPressed;
    private static double orders = 0;
    private static long totalSecClocked = 0;
    public static boolean clockedIn = false;
    private static String ln = "\n";
    private static DecimalFormat oph = new DecimalFormat("#.00");
    private static int key;

    private Color bg = Color.LIGHT_GRAY;
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

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
        //clockInOut.setFont(font);
        addOrder.addActionListener(this);
        //addOrder.setFont(font);
        stats.setBackground(bg);
        stats.setEditable(false);
        stats.setFont(font);
 
        setBackground(bg);
        add(clockInOut, BorderLayout.WEST);
        add(addOrder, BorderLayout.CENTER);
        add(stats, BorderLayout.EAST);

        getStats();
        
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
            updateBttns();

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
        if (key == 8) if (orders > 0) orders--; // Remove an order with backspace
        if (key == 48)  { // Clock in/out with 0
            
            clockedIn = !clockedIn;
            updateBttns();

        }
        if (key == 38) orders++; // Add orders with up arrow

        tick(false);
		
	}

	public void keyReleased(KeyEvent e) {}

    private void updateBttns() {

        if (clockedIn)  { 
            
            clockInOut.setText("Clock out");

        } else clockInOut.setText("Clock in");

    }

}
