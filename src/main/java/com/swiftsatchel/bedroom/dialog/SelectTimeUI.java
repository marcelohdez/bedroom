package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.SetTime;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.settings.SettingsDialog;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener, KeyListener {

    private final TimeWindowType type;
    private final SelectTimeWindow parent;

    // ======= List boxes: =======
    private final JComboBox<String> amPMBox = new JComboBox<>(new String[]{"AM", "PM"});
    private final JComboBox<String> hrBox = new JComboBox<>(Ops.createNumberList(1, 12, ":"));
    // Create minutes (0-59) and hourly targets (1-24)
    private final JComboBox<String> minBox = new JComboBox<>(Ops.createNumberList(0, 59, null));
    private final JComboBox<String> setTarget = new JComboBox<>(Ops.createNumberList(1, 24, null));

    // Other components:
    private final JButton select = new JButton("Select");   // Select button
    private final JLabel topText = new JLabel();                 // Top text
    private JLabel targetText;                                  // Select target text

    // Component rows:
    private final JPanel labelRow = new JPanel();
    private final JPanel timeBoxesRow = new JPanel();
    private JPanel setTargetRow = createTargetRow();
    private final JPanel selectRow = new JPanel();

    public SelectTimeUI(SelectTimeWindow parent) {

        type = parent.type;
        this.parent = parent;

        setBackground(UI.bg); // Set background color
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setListBoxIndexes(SetTime.CURRENT); // Set to current time
        addKeyListener(this);

        Dimension listBoxSize = new Dimension(80, 30);
        Dimension smallListBoxSize = new Dimension(65, 30);

        switch (type) { // Change top text depending on window type
            case CLOCK_OUT -> topText.setText("Select CLOCK OUT time:");
            case START_BREAK -> topText.setText("Select BREAK START time:");
            case END_BREAK -> topText.setText("Select BREAK END time:");
            case CLOCK_IN -> topText.setText("  Select CLOCK IN time:  ");
        }

        // Customize components
        topText.setFont(UI.boldText);
        select.addActionListener(this);
        select.setPreferredSize(new Dimension(235, 40));
        hrBox.setPreferredSize(listBoxSize);
        hrBox.addKeyListener(this);
        minBox.setPreferredSize(listBoxSize);
        minBox.addKeyListener(this);
        amPMBox.setPreferredSize(smallListBoxSize);
        amPMBox.addKeyListener(this);
        setTarget.setPreferredSize(smallListBoxSize);
        setTarget.setSelectedIndex(8); // Set default to 9 (what I need @ my job, so a lil Easter egg)
        setTarget.addKeyListener(this);

        // ======= Set colors =======
        colorSelf();

        // Add components to their rows
        labelRow.add(topText);
        timeBoxesRow.add(hrBox);
        timeBoxesRow.add(minBox);
        timeBoxesRow.add(amPMBox);
        selectRow.add(select);

        // Add rows to UI
        add(labelRow);
        add(timeBoxesRow);
        if (type.equals(TimeWindowType.CLOCK_OUT)) add(setTargetRow);
        add(selectRow);

        requestFocus();

    }

    public void setListBoxIndexes(SetTime type) { // Set list boxes to a time:

        int hour = LocalTime.now().getHour(); // Store hour to not be rechecked
        int minute; // Store minute

        switch (type) { // Set minBox depending on type and get wanted hour int
            // Case 0 is to get current time, for hour it is already stored above
            case CURRENT -> // Set minBox to current minute
                    minBox.setSelectedIndex(LocalTime.now().getMinute());
            case CLOCK_IN_PLUS_DEFAULT -> { // Get 4hrs after clock in time, for clock out window
                hour = Main.clockInTime.getHour() + 4;    // Add 4 to clock in time's hours
                if (hour >= 24) hour -= 24;             // If it's over 24 now, loop it
                minBox.setSelectedIndex(Main.clockInTime.getMinute()); // Set minBox to clock in time's minute
            }
            case BREAK_START_PLUS_30M -> { // Set leave break window's default minutes to 30 above break in time.
                minute = Main.breakInTime.getMinute() + 30; // +30 minutes after break start
                hour = Main.breakInTime.getHour();        // Get break start time's hour
                if (minute > 59) {                      // If it is over 59, loop it
                    minute -= 60;
                    hour = Main.breakInTime.getHour() + 1; // Add an hour since it went over 59 minutes
                }
                minBox.setSelectedIndex(minute);        // Set minBox's index to the minute value now
            }
        }

        setListBoxesByHour(hour);

    }

    private void selectTime() {

        LocalTime newTime = LocalTime.parse(Time.makeTime24Hour(
                hrBox.getSelectedIndex() + 1,
                minBox.getSelectedIndex(),
                (amPMBox.getSelectedIndex() == 1)));

        if (type.equals(TimeWindowType.CLOCK_IN)) { // ======= For clock in time=======
            Main.clockInTime = newTime; // Set clock in time
            proceedWith(TimeWindowType.CLOCK_OUT, SetTime.CLOCK_IN_PLUS_DEFAULT);
        } else if (type.equals(TimeWindowType.CLOCK_OUT)) { // ======= For clock out time =======
            setClockOutTime(newTime);
        } else if (type.equals(TimeWindowType.START_BREAK)) { // ======= For entering break =======
            setBreakStartTime(newTime);
        } else if (type.equals(TimeWindowType.END_BREAK)) { // ======= For leaving break =======
            setBreakEndTime(newTime);
        }

        if (Main.timesChosen) Main.updateTime();

    }

    private void setClockOutTime(LocalTime time) {

        if (time.isAfter(Main.clockInTime)) {

            Main.clockOutTime = time;               // Set clock out time
            Main.target = setTarget.getSelectedIndex() + 1; // Set to the list box selection
            Main.timesChosen = true;                // Clock out time is now chosen
            parent.dispose();                       // Close clock out time window

        } else {
            new ErrorDialog(parent, ErrorType.NEGATIVE_SHIFT_TIME);
        }

    }

    private void setBreakStartTime(LocalTime time) {

        if ((time.isAfter(Main.clockInTime)) && time.isBefore(Main.clockOutTime) ||
                time.equals(Main.clockInTime)) {

            Main.breakInTime = time; // Set enter break time
            proceedWith(TimeWindowType.END_BREAK, SetTime.BREAK_START_PLUS_30M); // Open end break window

        } else {
            new ErrorDialog(parent, ErrorType.BREAK_OUT_OF_SHIFT);
        }

    }

    private void setBreakEndTime(LocalTime time) {

        if (time.isAfter(Main.breakInTime) && time.isBefore(Main.clockOutTime) ||
                time.equals(Main.clockOutTime)) {

            Main.breakOutTime = time; // Set leave break time
            Main.breakTimesChosen = true;
            parent.dispose();       // Close leave break window

        } else {
            new ErrorDialog(parent, ErrorType.NEGATIVE_BREAK_TIME);
        }

    }

    private void proceedWith(TimeWindowType newType, SetTime setTimeTo) {

        parent.setVisible(false);
        new SelectTimeWindow(parent, newType);

    }

    private void setListBoxesByHour(int hour) { // Convert time to 12-hour format for list boxes

        if (hour >= 12) {
            amPMBox.setSelectedIndex(1);            // Set AM/PM list box to PM
            // This if statement sets the list box index to current hour,
            // since LocalTime is in 24hr format, we have to do some maths
            // to get it to 12hr am/pm.
            if (hour != 12) {                       // Set hour to 1-11pm
                hrBox.setSelectedIndex(hour - 13);
            } else hrBox.setSelectedIndex(11); // Set hour to 12pm
        } else {
            amPMBox.setSelectedIndex(0);            // Set AM/PM list box to AM
            if (hour != 0) {                        // Set hour to 1-11am
                hrBox.setSelectedIndex(hour - 1);
            } else hrBox.setSelectedIndex(11); // Set hour to 12am (or 0 in 24hr)
        }

    }

    private JPanel createTargetRow() {

        setTargetRow = new JPanel();
        targetText = new JLabel("  Please set an hourly target:"); // Create target label

        if (System.getProperty("os.name").equals("Mac OS X"))
            targetText.setText(" Select your hourly target:");// Due to diff mac font, set diff text
        targetText.setPreferredSize(new Dimension(165, 25));
        targetText.setForeground(UI.textColor);
        setTargetRow.setBackground(UI.bg);

        // Add the specific stuffs
        setTargetRow.add(targetText);
        setTargetRow.add(setTarget);

        return setTargetRow; // Return the new panel

    }

    public void colorSelf() {

        Theme.colorThese(new JComponent[]{labelRow, topText, select, hrBox,
                minBox, amPMBox, setTarget, selectRow, timeBoxesRow});

        if (type.equals(TimeWindowType.CLOCK_OUT)) {
            setTargetRow.setBackground(UI.bg);
            targetText.setForeground(UI.textColor);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Select")) selectTime();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER, 13 -> selectTime(); // Select time with Enter (return on macOS, which is 13)
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}