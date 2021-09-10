package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YesNoDialog extends AlertDialog implements ActionListener {

    private final JButton cancelButton;
    private final JButton acceptButton;
    private boolean accepted;

    public YesNoDialog(WindowParent parent, String message) {
        super(parent, message, true);

        cancelButton = new JButton("No");
        acceptButton = new JButton("Yes");
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on button
        acceptButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on button
        addToButtonRow(acceptButton, this, false);
        addToButtonRow(cancelButton, this, true);

    }

    public boolean accepted() {
        requestFocus();
        setVisible(true);
        return accepted;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == acceptButton) {
            accepted = true;
            setVisible(false);
        } else if (e.getSource() == cancelButton) {
            accepted = false;
            setVisible(false);
        }
    }

}
