package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
import com.swiftsatchel.bedroom.enums.TimeWindowType;
import com.swiftsatchel.bedroom.settings.SettingsDialog;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class UI extends JPanel implements ActionListener, KeyListener {

    private final BedroomWindow parent;

    // Components used outside of constructor
    private static final JTextArea stats = new JTextArea("Please clock in.\n\n");
    private static final JButton breakButton = new JButton("Set Break");
    private static final JButton addOrder = new JButton("Add Order");

    // ======= Public reusable colors & fonts =======
    public static Font boldText = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    // Get colors for UI from user's preferences
    public static Color textColor = loadColorOf("text", 240);
    // Get color of button text
    public static Color buttonTextColor = loadColorOf("buttonText", 240);
    // Get color of buttons
    public static Color buttonColor = loadColorOf("button", 80);
    // Get color of background
    public static Color bg = loadColorOf("bg", 64);

    public UI(BedroomWindow parent) { // Set UI's properties

        this.parent = parent;

        setFocusable(true);
        addKeyListener(this);

        // Set components' properties
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        stats.addKeyListener(this);
        addOrder.addKeyListener(this);
        addOrder.addActionListener(this);
        breakButton.addKeyListener(this);
        breakButton.addActionListener(this);
        breakButton.setToolTipText("<html><b>Currently no break is set</b></html>"); // Default tooltip

        // Set colors
        colorComponents();

        // Add components
        add(breakButton);
        add(addOrder);
        add(stats);

        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

        Main.updateStats();

    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Add Order" -> Main.changeOrders(1);
            case "Set Break" -> enterBreak();
        }

    }

    private void enterBreak() {
        new SelectTimeDialog(parent, TimeWindowType.START_BREAK);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        // ======= Shortcuts =======
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN ->
                    Main.changeOrders(-1); // Remove orders with BckSpc & Down Arrow
            case KeyEvent.VK_0 -> enterBreak();             // Set break times with 0
            case KeyEvent.VK_UP -> Main.changeOrders(1); // Add orders with up arrow
            case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE ->
                    new SettingsDialog(parent);  // Open settings with Delete or Backspace keys
        }
    }

    public static void display(String message) {

        stats.setText(message);
        setTooltips();

    }

    static void setTooltips() {

        // Add Order's tool tips
        double neededForTarget = (double) Main.totalSecClockedIn/3600 * Main.target;
        StringBuilder sb;
        if (neededForTarget > Main.orders) { // Tell us how many orders we need to reach our target

            sb = new StringBuilder();
            int amountMissing = (int) Math.round(Math.ceil(neededForTarget - Main.orders));
            addOrder.setToolTipText(sb.append("<html><b>You are ")
                    .append(amountMissing)
                    .append(" order")
                    .append(Ops.isPlural(amountMissing))
                    .append(" behind your hourly target</b></html>").toString());

        } else addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");

        // Set Break's tool tips
        if (Main.breakTimesChosen) { // If we have chosen break times, change the tooltip to them.

            sb = new StringBuilder();
            sb.append("<html><b>Current: ");
            Time.append12HrTimeTo(sb, Main.breakInTime.toLocalTime());
            sb.append("-");
            Time.append12HrTimeTo(sb, Main.breakOutTime.toLocalTime());
            sb.append("</b></html>");

            breakButton.setToolTipText(sb.toString());

        }

    }

    private static Color loadColorOf(String component, int def) {

        return new Color(Main.userPrefs.getInt(component + "Red", def),
                Main.userPrefs.getInt(component + "Green", def),
                Main.userPrefs.getInt(component + "Blue", def));

    }

    public void reloadColors() {

        textColor = loadColorOf("text", 240);
        buttonTextColor = loadColorOf("buttonText", 240);
        buttonColor = loadColorOf("button", 80);
        bg = loadColorOf("bg", 64);

        colorComponents();

    }

    private void colorComponents() {

        Theme.colorThis(breakButton);
        Theme.colorThis(addOrder);
        Theme.colorThis(stats);
        setBackground(bg);

    }

    void sizeButtons() {

        // Get the largest width * 1.2 for some buffer
        int length = (int) ((Math.max(addOrder.getWidth(), breakButton.getWidth())) * 1.2);
        // Set the buttons to that width, and half that for height to make identical rectangles
        addOrder.setPreferredSize(new Dimension(length, length/2));
        breakButton.setPreferredSize(new Dimension(length, length/2));

    }

}
