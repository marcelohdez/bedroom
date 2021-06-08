import java.awt.Dimension;

import javax.swing.JFrame;

public class SelectTimeWindow extends JFrame {

    public SelectTimeWindow(int type) {

        setSize(new Dimension(260, 150)); // Default window size
        if (Window.isOSX) setSize(new Dimension(new Dimension(260, 140)));
                                                    // Default window size on MacOS (smaller due to
                                                    // the title bar & fonts on Mac being different)

        switch (type) {
            case 1 -> { // ======= For clock out window =======

                SelectTimeUI clockOutUI = new SelectTimeUI(1);
                setTitle("Clocking out:");
                add(clockOutUI);
                setSize(new Dimension(260, 210)); // Specific sizing for this window
                if (Window.isOSX) setSize(new Dimension(260, 200)); // MacOS version

            }
            case 2 -> { // ======= For enter break window =======

                SelectTimeUI enterBreakUI = new SelectTimeUI(2);
                setTitle("Enter break:");
                add(enterBreakUI);

            }
            case 3 -> { // ======= For leave break window =======

                SelectTimeUI leaveBreakUI = new SelectTimeUI(3);
                setTitle("Leave break:");
                add(leaveBreakUI);

            }
            default -> { // ======= For clock in window =======

                SelectTimeUI clockInUI = new SelectTimeUI(0);
                setTitle("Clocking in:");
                add(clockInUI);
                setVisible(true);

            }
        }

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

    }
    
}
