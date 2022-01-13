package me.soggysandwich.bedroom.dialog.history;

import me.soggysandwich.bedroom.dialog.alert.AlertDialog;
import me.soggysandwich.bedroom.dialog.alert.YesNoDialog;
import me.soggysandwich.bedroom.util.Ops;
import me.soggysandwich.bedroom.util.Settings;
import me.soggysandwich.bedroom.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;

public class ShiftHistoryWindow extends JFrame implements KeyListener, WindowListener, MouseListener {

    private final Component parent;
    private final ShiftHistoryChart chart = new ShiftHistoryChart();

    private final JLabel datesShown = new JLabel("None");
    private final JButton firstButton = new JButton("|<<");
    private final JButton leftButton = new JButton("<");
    private final JLabel pagesLabel = new JLabel("Page 1/1");
    private final JButton rightButton = new JButton(">");
    private final JButton lastButton = new JButton(">>|");

    private final JPanel botRow = new JPanel(); // Bottom row panel

    private int clickedDateIndex = -1;
    private boolean isShifting = false;

    public ShiftHistoryWindow(Component parent) {
        this.parent = parent;
        if (parent instanceof JDialog &&
                ((JDialog) parent).getModalityType() == Dialog.ModalityType.APPLICATION_MODAL) {
            parent.setVisible(false);
        } else parent.setEnabled(false); // Disable parent window
        chart.addMouseListener(this);

        // Set window properties
        setAlwaysOnTop(Settings.getAlwaysOnTop());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Shift Performance History");
        addWindowListener(this);
        addKeyListener(this);
        init(); // Initialize everything
        updatePageInfo(); // Get correct page numbers and disable left/right buttons as needed
        pack();
        setResponsiveSizes();

        Ops.setHandCursorOnCompsFrom(getContentPane()); // Add hand cursor to needed components
        setLocationRelativeTo(parent); // Center on parent window
        setVisible(true); // Show dialog
    }

    private void setResponsiveSizes() {
        Dimension minSize = new Dimension((getWidth() - firstButton.getWidth() - lastButton.getWidth()),
                (int) (getWidth()/1.7));
        Dimension minComfortableSize = new Dimension((int) (getWidth()*1.05), (int) (getWidth()/1.5));
        setMinimumSize(minSize);
        setSize(minComfortableSize);

        addComponentListener(new ComponentAdapter() { // Check for window resizing
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                // Hide first and last page buttons when they do not fit.
                firstButton.setVisible(!(getWidth() < minComfortableSize.getWidth()));
                lastButton.setVisible(!(getWidth() < minComfortableSize.getWidth()));
            }
        });
    }

    private String[] getAllowedAmounts() {
        ArrayList<String> amounts = new ArrayList<>();

        amounts.add("8"); // View of 8 will always be available
        if (chart.totalDates() > 8) {
            if (chart.totalDates() > 16) {
                amounts.add("16");
                if (chart.totalDates() > 32) {
                    amounts.add("32");
                }
            }
            amounts.add("All");
        }

        return amounts.toArray(new String[0]);
    }

    private void init() {
        JPanel topRow = new JPanel();

        JComboBox<String> ptsAmount = new JComboBox<>(getAllowedAmounts());
        JButton historyFolderButton = new JButton("Open history directory");
        // Right-click menu stuffs:
        JPopupMenu delMenu = new JPopupMenu();
        JMenuItem deleteDate = new JMenuItem("Delete");

        // Apply listeners to needed components
        initPageButtons();
        ptsAmount.addKeyListener(this);
        ptsAmount.addItemListener(e -> {
            if (ptsAmount.getSelectedIndex() == (ptsAmount.getItemCount() - 1)) { // "All" is always last item on list
                chart.showAll();
            } else if (ptsAmount.getSelectedItem() != null)
                chart.showAmount(Float.parseFloat((String) ptsAmount.getSelectedItem()));

            chart.repaint();
            updatePageInfo();
        });
        historyFolderButton.addKeyListener(this);
        historyFolderButton.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
            try {
                openHistoryDirectory();
            } catch (Exception ex) { ex.printStackTrace(); }
        }));
        deleteDate.addKeyListener(this);
        deleteDate.addActionListener(e -> {
            if (clickedDateIndex >= 0) {
                if (new YesNoDialog(this, """
                        Are you sure you want to
                        delete your shift from
                        $d?"""
                        .replace("$d", chart.getDateAt(clickedDateIndex)
                                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)).toUpperCase(Locale.ROOT)))
                        .accepted()) {

                    chart.deleteDateAt(clickedDateIndex);
                    updatePageInfo();
                }
            } else new AlertDialog(null, "No existing date selected");
        });

        // Add to their respective places
        delMenu.add(deleteDate);
        chart.setComponentPopupMenu(delMenu);
        topRow.add(new JLabel("Data points to show:"));
        topRow.add(ptsAmount);
        topRow.add(datesShown);

        topRow.add(firstButton);
        topRow.add(leftButton);
        topRow.add(pagesLabel);
        topRow.add(rightButton);
        topRow.add(lastButton);

        botRow.add(historyFolderButton);
        add(topRow, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        add(botRow, BorderLayout.SOUTH);

        // Color bottom row a bit brighter than the background:
        if (!Settings.isSystemLAFEnabled())
            botRow.setBackground(Theme.contrastWithShade(
                    Theme.getBgColor(), Settings.isContrastEnabled() ? 200 : 20));

    }

    /** Adds action listeners to page changing buttons */
    private void initPageButtons() {
        firstButton.addKeyListener(this);
        firstButton.addActionListener(e -> doJobThenUpdate(chart::oldestPage));
        leftButton.addKeyListener(this);
        leftButton.addActionListener(e -> doJobThenUpdate(chart::prevPage));
        rightButton.addKeyListener(this);
        rightButton.addActionListener(e -> doJobThenUpdate(chart::nextPage));
        lastButton.addKeyListener(this);
        lastButton.addActionListener(e -> doJobThenUpdate(chart::newestPage));
    }

    private void doJobThenUpdate(Runnable job) {
        job.run();
        updatePageInfo();
    }

    public void updatePageInfo() {

        pagesLabel.setText("Page " + chart.page() + "/" + chart.totalPages());
        // Disable previous-page-going buttons if on first page
        firstButton.setEnabled(chart.page() != 1);
        leftButton.setEnabled(chart.page() != 1);
        // Disable next-page-going buttons if on last page
        rightButton.setEnabled(chart.page() != chart.totalPages());
        lastButton.setEnabled(chart.page() != chart.totalPages());
        datesShown.setText(chart.pageDateRange());

    }

    /** Open working directory in system's explorer */
    private void openHistoryDirectory() throws IOException {
        try {
            // Create instance of history file to select it in explorer
            File shiftHistoryFile = new File(Settings.getWorkingDir() + File.separator + "shift.history");
            Desktop.getDesktop().browseFileDirectory(shiftHistoryFile); // Only works on macOS and Win7/8 :(

        } catch (Exception e) {
            if (System.getProperty("os.name").contains("Windows")) {
                // Due to browseFileDirectory not working on Windows10+ we use specific commands:
                Runtime.getRuntime().exec("explorer \"$d\"".replace("$d", Settings.getWorkingDir()));
            } else new AlertDialog(null, """
                        Unable to open directory, this
                        desktop's file explorer is not
                        supported, wanted directory:
                        
                        """ + limitLineLength(Settings.getWorkingDir()));
        }
    }

    private String limitLineLength(String s) {
        // Get how many times 30 goes into the string length
        int limit = 30;
        int divisions = s.length() / limit;

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < divisions) { // Append all *limit* length lines:
            sb.append(s, i * limit, (i * limit) + limit).append("\n");
            i++;
        } // Then append what's left of the last line:
        sb.append(s, i * limit, (i * limit) + s.length() % limit);

        return sb.toString();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT -> isShifting = true;
            case KeyEvent.VK_ESCAPE -> dispose();
            case KeyEvent.VK_LEFT -> {
                if (isShifting) {
                    doJobThenUpdate(chart::oldestPage);
                } else doJobThenUpdate(chart::prevPage);
            }
            case KeyEvent.VK_RIGHT -> {
                if (isShifting) {
                    doJobThenUpdate(chart::newestPage);
                } else doJobThenUpdate(chart::nextPage);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) isShifting = false;
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (!parent.isVisible()) {
            parent.setVisible(true);
        } else parent.setEnabled(true); // Re-enable the summoner window
        parent.requestFocus();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickedDateIndex = chart.getDateFromBarAt(e.getX());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clickedDateIndex = chart.getDateFromBarAt(e.getX());
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
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
