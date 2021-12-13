package me.soggysandwich.bedroom.dialog.alert;

import javax.swing.*;
import java.awt.*;

public class YesNoDialog extends AlertDialog {

    private boolean accepted = false;

    public YesNoDialog(Component parent, String message) {
        super(parent);

        okButton.setText("Yes");
        okButton.addActionListener((e) -> {
            accepted = true;
            setVisible(false); // Hide, allowing any one asking for accepted() to get their value
            dispose();  // Dispose window
        });

        JButton noButton = new JButton("No");
        noButton.setMargin(okButton.getMargin()); // Set margins to match okButton's
        addToButtonRow(noButton, (e) -> {
            setVisible(false); // Hide, allowing any one asking for accepted() to get their value
            dispose();  // Dispose window
        });

        initDialog("Alert", message);

    }

    public boolean accepted() {
        return accepted;
    }

}
