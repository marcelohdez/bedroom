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

        Graphics2D g = (Graphics2D) graphics; // Cast Graphics object into Graphics2d

        int barXDiff = getWidth() / (pointsAmount-1);   // Difference in X coordinates between bars
        int thickness = barXDiff - 1;               // Have one pixel of separation, giving the look of a histogram
        Color barColor = Theme.contrastWithBnW(getBackground()); // Set bars to contrasting color

        // Draw lines behind chart for each whole number in range
        g.setColor(Theme.contrastWithShade(barColor, 120)); // Set color to grey-ish color
        for (int i = 0; i < range; i++) // For each integer in range:
            // draw a line across the screen at its value height
            g.drawLine(0, (getHeight()/range) * i, getWidth(), (getHeight()/ range) * i);

        // Draw the chart
        g.setColor(barColor);
        int emptySpaces = 0; // Amount of NaN values, to be able to ignore their spacing.
        for (int point = 0; point < pointsAmount; point++) { // For each point:

            int index = pointsAmount * (currentPage - 1) + point; // Get actual index by adding the offset
            float value = // If index exists get its value, else default to negative one.
                    (index < keys.length) ? shiftHistoryData.get(keys[index]) : -1;
            int barHeight = (int) (getHeight() - ((getHeight()/ range) * value)); // Calculate height of bar

            // draw the bar (a rectangle) if value is a number and not -1 (to filter out nonexistent values)
            if (!Float.isNaN(value) && value >= 0 ) {

                g.fillRect((barXDiff / 2) + (barXDiff * (point - emptySpaces) - (thickness/2)),
                        barHeight, thickness, getHeight() - barHeight);

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
            if (offset + i < keys.length)
                if (!Float.isNaN(shiftHistoryData.get(keys[offset + i])))
                    p++;
        }
        return p;

    }

    /**
     * Get the range of dates on current page, ex: "8/12/2020-8/25/2020"
     *
     * @return The range of dates currently being shown
     */
    public String getShownDates() {
        int offset = pointsAmount * (currentPage - 1); // Initial offset
        int endDateIndex = offset + getPointsBeingShown(); // Index of ending date
        if (endDateIndex == keys.length) endDateIndex = offset + getPointsBeingShown() - 1;

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
        for (int p = 0; p < pointsAmount; p++) { // For each point we can show:

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
