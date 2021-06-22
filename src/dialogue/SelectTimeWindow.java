package dialogue;

import garage.*;
import java.awt.Dimension;
import javax.swing.JFrame;

public class SelectTimeWindow extends JFrame {

    private final SelectTimeUI ui;

    public SelectTimeWindow(Main.TIME_WINDOW type) {

        setSize(new Dimension(260, 150)); // Default window size
        if (Main.isOSX) setSize(new Dimension(new Dimension(260, 140)));
                                                    // Default window size on MacOS (smaller due to
                                                    // the title bar & fonts on Mac being different)

        ui = new SelectTimeUI(type);                // Create ui based on window type
        add(ui);                                    // Add the UI

        switch (type) {
            case CLOCK_OUT_WINDOW -> { // ======= For clock out window =======
                setTitle("Clocking out:");
                setSize(new Dimension(260, 185)); // Specific sizing for this window
                if (Main.isOSX) setSize(new Dimension(260, 175)); // MacOS version
            }
            case START_BREAK_WINDOW -> // ======= For start break window =======
                    setTitle("Enter break:");
            case END_BREAK_WINDOW -> // ======= For leave break window =======
                    setTitle("Leave break:");
            case CLOCK_IN_WINDOW -> // ======= For clock in window =======
                setTitle("Clocking in:");
        }

        if (!type.equals(Main.TIME_WINDOW.START_BREAK_WINDOW))
            setVisible(true); // Automatically show window except enter break

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    public void setUIToCurrentTime() { // Pass through to UI
        // Set to current time, for windows opened up after program start up like break start window
        ui.setListBoxIndexes(SelectTimeUI.GET_TIME.CURRENT);
    }
    
}
