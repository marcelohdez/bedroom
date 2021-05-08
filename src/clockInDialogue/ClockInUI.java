import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.time.LocalTime;

public class ClockInUI extends JPanel implements ActionListener {

    private String bttn;
    private JLabel text = new JLabel(" When will/did you clock in?");
    private JButton select = new JButton("Select");
    private String[] amPMOptions = {"AM","PM"}, hr = {"01:", "02:", "03:", "04:", "05:", "06:",
                                                    "07:", "08:", "09:", "10:", "11:", "12:"},
                                                min = {"00", "01", "02", "03", "04", "05",
                                                    "06","07", "08", "09", "10", "11", 
                                                    "12", "13", "14", "15", "16", "17", 
                                                    "18", "19", "20", "21", "22", "23", 
                                                    "24", "25", "26", "27", "28", "29", 
                                                    "30", "31", "32", "33", "34", "35", 
                                                    "36", "37", "38", "39", "40", "41", 
                                                    "42", "43", "44", "45", "46", "47", 
                                                    "48", "49", "50", "51", "52", "53", 
                                                    "54", "55", "56", "57", "58", "59"};
                                                    
    private JComboBox<String> amPM = new JComboBox<>(amPMOptions), hrBox = new JComboBox<>(hr),
            minBox = new JComboBox<>(min);

    public ClockInUI() {

        setFocusable(true);
        setLayout(new BorderLayout());

        select.addActionListener(this);
        text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        add(text, BorderLayout.NORTH);
        add(select, BorderLayout.SOUTH);
        add(hrBox, BorderLayout.WEST);
        add(minBox, BorderLayout.CENTER);
        add(amPM, BorderLayout.EAST);

        requestFocus();

    }

    public void actionPerformed(ActionEvent e) {

        bttn = e.getActionCommand();

        if (bttn == "Select") {

            int inHour = hrBox.getSelectedIndex() + 1;
            int inMin = minBox.getSelectedIndex() + 1;
            int realInHr = 0;

            // ======= Translate time into 24-hour clock =======
            if (amPM.getSelectedIndex() == 0) { // AM is selected

                if (inHour == 12) { realInHr = 0; 
                } else realInHr = inHour;

            } else { // PM is selected

                if (inHour == 12) { realInHr = inHour;
                } else realInHr = inHour + 12;

            }

            // Make sure time is in format "00:00" so single digits get a 0 added
            String hrString = "" + realInHr;
            if (realInHr < 10) hrString = "0" + realInHr;
            String minString = ":" + inMin;
            if (inMin < 10) minString = ":0" + inMin;

            UI.clockInTime = LocalTime.parse(hrString + minString);

            UI.getTime();
            Window.timesChosen = true;

        }

    }

}