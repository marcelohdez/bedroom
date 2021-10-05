package com.swiftsatchel.bedroom.dialog.time;

import com.swiftsatchel.bedroom.dialog.alert.ErrorDialog;
import com.swiftsatchel.bedroom.dialog.alert.YesNoDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class SelectTimeUI extends JPanel implements ActionListener {

    private final TimeWindowType type;      // Keep track of this window's type
    private final SelectTimeDialog parent;  // This UI's parent (its container)
    private final WindowParent windowParent; // This set of select time dialog's parent window
    private final GridBagLayout layout = new GridBagLayout();     // Layout
    private final GridBagConstraints gbc = new GridBagConstraints();   // Constraints

    // ======= Components: =======
    private final JLabel topText = new JLabel("CLOCK IN time:"); // Top text label
    private final JComboBox<String> amPMBox = new JComboBox<>(new String[]{"AM", "PM"});    // AM/PM list box

    // Hours list box
    private final JComboBox<String> hrBox = new JComboBox<>(Ops.createNumberList(true, 1, 12, ":"));
    // Minutes list box
    private final JComboBox<String> minBox = new JComboBox<>(Ops.createNumberList(true, 0, 59));
    // Targets list box
    private final JComboBox<String> targetBox = new JComboBox<>(Ops.createNumberList(true, 1, 24));

    private final JButton selectButton = new JButton("Select"); // Select button
    private final JLabel targetLabel = new JLabel("Your hourly target:"); // Select target text

    // If the overnight shift dialog is accepted, make a date time format to check if tomorrow or
    // yesterday's date is valid (ex: not Feb 30)
    private DateTimeFormatter dtf;

    // Keep track of last select time dialog's selected time ex: save break start time to only
    // set break time once both times have been selected. This fixes the bug where setting a break
    // from 3pm-3:30pm then opening a set break window and putting 2pm but then cancelling
    // would save your break as 2pm-3:30pm
    private LocalDateTime lastTime;

    public SelectTimeUI(SelectTimeDialog parent) {
        // Initial properties
        type = parent.type;
        this.parent = parent;
        windowParent = parent.getWindowParent();
        init();
    }

    public SelectTimeUI(SelectTimeDialog parent, LocalDateTime lastTime, WindowParent windowParent) {
        // Initial properties
        type = parent.type;
        this.parent = parent;
        this.lastTime = lastTime;
        this.windowParent = windowParent;
        init();
    }

    private void init() {

        setLayout(layout);                  // Set layout
        addKeyListener(parent);             // Add key listener

        switch (type) { // Set window type-specific things
            case CLOCK_OUT -> { // For clock out time window, add its specific components as well
                topText.setText("CLOCK OUT time:");
                addComponent(targetLabel, 0, 2, 2, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0));
                addComponent(targetBox, 2, 2, 1, GridBagConstraints.BOTH, new Insets(2, 2, 2, 4));
            }
            case START_BREAK -> topText.setText("Break start:");
            case END_BREAK -> topText.setText("Break end:");
            case EARLY_CLOCK_OUT -> {
                topText.setText("Clocking out early?");
                selectButton.setText("Clock Out");
            }
        }

        initComponents(); // Add and customize components
        setListBoxIndexes(type); // Set list boxes to wanted time
        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

    }

    private void initComponents() {

        // Add components
        addComponent(topText, 0, 0, 3, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 2, 4));
        addComponent(hrBox, 0, 1, 1, GridBagConstraints.BOTH, new Insets(4, 4, 4, 2));
        addComponent(minBox, 1, 1, 1, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4));
        addComponent(amPMBox, 2, 1, 1, GridBagConstraints.BOTH, new Insets(4, 2, 4, 4));
        addComponent(selectButton, 0, 4, 3, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4));

        // Customize em
        topText.setFont(Theme.getBoldFont());
        topText.setHorizontalAlignment(JLabel.CENTER);
        selectButton.addActionListener(this);
        selectButton.addKeyListener(parent);
        hrBox.addKeyListener(parent);
        minBox.addKeyListener(parent);
        amPMBox.addKeyListener(parent);
        targetLabel.setHorizontalAlignment(JLabel.CENTER);
        targetBox.setSelectedIndex(Settings.getDefaultTarget()-1); // Set index to target - 1 since list starts at 1
        targetBox.addKeyListener(parent);

    }

    private void addComponent(JComponent comp, int x, int y, int width, int fill, Insets insets) {

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = insets;
        gbc.fill = fill;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = 1;
        layout.setConstraints(comp, gbc);
        add(comp, gbc);

    }

    private void setListBoxIndexes(TimeWindowType type) { // Set list boxes to a time:

        int hour = LocalTime.now().getHour();

        switch (type) { // Set minBox depending on type and get wanted hour int

            case CLOCK_IN, START_BREAK, EARLY_CLOCK_OUT ->
                    minBox.setSelectedIndex(LocalTime.now().getMinute()); // Set to current time

            case CLOCK_OUT -> { // Set to chosen default hour value after clock in time
                hour = lastTime.getHour() + Main.userPrefs.getInt("defaultShiftLength", 4);
                if (hour >= 24) hour -= 24;     // If it's over 24 now, loop it
                minBox.setSelectedIndex(lastTime.getMinute()); // Set minBox to clock in time's minute
            }

            case END_BREAK -> { // Set leave break window's default minutes to 30 above break in time.
                int minute = lastTime.getMinute() + 30; // +30 minutes after break start
                hour = lastTime.getHour();      // Get break start time's hour
                if (minute > 59) {              // If it is over 59, loop it and add an hour
                    minute -= 60;
                    hour = lastTime.getHour() + 1;
                }
                minBox.setSelectedIndex(minute); // Set minBox's index to the minute value now
            }

        }

        setListBoxesByHour(hour); // Do maths for when hour value is over 12, ex 16:00 -> 4PM

    }

    void selectTime() {

        // Parse the current date and time in format: "2021-8-16T17:20" for 5:20PM on Aug 18, 2021
        LocalDateTime newTime = LocalDateTime.parse(LocalDate.now() + "T" +
                Time.makeTime24Hour(hrBox.getSelectedIndex() + 1,
                minBox.getSelectedIndex(), (amPMBox.getSelectedIndex() == 1)));

        switch (type) {
            case CLOCK_IN -> {
                lastTime = newTime; // Set clock in time
                proceedWith(TimeWindowType.CLOCK_OUT);
            }
            case CLOCK_OUT -> setClockOutTime(newTime);
            case START_BREAK -> setBreakStartTime(newTime);
            case END_BREAK -> setBreakEndTime(newTime);
            case EARLY_CLOCK_OUT -> clockOutEarly(newTime);
        }

        if (Main.timesChosen()) Main.update();

    }

    private void setClockOutTime(LocalDateTime time) {

        if (time.isAfter(lastTime)) {

            Main.setShift(lastTime, time);          // Set new shift times
            Main.setTarget(targetBox.getSelectedIndex() + 1); // Set target
            windowParent.setDisabled(false);        // Re-enable parent window
            windowParent.askForFocus();             // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        } else if (new YesNoDialog(parent, """
                It seems you have selected
                a clock out time before
                your clock in time. ($lastTime)
                Is this an overnight shift?"""
                .replace("$lastTime", Time.makeTime12Hour(lastTime.toLocalTime())))
                .accepted()) {

            // Create DateTimeFormatter as an overnight shift has been initialized
            dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.LENIENT);
            // Now parse the new date with that format, if it works set it
            Main.setShift(lastTime, LocalDateTime.parse(time.plusDays(1).format(dtf)));

            Main.setTarget(targetBox.getSelectedIndex() + 1); // Set target
            windowParent.setDisabled(false);        // Re-enable parent window
            windowParent.askForFocus();             // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        }

    }

    private void setBreakStartTime(LocalDateTime time) {

        // Make sure time chosen is inside of shift
        if (time.isAfter(Main.getClockInTime()) && time.isBefore(Main.getClockOutTime())) {

            lastTime = time; // Set enter break time
            proceedWith(TimeWindowType.END_BREAK); // Open end break window

        } else if (time.plusDays(1).isAfter(Main.getClockInTime()) && // If not, check if user meant tomorrow's date
                time.plusDays(1).isBefore(Main.getClockOutTime())) {

            lastTime = LocalDateTime.parse(time.plusDays(1).format(dtf));
            proceedWith(TimeWindowType.END_BREAK); // Open end break window

        } else if (time.minusDays(1).isAfter(Main.getClockInTime()) && // If not, check if user meant yesterday's date
                time.minusDays(1).isBefore(Main.getClockOutTime())) {

            lastTime = LocalDateTime.parse(time.minusDays(1).format(dtf));
            proceedWith(TimeWindowType.END_BREAK); // Open end break window

        } else {
            new ErrorDialog(parent, ErrorType.BREAK_OUT_OF_SHIFT);
        }

    }

    private void setBreakEndTime(LocalDateTime time) {

        if (time.isAfter(lastTime) && time.isBefore(Main.getClockOutTime())) {

            Main.setBreak(lastTime, time);      // Set new break times
            windowParent.setDisabled(false);    // Re-enable parent window
            windowParent.askForFocus();         // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        } else if (time.plusDays(1).isAfter(lastTime) && time.plusDays(1).isBefore(Main.getClockOutTime())) {

            Main.setBreak(lastTime, LocalDateTime.parse(time.plusDays(1).format(dtf)));
            windowParent.setDisabled(false);    // Re-enable parent window
            windowParent.askForFocus();         // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        } else if (time.minusDays(1).isAfter(lastTime) && time.minusDays(1).isBefore(Main.getClockOutTime())) {

            Main.setBreak(lastTime, LocalDateTime.parse(time.minusDays(1).format(dtf)));
            windowParent.setDisabled(false);    // Re-enable parent window
            windowParent.askForFocus();         // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        } else {
            new ErrorDialog(parent, ErrorType.NEGATIVE_BREAK_TIME, lastTime);
        }

    }

    private void clockOutEarly(LocalDateTime time) {

        if (time.isAfter(Main.getClockInTime())) {

            if (time.isBefore(Main.getClockOutTime())) {

                Main.clockOut(time); // Save this shift's performance and close application

            } else new ErrorDialog(parent, ErrorType.EARLY_CLOCK_OUT_NOT_EARLY);

        } else new ErrorDialog(parent, ErrorType.NON_POSITIVE_SHIFT_TIME);

    }

    private void proceedWith(TimeWindowType newType) {

        parent.setVisible(false);
        new SelectTimeDialog(parent, newType, lastTime, parent.getWindowParent());

    }

    private void setListBoxesByHour(int hour) { // Convert time to 12-hour format for list boxes

        if (hour > 23) hour -= 24;

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

    public void colorSelf() {

        Theme.color(this, topText, selectButton, hrBox, minBox, amPMBox);

        if (type.equals(TimeWindowType.CLOCK_OUT)) {
            Theme.color(targetLabel, targetBox);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectTime();
    }

}