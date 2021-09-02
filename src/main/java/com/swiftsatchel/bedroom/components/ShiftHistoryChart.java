package com.swiftsatchel.bedroom.components;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TreeMap;

/**
 * A bar graph showing the orders per hour user ended with on existing shifts
 */
public class ShiftHistoryChart extends JPanel {

    private int pointsAmount = 7; // Amount of data points to show

    private final TreeMap<LocalDate, Float> shiftHistoryData = Main.getShiftHistory();
    private final LocalDate[] keys = shiftHistoryData.keySet().toArray(new LocalDate[0]);
    private int totalPages = (int) Math.ceil((double) keys.length / (double) pointsAmount);
    private int currentPage = totalPages; // Default to last page, being the newest shifts

    private int range = getCurrentRange();

    @Override
    public void paintComponent(Graphics graphics) { // Drawing the graph
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics; // Cast Graphics object to Graphics2D

        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16); // Save the font we'll use
        int initXOffset = (int)(font.getSize() * 1.5); // Space on left of chart for numbers
        int barXDiff = ((getWidth() - initXOffset) / pointsAmount); // Difference in X coordinates between bars
        int thickness = barXDiff - 1;               // Have one pixel of separation, giving the look of a histogram
        Color barColor = Theme.getTextColor();      // Set a constant bar color
        g.setFont(font); // Set to font

        // Draw lines and the value they represent behind chart for each whole number in range
        g.setColor(Theme.contrastWithShade(barColor, 120)); // Set color to grey-ish color
        for (int i = 0; i < range; i++) { // For each integer in range:
            // draw a line across the screen at its value height
            g.drawLine(0, (getHeight() / range) * i, getWidth(), (getHeight() / range) * i);
            g.drawString(String.valueOf(range - i), 1, ((getHeight() / range) * i) + font.getSize());
        }

        // ======== Draw the chart ========
        int emptySpaces = 0;    // Amount of NaN values, to ignore them when drawing the bars
        int lastMonth = 0;      // Keep track of last month value to only put month name when changed
        for (int point = 0; point <= pointsAmount; point++) { // For each point:

            int index = pointsAmount * (currentPage - 1) + point; // Get actual index by adding the offset
            // If index exists get its value, else default to negative one.
            float value = (index < keys.length) ? shiftHistoryData.get(keys[index]) : -1F;

            // draw the bar (a rectangle) if value is a number and not -1 (to filter out nonexistent values)
            // also draw the date and month if needed at its bottom
            if (!Float.isNaN(value) && value != -1F) {

                int top = (int) (getHeight() - ((getHeight() / range) * value)); // Top of current bar
                // Get x of bar plus initial offset
                int x = initXOffset + (barXDiff/2) + (barXDiff * (point - emptySpaces) - (thickness / 2));
                int fontSize = font.getSize();

                // Draw bar
                g.setColor(barColor);
                g.fillRect(x, top, thickness, getHeight() - top);

                // Draw bar value
                Color opposite = Theme.contrastWithBnW(barColor); // Get a constant opposite color
                if (barXDiff > fontSize*2) {
                    if (top < getHeight() * 0.8) { // If bar is at least 20% the height of the screen show value in it:
                        g.setColor(opposite);
                        g.drawString(String.valueOf(value), x + 2, top + fontSize);
                    } else { // Else show the text on top and change color accordingly:
                        g.drawString(String.valueOf(value), x + 2, top - fontSize);
                        g.setColor(opposite);
                    }
                } else g.setColor(opposite);

                // Draw date of shift at the bottom of the bar
                g.fillRect(x, getHeight() - fontSize, (int)(fontSize * 1.4), fontSize); // Draw box behind date
                if (keys[index].getMonthValue() != lastMonth) { // If the month has changed:
                    lastMonth = drawMonth(g, barColor, index, x); // Draw month and save new value
                } else g.setColor(barColor); // Set color to write text on top of box

                g.drawString(String.valueOf(keys[index].getDayOfMonth()), x, getHeight() - 1); // Draw date number

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
        for (int i = 0; i <= pointsAmount; i++) {
            int offset = pointsAmount * (currentPage - 1);
            if (offset + i < keys.length)
                if (!Float.isNaN(shiftHistoryData.get(keys[offset + i])))
                    p++;
        }
        return p;

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
     * Get the range of dates on current page, ex: "8/12/2020-8/25/2020"
     *
     * @return The range of dates currently being shown
     */
    public String getShownDates() {
        int offset = pointsAmount * (currentPage - 1); // Initial offset
        int pointsShown = getPointsBeingShown(); // Store points being shown to not do for loop again
        int endDateIndex = offset + pointsShown; // Index of ending date
        if (endDateIndex == keys.length) endDateIndex--;

        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[offset])
                + "-" +
                DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(keys[endDateIndex]);
    }

    /**
     * Get range (max number that the values reach) of current page
     *
     * @return Max number that values on current page reach
     */
    public int getCurrentRange() {

        int r = 0;
        for (int p = 0; p <= pointsAmount; p++) { // For each point we can show:

            int index = p + (pointsAmount * (currentPage - 1)); // Get its position in the array
            if (index < keys.length) { // If index exists:
                if (shiftHistoryData.get(keys[index]) > r) // We check if it is greater than the last max
                    r = (int) Math.ceil(shiftHistoryData.get(keys[index])); // If it is, set to new max
            }

        }
        return r; // Return range

    }

    /**
     * Go to previous page if there is one
     */
    public void prevPage() {

        if (currentPage > 1) { // If we are above page 1, subtract 1.
            currentPage--;
            range = getCurrentRange();
            repaint();
        }

    }

    /**
     * Go to next page if there is one
     */
    public void nextPage() {

        if (currentPage < totalPages) { // If we are not at last page, add 1.
            currentPage++;
            range = getCurrentRange();
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
        totalPages = (int) Math.ceil((double) keys.length/ (double)pointsAmount);
        currentPage = totalPages;

    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return Amount of data points currently being shown
     */
    public int getPointsAmount() {
        return pointsAmount;
    }

}
