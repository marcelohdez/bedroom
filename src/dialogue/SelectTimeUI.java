import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener {

    // Lists (for the list boxes)
    private final String[] amPMOptions = {"AM","PM"},
            hr = {"01:", "02:", "03:", "04:", "05:", "06:", "07:", "08:", "09:", "10:", "11:", "12:"},
            min = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
                    "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                    "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"},
            targets = {"01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24"};

    // List boxes:
    private final JComboBox<String> amPM = new JComboBox<>(amPMOptions), hrBox = new JComboBox<>(hr),
            minBox = new JComboBox<>(min), setTarget = new JComboBox<>(targets);

    public SelectTimeUI(int type) {

        JButton select = new JButton("Select"); // Select button
        JLabel targetText = new JLabel("Target:"); // Target label
        JLabel ordersPerHrText = new JLabel("orders per hour"); // "Order per hour"
        Dimension listSize = new Dimension(80, 30);

        setBackground(UI.bg);

        switch (type) { // ======= Clock out UI =======

            case 1 -> {

                JLabel coText = new JLabel("  Select clock out time:  ");
                coText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                coText.setForeground(UI.textColor);
                add(coText);

            }
            case 2 -> { // ======= Enter break UI =======

                JLabel ebText = new JLabel("  Select break start time:  ");
                ebText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                ebText.setForeground(UI.textColor);
                add(ebText);
                setVisible(true);

            }
            case 3 -> { // ======= Leave break UI =======

                JLabel lbText = new JLabel("  Select break end time:  ");
                lbText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                lbText.setForeground(UI.textColor);
                add(lbText);
                setVisible(true);

            }
            default -> { // ======= Clock in UI =======

                JLabel ciText = new JLabel("  Select clock in time:  ");
                if (Window.isOSX) ciText.setText("    Select clock in time:    ");
                ciText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                ciText.setForeground(UI.textColor);
                add(ciText);

            }
        }

        // Set component sizes and action listeners (for clicks)
        select.addActionListener(this);
        select.setPreferredSize(new Dimension(180, 40));
        hrBox.setPreferredSize(listSize);
        minBox.setPreferredSize(listSize);
        amPM.setPreferredSize(new Dimension(68, 30));
        amPM.setSelectedIndex(1); // Default to PM
        setTarget.setPreferredSize(new Dimension(45, 25));
        setTarget.setSelectedIndex(8); // Set default to 9 (what i need @ my job, so a lil easter egg)

        // ======= Set colors =======
        select.setBackground(UI.buttonColor);
        select.setForeground(UI.textColor);
        hrBox.setBackground(UI.buttonColor);
        hrBox.setForeground(UI.textColor);
        minBox.setBackground(UI.buttonColor);
        minBox.setForeground(UI.textColor);
        amPM.setBackground(UI.buttonColor);
        amPM.setForeground(UI.textColor);
        setTarget.setBackground(UI.buttonColor);
        setTarget.setForeground(UI.textColor);
        targetText.setForeground(UI.textColor);
        ordersPerHrText.setForeground(UI.textColor);

        // Add components in order
        add(hrBox);
        add(minBox);
        add(amPM);
        if (type == 1) { // Add "set target orders" to clock out window

            add(targetText);
            add(setTarget);
            add(ordersPerHrText);

        }
        add(select);

        requestFocus();

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Select")) { // When time is selected:

            int hour = hrBox.getSelectedIndex() + 1;
            int min = minBox.getSelectedIndex();
            int realHr = 0;

            // ======= Translate time into 24-hour clock for LocalTime =======
            if (amPM.getSelectedIndex() == 0) { // AM is selected
                if (hour != 12) realHr = hour;
            } else { // PM is selected
                realHr = hour + 12;
                if (hour == 12) realHr = hour;
            }

            // Make sure time is in format "00:00" so single digits get a 0 added
            String hrString = "" + realHr;
            if (realHr < 10) hrString = "0" + realHr;
            String minString = ":" + min;
            if (min < 10) minString = ":0" + min;

            if (!Window.ciChosen) { // ======= Clock in UI type =======

                UI.clockInTime = LocalTime.parse(hrString + minString);
                Window.ciChosen = true;

                // Close clock-in window and show clock-out window
                Window.clockInWnd.dispose();
                Window.clockOutWnd.setVisible(true);

            } else if (!Window.coChosen) { // ======= Clock out UI type =======

                UI.clockOutTime = LocalTime.parse(hrString + minString);
                UI.target = setTarget.getSelectedIndex() + 1;

                UI.getTime();
                Window.coChosen = true;

                // Close clock-in window
                Window.clockOutWnd.dispose();

            } else if (!UI.inBreak) { // ======= Enter break UI type =======

                UI.breakInTime = LocalTime.parse(hrString + minString);
                Window.enterBreakWnd.dispose();
                UI.getTime();

            } else { // ======= Leave break UI type =======

                UI.breakOutTime = LocalTime.parse(hrString + minString);
                Window.leaveBreakWnd.dispose();
                UI.getTime();

            }



        }

    }

}