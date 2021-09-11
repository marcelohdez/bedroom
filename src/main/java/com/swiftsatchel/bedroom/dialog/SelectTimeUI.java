package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.alert.AlertDialog;
import com.swiftsatchel.bedroom.alert.YesNoDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SelectTimeUI extends JPanel implements ActionListener {

    private final TimeWindowType type;      // Keep track of this window's type
    private final SelectTimeDialog parent;  // This UI's parent (its container)
    private final WindowParent windowParent; // This set of select time dialog's parent window
    private final GridBagLayout layout;     // Layout
    private final GridBagConstraints gbc;   // Constraints

    // ======= Components: =======
    private JLabel topText;               // Top text label
    private JComboBox<String> amPMBox;    // AM/PM list box
    private JComboBox<String> hrBox;      // Hours list box
    private JComboBox<String> minBox;     // Minutes list box
    private JComboBox<String> targetBox;  // Targets list box
    private JButton selectButton;         // Select button
    private JLabel targetLabel;           // Select target text

    // Keep track of last select time dialog's selected time ex: save break start time to only
    // set break time once break end time is selected. This fixes the bug where setting a break
    // from 3pm-3:30pm then opening a set break window and putting 2pm but then cancelling
    // would save your break as 2pm-3:30pm
    private LocalDateTime lastTime;

    public SelectTimeUI(SelectTimeDialog parent) {
        // Initial properties
        type = parent.type;
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        this.parent = parent;
        windowParent = parent.getWindowParent();
        init();
    }

    public SelectTimeUI(SelectTimeDialog parent, LocalDateTime lastTime, WindowParent windowParent) {
        // Initial properties
        type = parent.type;
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        this.parent = parent;
        this.lastTime = lastTime;
        this.windowParent = windowParent;
        init();
    }

    private void init() {

        // Initialize component variables
        amPMBox = new JComboBox<>(new String[]{"AM", "PM"});
        hrBox = new JComboBox<>(Ops.createNumberList(true, 1, 12, ":"));
        minBox = new JComboBox<>(Ops.createNumberList(true, 0, 59));
        targetBox = new JComboBox<>(Ops.createNumberList(true, 1, 24));
        selectButton = new JButton("Select");
        topText = new JLabel("CLOCK IN time:");
        targetLabel = new JLabel("Your hourly target:");

        setBackground(Theme.getBgColor());  // Set background color
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
        colorSelf(); // Color self
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
        targetBox.setSelectedIndex(8); // Set default to 9 (what I need @ my job, so a lil Easter egg)
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

        if (Main.timesChosen) Main.update();

    }

    private void setClockOutTime(LocalDateTime time) {

        if (time.isAfter(lastTime)) {

            Main.clockOutTime = time;
            Main.clockInTime = lastTime; // Set clock in time as well

            Main.setTarget(targetBox.getSelectedIndex() + 1); // Set target
            Main.timesChosen = true;                // Clock out time is now chosen
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

            // Since the default date is the user's current date, if the clock out time is before
            // the clock in time, ask if an overnight shift is desired, if so add a day to time.
            Main.clockOutTime = time.plusDays(1);
            Main.clockInTime = lastTime; // Set clock in time as well

            Main.setTarget(targetBox.getSelectedIndex() + 1); // Set target
            Main.timesChosen = true;                // Clock out time is now chosen
            windowParent.setDisabled(false);        // Re-enable parent window
            windowParent.askForFocus();             // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        }

    }

    private void setBreakStartTime(LocalDateTime time) {

        if ((time.isAfter(Main.clockInTime)) && time.isBefore(Main.clockOutTime)) {

            lastTime = time; // Set enter break time
            proceedWith(TimeWindowType.END_BREAK); // Open end break window

        } else {
            new AlertDialog(parent, ErrorType.BREAK_OUT_OF_SHIFT);
        }

    }

    private void setBreakEndTime(LocalDateTime time) {

        if (time.isAfter(lastTime) && time.isBefore(Main.clockOutTime)) {

            Main.breakOutTime = time;           // Set leave break time
            Main.breakInTime = lastTime;        // Set enter break time now
            windowParent.setDisabled(false);    // Re-enable parent window
            windowParent.askForFocus();         // Give focus to parent window
            // Finish this dialog set by disposing this window and the previous
            parent.finish();

        } else {
            new AlertDialog(parent, ErrorType.NEGATIVE_BREAK_TIME, lastTime);
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
        new SelectTimeDialog(parent, newType, lastTime, parent.getWindowParent());

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

    public void colorSelf() {

        Theme.colorThese(new JComponent[]{this, topText, selectButton, hrBox, minBox, amPMBox});

        if (type.equals(TimeWindowType.CLOCK_OUT)) {
            Theme.colorThese(new JComponent[]{targetLabel, targetBox});
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectTime();
    }

}