package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

/**
 * A JDialog with a text area to include a message and an "OK" button to dismiss.
 * Can be used for alerts and for errors.
 */
public class AlertDialog extends JDialog implements ActionListener {

    private final WindowParent parent;
    private final JPanel buttonRow;
    private final JButton okButton;

    public AlertDialog(WindowParent parent, String message) {

        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
        init("Alert", message, false);

    }

    public AlertDialog(WindowParent parent, ErrorType e) {

        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
        init("Error", getErrorMessage(e), false);

    }

    public AlertDialog(SelectTimeDialog parent, ErrorType e, LocalDateTime lastTime) {

        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
        init("Error", getErrorMessage(e, lastTime), false);

    }

    public AlertDialog(WindowParent parent, String message, boolean isYesNoDialog) {

        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
        init("Alert", message, isYesNoDialog);

    }

    private void init(String title, String message, boolean isYesNoDialog) {

        // Create components
        JPanel topUI = new JPanel();
        JTextArea messageBox = new JTextArea(message);

        // Customize components
        Theme.colorThese(new JComponent[]{topUI, buttonRow, messageBox});
        messageBox.setFont(Theme.getBoldFont());
        messageBox.setEditable(false);
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on button

        // Add components
        topUI.add(messageBox);
        if (!isYesNoDialog) addToButtonRow(okButton, this, false);
        add(topUI, BorderLayout.PAGE_START);
        add(buttonRow, BorderLayout.PAGE_END);

        // Set window properties
        setModalityType(ModalityType.APPLICATION_MODAL); // Retain input from other windows
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle(title);
        sizeButtonRow();

        // Center on summoner
        setLocation(parent.getXYWidthHeight()[0] + ((parent.getXYWidthHeight()[2] / 2) - (getWidth() / 2)),
                parent.getXYWidthHeight()[1] + ((parent.getXYWidthHeight()[3] / 2) - (getHeight() / 2)));

        // Show
        if (!isYesNoDialog) setVisible(true);

    }

    private void sizeButtonRow() {
        pack(); // Make Swing size everything
        for (Component c : buttonRow.getComponents()) {
            // Set to width we want, and 1.5x teh height swing chose for font
            c.setPreferredSize(new Dimension(getWidth()/buttonRow.getComponentCount() -
                    (5*buttonRow.getComponentCount()), (int) (c.getHeight()*1.5)));
        }
        pack(); // Let Swing react accordingly
    }

    // Get error message per error type
    private String getErrorMessage(ErrorType e) {

        switch(e) {
            case BREAK_OUT_OF_SHIFT -> {
                return """
                        Breaks may only start or end
                        inside of shifts. Current
                        shift is:\040""" +
                        Time.makeTime12Hour(Main.clockInTime.toLocalTime()) + "-" +
                        Time.makeTime12Hour(Main.clockOutTime.toLocalTime());
            }
            case NON_POSITIVE_SHIFT_TIME -> {
                return """
                        Clock out time has to be
                        after your clock in time.
                        Current clock in time:
                        """ +
                        Time.makeTime12Hour(Main.clockInTime.toLocalTime());
            }
            case NO_FILE_ASSOCIATION -> {
                return """
                        One of your work apps was not
                        able to be started as it does
                        not have a program associated
                        with its file type""";
            }
            case WORK_APPS_FULL -> {
                return """
                        You can not add any more
                        work apps.""";
            }
            case WORK_APP_DOES_NOT_EXIST -> {
                return """
                        One of your work apps was not
                        able to be started as it no
                        longer exists. Please go to
                        Settings > Manage Work Apps.""";
            }
            case EARLY_CLOCK_OUT_NOT_EARLY -> {
                return """
                        Early clock outs must be
                        before original clock out
                        time. Your current clock
                        out time:\040""" +
                        Time.makeTime12Hour(Main.clockOutTime.toLocalTime());
            }
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    // Get error messages that need additional LocalDateTime variables
    private String getErrorMessage(ErrorType e, LocalDateTime time) {

        if (e == ErrorType.NEGATIVE_BREAK_TIME) {
            return """
                    A break's end time can not be
                    before the break's start time.
                    Current break start:\040""" + Time.makeTime12Hour(time.toLocalTime());
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    protected void addToButtonRow(JButton b, ActionListener al, boolean updateSizes) {
        Theme.colorThis(b);
        b.addActionListener(al);
        buttonRow.add(b);
        if (updateSizes) sizeButtonRow();
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Do sum when OK is pressed
        if (e.getSource() == okButton) dispose();
    }
}
