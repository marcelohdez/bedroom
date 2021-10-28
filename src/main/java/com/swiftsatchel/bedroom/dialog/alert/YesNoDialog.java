package com.swiftsatchel.bedroom.dialog.alert;

import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YesNoDialog extends AlertDialog implements ActionListener {

    private final JButton acceptButton;
    private boolean accepted;

    public YesNoDialog(WindowParent parent, String message) {
        super(parent, message, true);

        JButton cancelButton = new JButton("No");
        acceptButton = new JButton("Yes");
        addToButtonRow(acceptButton, this, false);
        addToButtonRow(cancelButton, this, true);
        Ops.setHandCursorOnCompsFrom(getButtonRow());

    }

    public boolean accepted() {
        requestFocus();
        setVisible(true);
        return accepted;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        accepted = (e.getSource() == acceptButton);
        setVisible(false);
        dispose();
    }

}
