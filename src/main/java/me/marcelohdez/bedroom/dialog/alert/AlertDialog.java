package me.marcelohdez.bedroom.dialog.alert;

import me.marcelohdez.bedroom.util.Theme;
import me.marcelohdez.bedroom.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A JDialog with a text area to include a message and an "OK" button to dismiss.
 * Can be used for alerts and for errors.
 */
public class AlertDialog extends JDialog {

    private final Component parent;
    private final JPanel buttonRow = new JPanel();
    protected final JButton okButton = new JButton("OK");

    /**
     * Default Alert Dialog constructor, creates a dialog with a message and an ok button to dismiss.
     *
     * @param parent Window parent, only used to center window after creating its UI
     * @param message Message to display
     */
    public AlertDialog(Component parent, String message) {
        this(parent);
        initDialog("Alert", message);
    }

    /**
     * Alert Dialog constructor which can be initialized after the fact.
     *
     * @param parent Window parent, only used to center window after creating its UI
     */
    protected AlertDialog(Component parent) {
        this.parent = parent;
        // Initialize okButton
        okButton.setMargin(new Insets(8, 20, 8, 20));
        addToButtonRow(okButton, (e) -> dispose());
    }

    /**
     * Initialize the components and properties of this dialog
     *
     * @param title Title of dialog
     * @param message Message to display
     */
    protected void initDialog(String title, String message) {

        // Create components
        JPanel topUI = new JPanel();
        JTextArea messageBox = new JTextArea(message);

        // Customize components
        messageBox.setFont(Theme.getBoldFont());
        messageBox.setEditable(false);

        // Add components
        topUI.add(messageBox);
        add(topUI, BorderLayout.CENTER);
        add(buttonRow, BorderLayout.SOUTH);

        // Set properties
        setTitle(title);
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        pack(); // Let swing size the window
        setLocationRelativeTo(parent); // Center on parent, if there is one, else center on screen
        setModalityType(ModalityType.APPLICATION_MODAL); // Retain input from other windows
        setVisible(true); // Show dialog

    }

    /**
     * Add given button to button row and give it the given ActionListener, the hand cursor, and then pack window
     *
     * @param b Button
     * @param al The button's action listener
     */
    protected void addToButtonRow(JButton b, ActionListener al) {
        b.addActionListener(al);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Set hand cursor on button
        buttonRow.add(b);
        pack();
    }

}
