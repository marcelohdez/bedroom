import java.awt.Dimension;

import javax.swing.JFrame;

public class ClockInWindow extends JFrame {
    
    public ClockInWindow(int type) {

        switch (type) {

            case 1: // ====== Clock out window ======

            ClockInUI outui = new ClockInUI(1);
            setTitle("Clocking out:");
            add(outui);
            setSize(new Dimension(260, 180));
            break;

            default: // ====== Clock in window ======
            
            ClockInUI inui = new ClockInUI(0);
            setTitle("Clocking in:");
            add(inui);
            setSize(new Dimension(260, 150));

        }

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        if (type == 0) setVisible(true); // Set visible after fully loading

    }
    
}
