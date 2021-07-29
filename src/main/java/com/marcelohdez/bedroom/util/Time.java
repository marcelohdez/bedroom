package com.marcelohdez.bedroom.util;

import java.time.LocalTime;

public class Time { // Time operations

    /**
     * Returns an int[] of the hours, minutes, and seconds respectively of the seconds value.
     *
     * @param seconds The value to divide into hours, minutes, and seconds
     * @return Hours, minutes and seconds.
     */
    public static int[] shrinkTime(long seconds) {

        int hours = 0;
        int minutes = 0;

        while (seconds > 59) {
            minutes++;
            seconds -= 60;
        }
        while (minutes > 59) {
            hours++;
            minutes -= 60;
        }
        // A list of integers, each index represents hours, minutes, and seconds respectively:
        return new int[]{hours, minutes, (int) seconds};

    }

    /**
     * Appends time converted to 12-hour format (ex: 16:00 -> 4:00pm) to a StringBuilder.
     *
     * @param sb The StringBuilder to append to
     * @param time The time to be converted
     */
    public static void append12HrTimeTo(StringBuilder sb, LocalTime time) {

        int hour = time.getHour();
        int minute = time.getMinute();
        boolean isPM = true;

        if (hour >= 12) { // Set correct hour and am/pm value
            if (hour != 12) {       // Set hour to 1-11pm
                sb.append(hour-12);
            } else sb.append(12);   // Set to 12pm
        } else {
            isPM = false;           // Time is no longer PM.
            if (hour != 0) {        // Set hour to 1-11am
                sb.append(hour);
            } else sb.append(12);   // Set to 12am
        }

        sb.append(":");
        if (minute < 10) sb.append("0"); // Add 0 first if minute is less than 10 (ex: 4:1pm -> 4:01pm)
        sb.append(minute);
        if (isPM) {
            sb.append("PM");
        } else sb.append("AM");

    }

    /**
     * Appends human-readable time from separate hour, minute, and second values to a StringBuilder.
     * Zeros are also added when the minutes or second values are below 10.
     * ex: hour = 2, minute = 4, second = 6; we get "2:04:06".
     *
     * @param sb The StringBuilder to append to
     * @param h Hours value
     * @param m Minutes value
     * @param s Seconds value
     */
    public static void appendReadableTimeTo(StringBuilder sb, int h, int m, int s) { // Set time to "00:00:00" format

        if (h < 10) sb.append("0");
        sb.append(h).append(":");
        if (m < 10) sb.append("0");
        sb.append(m).append(":");
        if (s < 10) sb.append("0");
        sb.append(s);

    }

    /**
     * Converts 12-hour time to military time (ex: 6:00PM -> 18:00) and returns it as a String.
     *
     * @param oldHr 12-Hour time hour
     * @param min Minute value
     * @param isPM Is the time AM or PM?
     * @return Returns a
     */
    public static String makeTime24Hour(int oldHr, int min, boolean isPM) {

        StringBuilder sb = new StringBuilder();
        int newHr = 0;

        // Convert hour to 24-hours from 12
        if (isPM) {
            newHr = oldHr + 12;
            if (oldHr == 12) newHr = oldHr;
        } else {
            if (oldHr != 12) newHr = oldHr;
        }

        // Make sure time is in format "00:00" so single digits get a 0 added
        if (newHr < 10) sb.append("0");
        sb.append(newHr).append(":");
        if (min < 10) sb.append("0");
        sb.append(min);

        return sb.toString();

    }

    /**
     * Converts 24-hour time (what LocalTime is saved in) into 12-hour time.
     * It is then returned as a String.
     *
     * @param time The LocalTime to be converted
     * @return A String of the converted time (ex: "4:00PM")
     */
    public static String makeTime12Hour(LocalTime time) {

        StringBuilder sb = new StringBuilder();
        int hour = time.getHour();
        int minute = time.getMinute();
        boolean isPM = true;

        if (hour >= 12) { // Set correct hour and am/pm value
            if (hour != 12) {       // Set hour to 1-11pm
                sb.append(hour-12);
            } else sb.append(12);   // Set to 12pm
        } else {
            isPM = false;           // Time is no longer PM.
            if (hour != 0) {        // Set hour to 1-11am
                sb.append(hour);
            } else sb.append(12);   // Set to 12am
        }

        sb.append(":");
        if (minute < 10) sb.append("0"); // Add 0 first if minute is less than 10 (ex: 4:1pm -> 4:01pm)
        sb.append(minute);
        if (isPM) {
            sb.append("PM");
        } else sb.append("AM");

        return sb.toString();

    }

}
