import javax.swing.*;

public class Window extends JFrame {

    public Window() {

        setTitle("Garage 1.1 (Beta 3)");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new UI());
        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

}
