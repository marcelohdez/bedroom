import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener {

    // Lists (for the list boxes)
    private final String[] amPMOptions = {"AM","PM"},
            hours = {"01:", "02:", "03:", "04:", "05:", "06:", "07:", "08:", "09:", "10:", "11:", "12:"},
            minutes = {"00", "01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
                    "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                    "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"},
            targets = {"01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
                    "24"};

    // List boxes:
    private final JComboBox<String> amPMBox = new JComboBox<>(amPMOptions), hrBox = new JComboBox<>(hours),
            minBox = new JComboBox<>(minutes), setTarget = new JComboBox<>(targets);

    public SelectTimeUI(int type) {

        setBackground(UI.bg); // Set background color
        JButton select = new JButton("Select");                     // Select button
        JButton skip = new JButton("Skip");                         // Skip button
        JLabel targetText = new JLabel("Target orders per hour:");  // Target label
        JLabel ordersPerHrText = new JLabel("orders per hour");     // "Order per hour"
        Dimension listSize = new Dimension(80, 30);         // List box size
        JLabel topText = new JLabel();                                  // Top text

        switch (type) { // Change top text depending on window
            case 1 -> // ======= Clock out UI =======
                topText.setText("  Select CLOCK OUT time:  ");
            case 2 -> // ======= Enter break UI =======
                topText.setText("  Select ENTER BREAK time:  ");
            case 3 -> // ======= Leave break UI =======
                topText.setText("  Select LEAVE BREAK time:  ");
            default -> { // ======= Clock in UI =======
                topText.setText("  Select CLOCK IN time:  ");
                if (Main.isOSX) topText.setText("    Select CLOCK IN time:    ");
            }
        }

        // Set top text font
        topText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        // Set component sizes and action listeners (for clicks)
        select.addActionListener(this);
        select.setPreferredSize(new Dimension(235, 40));
        skip.addActionListener(this);
        hrBox.setPreferredSize(listSize);
        minBox.setPreferredSize(listSize);
        amPMBox.setPreferredSize(new Dimension(65, 30));
        amPMBox.setSelectedIndex(1); // Default to PM
        setTarget.setPreferredSize(new Dimension(45, 25));
        setTarget.setSelectedIndex(8); // Set default to 9 (what i need @ my job, so a lil easter egg)

        // ======= Set colors =======
        topText.setForeground(UI.textColor);
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
        add(topText);
        add(hrBox);
        add(minBox);
        add(amPMBox);
        switch (type) { // Do UI type specific stuffs
            case 1 -> { // For clock out UI
                add(targetText);
                add(setTarget);
                add(skip);
                setListBoxIndexes(1); // Set list box indexes to 4hrs after clock in time
            }
            case 3 -> setListBoxIndexes(2); // Set to 30 minutes after break start (Leave break UI)
            default -> setListBoxIndexes(0); // Set to current time
        }
        add(select);

        requestFocus();

    }

    public void setListBoxIndexes(int type) {
        // ======= Set list box times to current/clock out time =======
        int hour = LocalTime.now().getHour(); // Store hour to not be rechecked
        int minute; // Store minute

        switch (type) { // Set minBox depending on type and get wanted hour int
            // Case 0 is to get current time, for hour it is already stored above
            case 0 -> minBox.setSelectedIndex(LocalTime.now().getMinute()); // Set minBox to current minute
            case 1 -> { // Get 4hrs after clock in time, for clock out window
                hour = UI.clockInTime.getHour() + 4;    // Add 4 to clock in time's hours
                if (hour >= 24) hour -= 24;             // If it's over 24 now, loop it
                minBox.setSelectedIndex(UI.clockInTime.getMinute()); // Set minBox to clock in time's minute
            }
            case 2 -> { // Set leave break window's default minutes to 30 above break in time.
                minute = UI.breakInTime.getMinute() + 30; // +30 minutes after break start
                if (minute > 59) {                      // If it is over 59, loop it
                    minute -= 60;
                    hour = UI.breakInTime.getHour() + 1; // Add an hour since it went over 59 minutes
                }
                minBox.setSelectedIndex(minute);        // Set minBox's index to the minute value now
            }
        }

        if (hour >= 12) {
            amPMBox.setSelectedIndex(1);            // Set AM/PM list box to PM
            // This if statement sets the list box index to current hour,
            // since LocalTime is in 24hr format, we have to do some maths
            // to get it to 12hr am/pm.
            if (hour != 12) {                       // Set hour to 1-11pm
                hrBox.setSelectedIndex(hour - 13);
            } else hrBox.setSelectedIndex(11);      // Set hour to 12pm
        } else {
            amPMBox.setSelectedIndex(0);            // Set AM/PM list box to AM
            if (hour != 0) {                        // Set hour to 1-11am
                hrBox.setSelectedIndex(hour - 1);
            } else hrBox.setSelectedIndex(11);      // Set hour to 12am (or 0 in 24hr)
        }

    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Select" -> {  // Setting time to selected values

                int hour = hrBox.getSelectedIndex() + 1;
                int min = minBox.getSelectedIndex();
                int realHr = 0;

                // ======= Translate time into 24-hour clock for LocalTime =======
                if (amPMBox.getSelectedIndex() == 0) {  // if AM is selected
                    if (hour != 12) realHr = hour;
                } else {                                // if PM is selected
                    realHr = hour + 12;
                    if (hour == 12) realHr = hour;
                }

                // Make sure time is in format "00:00" so single digits get a 0 added
                String hrString = "" + realHr;
                if (realHr < 10) hrString = "0" + realHr;
                String minString = ":" + min;
                if (min < 10) minString = ":0" + min;
                if (!Main.ciChosen) { // ======= For clock in time=======

                    UI.clockInTime = LocalTime.parse(hrString + minString); // Set clock in time
                    Main.ciChosen = true;                 // Clock in time is now chosen
                    Main.clockInWnd.dispose();            // Get rid of clock-in window
                    Main.clockOutWnd = new SelectTimeWindow(1); // Create clock out window

                } else if (!Main.coChosen) { // ======= For clock out time =======

                    UI.clockOutTime = LocalTime.parse(hrString + minString); // Set clock out time
                    UI.target = setTarget.getSelectedIndex() + 1; // Set to the list box selection

                    UI.getTime();                           // Tell UI to update times
                    Main.coChosen = true;                 // Clock out time is now chosen
                    Main.clockOutWnd.dispose();           // Close clock out time window

                } else if (Main.enterBreakWnd.isVisible()) { // ======= For entering break =======

                    UI.breakInTime = LocalTime.parse(hrString + minString); // Set enter break time
                    Main.enterBreakWnd.dispose();         // Close enter break window
                    Main.leaveBreakWnd = new SelectTimeWindow(3);
                    UI.getTime();

                } else { // ======= For leaving break =======

                    UI.breakOutTime = LocalTime.parse(hrString + minString); // Set leave break time
                    UI.breakTimeChosen = true;
                    Main.leaveBreakWnd.dispose();         // Close leave break window
                    UI.getTime();

                }
            }
            case "Skip" -> { // For skipping clock out time input

                UI.clockOutSkipped = true;                  // Clock out time skipped
                UI.getTime();                               // Tell UI to update times
                Main.coChosen = true;                     // Clock out time is now "chosen"
                Main.clockOutWnd.dispose();               // Close clock out window

            }
        }

    }

}