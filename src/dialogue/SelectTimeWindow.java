import java.awt.Dimension;

import javax.swing.JFrame;

public class SelectTimeWindow extends JFrame {

    public SelectTimeWindow(int type) {
        Dimension smallWindow = new Dimension(260, 150), // Small window size
            smallWindowMac = new Dimension(new Dimension(260, 140)); // On MacOS

        switch (type) {
            case 1 -> { // ======= Clock out window =======


                SelectTimeUI clockOutUI = new SelectTimeUI(1);
                setTitle("Clocking out:");
                add(clockOutUI);
                setSize(new Dimension(260, 180));
                if (Window.isOSX) setSize(new Dimension(260, 170)); // Fix size on MacOS

            }
            case 2 -> { // ======= Enter break window =======

                SelectTimeUI enterBreakUI = new SelectTimeUI(2);
                setTitle("Enter break:");
                add(enterBreakUI);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS

            }
            case 3 -> { // ======= Leave break window =======

                SelectTimeUI leaveBreakUI = new SelectTimeUI(3);
                setTitle("Leave break:");
                add(leaveBreakUI);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS

            }
            default -> { // ======= Clock in window =======

                SelectTimeUI clockInUI = new SelectTimeUI(0);
                setTitle("Clocking in:");
                add(clockInUI);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS
                setVisible(true);

            }
        }

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

    }
    
}
