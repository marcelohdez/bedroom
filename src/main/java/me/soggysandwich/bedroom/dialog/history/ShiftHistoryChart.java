package me.soggysandwich.bedroom.dialog.history;

import me.soggysandwich.bedroom.Bedroom;
import me.soggysandwich.bedroom.util.Settings;
import me.soggysandwich.bedroom.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class ShiftHistoryChart extends JPanel {

    private boolean noHistory = false;
    private boolean canShowToday = Bedroom.clockInTimePassed(); // If we're currently clocked in

    private final ArrayList<LocalDate> dates = getDates();
    private int pointsAmount = 8;
    private int currentPage;
    private int totalPages = getPageAmount();
    // Missing dates to fill last page
    private int missingDates = dates.size() % pointsAmount;

    // Values to use when drawing:
    private float range = getValueRange(); // For range lines
    // Updated every draw call:
    private int rangeTextSpacing;
    private int barSpacing;

    @Override
    public void paintComponent(Graphics gfx) { // Run on every draw call
        super.paintComponent(gfx);
        Graphics2D g = (Graphics2D) gfx;

        g.setFont(Theme.getChartFont());
        drawChart(g, Theme.getTextColor(), Theme.contrastWithBnW(Theme.getTextColor()));
    }

    // ----- Private methods -----

    /** Get history data and clean up its keys, and add today's if applicable */
    private ArrayList<LocalDate> getDates() {
        ArrayList<LocalDate> newList = new ArrayList<>();

        if (Bedroom.getShiftHistory() != null) { // Add existing shift history:
            for (LocalDate key : Bedroom.getShiftHistory().keySet()) {
                if (Bedroom.getShiftHistory().get(key) != null && !Bedroom.getShiftHistory().get(key).isNaN()) {
                    newList.add(key);
                }
            }
            noHistory = !(newList.size() > 0);
            if (canShowToday) newList.add(LocalDate.MAX); // LocalDate.MAX represents today's date
        } else noHistory = true;

        return newList;
    }

    /** Returns the total amount of pages */
    private int getPageAmount() {
        int pageAmount = (int) Math.ceil((double) totalDates() / (double) pointsAmount);
        currentPage = pageAmount;
        canShowToday = Bedroom.clockInTimePassed();

        return pageAmount;
    }

    /** Returns the highest value on current page to draw background lines accordingly */
    private int getValueRange() {
        int max = 0; // Reset range value
        for (int i = 0; i < pointsAmount; i++) {
            int index = indexOf(i + pointsAmount * (currentPage - 1)); // Get its index
            float valueToCheck = 0;

            if (index < dates.size() - (Bedroom.clockInTimePassed() ? 1 : 0)) {
                valueToCheck = Bedroom.getShiftHistory().get(dates.get(index));
            } else if (canShowToday)
                valueToCheck = todayOrdersPerHr();

            // If value
            if (valueToCheck > max) max = (int) Math.ceil( valueToCheck );
        }
        return max;
    }

    private void updateAllInfo() {
        totalPages = getPageAmount();
        range = getValueRange();
        missingDates = dates.size() % pointsAmount;
    }

    private void drawChart(Graphics2D g, Color barColor, Color contrastColor) {
        if (!noHistory) { // Show history if there is history to show:
            rangeTextSpacing = g.getFontMetrics().stringWidth(String.valueOf(range));
            barSpacing = (getWidth() - rangeTextSpacing) / pointsAmount;

            drawRange(g);
            drawBars(g, barSpacing, barColor, contrastColor);

        } else { // Show message:
            String textToShow = "No data to show.";
            if (!Settings.isDoneLoadingShiftHistory()) textToShow = "Still loading, please reopen me.";

            int textWidth = g.getFontMetrics().stringWidth(textToShow);
            g.setColor(Theme.getTextColor());
            g.drawString(textToShow,
                    (getWidth() / 2) - textWidth/2, // Center on width
                    (getHeight() / 2) + g.getFont().getSize()/2); // Center on height
        }
    }

    private void drawRange(Graphics2D g) {
        // Set range lines and text to different contrast depending on high contrast being enabled
        g.setColor(Theme.contrastWithShade(Theme.getBgColor(), Settings.isContrastEnabled() ? 255 : 120));

        int divisor; // Amount of units to divide by
        if (getHeight() / range > g.getFont().getSize() * 1.8) { // If 1 by 1 fits (ex: 1, 2, 3, 4) set divisor to 1
            divisor = 1;
        } else if (getHeight() / range > g.getFont().getSize() * 0.8) { // If 2 fits (ex: 2, 4, 6) set divisor to 2
            divisor = 2;
        } else divisor = 4; // Else set divisor to 4 (ex: 4, 8, 12)

        for (int i = 0; i < (range /divisor); i++) { // For each integer in range:
            // draw a line across the screen at its value height
            g.drawLine(0, (int)((getHeight() / range) * (i*divisor)), getWidth(),
                    (int)((getHeight() / range) * (i*divisor)));
            g.drawString(String.valueOf((int)(range - (i*divisor))), 1,
                    (int)((getHeight() / range) * (i*divisor)) + g.getFont().getSize());
        }
    }

    private void drawBars(Graphics2D g, int barSpacing, Color barColor, Color contrastColor) {
        for (int bar = 0; bar < pointsAmount; bar++) {

            int index = indexOf(pointsAmount * (currentPage - 1) + bar);
            boolean onToday = canShowToday && index == dates.size() - 1;

            float value;
            if (index < dates.size()) {
                value = !onToday ? Bedroom.getShiftHistory().get(dates.get(index)) : todayOrdersPerHr();
            } else break;

            int top = (int) (getHeight() - (getHeight() / range) * value); // Top of current bar
            int x = rangeTextSpacing + (barSpacing * bar);

            g.setColor(barColor); // Bar is colored same as Theme's text color
            g.fillRect(x, top, (barSpacing - 1), getHeight() - top); // Draw bar

            drawBarInfo(g, onToday, bar, index, value, x, top, barColor, contrastColor);
        }
    }

    private void drawBarInfo(Graphics2D g, boolean onToday, int bar, int index, float value,
                             int x, int top, Color barColor, Color contrastColor) {
        boolean newMonth = barSpacing > g.getFont().getSize() * 1.5 &&
                (onToday || bar == 0 || dates.get(index).getMonth() != dates.get(index - 1).getMonth());

        drawBarValue(g,
                newMonth,
                value,
                x,
                top,
                barColor,
                contrastColor,
                onToday ? "NOW" : dates.get(index).getMonth().toString().substring(0, 3)
        );
        drawDate(g,
                onToday ? LocalDate.now().getDayOfMonth() : dates.get(index).getDayOfMonth(),
                x,
                newMonth,
                onToday ? "NOW" : dates.get(index).getMonth().toString().substring(0, 3),
                barColor,
                contrastColor
        );
    }

    /** Draw the bar's day of the month at the bottom */
    private void drawDate(Graphics2D g, int dayOfMonth, int x,
                          boolean monthChanged, String month, Color barColor, Color contrastColor) {

        if (barSpacing > g.getFont().getSize()*1.3) { // If there is space to do so:
            g.setColor(contrastColor);

            g.fillRect(x, getHeight() - g.getFont().getSize(), (int) (g.getFont().getSize() * 1.4),
                    g.getFont().getSize()); // Draw box behind date

            if (monthChanged) { // If the month has changed:
                drawMonthText(g, barColor, month, x); // Draw month and save new value
            } else g.setColor(barColor); // Set color to write text on top of box
            g.drawString(String.valueOf(dayOfMonth), x, getHeight() - 1); // Draw date number

        }

    }

    private void drawMonthText(Graphics2D g, Color textColor, String text, int distFromBottom) {

        int fontSize = g.getFont().getSize();
        int textWidth = g.getFontMetrics().stringWidth(text) + 4;
        g.rotate(-Math.PI/2); // Rotate -90 degrees
        // Draw box behind month name
        g.fillRect(-(getHeight() - (int)(fontSize*1.1)), distFromBottom, textWidth, (int)(fontSize * 1.2));
        g.setColor(textColor); // Set back to text color
        g.drawString(text, -(getHeight() - (int)(fontSize*1.2)), distFromBottom + fontSize);
        g.rotate(Math.PI/2); // Rotate back to normal (+90 degrees)

    }

    private float todayOrdersPerHr() {
        String text = Bedroom.getOrdersPerHour();
        return Float.parseFloat(text.substring(0, text.length() - 3));
    }

    // TODO: Make this method actually readable.
    private void drawBarValue(Graphics2D g, boolean newMonth, float value, int x, int barTop,
                              Color barColor, Color contrastColor, String monthText) {

        int textWidth = g.getFontMetrics().stringWidth(String.valueOf(value));
        int dateMonthHeight = g.getFontMetrics().stringWidth(monthText) * 2 + 4;
        g.setColor(contrastColor); // Set to opposite of bar color for text

        if (barSpacing > textWidth + 4) { // If bar is thick enough to fit the text plus some legroom:
            // If bar is taller than the date/month text, draw text inside
            if (barTop < getHeight() - (newMonth ? dateMonthHeight : g.getFont().getSize()*2)) {
                g.drawString(String.valueOf(value), x + 2, barTop + g.getFont().getSize());
            } else { // Else show the text on top of date/month text with background and change color accordingly:
                g.fillRect(x, (int)((getHeight() - (newMonth ? dateMonthHeight : g.getFont().getSize()*2.5F))),
                        textWidth + 4, g.getFont().getSize() + 4); // Draw background box
                g.setColor(barColor);
                g.drawString(String.valueOf(value), x + 2, ((getHeight() - (newMonth ? dateMonthHeight :
                        g.getFont().getSize()*2.5F)) + g.getFont().getSize())); // Draw text
            }
        } else if (barSpacing > g.getFont().getSize()) { // Else if it is thick enough to fit the text horizontally:
            g.rotate(-Math.PI/2); // Rotate canvas -90 degrees
            if (barTop < getHeight() - (newMonth ? dateMonthHeight : g.getFont().getSize()*3)) {
                // If bar is taller than the date/month text then draw value inside
                g.drawString(String.valueOf(value), -(barTop + textWidth + 2), x + g.getFont().getSize());
            } else { // Else show the value on top of date/month text with a background and change color accordingly:
                g.fillRect(-(getHeight() - (newMonth ? dateMonthHeight - g.getFont().getSize() - 4: // Background box
                        g.getFont().getSize() + 1)), x, textWidth + 6, g.getFont().getSize() + 4);
                g.setColor(barColor);
                g.drawString(String.valueOf(value), -(getHeight() - (newMonth ? dateMonthHeight - g.getFont().getSize() :
                        g.getFont().getSize()+4)), x + g.getFont().getSize()); // Draw text
            }
            g.rotate(Math.PI/2); // Rotate 90 degrees to get canvas back to normal
        }
        g.setColor(barColor);
    }

    /**
     * Gets the true index of the given value in the dates array, taking into account
     * the final page's missing dates
     */
    private int indexOf(int value) {
        if (currentPage == totalPages && totalPages > 1 && missingDates > 0) {
            if (pointsAmount - missingDates != dates.size())
                value -= pointsAmount - missingDates;
        }

        return value;
    }

    // ----- Public Methods -----

    /** Returns the current page number */
    public int page() {
        return currentPage;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalDates() {
        return dates.size();
    }

    /** Return's current page's viewed dates range (first bar and last bar's date) */
    public String pageDateRange() {
        if (!noHistory) { // If there is history to show, get first and last dates currently shown:
            int start = indexOf(pointsAmount * (currentPage - 1)); // Get the true starting index

            int shown = pointsAmount; // Default to pointsAmount since we always show this amount unless we have less:
            if (dates.size() < pointsAmount) {
                shown = dates.size();
            }

            if (!canShowToday) {
                return "start-end"
                        .replace("start", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                                .format(dates.get(start)))
                        .replace("end", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                                .format(dates.get(start + shown - 1)));
            } else return "start-Today"
                    .replace("start", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                            .format(dates.get(start)));
        } else return "None";
    }

    /** Sets the amount of bars to show per page to the given integer */
    public void show(int amount) {
        pointsAmount = amount;
        updateAllInfo();
    }

    public void showAll() {
        pointsAmount = dates.size();
        updateAllInfo();
    }

    public void goToLastPage() {
        currentPage = totalPages;
        canShowToday = Bedroom.clockInTimePassed() && currentPage == totalPages;
        range = getValueRange();
        repaint();
    }

    public void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            canShowToday = Bedroom.clockInTimePassed() && currentPage == totalPages;
            range = getValueRange();
            repaint();
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            canShowToday = Bedroom.clockInTimePassed() && currentPage == totalPages;
            range = getValueRange();
            repaint();
        }
    }

    public void goToFirstPage() {
        currentPage = 1;
        canShowToday = Bedroom.clockInTimePassed() && currentPage == totalPages;
        range = getValueRange();
        repaint();
    }

    /** Returns index of date of bar at given X coordinate */
    public int getDateFromBarAt(int x) {
        int index = indexOf(pointsAmount * (currentPage - 1) + (x - rangeTextSpacing)/barSpacing);

        if (index < dates.size() - 1) { // If a date exists at X return its index
            return index;
        } else return -1; // Else return -1
    }

    public LocalDate getDateAt(int index) {
        return dates.get(index);
    }

    public void deleteDateAt(int index) {
        Bedroom.getShiftHistory().remove(dates.get(index));
        dates.remove(index);
        updateAllInfo();
        repaint();
    }

}
