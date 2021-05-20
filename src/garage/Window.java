import java.lang.Runnable;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable {

    private final String version = "1.1";
    private boolean running;
    private long lastUpdate = System.nanoTime();
    private int secCount = 0;
    private static boolean doneLoading;
    public static boolean packNow = false;

    private static ClockInWindow cwnd = new ClockInWindow(0); // Clock in time

    public static boolean timesChosen = false;

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

                if (UI.clockedIn) {

                    UI.tick();
                    lastUpdate = System.nanoTime();

                } else if (timesChosen && !UI.clockInTimePassed) {

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
                
                cwnd.requestFocus();
                doneLoading = false; // Only request it once as to be on top, not 1,000 times
                                     // a second, as that would eb annoying for other tasks.

            }

            if (timesChosen) cwnd.dispose(); // Close clockInWindow when times are chosen

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
