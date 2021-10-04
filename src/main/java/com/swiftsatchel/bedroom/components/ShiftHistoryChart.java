package com.swiftsatchel.bedroom.components;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.ShiftHistoryWindow;
import com.swiftsatchel.bedroom.dialog.alert.YesNoDialog;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.Theme;
import com.swiftsatchel.bedroom.util.WindowParent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * A bar graph showing the orders per hour user ended with on existing shifts
 */
public class ShiftHistoryChart extends JPanel implements ActionListener, MouseListener {

    private final ShiftHistoryWindow container;

    private boolean noHistory = true;
    private int pointsAmount = 8; // Amount of data points to show

    private final TreeMap<LocalDate, Float> shiftHistoryData = Main.getShiftHistory();
    private LocalDate[] keys = cleanUpKeys();
    private int currentPage = 1;
    private int totalPages = 1;
    private final Color barColor = Theme.getTextColor(); // Constant bar color

    private float range;
    private int barXDiff;

    private final JMenuItem deleteDate = new JMenuItem("Delete");
    private LocalDate currentlyObserved;

    public ShiftHistoryChart(ShiftHistoryWindow container) {
        this.container = container;
        addMouseListener(this);

        JPopupMenu delMenu = new JPopupMenu();
        deleteDate.addActionListener(this);
        delMenu.add(deleteDate);
        setComponentPopupMenu(delMenu);

        updatePages(); // Update page numbers

    }

    @Override
    public void paintComponent(Graphics graphics) { // Drawing the graph
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics; // Cast Graphics object to Graphics2D

        g.setFont(Theme.getChartFont()); // Set to font we'll use
        updatePages();
        if (!noHistory) {
            range = getCurrentRange();
            barXDiff = getBarXDiff();
            drawRangeLines(g);
            drawBars(g);
        } else {

            String text = Settings.isDoneLoadingShiftHistory() ? // Are we done loading the history?
                    "There is no data to be shown." : "Still loading data, please reopen."; // If not inform user.

            g.drawString(text,
                    getWidth()/2 - g.getFontMetrics().stringWidth(text)/2,
                    getHeight()/2 - g.getFont().getSize()/2);

        }

    }

    private void updatePages() {
        if (!noHistory) {
            totalPages = (int) Math.ceil((double) keys.length / (double) pointsAmount);
            if (currentPage > totalPages) currentPage = totalPages;
        }
    }

    /**
     * Clean up the shift history's keys to keep only valid values
     *
     * @return Keys with valid values (is a number and is not null)
     */
    private LocalDate[] cleanUpKeys() {

        if (shiftHistoryData != null) {

            ArrayList<LocalDate> cleanedList = new ArrayList<>();
            for (LocalDate key : shiftHistoryData.keySet()) {
                if (shiftHistoryData.get(key) != null && !shiftHistoryData.get(key).isNaN()) {
                    cleanedList.add(key);
                }
            }
            noHistory = !(cleanedList.size() > 0);

            return cleanedList.toArray(new LocalDate[0]); // Convert cleanedList to LocalDate array

        } else {

            noHistory = true;
            return new LocalDate[0];

        }

    }

    /**
     * Update keys list
     */
    private void getNewKeys() {
        keys = cleanUpKeys();
    }

    /**
     * Draw performance history bars
     *
     * @param g Graphics2D object to draw with
     */
    private void drawBars(Graphics2D g) {
        // Difference in X coordinates between bars
        int emptySpaces = 0;    // Amount of NaN values, to ignore them when drawing the bars
        int lastMonth = 0;      // Keep track of last month value to only put month name when changed
        for (int point = 0; point < pointsAmount; point++) { // For each point:

            int index = pointsAmount * (currentPage - 1) + point; // Get actual index by adding the offset
            // If index exists get its value, else default to negative one.
            float value = (index < keys.length) ? shiftHistoryData.get(keys[index]) : -1F;

            if (value != -1F) { // draw the bar (a rectangle) if value is not -1 (to filter out nonexistent values)

                int top = (int) (getHeight() - ((getHeight() / range) * value)); // Top of current bar
                // Get x of bar plus initial offset
                int x = (int) ((g.getFont().getSize() * 1.5) +
                        (barXDiff / 2) + (barXDiff * (point - emptySpaces) - ((barXDiff - 1) / 2)));

                // Draw bar
                g.setColor(barColor);
                g.fillRect(x, top, (barXDiff - 1), getHeight() - top);

                // Draw bar value and date
                drawBarValue(g, keys[index].getMonthValue() != lastMonth, value, x, top);
                lastMonth = drawDate(g, index, x, lastMonth);

            } else emptySpaces++; // Else add as a spot to ignore on next data point
        }
    }

    /**
     * Get current amount of data points being shown on page.
     *
     * @return Amount of data points on-screen
     */
    private int getPointsBeingShown() {

        int p = 0; // Amount of points currently being shown
        for (int i = 0; i < pointsAmount; i++) {
            int offset = pointsAmount * (currentPage - 1);
            if (offset + i < keys.length) p++;
        }
        return p;

    }

    /**
     * Draw lines and the value they represent behind chart for each whole number in range
     *
     * @param g Graphics2D object to draw with
     */
    private void drawRangeLines(Graphics2D g) {

        if (!Settings.isContrastEnabled()) {
            g.setColor(Theme.contrastWithShade(Theme.getBgColor(), 120)); // Set color to grey-ish color
        } else g.setColor(Theme.contrastWithShade(Theme.getBgColor(), 255)); // Higher contrast

        float divisor; // Amount of units to divide by
        if (getHeight() / range > g.getFont().getSize() * 1.8) { // If 1 by 1 fits (ex: 1, 2, 3, 4) set divisor to 1
            divisor = 1;
        } else if (getHeight() / range > g.getFont().getSize() * 0.8) { // If 2 fits (ex: 2, 4, 6) set divisor to 2
            divisor = 2;
        } else divisor = 4; // Else set divisor to 4 (ex: 4, 8, 12)

        for (int i = 0; i < (range/divisor); i++) { // For each integer in range:
            // draw a line across the screen at its value height
            g.drawLine(0, (int)((getHeight() / range) * (i*divisor)), getWidth(),
                    (int)((getHeight() / range) * (i*divisor)));
            g.drawString(String.valueOf((int)(range - (i*divisor))), 1,
                    (int)((getHeight() / range) * (i*divisor)) + g.getFont().getSize());
        }
    }

    /**
     * Draw date of shift at the bottom of the bar if there is space
     *
     * @param g Graphics2D object to draw with
     * @param dateIndex Index of date on keys array
     * @param x X coordinate
     * @param lastMonth Current last month value
     * @return New lastMonth value if changed
     */
    private int drawDate(Graphics2D g, int dateIndex, int x, int lastMonth) {

        if (barXDiff > g.getFont().getSize()*1.3) { // If there is space to do so:
            g.setColor(Theme.contrastWithBnW(barColor));

            g.fillRect(x, getHeight() - g.getFont().getSize(), (int) (g.getFont().getSize() * 1.4),
                    g.getFont().getSize()); // Draw box behind date

            if (keys[dateIndex].getMonthValue() != lastMonth) { // If the month has changed:
                lastMonth = drawMonth(g, barColor, dateIndex, x); // Draw month and save new value
            } else g.setColor(barColor); // Set color to write text on top of box
            g.drawString(String.valueOf(keys[dateIndex].getDayOfMonth()), x, getHeight() - 1); // Draw date number

        }
        return lastMonth;

    }

    /**
     * Draws the first 3 letters of a month 90 degrees counter clock wise with a filled rectangle behind it
     * then returns the new month value.
     *
     * @param g Graphics2D object to draw with
     * @param textColor Text color
     * @param dateIndex Index of date on keys array
     * @param y Y coordinate of drawing (really x, because coordinate system is flipped -90 deg.)
     * @return The new month value
     */
    private int drawMonth(Graphics2D g, Color textColor, int dateIndex, int y) {

        int fontSize = g.getFont().getSize();
        g.rotate(-Math.PI/2); // Rotate -90 degrees
        // Draw box behind month name
        g.fillRect(-(getHeight() - (int)(fontSize*1.1)), y, (int)(fontSize * 2.4), (int)(fontSize * 1.2));
        g.setColor(textColor); // Set back to text color
        g.drawString(keys[dateIndex].getMonth().name().substring(0, 3), -(getHeight() - (int)(fontSize*1.2)), y + fontSize);
        g.rotate(Math.PI/2); // Rotate back to normal (+90 degrees)

        return keys[dateIndex].getMonthValue(); // return new month value

    }

    /**
     * Draws the represented value either inside or outside on the top of the bar
     * depending on its height, and it could draw the value either horizontally,
     * vertically or no way at all depending on the bar's width.
     *
     * @param g Graphics2D object to draw with
     * @param isMonth Is the current bar starting on a new month
     * @param value Value to draw
     * @param x X coordinate
     * @param barTop Top y value of bar
     */
    private void drawBarValue(Graphics2D g, boolean isMonth, float value, int x, int barTop) {
        Color opposite = Theme.contrastWithBnW(barColor);
        int textWidth = g.getFontMetrics().stringWidth(String.valueOf(value));
        int dateMonthHeight = g.getFont().getSize()*2 + g.getFontMetrics().stringWidth("ABCD");

        if (barXDiff > textWidth + 4) { // If bar is thick enough to fit the text plus some legroom:
            g.setColor(opposite); // Set to opposite of bar color
            // If bar is taller than the date/month text, draw text inside
            if (barTop < getHeight() - (isMonth ? dateMonthHeight : g.getFont().getSize()*2)) {
                g.drawString(String.valueOf(value), x + 2, barTop + g.getFont().getSize());
            } else { // Else show the text on top of date/month text with background and change color accordingly:
                g.fillRect(x, (int)((getHeight() - (isMonth ? dateMonthHeight : g.getFont().getSize()*2.5F))),
                        textWidth + 4, g.getFont().getSize() + 4); // Draw background box
                g.setColor(barColor);
                g.drawString(String.valueOf(value), x + 2, ((getHeight() - (isMonth ? dateMonthHeight :
                        g.getFont().getSize()*2.5F)) + g.getFont().getSize())); // Draw text
            }
        } else if (barXDiff > g.getFont().getSize()) { // Else if it is thick enough to fit the text horizontally:
            g.setColor(opposite); // Set to opposite of bar color
            g.rotate(-Math.PI/2); // Rotate canvas -90 degrees
            if (barTop < getHeight() - (isMonth ? dateMonthHeight : g.getFont().getSize()*3)) {
                // If bar is taller than the date/month text then draw value inside
                g.drawString(String.valueOf(value), -(barTop + textWidth + 2), x + g.getFont().getSize());
            } else { // Else show the value on top of date/month text with a background and change color accordingly:
                g.fillRect(-(getHeight() - (isMonth ? dateMonthHeight - g.getFont().getSize() - 4: // Background box
                                g.getFont().getSize() + 1)), x, textWidth + 6, g.getFont().getSize() + 4);
                g.setColor(barColor);
                g.drawString(String.valueOf(value), -(getHeight() - (isMonth ? dateMonthHeight - g.getFont().getSize() :
                        g.getFont().getSize()+4)), x + g.getFont().getSize()); // Draw text
            }
            g.rotate(Math.PI/2); // Rotate 90 degrees to get canvas back to normal
        }

    }

    /**
     * Get the range of dates on current page, ex: "8/12/2020-8/25/2020"
     *
     * @return The range of dates currently being shown
     */
    public String getShownDates() {
        if (!noHistory) {
            int endDateIndex = (pointsAmount * (currentPage - 1)) + getPointsBeingShown() - 1; // Index of ending date

            return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[pointsAmount * (currentPage - 1)])
                    + "-" +
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[endDateIndex]);
        } return "None";
    }

    /**
     * Get range (max number that the values reach) of current page
     *
     * @return Max number that values on current page reach
     */
    public int getCurrentRange() {

        int r = 0;
        if (!noHistory) {
            for (int p = 0; p < pointsAmount; p++) { // For each point we can show:

                int index = p + (pointsAmount * (currentPage - 1)); // Get its position in the array
                if (index < keys.length) { // If index exists:
                    if (shiftHistoryData.get(keys[index]) > r) // We check if it is greater than the last max
                        r = (int) Math.ceil(shiftHistoryData.get(keys[index])); // If it is, set to new max
                }

            }
        }
        return r; // Return range

    }

    /**
     * Get the difference in X coordinates between the bars
     *
     * @return The difference in X coordinates between the bars
     */
    private int getBarXDiff() {
        return (int) ((getWidth() - Theme.getChartFont().getSize()*1.5F) / pointsAmount);
    }

    /**
     * Go to previous page if there is one
     */
    public void prevPage() {

        if (currentPage > 1) { // If we are above page 1, subtract 1.
            currentPage--;
            repaint();
        }

    }

    /**
     * Go to next page if there is one
     */
    public void nextPage() {

        if (currentPage < totalPages) { // If we are not at last page, add 1.
            currentPage++;
            repaint();
        }

    }

    /**
     * Set amount of data points to show on screen
     *
     * @param newValue New amount
     */
    public void setPointsAmount(int newValue) {

        pointsAmount = newValue;
        updatePages();

    }

    /**
     * @return The current page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @return The total amount of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return Amount of data points currently being shown
     */
    public int getPointsAmount() {
        return pointsAmount;
    }

    private void rightClickBarAt(int x) {
        // Get the bar which our x pos matches
        int bar = (int) ((x - (Theme.getChartFont().getSize()*1.5F))/barXDiff);

        if ((pointsAmount * (currentPage - 1)) + bar < keys.length) { // If a date exists at this X position:

            currentlyObserved = keys[(pointsAmount * (currentPage - 1)) + bar]; // Set this date to currentlyObserved

        } else {

            currentlyObserved = null; // And remove the value of currentlyObserved

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(deleteDate)) {
            if (currentlyObserved != null) {
                if (new YesNoDialog(container, """
                        Are you sure you want to
                        delete shift $observed?"""
                        .replace("$observed",
                                currentlyObserved.getMonth().name().substring(0, 3) + " " +
                                        currentlyObserved.getDayOfMonth() + " " +
                                        currentlyObserved.getYear()))
                        .accepted()) {

                    shiftHistoryData.remove(currentlyObserved);
                    Main.removeFromHistory(currentlyObserved);
                    getNewKeys();
                    updatePages();
                    container.updatePageInfo();
                    repaint();
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        rightClickBarAt(e.getX());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rightClickBarAt(e.getX());
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
