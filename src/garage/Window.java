import java.lang.Runnable;

import javax.swing.*;

public class Window extends JFrame implements Runnable {

    public static boolean isOSX = System.getProperty("os.name").contains("Mac OS X"); // Check if OS is MacOS

    private boolean running; // Is program running
    private long lastUpdate = System.nanoTime(); // Keep track of time updates (seconds)
    private int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds
    private static boolean doneLoading; // To track main window finishing loading
    public static boolean packNow = false; // Should this window pack right now?

    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean ciChosen = false, coChosen = false, // Clock in/out times chosen?
        selectBreakStart = false, selectBreakEnd = false,
            bsChosen = false, beChosen = false; // Break enter/leave times

    public Window() {

        UI ui = new UI();

        setTitle("Garage 1.1 (Beta 2)");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(ui);
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

        doneLoading = true;

    }

    public static void main(String[] args) {
        try { // Set cross-platform look and feel, fixes MacOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }
        // Start clock in/out time windows
        clockInWnd = new SelectTimeWindow(0);
        clockOutWnd = new SelectTimeWindow(1);
        // Start break enter/leave time windows
        enterBreakWnd = new SelectTimeWindow(2);
        leaveBreakWnd = new SelectTimeWindow(3);
        // Open main window
        new Window().start();
    }

    public void start() {

        Thread thread = new Thread(this);
        thread.start(); // Start thread
        running = true;

    }
    
    public void stop() { // When application is stopped:

        running = false; // Stop "while (running)"

    }

    public void run() {

        while (running) {

            if (System.nanoTime() - lastUpdate >= 10e8) { // Update every second

                if (!UI.inBreak) {

                    UI.tick();

                } else if (coChosen && !UI.clockInTimePassed) {

                    UI.getTime();
    
                }

                this.pack();

                secCount++;
                if (secCount > 119) { // Run every 2 minutes

                    System.gc(); // Garbage collect

                    if (!UI.clockInTimePassed) { UI.recheckTimeTill = true; // Recheck time left till clock in
                    } else UI.recheckTime = true; // Recheck time clocked in
                    UI.getTime();

                    secCount = 0;

                }

                lastUpdate = System.nanoTime(); // Reset timer
            }

            if (doneLoading) { // Give focus to time choosing window once main window loaded
                clockInWnd.requestFocus();
                doneLoading = false; // Only request it once as to be on top, not 1,000 times
                                     // a second, as that would eb annoying for other tasks.
            }

            if (packNow) {
                this.pack();
                packNow = false;
            }

            try { 

                Thread.sleep(1); // Busy-waiting, may be removed in the future

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
