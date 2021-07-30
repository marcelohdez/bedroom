package com.marcelohdez.bedroom.dialog;

import com.marcelohdez.bedroom.main.*;
import com.marcelohdez.bedroom.enums.*;
import com.marcelohdez.bedroom.util.Ops;
import com.marcelohdez.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener, KeyListener {

    private final TimeWindowType type;

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

    public SelectTimeUI(TimeWindowType type) {

        this.type = type;
        setBackground(UI.bg); // Set background color
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setListBoxIndexes(SetTime.CURRENT); // Set to current time

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
        hrBox.addActionListener(this);
        minBox.setPreferredSize(listBoxSize);
        minBox.addActionListener(this);
        amPMBox.setPreferredSize(smallListBoxSize);
        amPMBox.addActionListener(this);
        setTarget.setPreferredSize(smallListBoxSize);
        setTarget.setSelectedIndex(8); // Set default to 9 (what i need @ my job, so a lil easter egg)
        setTarget.addActionListener(this);

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
                    this.minBox.setSelectedIndex(LocalTime.now().getMinute());
            case CLOCK_IN_PLUS_DEFAULT -> { // Get 4hrs after clock in time, for clock out window
                hour = UI.clockInTime.getHour() + 4;    // Add 4 to clock in time's hours
                if (hour >= 24) hour -= 24;             // If it's over 24 now, loop it
                this.minBox.setSelectedIndex(UI.clockInTime.getMinute()); // Set minBox to clock in time's minute
            }
            case BREAK_START_PLUS_30M -> { // Set leave break window's default minutes to 30 above break in time.
                minute = UI.breakInTime.getMinute() + 30; // +30 minutes after break start
                hour = UI.breakInTime.getHour();        // Get break start time's hour
                if (minute > 59) {                      // If it is over 59, loop it
                    minute -= 60;
                    hour = UI.breakInTime.getHour() + 1; // Add an hour since it went over 59 minutes
                }
                this.minBox.setSelectedIndex(minute);        // Set minBox's index to the minute value now
            }
        }

        setListBoxesByHour(hour);

    }

    private void selectTime() {

        LocalTime newTime = LocalTime.parse(Time.makeTime24Hour(
                this.hrBox.getSelectedIndex() + 1,
                this.minBox.getSelectedIndex(),
                (this.amPMBox.getSelectedIndex() == 1)));

        if (this.type.equals(TimeWindowType.CLOCK_IN)) { // ======= For clock in time=======
            UI.clockInTime = newTime; // Set clock in time
            setTimeAndProceed(Main.clockInWnd, Main.clockOutWnd, SetTime.CLOCK_IN_PLUS_DEFAULT);
        } else if (this.type.equals(TimeWindowType.CLOCK_OUT)) { // ======= For clock out time =======
            setClockOutTime(newTime);
        } else if (this.type.equals(TimeWindowType.START_BREAK)) { // ======= For entering break =======
            setBreakStartTime(newTime);
        } else if (this.type.equals(TimeWindowType.END_BREAK)) { // ======= For leaving break =======
            setBreakEndTime(newTime);
        }

        if (Main.timesChosen) UI.getTime();

    }

    private void setClockOutTime(LocalTime time) {

        if (time.isAfter(UI.clockInTime)) {

            UI.clockOutTime = time;                 // Set clock out time
            UI.target = setTarget.getSelectedIndex() + 1; // Set to the list box selection
            Main.timesChosen = true;                // Clock out time is now chosen
            Main.clockOutWnd.dispose();             // Close clock out time window

        } else new ErrorDialog(ErrorType.NEGATIVE_SHIFT_TIME);

    }

    private static void setBreakStartTime(LocalTime time) {

        if ((time.isAfter(UI.clockInTime)) && time.isBefore(UI.clockOutTime) ||
                time.equals(UI.clockInTime)) {

            UI.breakInTime = time; // Set enter break time
            setTimeAndProceed(Main.enterBreakWnd, Main.leaveBreakWnd,
                    SetTime.BREAK_START_PLUS_30M);

        } else new ErrorDialog(ErrorType.BREAK_OUT_OF_SHIFT);

    }

    private static void setBreakEndTime(LocalTime time) {

        if (time.isAfter(UI.breakInTime) && time.isBefore(UI.clockOutTime) ||
                time.equals(UI.clockOutTime)) {

            UI.breakOutTime = time; // Set leave break time
            UI.breakTimesChosen = true;
            Main.leaveBreakWnd.dispose();       // Close leave break window

        } else new ErrorDialog(ErrorType.NEGATIVE_BREAK_TIME);

    }

    private static void setTimeAndProceed(SelectTimeWindow oldWindow, SelectTimeWindow newWindow,
                                          SetTime newWindowType) {

        oldWindow.dispose();
        newWindow.centerOnMainWindow();
        newWindow.setUITime(newWindowType);
        newWindow.setVisible(true);

    }

    private void setListBoxesByHour(int hour) { // Convert time to 12 hour format for list boxes

        if (hour >= 12) {
            this.amPMBox.setSelectedIndex(1);            // Set AM/PM list box to PM
            // This if statement sets the list box index to current hour,
            // since LocalTime is in 24hr format, we have to do some maths
            // to get it to 12hr am/pm.
            if (hour != 12) {                       // Set hour to 1-11pm
                this.hrBox.setSelectedIndex(hour - 13);
            } else this.hrBox.setSelectedIndex(11); // Set hour to 12pm
        } else {
            this.amPMBox.setSelectedIndex(0);            // Set AM/PM list box to AM
            if (hour != 0) {                        // Set hour to 1-11am
                this.hrBox.setSelectedIndex(hour - 1);
            } else this.hrBox.setSelectedIndex(11); // Set hour to 12am (or 0 in 24hr)
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

        Ops.colorThis(this.labelRow);
        Ops.colorThis(this.topText);
        Ops.colorThis(this.select);
        Ops.colorThis(this.hrBox);
        Ops.colorThis(this.minBox);
        Ops.colorThis(this.amPMBox);
        Ops.colorThis(this.setTarget);
        Ops.colorThis(this.selectRow);
        Ops.colorThis(this.timeBoxesRow);

        if (this.type.equals(TimeWindowType.CLOCK_OUT)) {
            this.setTargetRow.setBackground(UI.bg);
            this.targetText.setForeground(UI.textColor);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Select")) selectTime();
        this.requestFocus(); // Get focus back from component clicked (for shortcuts)
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 13 || e.getKeyCode() == 10) // Enter selects time (return on MacOS)
            selectTime();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}