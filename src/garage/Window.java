import java.awt.Dimension;
import java.lang.Runnable;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable {

    private final String version = "1.1";
    private boolean running;
    private long lastUpdate = System.nanoTime();
    public static boolean timesChosen = false;
    private static boolean doneLoading;

    private static ClockInWindow cwnd = new ClockInWindow();

    public Window() {

        UI ui = new UI();

        setTitle("Garage " + version);
        setMinimumSize(new Dimension(380, 90));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(ui);

        setVisible(true);
        pack();

        doneLoading = true;

    }

    public static void main(String[] args) {

        Window wnd = new Window();
        wnd.start();

    }

    public void start() {

        Thread thread = new Thread(this);
        thread.start();
        running = true;

    }
    
    public void stop() {

        running = false;

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

            }

            if (doneLoading) { // Give focus to time choosing window once main window loaded
                
                cwnd.requestFocus();
                doneLoading = false; // Only request it once as to be on top, not 1,000 times
                                     // a second, as that would eb annoying for other tasks.

            }

            if (timesChosen) cwnd.dispose(); // Close clockInWindow when times are chosen

            try { 

                Thread.sleep(1);

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
