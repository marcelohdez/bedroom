package me.marcelohdez.bedroom.util;

import java.time.LocalTime;

public final class Time { // Time operations

    /**
     * Appends time converted to 12-hour format (ex: 16:00 -> 4:00pm) to a StringBuilder.
     *
     * @param sb The StringBuilder to append to
     * @param time The time to be converted
     */
    public static void append12HrTimeTo(StringBuilder sb, LocalTime time) {

        int hour = time.getHour();
        int minute = time.getMinute();
        boolean isPM = (hour >= 12);

        if (isPM) { // PM
            sb.append(hour == 12 ? hour : hour - 12); // If hour is 12, it's 12pm, else remove 12 ex: 16:00 -> 4:00PM
        } else { // AM
            sb.append(hour != 0 ? hour : 12); // If hour = 0 then it's 12AM, else it is (hour) AM
        }

        sb.append(":");
        if (minute < 10) sb.append("0"); // Add 0 first if minute is less than 10 (ex: 4:1pm -> 4:01pm)
        sb.append(minute)
                .append(isPM ? "PM" : "AM"); // Append PM or AM at the end depending on isPM value

    }

    /**
     * Appends human-readable time from separate hour, minute, and second values to a StringBuilder.
     * Human-readable time is in 00:00:00 format, with the : being added when needed
     *
     * @param sb The StringBuilder to append to
     * @param h Hours value
     * @param m Minutes value
     * @param s Seconds value
     */
    public static void appendReadableTimeTo(StringBuilder sb, int h, int m, int s) {

        if (h > 0) { // If we have hours to show, show hours, and put a zero behind minutes under 10
            sb.append(h).append(":")
                    .append(Ops.addZeroUnder10(m))
                    .append(":");
            if (s < 10) sb.append("0");
        } else if (m > 0) { // If we have minutes to show, show minutes, and put a zero behind seconds under 10.
            sb.append(m).append(":");
            if (s < 10) sb.append("0");
        }
        sb.append(s); // Always show seconds

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
        append12HrTimeTo(sb, time);

        return sb.toString();

    }

    /**
     * Converts the amount of seconds given into "readable" time, or HH:MM:SS format,
     * with the hours and minutes values being added as needed.
     *
     * @param seconds Seconds to convert
     * @return "Readable" time, in HH:MM:SS format, if there are hours or minutes to show.
     */
    public static String secondsToTime(long seconds) {
        StringBuilder sb = new StringBuilder();

        int sec = (int) seconds % 60;
        int min = ((int) seconds / 60) % 60;
        int hr = (int) Math.floor(seconds / 3600f);

        appendReadableTimeTo(sb, hr, min, sec);
        return sb.toString();
    }

}
