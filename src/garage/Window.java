import java.lang.Runnable;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable {

    private final String version = "1.1 (Beta)";
    private boolean running;
    private long lastUpdate = System.nanoTime();
    private int secCount = 0;
    private static boolean doneLoading;
    public static boolean packNow = false;

    private static ClockInWindow ciwnd = new ClockInWindow(0),
                    cownd = new ClockInWindow(1); // Clock in/out time windows

    public static boolean ciChosen = false, coChosen = false;

    public Window() {

        UI ui = new UI();

        setTitle("Garage " + version);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(ui);
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

        doneLoading = true;

    }

    public static void main(String[] args) {

        Window wnd = new Window();
        wnd.start();

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
                    lastUpdate = System.nanoTime();

                } else if (coChosen && !UI.clockInTimePassed) {

                    UI.getTime();
    
                }

                this.pack();

                secCount++;
                if (secCount > 59) { // Run GC every 60 seconds

                    System.gc();
                    secCount = 0;

                }

            }

            if (doneLoading) { // Give focus to time choosing window once main window loaded
                
                ciwnd.requestFocus();
                doneLoading = false; // Only request it once as to be on top, not 1,000 times
                                     // a second, as that would eb annoying for other tasks.

            }

            if (ciChosen && cownd.isEnabled()) {

                ciwnd.dispose(); // Close clock-in time window when finished
                if (!cownd.isVisible()) cownd.setVisible(true); // Show clock-out time window

            }

            if (coChosen) {

                cownd.setEnabled(false);
                cownd.dispose(); // Clock clock-out time window when finished

            }

            if (packNow) {

                this.pack();
                packNow = false;

            }

            try { 

                Thread.sleep(1);

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
