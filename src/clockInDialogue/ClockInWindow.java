import java.awt.Dimension;

import javax.swing.JFrame;

public class ClockInWindow extends JFrame {
    
    public ClockInWindow() {

        setTitle("When will you clock in?");
        setMinimumSize(new Dimension(320, 160));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);

    }
    
}
