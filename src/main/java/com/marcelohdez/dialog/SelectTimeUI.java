package com.marcelohdez.dialog;

import com.marcelohdez.bedroom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.Objects;

public class SelectTimeUI extends JPanel implements ActionListener {

    public enum GET_TIME { // What time to get and set to list boxes:
        CURRENT,                // Current time
        BREAK_START_PLUS_30M,   // Get 30 minutes after break start
        CLOCK_IN_PLUS_4H        // Get 4 hours after clock in
    }

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

    public SelectTimeUI(Main.TIME_WINDOW type) {

        setBackground(UI.bg); // Set background color
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setListBoxIndexes(GET_TIME.CURRENT); // Set to current time

        JPanel labelRow = new JPanel(), // Create content rows
                timeBoxesRow = new JPanel(), selectRow = new JPanel();

        JButton select = new JButton("Select");                     // Select button
        Dimension listBoxSize = new Dimension(80, 30);      // List box size
        Dimension smallListBoxSize = new Dimension(65, 30); // Skip and am/pm components
        JLabel topText = new JLabel();                                  // Top text

        switch (type) { // Change top text depending on window type
            case CLOCK_OUT_TYPE -> topText.setText("Select CLOCK OUT time:");
            case START_BREAK_TYPE -> topText.setText("Select BREAK START time:");
            case END_BREAK_TYPE -> topText.setText("Select BREAK END time:");
            case CLOCK_IN_TYPE -> topText.setText("  Select CLOCK IN time:  ");
        }

        // Set top text font
        topText.setFont(UI.boldText);
        // Set component sizes and action listeners (for clicks)
        select.addActionListener(this);
        select.setPreferredSize(new Dimension(235, 40));
        hrBox.setPreferredSize(listBoxSize);
        minBox.setPreferredSize(listBoxSize);
        amPMBox.setPreferredSize(smallListBoxSize);
        setTarget.setPreferredSize(smallListBoxSize);
        setTarget.setSelectedIndex(8); // Set default to 9 (what i need @ my job, so a lil easter egg)

        // ======= Set colors =======
        labelRow.setBackground(UI.bg);
        topText.setForeground(UI.textColor);
        select.setBackground(UI.buttonColor);
        select.setForeground(UI.textColor);
        hrBox.setBackground(UI.buttonColor);
        hrBox.setForeground(UI.textColor);
        minBox.setBackground(UI.buttonColor);
        minBox.setForeground(UI.textColor);
        amPMBox.setBackground(UI.buttonColor);
        amPMBox.setForeground(UI.textColor);
        setTarget.setBackground(UI.buttonColor);
        setTarget.setForeground(UI.textColor);
        selectRow.setBackground(UI.bg);
        timeBoxesRow.setBackground(UI.bg);

        // Add components to their rows
        labelRow.add(topText);
        timeBoxesRow.add(hrBox);
        timeBoxesRow.add(minBox);
        timeBoxesRow.add(amPMBox);
        selectRow.add(select);

        // Add rows to UI
        add(labelRow);
        add(timeBoxesRow);
        if (type.equals(Main.TIME_WINDOW.CLOCK_OUT_TYPE)) add(createTargetRow());
        add(selectRow);

        requestFocus();

    }

    public void setListBoxIndexes(GET_TIME type) { // Set time list boxes:
        // ======= Set list box times to current/clock out time =======
        int hour = LocalTime.now().getHour(); // Store hour to not be rechecked
        int minute; // Store minute

        switch (type) { // Set minBox depending on type and get wanted hour int
            // Case 0 is to get current time, for hour it is already stored above
            case CURRENT -> // Set minBox to current minute
                    minBox.setSelectedIndex(LocalTime.now().getMinute());
            case CLOCK_IN_PLUS_4H -> { // Get 4hrs after clock in time, for clock out window
                hour = UI.clockInTime.getHour() + 4;    // Add 4 to clock in time's hours
                if (hour >= 24) hour -= 24;             // If it's over 24 now, loop it
                minBox.setSelectedIndex(UI.clockInTime.getMinute()); // Set minBox to clock in time's minute
            }
            case BREAK_START_PLUS_30M -> { // Set leave break window's default minutes to 30 above break in time.
                minute = UI.breakInTime.getMinute() + 30; // +30 minutes after break start
                hour = UI.breakInTime.getHour();        // Get break start time's hour
                if (minute > 59) {                      // If it is over 59, loop it
                    minute -= 60;
                    hour = UI.breakInTime.getHour() + 1; // Add an hour since it went over 59 minutes
                }
                minBox.setSelectedIndex(minute);        // Set minBox's index to the minute value now
            }
        }

        setListBoxesByHour(hour);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Select")) { // Setting time to selected values

                LocalTime newTime =
                        LocalTime.parse(makeTime24Hour(hrBox.getSelectedIndex() + 1,
                        minBox.getSelectedIndex(),
                        Objects.requireNonNull(amPMBox.getSelectedItem()).toString()));

                if (Main.clockInWnd.isVisible()) { // ======= For clock in time=======

                    UI.clockInTime = newTime; // Set clock in time
                    closeAndProceed(Main.clockInWnd, Main.clockOutWnd, GET_TIME.CLOCK_IN_PLUS_4H);

                } else if (Main.clockOutWnd.isVisible()) { // ======= For clock out time =======

                    if (newTime.isAfter(UI.clockInTime)) {

                        UI.clockOutTime = newTime; // Set clock out time
                        UI.target = setTarget.getSelectedIndex() + 1; // Set to the list box selection
                        Main.timesChosen = true;               // Clock out time is now chosen
                        Main.clockOutWnd.dispose();         // Close clock out time window

                    } else new ErrorWindow(Main.ERROR.NEGATIVE_SHIFT_TIME);

                } else if (Main.enterBreakWnd.isVisible()) { // ======= For entering break =======

                    if ((newTime.isAfter(UI.clockInTime)) && newTime.isBefore(UI.clockOutTime) ||
                            newTime.equals(UI.clockInTime)) {

                        UI.breakInTime = newTime; // Set enter break time
                        closeAndProceed(Main.enterBreakWnd, Main.leaveBreakWnd,
                                GET_TIME.BREAK_START_PLUS_30M);

                    } else new ErrorWindow(Main.ERROR.BREAK_OUT_OF_SHIFT);

                } else if (Main.leaveBreakWnd.isVisible()) { // ======= For leaving break =======

                    if (newTime.isAfter(UI.breakInTime) && newTime.isBefore(UI.clockOutTime) ||
                            newTime.equals(UI.clockOutTime)) {

                        UI.breakOutTime = newTime; // Set leave break time
                        UI.breakTimesChosen = true;
                        Main.leaveBreakWnd.dispose();       // Close leave break window

                    } else new ErrorWindow(Main.ERROR.NEGATIVE_BREAK_TIME);

                }

                if (Main.timesChosen) UI.getTime();

        }

    }

    private String makeTime24Hour(int oldHr, int min, String amPM) {

        StringBuilder sb = new StringBuilder();
        int newHr = 0;

        // Convert hour to 24-hours from 12
        switch (amPM) {
            case "AM" -> {
                if (oldHr != 12) newHr = oldHr;
            }
            case "PM" -> {
                newHr = oldHr + 12;
                if (oldHr == 12) newHr = oldHr;
            }
        }

        // Make sure time is in format "00:00" so single digits get a 0 added
        if (newHr < 10) sb.append("0");
        sb.append(newHr).append(":");
        if (min < 10) sb.append("0");
        sb.append(min);

        return sb.toString();

    }

    private void closeAndProceed(SelectTimeWindow windowToClose,
                                   SelectTimeWindow windowToShow, GET_TIME newWindowType) {

        windowToClose.dispose();
        windowToShow.centerOnMainWindow();
        windowToShow.setUITime(newWindowType);
        windowToShow.setVisible(true);

    }

    private void setListBoxesByHour(int hour) {

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

    private JPanel createTargetRow() {

        JPanel targetRow = new JPanel(); // Create the target text/list box panel
        JLabel targetText = new JLabel(); // Create target label

        targetRow.setBackground(UI.bg);

        targetText.setPreferredSize(new Dimension(165, 25));
        targetText.setForeground(UI.textColor);
        targetText.setText("  Please set an hourly target:"); // Due to diff mac font, set diff text:
        if (Main.isOSX) targetText.setText(" Select your hourly target:");

        // Add the specific stuffs
        targetRow.add(targetText);
        targetRow.add(setTarget);

        return targetRow; // Return the new panel

    }

}