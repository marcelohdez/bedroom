package bedroom;

import dialogue.*;
import java.awt.event.*;
import javax.swing.*;

public class Main {

    public static String version = "2";

    public static boolean isOSX = System.getProperty("os.name").contains("Mac OS X"); // Check if OS is MacOS

    private static int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds

    public static Window wnd; // This window
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean ciChosen = false, coChosen = false; // Has clock in/clock out time been chosen?

    public enum TIME_WINDOW { // Type of select time windows
        CLOCK_IN_WINDOW,
        CLOCK_OUT_WINDOW,
        START_BREAK_WINDOW,
        END_BREAK_WINDOW
    }

    public static void main(String[] args) {
        try { // Set cross-platform look and feel, fixes MacOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }

        // Open main window
        wnd = new Window();
        // Create clock in/out windows
        clockInWnd = new SelectTimeWindow(TIME_WINDOW.CLOCK_IN_WINDOW);
        clockInWnd.setToCenterOfMainWindow(); // Center clock in window on main window
        clockOutWnd = new SelectTimeWindow(TIME_WINDOW.CLOCK_OUT_WINDOW);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(TIME_WINDOW.START_BREAK_WINDOW);
        leaveBreakWnd = new SelectTimeWindow(TIME_WINDOW.END_BREAK_WINDOW);

        // Update that occurs every second
        ActionListener update = e -> {
            if (coChosen) // If we have passed clock in/out time windows:
                if (UI.clockInTimePassed && !UI.inBreak) { UI.tick();
                } else UI.getTime(); // To get time until clock in

            secCount++;

            if (secCount > 59) { // Run every minute
                System.gc(); // Garbage collect

                UI.getTime(); // Recheck time clocked in, in case of computer sleep and for accuracy

                secCount = 0;
            }

            wnd.pack();
        };

        // Start updating every second
        Timer t = new Timer(1000, update);
        t.setRepeats(true);
        t.start();
    }

}
