package me.soggysandwich.bedroom.dialog.alert;

import me.soggysandwich.bedroom.dialog.time.SelectTimeDialog;
import me.soggysandwich.bedroom.util.Time;
import me.soggysandwich.bedroom.util.WindowParent;
import me.soggysandwich.bedroom.Main;
import me.soggysandwich.bedroom.enums.ErrorType;

/**
 * An AlertDialog that has a predefined message from an ErrorType.
 * Used to inform the user of an error which does not allow a certain
 * immediate action, ex: when selecting break times an error dialog
 * could pop up if user attempts to set break times out of shift.
 */
public class ErrorDialog extends AlertDialog {

    public ErrorDialog(WindowParent parent, ErrorType e) {
        super(parent);
        initDialog("Error", getErrorMessage(e));
    }

    public ErrorDialog(SelectTimeDialog parent, ErrorType e, String extraInfo) {
        super(parent);
        initDialog("Error", getErrorMessage(e, extraInfo));
    }

    // Get error message per error type
    private String getErrorMessage(ErrorType e) {

        switch(e) {
            case BREAK_OUT_OF_SHIFT -> {
                if (!Main.isOvernightShift()) {
                    return """
                            Breaks may only start or end
                            inside of shifts. Current
                            shift is:\040""" +
                            Time.makeTime12Hour(Main.getClockInTime().toLocalTime()) + "-" +
                            Time.makeTime12Hour(Main.getClockOutTime().toLocalTime());
                } else return """
                            Breaks may only start or end
                            inside of shifts. Current
                            shift is:\040""" +
                        Main.getClockInTime().getDayOfWeek().toString().substring(0,3) + " " +
                        Time.makeTime12Hour(Main.getClockInTime().toLocalTime()) + "-" +
                        Main.getClockOutTime().getDayOfWeek().toString().substring(0,3) + " " +
                        Time.makeTime12Hour(Main.getClockOutTime().toLocalTime());
            }
            case NON_POSITIVE_SHIFT_TIME -> {
                return """
                        Clock out time has to be
                        after your clock in time.
                        Current clock in time:
                        """ +
                        Time.makeTime12Hour(Main.getClockInTime().toLocalTime());
            }
            case NO_FILE_ASSOCIATION -> {
                return """
                        One of your startup items was
                        not able to be started as it
                        does not have a program
                        associated with its file type""";
            }
            case STARTUP_ITEMS_FULL -> {
                return """
                        You can not add any more
                        startup items.""";
            }
            case STARTUP_ITEM_NONEXISTENT -> {
                return """
                        One of your startup items was
                        not able to be started as it
                        no longer exists. Please go to
                        Settings > Manage Startup Items.""";
            }
            case EARLY_CLOCK_OUT_NOT_EARLY -> {
                return """
                        Early clock outs must be
                        before original clock out
                        time. Your current clock
                        out time:\040""" +
                        Time.makeTime12Hour(Main.getClockOutTime().toLocalTime());
            }
            case SAVING_HISTORY_FAILED -> {
                return "Unable to save shift history";
            }
            case FAILED_TO_LOAD_SHIFT_HISTORY -> {
                return """
                    Bedroom was unable to load
                    your past shift history as
                    a character loaded was not
                    a number. Please check
                    your history file.""";
            }
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    // Get error messages that need additional info
    private String getErrorMessage(ErrorType e, String extraInfo) {

        switch (e) {
            case NEGATIVE_BREAK_TIME -> {
                return """
                        A break's end time can not be
                        before the break's start time.
                        Current break start:\040""" + extraInfo;
            }
            case EXPLORER_UNSUPPORTED -> {
                return """
                        Unable to open directory, this
                        desktop's file explorer is not
                        supported, wanted directory:
                        
                        """ + limitStringLines(extraInfo);
            }
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    private String limitStringLines(String s) {
        // Get how many times 30 goes into the string length
        int limit = 30;
        int divisions = s.length() / limit;

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < divisions) {
            sb.append(s, i * limit, (i * limit) + limit).append("\n");
            i++;
        }
        sb.append(s, i * limit, (i * limit) + s.length() % limit);

        return sb.toString();

    }

}
