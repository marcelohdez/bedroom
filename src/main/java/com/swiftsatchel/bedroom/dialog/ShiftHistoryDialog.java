package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.components.ShiftHistoryChart;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class ShiftHistoryDialog extends JDialog implements ActionListener, KeyListener, ItemListener {

    private final WindowParent parent;

    private final ShiftHistoryChart chart = new ShiftHistoryChart();

    // ======= Top panel components =======
    private final JPanel topRow = new JPanel(); // The panel itself
    private final JLabel showingLabel = new JLabel("Showing ");
    private final JComboBox<Integer> ptsAmount = new JComboBox<>(new Integer[]{chart.getPointsAmount(), 15, 25});
    private final JLabel shiftsLabel = new JLabel(" shifts");
    private final JButton leftButton = new JButton("<");
    private final JLabel pagesLabel = new JLabel("Page 1/1");
    private final JButton rightButton = new JButton(">");

    public ShiftHistoryDialog(WindowParent parent) {
        this.parent = parent;

        init(); // Initialize everything
        updatePageAmount(); // Get correct page numbers and disable left/right buttons as needed
        pack(); // Let swing size window appropriately with the components added
        setMinimumSize(new Dimension(getWidth(), getWidth())); // Set that width to the minimum size

        centerOnParent();

        Ops.setHandCursorOnCompsFrom((JPanel) getContentPane()); // Add hand cursor to needed components
        setVisible(true); // Show dialog

    }

    private void init() {

        // Set window properties
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Retain input from other windows
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        ///setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Shift Performance History");

        // Apply listeners to needed components
        addKeyListener(this); // Add key listener to self
        ptsAmount.addItemListener(this);
        leftButton.addActionListener(this);
        rightButton.addActionListener(this);

        // Add to their respective places
        topRow.add(showingLabel);
        topRow.add(ptsAmount);
        topRow.add(shiftsLabel);
        topRow.add(leftButton);
        topRow.add(pagesLabel);
        topRow.add(rightButton);
        add(topRow, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);

        // Color components
        Theme.colorThese(new JComponent[]{topRow, showingLabel, ptsAmount, shiftsLabel,
                pagesLabel, leftButton, rightButton, chart});

    }

    private void centerOnParent() {

        int[] xyWidthHeight = parent.getXYWidthHeight();
        setLocation(xyWidthHeight[0] + ((xyWidthHeight[2]/2) - (getWidth()/2)),
                xyWidthHeight[1]  + ((xyWidthHeight[3]/2) - (getHeight()/2)));

    }

    private void updatePageAmount() {

        pagesLabel.setText("Page " + chart.getCurrentPage() + "/" + chart.getTotalPages());

        leftButton.setEnabled(chart.getCurrentPage() != 1); // Disable left button if on first page
        rightButton.setEnabled(chart.getCurrentPage() != chart.getTotalPages()); // Disable right button if on last page

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(leftButton)) chart.prevPage();
        if (e.getSource().equals(rightButton)) chart.nextPage();
        updatePageAmount();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose(); // Close self with Escape
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(ptsAmount))
            chart.setPointsAmount((int) Objects.requireNonNull(ptsAmount.getSelectedItem()));

        repaint();
        updatePageAmount();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

}
