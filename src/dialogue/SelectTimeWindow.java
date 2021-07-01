package dialogue;

import garage.*;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class SelectTimeWindow extends JFrame implements WindowListener {

    private final SelectTimeUI ui;

    public SelectTimeWindow(Main.TIME_WINDOW type) {

        // Set window size
        setSize(new Dimension(260, 150)); // Default window size
        if (Main.isOSX) setSize(new Dimension(new Dimension(260, 140)));
                                                // Default window size on MacOS (smaller due to
                                                // the title bar & fonts on Mac being different)

        // Initial properties
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        ui = new SelectTimeUI(type);            // Create ui based on window type
        add(ui);                                // Add the UI

        // Set window title per window type
        switch (type) {
            case CLOCK_OUT_WINDOW -> { // Also resize it for the bigger clock out window:
                setTitle("Clocking out:");
                setSize(new Dimension(260, 185)); // Specific sizing for this window
                if (Main.isOSX) setSize(new Dimension(260, 175)); // MacOS version
            }
            case START_BREAK_WINDOW -> setTitle("Enter break:");
            case END_BREAK_WINDOW -> setTitle("Leave break:");
            case CLOCK_IN_WINDOW -> setTitle("Clocking in:");
        }

        if (type.equals(Main.TIME_WINDOW.CLOCK_IN_WINDOW))
            setVisible(true); // Automatically show clock in window

    }

    public void setUITime(SelectTimeUI.GET_TIME type) { // Pass through to UI
        // Set to current time, for windows opened up after program start up like break start window
        ui.setListBoxIndexes(type);
    }

    // Cancel setting break times if user tries to close window
    @Override
    public void windowClosing(WindowEvent e) {

        if ( Main.enterBreakWnd.isVisible()) { // If setting break start time:

            Main.enterBreakWnd.dispose();   // Close the window

        } else if (Main.leaveBreakWnd.isVisible()) { // If setting break end time:

            UI.breakInTime = null;          // Delete break start time that was set
            Main.leaveBreakWnd.dispose();   // Close the window

        }

    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

}
