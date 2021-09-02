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

                // Draw bar value and date
                Color opposite = Theme.contrastWithBnW(barColor); // Get a constant opposite color
                drawBarValue(g, opposite, barXDiff, value, x, top);
                lastMonth = drawDate(g, barColor, index, x, lastMonth, barXDiff);

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
     * Draw date of shift at the bottom of the bar if there is space
     *
     * @param g Graphics2D object to draw with
     * @param textColor Text color
     * @param dateIndex Index of date on keys array
     * @param x X coordinate
     * @param lastMonth Current last month value
     * @param barWidth Width of current bar (difference of bars)
     * @return New lastMonth value if changed
     */
    private int drawDate(Graphics2D g, Color textColor, int dateIndex, int x, int lastMonth, int barWidth) {

        if (barWidth > g.getFont().getSize()*1.4) {

            g.fillRect(x, getHeight() - g.getFont().getSize(), (int) (g.getFont().getSize() * 1.4),
                    g.getFont().getSize()); // Draw box behind date

            if (keys[dateIndex].getMonthValue() != lastMonth) { // If the month has changed:
                lastMonth = drawMonth(g, textColor, dateIndex, x); // Draw month and save new value
            } else g.setColor(textColor); // Set color to write text on top of box
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
     * @param textColor Text color
     * @param barXDiff Current difference between bars (for width detection)
     * @param value Value to draw
     * @param x X coordinate
     * @param barTop Top y value of bar
     */
    private void drawBarValue(Graphics2D g, Color textColor, int barXDiff, float value, int x, int barTop) {

        if (barXDiff > g.getFont().getSize()*2) { // If bar is thick enough to fit the text:
            if (barTop < getHeight() * 0.8) { // If bar is at least 20% the height of the screen show value in it:
                g.setColor(textColor);
                g.drawString(String.valueOf(value), x + 2, barTop + g.getFont().getSize());
            } else { // Else show the text on top and change color accordingly:
                g.drawString(String.valueOf(value), x + 2, barTop - g.getFont().getSize());
                g.setColor(textColor);
            }
        } else if (barXDiff > g.getFont().getSize()) { // Else if it is thick enough to fit the text horizontally:
            if (barTop < getHeight() * 0.8) { // If bar is at least 20% the height of the screen show value in it:
                g.rotate(-Math.PI/2);
                g.setColor(textColor);
                g.drawString(String.valueOf(value), -(barTop + g.getFont().getSize()*2), x + g.getFont().getSize());
                g.rotate(Math.PI/2);
            } else { // Else show the text on top and change color accordingly:
                g.rotate(-Math.PI/2);
                g.drawString(String.valueOf(value), -(barTop - g.getFont().getSize()*2), x + g.getFont().getSize());
                g.setColor(textColor);
                g.rotate(Math.PI/2);
            }
        } else g.setColor(textColor); // If too skinny for both, just do not show text

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
