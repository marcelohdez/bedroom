import javax.swing.*;

public class Window extends JFrame implements Runnable {

    public static boolean isOSX = System.getProperty("os.name").contains("Mac OS X"); // Check if OS is MacOS

    private boolean running; // Is program running
    private long lastUpdate = System.nanoTime(); // Keep track of time updates (seconds)
    private int secCount = 0; // Keep count of seconds to do certain tasks every 60 seconds

    public static Window wnd; // This window
    public static SelectTimeWindow clockInWnd, clockOutWnd, enterBreakWnd, leaveBreakWnd; // Select time windows
    public static boolean ciChosen = false, coChosen = false; // Has clock in/clock out time been chosen?

    public Window() {

        setTitle("Garage 1.1 (Beta 3)");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new UI());
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

    public static void main(String[] args) {
        try { // Set cross-platform look and feel, fixes MacOS buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) { e.printStackTrace(); }
        // Open main window
        wnd = new Window();
        // Create clock in window
        clockInWnd = new SelectTimeWindow(0);
        // Create enter/leave break windows
        enterBreakWnd = new SelectTimeWindow(2);
        wnd.start();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start(); // Start thread
        running = true;
    }

    public void run() {

        while (running) {

            if (System.nanoTime() - lastUpdate >= 10e8) { // Update every second

                if (!UI.inBreak) {

                    UI.tick();

                } else {

                    UI.getTime();

                }

                this.pack();

                secCount++;
                if (secCount > 59) { // Run every minute

                    System.gc(); // Garbage collect

                    if (!UI.clockInTimePassed) { UI.recheckTimeTill = true; // Recheck time left till clock in
                    } else UI.recheckTime = true; // Recheck time clocked in
                    UI.getTime();

                    secCount = 0;

                }

                lastUpdate = System.nanoTime(); // Reset timer
            }

            try {

                Thread.sleep(1); // Busy-waiting, may be removed in the future

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
