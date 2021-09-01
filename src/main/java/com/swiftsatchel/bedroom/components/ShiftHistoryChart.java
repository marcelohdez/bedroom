package com.swiftsatchel.bedroom.components;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.TreeMap;

/**
 * A bar graph showing the orders per hour user ended with on existing shifts
 */
public class ShiftHistoryChart extends JPanel {

    private int pointsAmount = 7; // Amount of data points to show

    private final TreeMap<LocalDate, Float> shiftHistoryData = Main.getShiftHistory();
    private final LocalDate[] keys = shiftHistoryData.keySet().toArray(new LocalDate[0]);
    private int totalPages = (int) Math.ceil((double) keys.length/ (double)pointsAmount);
    private int currentPage = totalPages; // Default to last page, being the newest shifts

    private int range = getPageRange();

    @Override
    public void paintComponent(Graphics graphics) { // Drawing the graph
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics; // Cast Graphics object into Graphics2d

        int barXDiff = getWidth() / pointsAmount;   // Difference in X coordinates between bars
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

            int index = point * currentPage; // Get index
            float value = // If index exists get its value, else default to zero.
                    (index < keys.length) ? shiftHistoryData.get(keys[index]) : 0;
            int barHeight = (int) (getHeight() - ((getHeight()/ range) * value)); // Calculate height of bar

            // draw the bar (a rectangle)
            if (!Float.isNaN(value)) {
                g.fillRect((barXDiff / 2) + (barXDiff * (point - emptySpaces) - (thickness/2)),
                        barHeight, thickness, getHeight() - barHeight);
            } else emptySpaces++;

        }

    }

    /**
     * Get range (max number that the values reach) of current page
     *
     * @return Max number that values on current page reach
     */
    public int getPageRange() {

        int r = 0;

        for (int p = 0; p < pointsAmount; p++) { // For each point we can show:

            int index = p * currentPage;    // Get its position in the array
            if (index < keys.length) {      // If index exists:
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
            range = getPageRange();
            repaint();
        }

    }

    /**
     * Go to next page if there is one
     */
    public void nextPage() {

        if (currentPage < totalPages) { // If we are not at last page, add 1.
            currentPage++;
            range = getPageRange();
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
