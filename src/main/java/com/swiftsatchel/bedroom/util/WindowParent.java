package com.swiftsatchel.bedroom.util;

/**
 * Allows this window's children to have some information and control over them.
 */
public interface WindowParent {

    /**
     * Used to return this object's x, y, width, and height respectively in an int[]
     * to be used for centering, etc. by the child
     *
     * @return An int[] of the window's x, y, width, and height
     */
    int[] getXYWidthHeight();

    /**
     * Used to show/hide this object by the child.
     *
     * @param b Should this window be visible?
     */
    void makeVisible(boolean b);

    /**
     * Used to reload settings of this window by the child.
     */
    void reloadSettings();

    /**
     * Used to disable this object by the child
     *
     * @param b Should this window be disabled?
     */
    void setDisabled(boolean b);

    /**
     * Used to let the child tell this object to request focus on itself.
     */
    void askForFocus();

}
