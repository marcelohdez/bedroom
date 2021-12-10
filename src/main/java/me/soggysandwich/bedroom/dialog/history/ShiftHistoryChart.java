package me.soggysandwich.bedroom.dialog.history;

import me.soggysandwich.bedroom.Main;
import me.soggysandwich.bedroom.dialog.alert.AlertDialog;
import me.soggysandwich.bedroom.dialog.alert.YesNoDialog;
import me.soggysandwich.bedroom.util.Settings;
import me.soggysandwich.bedroom.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

/**
 * A histogram showing the orders per hour user ended with on past shifts
 */
public class ShiftHistoryChart extends JPanel implements MouseListener {

    private final ShiftHistoryWindow container;

    private boolean canShowToday;
    private boolean noHistory = true; // Stays true if there's no history data to show
    private int pointsAmount = 8; // Amount of data points to show

    private final TreeMap<LocalDate, Float> shiftHistoryData = Main.getShiftHistory();
    private LocalDate[] keys = cleanUpKeys();
    private int currentPage = 1;
    private int totalPages = 1;
    private final Color barColor = Theme.getTextColor(); // Constant bar color

    private int lastPageRemainder = keys.length % pointsAmount;
    private float max; // Current max orders/hr value
    private int barXDiff; // Space from one bar to the next (really the thickness)

    private LocalDate currentlyObserved; // Date that got right-clicked on

    public ShiftHistoryChart(ShiftHistoryWindow container) {
        this.container = container;
        canShowToday = Main.timesChosen();
        addMouseListener(this);

        JMenuItem deleteDate = new JMenuItem("Delete");
        deleteDate.addActionListener((e) -> deleteSelectedDate());
        JPopupMenu delMenu = new JPopupMenu();
        delMenu.add(deleteDate);
        setComponentPopupMenu(delMenu);

        updateInfo(); // Update page numbers + other stuff

    }

    @Override
    public void paintComponent(Graphics graphics) { // Drawing the graph
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics; // Cast Graphics object to Graphics2D
        g.setFont(Theme.getChartFont()); // Set to font we'll use

        if (!noHistory) {
            barXDiff = (int) ((getWidth() - Theme.getChartFont().getSize()*1.5F) / pointsAmount); // Update bar separation
            drawRangeLines(g);
            drawBars(g);
        } else {
            g.setColor(Theme.getTextColor());

            String text = Settings.isDoneLoadingShiftHistory() ? // Are we done loading the history?
                    "There is no data to be shown." : "Still loading data, please reopen."; // If not inform user.

            g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2,
                    getHeight()/2 - g.getFont().getSize()/2);
        }

    }

    private void updateInfo() {
        if (!noHistory) {
            // Update pages
            final int lastTotal = totalPages;
            totalPages = (int) Math.ceil((double) totalDates() / (double) pointsAmount);
            if (totalPages != lastTotal) currentPage = totalPages; // If page amount changed, go to the newest dates
            canShowToday = Main.timesChosen() && currentPage == totalPages;

            // Update range for drawing the background lines
            max = 0;
            for (int p = 0; p < pointsAmount; p++) { // For each point we can show:
                int index = getTrueIndex(p + pointsAmount * (currentPage - 1)); // Get its index
                if (canShowToday) index += 1;

                float valueToCheck = max;

                if (index < keys.length) { // If index exists:
                    valueToCheck = shiftHistoryData.get(keys[index]);
                } else if (canShowToday && index == keys.length) {
                    String text = Main.getOrdersPerHour();
                    valueToCheck = Float.parseFloat(text.substring(0, text.length() - 3));
                }

                if (valueToCheck > max) // We check if it is greater than the last max
                    max = (int) Math.ceil(valueToCheck); // If it is, set to new max
            }
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
     * Draw performance history bars
     *
     * @param g Graphics2D object to draw with
     */
    private void drawBars(Graphics2D g) {
        int emptySpaces = 0;    // Amount of NaN values, to ignore them when drawing the bars
        boolean hasMonthChanged = true;
        for (int point = 0; point < pointsAmount; point++) { // For each point:

            boolean onToday = canShowToday && (point == pointsAmount - 1 || point == keys.length);
            // Get actual index by adding the offset and make sure graph is filled on last page
            int index = getTrueIndex(pointsAmount * (currentPage - 1) + point);
            // If index exists get its value, else default to negative one.
            float value = (index < keys.length) ? shiftHistoryData.get(keys[index]) : -1F;
            if (onToday) {
                String text = Main.getOrdersPerHour();
                value = Float.parseFloat(text.substring(0, text.length() - 3));
            }

            if (value != -1F) { // draw the bar (a rectangle) if value is not -1 (to filter out nonexistent values)

                int top = (int) (getHeight() - ((getHeight() / max) * value)); // Top of current bar
                // Get x of bar plus initial offset
                int x = (int) ((g.getFont().getSize() * 1.5) +
                        (barXDiff / 2) + (barXDiff * (point - emptySpaces) - ((barXDiff - 1) / 2)));

                // Draw bar
                g.setColor(barColor);
                g.fillRect(x, top, (barXDiff - 1), getHeight() - top);

                // Draw bar value and date
                if (point > 0 && !onToday) hasMonthChanged = (keys[index].getMonthValue() != keys[index-1].getMonthValue());
                drawBarValue(g, onToday || hasMonthChanged, value, x, top);
                drawDate(g, onToday ? LocalDate.now().getDayOfMonth() : keys[index].getDayOfMonth(), x,
                        onToday || hasMonthChanged,
                        onToday ? "NOW" : keys[index].getMonth().name().substring(0, 3));
                if (onToday) break;

            } else emptySpaces++; // Else add as a spot to ignore on next data point
        }
    }

    /**
     * Draw lines and the value they represent behind chart for each whole number in range
     *
     * @param g Graphics2D object to draw with
     */
    private void drawRangeLines(Graphics2D g) {

        // Set color to range lines depending on if high contrast is enabled
        g.setColor(Theme.contrastWithShade(Theme.getBgColor(), Settings.isContrastEnabled() ? 255 : 120));

        int divisor; // Amount of units to divide by
        if (getHeight() / max > g.getFont().getSize() * 1.8) { // If 1 by 1 fits (ex: 1, 2, 3, 4) set divisor to 1
            divisor = 1;
        } else if (getHeight() / max > g.getFont().getSize() * 0.8) { // If 2 fits (ex: 2, 4, 6) set divisor to 2
            divisor = 2;
        } else divisor = 4; // Else set divisor to 4 (ex: 4, 8, 12)

        for (int i = 0; i < (max /divisor); i++) { // For each integer in range:
            // draw a line across the screen at its value height
            g.drawLine(0, (int)((getHeight() / max) * (i*divisor)), getWidth(),
                    (int)((getHeight() / max) * (i*divisor)));
            g.drawString(String.valueOf((int)(max - (i*divisor))), 1,
                    (int)((getHeight() / max) * (i*divisor)) + g.getFont().getSize());
        }
    }

    /**
     * Draw date of shift at the bottom of the bar if there is space
     *
     * @param g Graphics2D object to draw with
     * @param dayOfMonth Current day of month
     * @param x X coordinate
     * @param monthChanged whether the month value has changed since previous bar drawn
     */
    private void drawDate(Graphics2D g, int dayOfMonth, int x, boolean monthChanged, String month) {

        if (barXDiff > g.getFont().getSize()*1.3) { // If there is space to do so:
            g.setColor(Theme.contrastWithBnW(barColor));

            g.fillRect(x, getHeight() - g.getFont().getSize(), (int) (g.getFont().getSize() * 1.4),
                    g.getFont().getSize()); // Draw box behind date

            if (monthChanged) { // If the month has changed:
                drawMonthText(g, barColor, month, x); // Draw month and save new value
            } else g.setColor(barColor); // Set color to write text on top of box
            g.drawString(String.valueOf(dayOfMonth), x, getHeight() - 1); // Draw date number

        }

    }

    /**
     * Draws the first 3 letters of a month 90 degrees counter clock wise with a filled rectangle behind it
     * then returns the new month value.
     *
     * @param g Graphics2D object to draw with
     * @param textColor Text color
     * @param text Text to show
     * @param y Y coordinate of drawing (really x, because coordinate system is flipped -90 deg.)
     */
    private void drawMonthText(Graphics2D g, Color textColor, String text, int y) {

        int fontSize = g.getFont().getSize();
        int textWidth = g.getFontMetrics().stringWidth(text) + 4;
        g.rotate(-Math.PI/2); // Rotate -90 degrees
        // Draw box behind month name
        g.fillRect(-(getHeight() - (int)(fontSize*1.1)), y, textWidth, (int)(fontSize * 1.2));
        g.setColor(textColor); // Set back to text color
        g.drawString(text, -(getHeight() - (int)(fontSize*1.2)), y + fontSize);
        g.rotate(Math.PI/2); // Rotate back to normal (+90 degrees)

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
        int textWidth = g.getFontMetrics().stringWidth(String.valueOf(value));
        int dateMonthHeight = g.getFont().getSize()*2 + g.getFontMetrics().stringWidth("ABCD");
        g.setColor(Theme.contrastWithBnW(barColor)); // Set to opposite of bar color for text

        if (barXDiff > textWidth + 4) { // If bar is thick enough to fit the text plus some legroom:
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
        g.setColor(barColor);

    }

    /**
     * Get the range of dates on current page, ex: "8/12/2020-8/25/2020"
     *
     * @return The range of dates currently being shown
     */
    public String getShownDateRange() {
        if (!noHistory) { // If there is history to show, get first and last dates currently shown:
            int start = getTrueIndex(pointsAmount * (currentPage - 1)); // Get the true starting index

            int shown = pointsAmount; // Default to pointsAmount since we always show this amount unless we have less:
            if (keys.length < pointsAmount) {
                shown = keys.length;
            } else if (Main.timesChosen() && lastPageRemainder == 0) shown = 1;

            return "$s-$e"
                    .replace("$s", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[start]))
                    .replace("$e", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[start + shown - 1]));
        } else return "None";
    }

    /**
     * Go to previous page if there is one
     */
    public void prevPage() {

        if (currentPage > 1) { // If we are above page 1, subtract 1.
            currentPage--;
            updateInfo();
            container.updatePageInfo();
            repaint();
        }

    }

    /**
     * Go to next page if there is one
     */
    public void nextPage() {

        if (currentPage < totalPages) { // If we are not at last page, add 1.
            currentPage++;
            updateInfo();
            container.updatePageInfo();
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
        lastPageRemainder = keys.length % pointsAmount;
        updateInfo();
    }

    /**
     * Set amount of data points shown to be all available dates.
     */
    public void setPointsAmountToAll() {
        pointsAmount = keys.length; // All keys
        updateInfo();
    }

    public int currentPage() {
        return currentPage;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalDates() {
        return keys.length + (Main.timesChosen() ? 1 : 0);
    }

    /**
     * Make sure to account for remainder of dates needed to fill a whole page, to
     * not have 1 date shown on the last page, but rather the last X dates.
     *
     * @param val value which will be subtracted from
     * @return new index
     */
    private int getTrueIndex(int val) {
        if (keys.length > pointsAmount && currentPage == totalPages) {
            if (lastPageRemainder > 0) {
                val -= (pointsAmount - lastPageRemainder);

            } else if (lastPageRemainder == 0 && Main.timesChosen()) // Make space to show our current orders/hr:
                val -= (pointsAmount - (keys.length + 1) % pointsAmount);
        }

        return val;
    }

    private void rightClickBarAt(int x) {
        // Get bar at x coordinate
        int bar = getTrueIndex((int) ((x - (Theme.getChartFont().getSize()*1.5F))/barXDiff));

        if (pointsAmount * (currentPage - 1) + bar < keys.length) {
            // If a date exists at this X position set currentlyObserved to it
            currentlyObserved = keys[(pointsAmount * (currentPage - 1)) + bar];
        } else currentlyObserved = null; // Else set currentlyObserved to none
    }

    private void deleteSelectedDate() {

        if (currentlyObserved != null) {
            if (new YesNoDialog(null, """
                        Are you sure you want to
                        delete shift $o?"""
                    .replace("$o",
                            currentlyObserved.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                                    .toUpperCase(Locale.ROOT)))
                    .accepted()) {

                shiftHistoryData.remove(currentlyObserved);
                Main.removeFromHistory(currentlyObserved);
                keys = cleanUpKeys();
                lastPageRemainder = keys.length % pointsAmount;
                updateInfo();
                repaint();
                container.updatePageInfo();
            }
        } else new AlertDialog(null, "No existing date selected");

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
