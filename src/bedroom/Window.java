package bedroom;

import javax.swing.*;

public class Window extends JFrame {

    public Window() {

        setTitle("Bedroom " + Main.version);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new UI());
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

}
