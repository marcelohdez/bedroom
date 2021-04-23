package window;

import java.awt.Dimension;
import java.lang.Runnable;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable {

    private final String version = "1.1";
    private boolean running;
    private long lastUpdate = System.nanoTime();

    public Window() {

        setTitle("Garage " + version);
        setMinimumSize(new Dimension(400, 80));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);

    }

    public static void main(String[] args) {

        Window wnd = new Window();
        UI ui = new UI();

        wnd.add(ui);
        wnd.start();
        wnd.pack();
        ui.requestFocus();

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

            if (System.nanoTime() - lastUpdate >= 10e8 && UI.clockedIn) {

                UI.tick(true);
                lastUpdate = System.nanoTime();

            }

            try { 

                Thread.sleep(1);

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
