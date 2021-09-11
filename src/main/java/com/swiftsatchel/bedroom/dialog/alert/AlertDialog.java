package com.swiftsatchel.bedroom.dialog.alert;

import com.swiftsatchel.bedroom.util.*;

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
    private final JPanel buttonRow;
    private JButton okButton;

    /**
     * Default Alert Dialog constructor, creates a dialog with a message and an ok button to dismiss.
     *
     * @param parent WindowParent to center on
     * @param message Message to display
     */
    public AlertDialog(WindowParent parent, String message) {
        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
        init("Alert", message, false);
    }

    /**
     * Alert Dialog constructor which can be initialized after the fact, for sub-classes with
     * different messages etc. (still has an OK button)
     *
     * @param parent WindowParent to center on
     */
    public AlertDialog(WindowParent parent) {
        this.parent = parent;
        buttonRow = new JPanel();
        okButton = new JButton("OK");
    }

    /**
     * Alert Dialog constructor for dialogs with custom buttons.
     * Setting isCustomDialog to false effectively makes a dialog with a message box
     * which can not be closed.
     *
     * @param parent WindowParent to center on
     * @param message Message to display
     * @param isCustomDialog Whether it is custom or not, for the init method
     */
    public AlertDialog(WindowParent parent, String message, boolean isCustomDialog) {
        this.parent = parent;
        buttonRow = new JPanel();
        init("Alert", message, isCustomDialog);
    }

    /**
     * Initialize the components and properties of this dialog
     *
     * @param title Title of dialog
     * @param message Message to display
     * @param isCustomDialog Does dialog have custom buttons?
     */
    protected void init(String title, String message, boolean isCustomDialog) {

        // Create components
        JPanel topUI = new JPanel();
        JTextArea messageBox = new JTextArea(message);

        // Customize components
        Theme.colorThese(new JComponent[]{topUI, buttonRow, messageBox});
        messageBox.setFont(Theme.getBoldFont());
        messageBox.setEditable(false);
        if (!isCustomDialog) okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on button

        // Add components
        topUI.add(messageBox);
        if (!isCustomDialog) addToButtonRow(okButton, this, false);
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
        if (!isCustomDialog) setVisible(true);

    }

    /**
     * Split width among buttons
     */
    private void sizeButtonRow() {
        pack(); // Make Swing size everything
        for (Component c : buttonRow.getComponents()) {
            // Set to width we want, and 1.5x teh height swing chose for font
            c.setPreferredSize(new Dimension(getWidth()/buttonRow.getComponentCount() -
                    (5*buttonRow.getComponentCount()), (int) (c.getHeight()*1.5)));
        }
        pack(); // Let Swing react accordingly
    }

    /**
     * Add to button row
     *
     * @param b Button
     * @param al The button's action listener
     * @param updateSizes Whether we update the sizes now/update the screen
     */
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
