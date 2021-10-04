package com.swiftsatchel.bedroom.main;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.components.FloatingSpinner;
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
        breakButton.addKeyListener(parent);
        breakButton.addActionListener(this);
        breakButton.setToolTipText("<html><b>Currently no break is set</b></html>"); // Default tooltip

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
            int amountMissing = (int) Math.round(Math.ceil(neededForTarget - Main.getOrders()));
            addOrder.setToolTipText(sb.append("<html><b>You are ")
                    .append(amountMissing)
                    .append(" order")
                    .append(Ops.isPlural(amountMissing))
                    .append(" behind your hourly target</b></html>").toString());

        } else addOrder.setToolTipText("<html><b>You are on track with your hourly target</b></html>");

        // Set Break's tool tips
        if (Main.breakTimesChosen()) { // If we have chosen break times, change the tooltip to them.

            sb = new StringBuilder();
            sb.append("<html><b>Current: ");
            Time.append12HrTimeTo(sb, Main.getBreakStart().toLocalTime());
            sb.append("-");
            Time.append12HrTimeTo(sb, Main.getBreakEnd().toLocalTime());
            sb.append("</b></html>");

            breakButton.setToolTipText(sb.toString());

        }

    }

    public void colorComponents() {

        Theme.color(addOrder, breakButton, stats);
        setBackground(Theme.getBgColor());

    }

    void sizeButtons() {

        // Get height of stats text box (minus 2 for some buffer) to base buttons off of that:
        int size = stats.getHeight() - 2;
        addOrder.setPreferredSize(new Dimension(size*2, size));
        breakButton.setPreferredSize(new Dimension(size*2, size));
                //(int) (size*1.8), size));

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
        if (b == Buttons.BOTH) {
            addOrder.setEnabled(false);
            breakButton.setEnabled(false);
        } else if (b == Buttons.ADD_ORDER) {
            addOrder.setEnabled(false);
        } else if (b == Buttons.SET_BREAK) {
            breakButton.setEnabled(false);
        }
    }

}
