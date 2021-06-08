import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener {

    // Lists (for the list boxes)
    private final String[] amPMOptions = {"AM","PM"},
            hr = {"01:", "02:", "03:", "04:", "05:", "06:", "07:", "08:", "09:", "10:", "11:", "12:"},
            min = {"00", "01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
                    "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                    "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"},
            targets = {"01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24"};

    // List boxes:
    private final JComboBox<String> amPMBox = new JComboBox<>(amPMOptions), hrBox = new JComboBox<>(hr),
            minBox = new JComboBox<>(min), setTarget = new JComboBox<>(targets);

    public SelectTimeUI(int type) {

        JButton select = new JButton("Select");                 // Select button
        JButton skip = new JButton("Skip");                     // Skip button
        JLabel targetText = new JLabel("Target:");              // Target label
        JLabel ordersPerHrText = new JLabel("orders per hour"); // "Order per hour"
        Dimension listSize = new Dimension(80, 30);     // List box size

        setBackground(UI.bg);

        switch (type) {
            case 1 -> { // ======= Clock out UI =======

                JLabel coText = new JLabel("  Select clock out time:  ");
                coText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                coText.setForeground(UI.textColor);
                add(coText);

            }
            case 2 -> { // ======= Enter break UI =======

                JLabel ebText = new JLabel("  Select enter break time:  ");
                ebText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                ebText.setForeground(UI.textColor);
                add(ebText);

            }
            case 3 -> { // ======= Leave break UI =======

                JLabel lbText = new JLabel("  Select leave break time:  ");
                lbText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                lbText.setForeground(UI.textColor);
                add(lbText);

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
        skip.addActionListener(this);
        hrBox.setPreferredSize(listSize);
        minBox.setPreferredSize(listSize);
        amPMBox.setPreferredSize(new Dimension(68, 30));
        amPMBox.setSelectedIndex(1); // Default to PM
        setTarget.setPreferredSize(new Dimension(45, 25));
        setTarget.setSelectedIndex(8); // Set default to 9 (what i need @ my job, so a lil easter egg)

        // ======= Set colors =======
        select.setBackground(UI.buttonColor);
        select.setForeground(UI.textColor);
        skip.setBackground(UI.buttonColor);
        skip.setForeground(UI.textColor);
        hrBox.setBackground(UI.buttonColor);
        hrBox.setForeground(UI.textColor);
        minBox.setBackground(UI.buttonColor);
        minBox.setForeground(UI.textColor);
        amPMBox.setBackground(UI.buttonColor);
        amPMBox.setForeground(UI.textColor);
        setTarget.setBackground(UI.buttonColor);
        setTarget.setForeground(UI.textColor);
        targetText.setForeground(UI.textColor);
        ordersPerHrText.setForeground(UI.textColor);

        // Add components in order for flow layout
        add(hrBox);
        add(minBox);
        add(amPMBox);
        if (type == 1) { // Clock out time UI specific components
            add(targetText);
            add(setTarget);
            add(ordersPerHrText);
            add (skip);
        } else { // ======= Set list box times to current time =======
            int currentHour = LocalTime.now().getHour(); // Store hour to not be rechecked

            if (currentHour >= 12) {
                amPMBox.setSelectedIndex(1);            // Set AM/PM list box to PM
                // This if statement sets the list box index to current hour,
                // since LocalTime is in 24hr format, we have to do some maths
                // to get it to 12hr am/pm.
                if (currentHour != 12) {                // Set hour to 1-11pm
                    hrBox.setSelectedIndex(currentHour - 13);
                } else hrBox.setSelectedIndex(11);      // Set hour to 12pm
            } else {
                amPMBox.setSelectedIndex(0);            // Set AM/PM list box to AM
                if (currentHour != 0) {                 // Set hour to 1-11am
                    hrBox.setSelectedIndex(currentHour - 1);
                } else hrBox.setSelectedIndex(11);      // Set hour to 12am (or 0 in 24hr)
            }

            // Set minute list box index to current minute count
            minBox.setSelectedIndex(LocalTime.now().getMinute());
        }
        add(select);



        requestFocus();

    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Select" -> {  // Setting time to selected values

                int hour = hrBox.getSelectedIndex() + 1;
                int min = minBox.getSelectedIndex();
                int realHr = 0;

                // ======= Translate time into 24-hour clock for LocalTime =======
                if (amPMBox.getSelectedIndex() == 0) { // AM is selected
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
                if (!Window.ciChosen) { // ======= For clock in UI =======

                    UI.clockInTime = LocalTime.parse(hrString + minString); // Set clock in time
                    Window.ciChosen = true;                 // Clock in time is now chosen
                    Window.clockInWnd.dispose();            // Get rid of clock-in window
                    Window.clockOutWnd.setVisible(true);    // Set clock-out window visible

                } else if (!Window.coChosen) { // ======= For clock out UI =======

                    UI.clockOutTime = LocalTime.parse(hrString + minString); // Set clock out time
                    UI.target = setTarget.getSelectedIndex() + 1; // Set to the list box selection

                    UI.getTime();                           // Tell UI to update times
                    Window.coChosen = true;                 // Clock out time is now chosen
                    Window.clockOutWnd.dispose();           // Close clock out time window

                } else if (!UI.inBreak) { // ======= For enter break UI =======

                    UI.breakInTime = LocalTime.parse(hrString + minString); // Set enter break time
                    Window.enterBreakWnd.dispose();         // Close enter break window
                    UI.getTime();

                } else { // ======= For leave break UI =======

                    UI.breakOutTime = LocalTime.parse(hrString + minString); // Set leave break time
                    Window.leaveBreakWnd.dispose();         // Close leave break window
                    UI.getTime();

                }
            }
            case "Skip" -> { // For skipping clock out time input

                UI.clockOutSkipped = true;                  // Clock out time skipped
                UI.getTime();                               // Tell UI to update times
                Window.coChosen = true;                     // Clock out time is now "chosen"
                Window.clockOutWnd.dispose();               // Close clock out window

            }
        }

    }

}