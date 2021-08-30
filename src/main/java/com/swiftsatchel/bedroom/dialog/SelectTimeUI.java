package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.SetTime;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.settings.SettingsDialog;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener, KeyListener {

    private final TimeWindowType type;
    private final SelectTimeDialog parent;

    // ======= List boxes: =======
    private final JComboBox<String> amPMBox = new JComboBox<>(new String[]{"AM", "PM"});
    private final JComboBox<String> hrBox = new JComboBox<>(Ops.createNumberList(true, 1, 12, ":"));
    // Create minutes (0-59) and hourly targets (1-24)
    private final JComboBox<String> minBox = new JComboBox<>(Ops.createNumberList(true, 0, 59, null));
    private final JComboBox<String> setTarget = new JComboBox<>(Ops.createNumberList(true, 1, 24, null));

    // Other components:
    private final JButton select = new JButton("Select");   // Select button
    private final JLabel topText = new JLabel();                 // Top text
    private JLabel targetText;                                  // Select target text

    // Component rows:
    private final JPanel labelRow = new JPanel();
    private final JPanel timeBoxesRow = new JPanel();
    private JPanel setTargetRow = createTargetRow();
    private final JPanel selectRow = new JPanel();

    public SelectTimeUI(SelectTimeDialog parent) {

        type = parent.type;
        this.parent = parent;

        setBackground(Theme.getBgColor()); // Set background color
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setListBoxIndexes(SetTime.CURRENT); // Set to current time
        addKeyListener(this);

        Dimension listBoxSize = new Dimension(80, 30);
        Dimension smallListBoxSize = new Dimension(65, 30);

        switch (type) { // Change top text depending on window type
            case CLOCK_OUT -> topText.setText("Select CLOCK OUT time:");
            case START_BREAK -> topText.setText("Select BREAK START time:");
            case END_BREAK -> topText.setText("Select BREAK END time:");
            case CLOCK_IN -> topText.setText("Select CLOCK IN time:");
            case EARLY_CLOCK_OUT -> {
                topText.setText("Clocking out early?");
                select.setText("Clock Out");
            }
        }

        // Customize components
        topText.setFont(Theme.getBoldText());
        select.addActionListener(this);
        select.addKeyListener(this);
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

        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

        requestFocus();

    }

    public void setListBoxIndexes(SetTime type) { // Set list boxes to a time:

        int hour = LocalTime.now().getHour();

        switch (type) { // Set minBox depending on type and get wanted hour int

            case CURRENT -> minBox.setSelectedIndex(LocalTime.now().getMinute()); // Set to current time

            case CLOCK_IN_PLUS_DEFAULT -> { // Set to chosen default hour value after clock in time
                hour = Main.clockInTime.getHour() + Main.userPrefs.getInt("defaultShiftLength", 4);
                if (hour >= 24) hour -= 24;             // If it's over 24 now, loop it
                minBox.setSelectedIndex(Main.clockInTime.getMinute()); // Set minBox to clock in time's minute
            }

            case BREAK_START_PLUS_30M -> { // Set leave break window's default minutes to 30 above break in time.
                int minute = Main.breakInTime.getMinute() + 30; // +30 minutes after break start
                hour = Main.breakInTime.getHour();      // Get break start time's hour
                if (minute > 59) {                      // If it is over 59, loop it and add an hour
                    minute -= 60;
                    hour = Main.breakInTime.getHour() + 1;
                }
                minBox.setSelectedIndex(minute);        // Set minBox's index to the minute value now
            }

        }

        setListBoxesByHour(hour); // Do maths for when hour value is over 12, ex 16:00 -> 4PM

    }

    private void selectTime() {

        // Parse the current date and time in format: "2021-8-16T17:20" for 5:20PM on Aug 18, 2021
        LocalDateTime newTime = LocalDateTime.parse(LocalDate.now() + "T" +
                Time.makeTime24Hour(hrBox.getSelectedIndex() + 1,
                minBox.getSelectedIndex(), (amPMBox.getSelectedIndex() == 1)));

        switch (type) {
            case CLOCK_IN -> {
                Main.clockInTime = newTime; // Set clock in time
                proceedWith(TimeWindowType.CLOCK_OUT);
            }
            case CLOCK_OUT -> setClockOutTime(newTime);
            case START_BREAK -> setBreakStartTime(newTime);
            case END_BREAK -> setBreakEndTime(newTime);
            case EARLY_CLOCK_OUT -> clockOutEarly(newTime);
        }

        if (Main.timesChosen) Main.update();

    }

    private void setClockOutTime(LocalDateTime time) {

        // Since the default date is the user's current date, if the clock out time is before
        // the clock in time, assume it is an overnight shift and set the clock out time's date
        // to the current date + 1 day.
        Main.clockOutTime = time.isAfter(Main.clockInTime) ? time : time.plusDays(1);

        Main.target = setTarget.getSelectedIndex() + 1; // Set target
        Main.timesChosen = true;                // Clock out time is now chosen
        parent.dispose();                       // Close clock out time window

    }

    private void setBreakStartTime(LocalDateTime time) {

        if ((time.isAfter(Main.clockInTime)) && time.isBefore(Main.clockOutTime)) {

            Main.breakInTime = time; // Set enter break time
            proceedWith(TimeWindowType.END_BREAK); // Open end break window

        } else {
            new AlertDialog(parent, ErrorType.BREAK_OUT_OF_SHIFT);
        }

    }

    private void setBreakEndTime(LocalDateTime time) {

        if (time.isAfter(Main.breakInTime) && time.isBefore(Main.clockOutTime)) {

            Main.breakOutTime = time; // Set leave break time
            Main.breakTimesChosen = true;
            parent.dispose();       // Close leave break window

        } else {
            new AlertDialog(parent, ErrorType.NEGATIVE_BREAK_TIME);
        }

    }

    private void clockOutEarly(LocalDateTime time) {

        if (time.isAfter(Main.clockInTime)) {

            if (time.isBefore(Main.clockOutTime)) {

                Main.clockOut(time); // Save this shift's performance and close application

            } else new AlertDialog(parent, ErrorType.EARLY_CLOCK_OUT_NOT_EARLY);

        } else new AlertDialog(parent, ErrorType.NON_POSITIVE_SHIFT_TIME);

    }

    private void proceedWith(TimeWindowType newType) {

        parent.setVisible(false);
        new SelectTimeDialog(parent, newType);

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
        Theme.colorThese(new JComponent[]{targetText, setTargetRow});

        // Add the specific stuffs
        setTargetRow.add(targetText);
        setTargetRow.add(setTarget);

        return setTargetRow; // Return the new panel

    }

    public void colorSelf() {

        Theme.colorThese(new JComponent[]{labelRow, topText, select, hrBox,
                minBox, amPMBox, setTarget, selectRow, timeBoxesRow});

        if (type.equals(TimeWindowType.CLOCK_OUT)) {
            Theme.colorThese(new JComponent[]{setTargetRow, targetText});
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectTime();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER, 13 -> selectTime(); // Select time with Enter (return on macOS, which is 13)
            case KeyEvent.VK_ESCAPE -> parent.close();
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(parent);  // Open settings with Delete or Backspace keys
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}