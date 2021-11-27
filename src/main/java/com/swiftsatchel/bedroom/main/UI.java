package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.FloatingSpinner;
import com.swiftsatchel.bedroom.dialog.alert.AlertDialog;
import com.swiftsatchel.bedroom.util.Ops;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JPanel implements ActionListener {

    private final BedroomWindow parent;

    // Components used outside of constructor
    private static final JTextArea stats = new JTextArea("Please clock in.\n\n");
    private static final JButton breakButton = new JButton("Set Break");
    private static final JButton addOrder = new JButton("Add Order");

    private final JMenuItem copyOrdersInfo;
    private final JMenuItem editOrders;

    public enum Buttons {
        ADD_ORDER,
        SET_BREAK,
        BOTH
    }

    public UI(BedroomWindow parent) { // Set UI's properties

        this.parent = parent;
        // Stats pop up menu components
        JPopupMenu statsPopup = new JPopupMenu("Stats");
        copyOrdersInfo = new JMenuItem("Copy orders/hr");
        editOrders = new JMenuItem("Set orders to...");

        setFocusable(true);
        addKeyListener(parent);

        // Init popup menu
        copyOrdersInfo.addActionListener(this);
        editOrders.addActionListener(this);
        statsPopup.add(copyOrdersInfo);
        statsPopup.add(editOrders);

        // Set components' properties
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        stats.addKeyListener(parent);
        stats.setComponentPopupMenu(statsPopup);
        addOrder.addKeyListener(parent);
        addOrder.addActionListener(this);
        addOrder.setMargin(new Insets(17, 24, 17, 24));
        breakButton.addKeyListener(parent);
        breakButton.addActionListener(this);
        breakButton.setToolTipText("<html><b>Currently no break is set</b></html>"); // Default tooltip
        breakButton.setMargin(new Insets(17, 24, 17, 24));

        // Add components
        add(breakButton);
        add(addOrder);
        add(stats);

        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

    }

    public static void display(String message) {

        stats.setText(message);
        setTooltips();

    }

    static void setTooltips() {

        // Add Order's tool tips
        double neededForTarget = (double) Main.getTotalSecClockedIn()/3600 * Main.getTarget();
        StringBuilder sb;
        if (neededForTarget > Main.getOrders()) { // Tell us how many orders we need to reach our target

            sb = new StringBuilder();
            int amountMissing = (int) Math.ceil(neededForTarget - Main.getOrders());
            addOrder.setToolTipText(sb.append("<html><b>You are ")
                    .append(amountMissing)
                    .append(" order")
                    .append(Ops.isPlural(amountMissing))
                    .append(" behind your hourly target</b></html>").toString());

        } else if (!(Main.getOrders() > Main.getOrdersNeeded())) {
            addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");
        } else { // If we have gotten all the orders needed for our shift.
            addOrder.setToolTipText("<html><b>You are done for the day!</b></html>");
        }

        // Set Break's tool tips
        if (Main.breakTimesChosen()) { // If we have chosen break times, change the tooltip to them.

            sb = new StringBuilder();
            sb.append("<html><b>Current: ");
            // Start time:
            if (Main.isOvernightShift()) sb.append(Main.getBreakStart().getDayOfWeek().toString(), 0, 3).append(" ");
            Time.append12HrTimeTo(sb, Main.getBreakStart().toLocalTime());
            sb.append("-");
            // End time:
            if (Main.isOvernightShift()) sb.append(Main.getBreakEnd().getDayOfWeek().toString(), 0, 3).append(" ");
            Time.append12HrTimeTo(sb, Main.getBreakEnd().toLocalTime());
            sb.append("</b></html>");

            breakButton.setToolTipText(sb.toString());

        }

    }

    public void colorComponents() {

        Theme.color(addOrder, breakButton, stats);
        setBackground(Theme.getBgColor());

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(addOrder)) {
            Main.changeOrders(1);
        } else if (e.getSource().equals(breakButton)) {
            parent.enterBreak();
        } else if (e.getSource().equals(copyOrdersInfo)) {
            StringSelection ordersPerHr = new StringSelection(Main.getOrdersPerHour());
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(ordersPerHr, ordersPerHr);
        } else if (e.getSource().equals(editOrders)) {
            if (Main.getOrders() < 999) {
                Main.setOrders(new FloatingSpinner(Main.getOrders(),
                        0, 999).showSelf());
                parent.pack(); // Update UI and window size for new number
            } else new AlertDialog(parent, """
                    You have way too many orders,
                    you are worth so much more
                    than they are paying you.""");
        }

    }

    public void enableButtons() {
        addOrder.setEnabled(true);
        breakButton.setEnabled(true);
    }

    public void disableButtons(Buttons b) {
        addOrder.setEnabled(!(b == Buttons.ADD_ORDER || b == Buttons.BOTH));
        breakButton.setEnabled(!(b == Buttons.SET_BREAK || b == Buttons.BOTH));
    }

}
