package com.swiftsatchel.bedroom.dialog.history;

import com.swiftsatchel.bedroom.dialog.alert.ErrorDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShiftHistoryWindow extends JFrame implements KeyListener, WindowListener {

    private final WindowParent parent;
    private final ShiftHistoryChart chart = new ShiftHistoryChart(this);

    private final JPanel topRow = new JPanel();
    private final JLabel showingLabel = new JLabel("Data points to show:");
    private final JComboBox<String> ptsAmount = new JComboBox<>(getAllowedAmounts());
    private final JLabel datesShown = new JLabel("None");
    private final JButton leftButton = new JButton("<");
    private final JLabel pagesLabel = new JLabel("Page 1/1");
    private final JButton rightButton = new JButton(">");

    private final JPanel botRow = new JPanel(); // Bottom row panel
    private final JButton historyFolderButton = new JButton("Open history directory");

    public ShiftHistoryWindow(WindowParent parent) {
        this.parent = parent;

        parent.setDisabled(true); // Disable parent window
        addWindowListener(this);
        init(); // Initialize everything
        updatePageInfo(); // Get correct page numbers and disable left/right buttons as needed
        pack();
        setMinimumSize(new Dimension((int) (getWidth()*1.1), (int) (getWidth()/1.4)));
        // Center on parent
        int[] arr = parent.getXYWidthHeight();
        setLocation(arr[0] + ((arr[2] / 2) - (getWidth() / 2)), arr[1] + ((arr[3] / 2) - (getHeight() / 2)));

        Ops.setHandCursorOnCompsFrom(getContentPane()); // Add hand cursor to needed components
        setVisible(true); // Show dialog

    }

    private String[] getAllowedAmounts() {

        ArrayList<String> amounts = new ArrayList<>();
        amounts.add("8"); // View of 8 will always be available
        if (chart.getTotalDates() >= 8) {
            if (chart.getTotalDates() >= 16) {
                amounts.add("16");
                if (chart.getTotalDates() >= 32) {
                    amounts.add("32");
                }
            }
            amounts.add("All");
        }

        return amounts.toArray(new String[0]);

    }

    private void init() {

        // Set window properties
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        ///setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Shift Performance History");

        // Apply listeners to needed components
        addKeyListener(this); // Add key listener to self
        ptsAmount.addItemListener((e) -> {
            if (ptsAmount.getSelectedIndex() == (ptsAmount.getItemCount() - 1)) { // "All" is always last item on list
                chart.setPointsAmountToAll();
            } else if (ptsAmount.getSelectedItem() != null)
                chart.setPointsAmount(Integer.parseInt((String) ptsAmount.getSelectedItem()));

            chart.repaint();
        });

        leftButton.addActionListener((e) -> chart.prevPage());
        rightButton.addActionListener((e) -> chart.nextPage());
        historyFolderButton.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
            try {
                openHistoryDirectory();
            } catch (Exception ex) { ex.printStackTrace(); }
        }));

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

        // Color bottom row:
        botRow.setBackground(Theme.contrastWithShade(Theme.getBgColor(),
                Settings.isContrastEnabled() ? 200 : 20));

    }

    public void updatePageInfo() {

        pagesLabel.setText("Page " + chart.getCurrentPage() + "/" + chart.getTotalPages());
        leftButton.setEnabled(chart.getCurrentPage() != 1); // Disable left button if on first page
        rightButton.setEnabled(chart.getCurrentPage() != chart.getTotalPages()); // Disable right button if on last page
        datesShown.setText(chart.getShownDateRange());

    }

    /**
     * Open working directory in system's explorer
     */
    private void openHistoryDirectory() throws IOException {

        // Create instance of history file to select it in explorer
        File shiftHistoryFile = new File(Settings.getWorkingDir() + File.separator + "shift.history");

        try {
            Desktop.getDesktop().browseFileDirectory(shiftHistoryFile); // Only works on macOS and Win7/8 :(
        } catch (Exception e) {
            // Due to browseFileDirectory not working on these OSs use specific commands:
            if (System.getProperty("os.name").contains("Windows")) { // Win10+

                Runtime.getRuntime().exec("explorer \"$d\"".replace("$d", Settings.getWorkingDir()));

            } else if (System.getProperty("os.name").contains("Linux")) { // Linux

                Runtime.getRuntime().exec("gio open \"$d\"".replace("$d", Settings.getWorkingDir()));

            } else new ErrorDialog(null, ErrorType.EXPLORER_UNSUPPORTED, shiftHistoryFile.toString());
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose(); // Close self with Escape
    }

    @Override
    public void windowClosed(WindowEvent e) {
        parent.setDisabled(false);
        parent.askForFocus();
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

}
