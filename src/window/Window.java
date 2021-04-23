package window;

import java.awt.Dimension;
import java.lang.Runnable;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable {

    private String version = "1.0";
    private boolean running;
    private long lastUpdate = System.nanoTime();

    public Window() {

        setTitle("Garage " + version);
        setMinimumSize(new Dimension(380, 75));
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

                //this.requestFocus();

            }

            try { 

                Thread.sleep(1);

            } catch (InterruptedException e) { e.printStackTrace(); }

        }

    }

}
