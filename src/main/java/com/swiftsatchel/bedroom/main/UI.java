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

public class UI extends JPanel {

    // Components used outside of constructor
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
            StringSelection ordersPerHr = new StringSelection(Main.getOrdersPerHour());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ordersPerHr, ordersPerHr);
        }));
        editOrders.addActionListener((e) -> {
            if (Main.getOrders() < 999) {
                Main.setOrders(new FloatingSpinner(Main.getOrders(),
                        0, 999).showSelf());
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
        addOrder.addActionListener((e) -> Main.setOrders(Main.getOrders() + 1));
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
        setTooltips();
    }

    private void setTooltips() {

        // Add Order button's tool tips
        if (Main.getOrdersNeededForTarget() > Main.getOrders()) { // Tell us how many orders we need to reach our target

            addOrder.setToolTipText("<html><b>You are $n orders behind your hourly target."
                    .replace("$n", String.valueOf(Main.getOrdersNeededForTarget())));

        } else if (Main.getOrders() > Main.getOrdersNeeded()) {
            addOrder.setToolTipText("<html><b>You are done for the day!</b></html>");
        } else { // If we have gotten all the orders needed for our shift.
            addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");
        }

        // Set Break button's tool tip
        if (Main.breakTimesChosen()) { // If we have chosen break times, change the tooltip to them.
            breakButton.setToolTipText("<html><b>Current break: $s-$e</b></html>"
                    // Start time:
                    .replace("$s", (Main.isOvernightShift() ?
                            Main.getBreakStart().getDayOfWeek().toString().substring(0, 3) : "") +
                            Time.makeTime12Hour(Main.getBreakStart().toLocalTime()))
                    // End time:
                    .replace("$e", (Main.isOvernightShift() ?
                            Main.getBreakEnd().getDayOfWeek().toString().substring(0, 3) : "") +
                            Time.makeTime12Hour(Main.getBreakEnd().toLocalTime())));
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
