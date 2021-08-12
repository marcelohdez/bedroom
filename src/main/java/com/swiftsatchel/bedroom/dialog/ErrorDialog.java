package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorDialog extends JDialog implements ActionListener {

    public ErrorDialog(WindowParent parent, ErrorType e) {

        // Create components
        JPanel topUI = new JPanel();
        JPanel botUI = new JPanel();
        JTextArea message = new JTextArea(getErrorMessage(e));
        JButton ok = new JButton("OK");

        // Customize components
        topUI.setBackground(UI.bg);
        botUI.setBackground(UI.bg);
        message.setBackground(UI.bg);
        message.setForeground(UI.textColor);
        message.setFont(UI.boldText);
        message.setEditable(false);
        ok.addActionListener(this);
        ok.setBackground(UI.buttonColor);
        ok.setForeground(UI.buttonTextColor);

        // Add components
        topUI.add(message);
        botUI.add(ok);
        add(topUI, BorderLayout.PAGE_START);
        add(botUI, BorderLayout.PAGE_END);

        // Set window properties
        setModalityType(ModalityType.APPLICATION_MODAL); // Retain input from all other windows
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Error");
        pack();
        ok.setPreferredSize(new Dimension(this.getWidth() - 5, 40));
        pack();

        // Center on summoner
        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + (xyWidthHeight[2]/2 - (this.getWidth()/2)),
                xyWidthHeight[1] + (xyWidthHeight[3]/2 - (this.getHeight()/2)));

        setVisible(true);

    }

    // Get error message per error type
    private String getErrorMessage(ErrorType e) {

        switch(e) {
            case BREAK_OUT_OF_SHIFT -> {
                return """
                        Breaks can not start or end
                        outside of shifts. Current
                        shift is:\040""" +
                        Time.makeTime12Hour(Main.clockInTime) + "-" +
                        Time.makeTime12Hour(Main.clockOutTime);
            }
            case NEGATIVE_BREAK_TIME -> {
                return """
                        A break's end time can not be
                        before the break's start time.
                        Current break start:\040""" +
                        Time.makeTime12Hour(Main.breakInTime);
            }
            case NEGATIVE_SHIFT_TIME -> {
                return """
                        Clock out time has to be
                        after your clock in time.
                        Current clock in time:
                        """ +
                        Time.makeTime12Hour(Main.clockInTime);
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
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    @Override
    public void actionPerformed(ActionEvent e) { // Do sum when OK is pressed
        if (e.getActionCommand().equals("OK")) {
            this.dispose();
        }
    }
}
