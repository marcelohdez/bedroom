package com.swiftsatchel.bedroom.dialog.alert;

import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.dialog.time.SelectTimeDialog;
import com.swiftsatchel.bedroom.enums.ErrorType;
import com.swiftsatchel.bedroom.util.Time;
import com.swiftsatchel.bedroom.util.WindowParent;

import java.time.LocalDateTime;

public class ErrorDialog extends AlertDialog {

    public ErrorDialog(WindowParent parent, ErrorType e) {
        super(parent);
        init("Error", getErrorMessage(e), false);
    }

    public ErrorDialog(SelectTimeDialog parent, ErrorType e, LocalDateTime lastTime) {
        super(parent);
        init("Error", getErrorMessage(e, lastTime), false);
    }

    // Get error message per error type
    private String getErrorMessage(ErrorType e) {

        switch(e) {
            case BREAK_OUT_OF_SHIFT -> {
                return """
                        Breaks may only start or end
                        inside of shifts. Current
                        shift is:\040""" +
                        Time.makeTime12Hour(Main.getClockInTime().toLocalTime()) + "-" +
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
                        One of your work apps was not
                        able to be started as it does
                        not have a program associated
                        with its file type""";
            }
            case WORK_APPS_FULL -> {
                return """
                        You can not add any more
                        work apps.""";
            }
            case WORK_APP_DOES_NOT_EXIST -> {
                return """
                        One of your work apps was not
                        able to be started as it no
                        longer exists. Please go to
                        Settings > Manage Work Apps.""";
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
            case CAN_NOT_OPEN_EXPLORER -> {
                return """
                        Can not open file explorer.""";
            }
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    // Get error messages that need additional LocalDateTime variables
    private String getErrorMessage(ErrorType e, LocalDateTime time) {

        if (e == ErrorType.NEGATIVE_BREAK_TIME) {
            return """
                    A break's end time can not be
                    before the break's start time.
                    Current break start:\040""" + Time.makeTime12Hour(time.toLocalTime());
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

}
