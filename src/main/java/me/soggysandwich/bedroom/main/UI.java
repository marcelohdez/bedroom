package me.soggysandwich.bedroom.main;

import me.soggysandwich.bedroom.Bedroom;
import me.soggysandwich.bedroom.dialog.FloatingSpinner;
import me.soggysandwich.bedroom.dialog.alert.AlertDialog;
import me.soggysandwich.bedroom.util.Ops;
import me.soggysandwich.bedroom.util.Theme;
import me.soggysandwich.bedroom.util.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UI extends JPanel {

    // Components
    private final JTextArea stats = new JTextArea("Please clock in.\n\n");
    private final JButton breakButton = new JButton("Set Break");
    private final JButton addOrder = new JButton("Add Order");

    public enum Buttons {
        ADD_ORDER,
        SET_BREAK,
        BOTH
    }

    public UI(BedroomWindow parent) { // Set UI's properties
        addKeyListener(parent);

        // Right click menu components
        JPopupMenu statsPopup = new JPopupMenu("Stats");
        JMenuItem copyOrdersInfo = new JMenuItem("Copy orders/hr");
        JMenuItem editOrders = new JMenuItem("Set orders to...");

        // Init popup menu
        statsPopup.add(copyOrdersInfo);
        statsPopup.add(editOrders);

        copyOrdersInfo.addActionListener((e -> {
            StringSelection ordersPerHr = new StringSelection(Bedroom.getOrdersPerHour());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ordersPerHr, ordersPerHr);
        }));
        editOrders.addActionListener((e) -> {
            if (Bedroom.getOrders() < 999) {
                Bedroom.setOrders(new FloatingSpinner(Bedroom.getOrders(),
                        0, 999).showSelf(), true);
                parent.pack(); // Update UI and window size for new number
            } else new AlertDialog(parent, """
                    You have way too many orders,
                    you are worth so much more
                    than they are paying you.""");
        });

        // Set components' properties
        stats.setEditable(false);
        stats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        stats.addKeyListener(parent);
        stats.setComponentPopupMenu(statsPopup);
        addOrder.addKeyListener(parent);
        addOrder.addActionListener((e) -> Bedroom.setOrders(Bedroom.getOrders() + 1, true)); // Add an order
        addOrder.setMargin(new Insets(17, 24, 17, 24));
        breakButton.addKeyListener(parent);
        breakButton.addActionListener((e) -> parent.enterBreak());
        breakButton.setToolTipText("<html><b>Currently no break is set</b></html>"); // Default tooltip
        breakButton.setMargin(new Insets(17, 24, 17, 24));

        // Add components
        add(breakButton);
        add(addOrder);
        add(stats);

        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

    }

    public void display(String message) {
        stats.setText(message);
        setAddOrderToolTip();
        setBreakButtonToolTip();
        setStatsToolTip();
    }

    private void setAddOrderToolTip() {

        if (Bedroom.getOrdersLeftForTarget() > 0) { // Tell us how many orders we need to reach our target
            addOrder.setToolTipText("<html><b>You are $n orders behind your hourly target."
                    .replace("$n", String.valueOf(Bedroom.getOrdersLeftForTarget())));
        } else if (Bedroom.getOrders() > Bedroom.getOrdersNeeded()) {
            addOrder.setToolTipText("<html><b>You are done for the day!</b></html>");
        } else { // If we have gotten all the orders needed for our shift.
            addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");
        }

    }

    private void setBreakButtonToolTip() {

        if (Bedroom.breakTimesChosen()) { // If we have chosen break times, change the tooltip to them.
            breakButton.setToolTipText("<html><b>Current break: $s-$e</b></html>"
                    // Start time:
                    .replace("$s", (Bedroom.isOvernightShift() ?
                            Bedroom.getBreakStart().getDayOfWeek().toString().substring(0, 3) : "") +
                            Time.makeTime12Hour(Bedroom.getBreakStart().toLocalTime()))
                    // End time:
                    .replace("$e", (Bedroom.isOvernightShift() ?
                            Bedroom.getBreakEnd().getDayOfWeek().toString().substring(0, 3) : "") +
                            Time.makeTime12Hour(Bedroom.getBreakEnd().toLocalTime())));
        }

    }

    private void setStatsToolTip() {

        if (Bedroom.getLastOrderChange() > 0) {
            long secondsSince = (System.currentTimeMillis() - Bedroom.getLastOrderChange()) / 1000;

            stats.setToolTipText("<html><b>Last order change was $ts ago</b></html>"
                    .replace("$t", Time.secondsToTime(secondsSince)));
        }

    }

    public void colorComponents() {
        Theme.color(this, addOrder, breakButton, stats);
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
