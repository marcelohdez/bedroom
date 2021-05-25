import java.awt.Dimension;

import javax.swing.JFrame;

public class SelectTimeWindow extends JFrame {

    public SelectTimeWindow(int type) {

        Dimension smallWindow = new Dimension(260, 150), // Small window size
            smallWindowMac = new Dimension(new Dimension(260, 140)); // On MacOS

        switch (type) {
            case 1 -> { // ======= Clock out window =======


                SelectTimeUI outui = new SelectTimeUI(1);
                setTitle("Clocking out:");
                add(outui);
                setSize(new Dimension(260, 180));
                if (Window.isOSX) setSize(new Dimension(260, 170)); // Fix size on MacOS

            }
            case 2 -> { // ======= Enter break window =======

                SelectTimeUI enterui = new SelectTimeUI(2);
                setTitle("Entering break:");
                add(enterui);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS

            }
            case 3 -> { // ======= Leave break window =======

                SelectTimeUI leaveui = new SelectTimeUI(3);
                setTitle("Leaving break:");
                add(leaveui);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS

            }
            default -> { // ======= Clock in window =======

                SelectTimeUI inui = new SelectTimeUI(0);
                setTitle("Clocking in:");
                add(inui);
                setSize(smallWindow);
                if (Window.isOSX) setSize(smallWindowMac); // Fix size on MacOS

            }
        }


        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        if (type == 0) setVisible(true); // Set visible after fully loading

    }
    
}
