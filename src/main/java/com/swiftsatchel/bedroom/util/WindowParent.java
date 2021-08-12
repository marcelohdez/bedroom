package com.swiftsatchel.bedroom.util;

/**
 * Allows this window's children to have some information and control over them.
 */
public interface WindowParent {

    /**
     * Used to return this window's x, y, width, and height respectively in an int[]
     * to be used for centering, etc. by the child
     *
     * @return An int[] of the window's x, y, width, and height
     */
    int[] getXYWidthHeight();

    /**
     * Used to show/hide this window by the child.
     *
     * @param b Should this window be visible?
     */
    void makeVisible(boolean b);

}
