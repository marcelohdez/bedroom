import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.time.LocalTime;

public class ClockInUI extends JPanel implements ActionListener {

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
    private static int windowType;

    public ClockInUI(int type) {

        Dimension listSize = new Dimension(80, 30);
        Dimension ampmSize = new Dimension(68, 30);
        Dimension selectSize = new Dimension(180, 40);

        switch (type) {

            case 1: // ======= Clock out UI =======

            JLabel coText = new JLabel("  Select clock out time:  ");
            coText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

            add(coText);
            break;

            default: // ======= Clock in UI =======

            JLabel ciText = new JLabel("  Select clock in time:  ");
            ciText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

            add(ciText);

        }

        select.addActionListener(this);
        select.setPreferredSize(selectSize);
        hrBox.setPreferredSize(listSize);
        minBox.setPreferredSize(listSize);
        amPM.setPreferredSize(ampmSize);

        add(hrBox);
        add(minBox);
        add(amPM);
        add(select);

        windowType = type;
        requestFocus();

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "Select") {

            int hour = hrBox.getSelectedIndex() + 1;
            int min = minBox.getSelectedIndex() + 1;
            int realHr = 0;

            if (windowType == 0) { // ======= Clock in UI type =======

                // ======= Translate time into 24-hour clock for LocalTime =======
                if (amPM.getSelectedIndex() == 0) { // AM is selected

                    if (hour == 12) { realHr = 0; 
                    } else realHr = hour;

                } else { // PM is selected

                    if (hour == 12) { realHr = hour;
                    } else realHr = hour + 12;

                }

                // Make sure time is in format "00:00" so single digits get a 0 added
                String hrString = "" + realHr;
                if (realHr < 10) hrString = "0" + realHr;
                String minString = ":" + min;
                if (min < 10) minString = ":0" + min;

                UI.clockInTime = LocalTime.parse(hrString + minString);

                UI.getTime();
                Window.timesChosen = true;

            } else { // ======= Clock out UI type =======

                // ======= Translate time into 24-hour clock for LocalTime =======
                if (amPM.getSelectedIndex() == 0) { // AM is selected

                    if (hour == 12) { realHr = 0; 
                    } else realHr = hour;

                } else { // PM is selected

                    if (hour == 12) { realHr = hour;
                    } else realHr = hour + 12;

                }

                // Make sure time is in format "00:00" so single digits get a 0 added
                String hrString = "" + realHr;
                if (realHr < 10) hrString = "0" + realHr;
                String minString = ":" + min;
                if (min < 10) minString = ":0" + min;

                UI.clockOutTime = LocalTime.parse(hrString + minString);

                UI.getTime();
                Window.timesChosen = true;

            }

        }

    }

}