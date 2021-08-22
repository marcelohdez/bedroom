package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JDialog with a text area to include a message and an "OK" button to dismiss.
 * Can be used for alerts and for errors.
 */
public class AlertDialog extends JDialog implements ActionListener {

    private final WindowParent parent;

    public AlertDialog(WindowParent parent, String message) {

        this.parent = parent;
        init("Alert", message);

    }

    public AlertDialog(WindowParent parent, ErrorType e) {

        this.parent = parent;
        init("Error", getErrorMessage(e));

    }

    private void init(String title, String message) {

        // Create components
        JPanel topUI = new JPanel();
        JPanel botUI = new JPanel();
        JTextArea messageBox = new JTextArea(message);
        JButton ok = new JButton("OK");

        // Customize components
        Theme.colorThese(new JComponent[]{topUI, botUI, messageBox, ok});
        messageBox.setFont(UI.boldText);
        messageBox.setEditable(false);
        ok.addActionListener(this);

        // Add components
        topUI.add(messageBox);
        botUI.add(ok);
        add(topUI, BorderLayout.PAGE_START);
        add(botUI, BorderLayout.PAGE_END);

        // Set window properties
        setModalityType(ModalityType.APPLICATION_MODAL); // Retain input from other windows
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle(title);
        pack();
        ok.setPreferredSize(new Dimension(getWidth() - 5, 40));
        pack();

        // Center on summoner
        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + (xyWidthHeight[2]/2 - (getWidth()/2)),
                xyWidthHeight[1] + (xyWidthHeight[3]/2 - (getHeight()/2)));

        // Show
        setVisible(true);

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
            case NEGATIVE_BREAK_TIME -> {
                return """
                        A break's end time can not be
                        before the break's start time.
                        Current break start:\040""" +
                        Time.makeTime12Hour(Main.breakInTime.toLocalTime());
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

    @Override
    public void actionPerformed(ActionEvent e) { // Do sum when OK is pressed
        dispose();
    }
}
