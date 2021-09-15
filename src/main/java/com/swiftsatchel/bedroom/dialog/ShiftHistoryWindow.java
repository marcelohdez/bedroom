package com.swiftsatchel.bedroom.dialog;

import com.swiftsatchel.bedroom.components.ShiftHistoryChart;
import com.swiftsatchel.bedroom.dialog.alert.ErrorDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ShiftHistoryWindow extends JFrame implements ActionListener, KeyListener, ItemListener, WindowListener,
        WindowParent {

    private final WindowParent parent;

    private final ShiftHistoryChart chart = new ShiftHistoryChart(this);

    // ======= Top panel components =======
    private final JPanel topRow = new JPanel(); // The panel itself
    private final JLabel showingLabel = new JLabel("Data points to show:");
    private final JComboBox<Integer> ptsAmount = new JComboBox<>(new Integer[]{chart.getPointsAmount(), 12, 16});
    private final JButton leftButton = new JButton("<");
    private final JLabel pagesLabel = new JLabel("Page 1/1");
    private final JButton rightButton = new JButton(">");
    private final JLabel datesShown = new JLabel("None");

    private final JPanel botRow = new JPanel(); // Bottom row panel
    private final JButton historyFolderButton = new JButton("Open history directory");

    public ShiftHistoryWindow(WindowParent parent) {
        this.parent = parent;

        parent.setDisabled(true); // Disable parent window
        addWindowListener(this);
        init(); // Initialize everything
        updatePageInfo(); // Get correct page numbers and disable left/right buttons as needed
        packAndSize();

        centerOnParent();

        Ops.setHandCursorOnCompsFrom((JPanel) getContentPane()); // Add hand cursor to needed components
        setVisible(true); // Show dialog

    }

    private void init() {

        // Set window properties
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        ///setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Shift Performance History");

        // Apply listeners to needed components
        addKeyListener(this); // Add key listener to self
        ptsAmount.addItemListener(this);
        leftButton.addActionListener(this);
        rightButton.addActionListener(this);
        historyFolderButton.addActionListener(this);

        // Add to their respective places
        topRow.add(showingLabel);
        topRow.add(ptsAmount);
        topRow.add(datesShown);
        topRow.add(leftButton);
        topRow.add(pagesLabel);
        topRow.add(rightButton);
        botRow.add(historyFolderButton);
        add(topRow, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        add(botRow, BorderLayout.SOUTH);

        // Color components
        Theme.colorThese(new JComponent[]{topRow, showingLabel, ptsAmount, pagesLabel,
                leftButton, rightButton, datesShown, chart, historyFolderButton});
        botRow.setBackground(Theme.contrastWithShade(Theme.getBgColor(), 40));

    }

    private void packAndSize() {

        Dimension last = getSize(); // Get size before packing
        pack(); // Let swing size window appropriately with updated components
        Dimension minimum = new Dimension(getWidth(), (int) (getWidth() /1.5)); // Store new minimum
        setMinimumSize(minimum); // Set new minimum size

        if (last.getWidth() > minimum.getWidth() && last.getHeight() > minimum.getHeight()) {
            setSize(last); // If it was greater than the new minimum, set size back to where it was.
        } else if (last.getWidth() > minimum.getWidth()) {
            setSize(new Dimension((int)last.getWidth(), (int)minimum.getHeight()));
        } else if (last.getHeight() > minimum.getHeight())
            setSize(new Dimension((int)minimum.getWidth(), (int)last.getHeight()));

    }

    private void centerOnParent() {

        setLocation(parent.getXYWidthHeight()[0] + ((parent.getXYWidthHeight()[2] / 2) - (getWidth() / 2)),
                parent.getXYWidthHeight()[1] + ((parent.getXYWidthHeight()[3] / 2) - (getHeight() / 2)));

    }

    private void updatePageInfo() {

        pagesLabel.setText("Page " + chart.getCurrentPage() + "/" + chart.getTotalPages());
        leftButton.setEnabled(chart.getCurrentPage() != 1); // Disable left button if on first page
        rightButton.setEnabled(chart.getCurrentPage() != chart.getTotalPages()); // Disable right button if on last page
        datesShown.setText(chart.getShownDates());

        packAndSize();

    }

    /**
     * Open working directory in system's explorer
     */
    private void openHistoryDirectory() {

        try {
            // Create instance of history file to select it in explorer
            File shiftHistoryFile = new File(Settings.getWorkingDir() + File.separator + "shift.history");

            if (!System.getProperty("os.name").contains("Windows")) { // Check if we are not on Windows
                Desktop.getDesktop().browseFileDirectory(shiftHistoryFile);
            } else // Due to browseFileDirectory not working on Win10 we have to use a specific command:
                try {
                    Runtime.getRuntime().exec("explorer " + Settings.getWorkingDir());
                } catch (SecurityException | IOException e) { e.printStackTrace(); }

        } catch (SecurityException e) {  // If we encounter an exception:
            new ErrorDialog(this, ErrorType.CAN_NOT_OPEN_EXPLORER);
            e.printStackTrace();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(leftButton)) chart.prevPage();
        if (e.getSource().equals(rightButton)) chart.nextPage();
        if (e.getSource().equals(historyFolderButton)) openHistoryDirectory();
        updatePageInfo();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose(); // Close self with Escape
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(ptsAmount))
            chart.setPointsAmount((int) Objects.requireNonNull(ptsAmount.getSelectedItem()));

        updatePageInfo();
        repaint();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        parent.setDisabled(false);
        parent.askForFocus();
    }

    @Override
    public int[] getXYWidthHeight() {
        return new int[]{getX(), getY(), getWidth(), getHeight()};
    }

    @Override
    public void makeVisible(boolean b) {
        setVisible(b);
    }

    @Override
    public void setDisabled(boolean b) {
        setEnabled(b);
    }

    @Override
    public void askForFocus() {
        requestFocus();
    }

    // Unused
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void reloadSettings() {}

}
