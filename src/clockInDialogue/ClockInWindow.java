import java.awt.Dimension;

import javax.swing.JFrame;

public class ClockInWindow extends JFrame {
    
    public ClockInWindow() {

        ClockInUI cui = new ClockInUI();

        setTitle("Select times");
        setMinimumSize(new Dimension(320, 130));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        add(cui);

        setVisible(true);
        pack();

    }
    
}
