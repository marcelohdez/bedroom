package com.swiftsatchel.bedroom.util;

import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.main.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A class of static methods used to create certain UI elements in order to save on
 * repeating code.
 */
public final class UIBuilder {

    /**
     * Creates a dialog UI, with a main message and an ok button under it.
     * Additionally, this sets the dialog to be an application modal, adds the ok button
     * to a desired ActionListener, and sets the alwaysOnTop value depending on user settings.
     *
     * @param dlg Dialog this is created for
     * @param al ActionListener to add to OK button
     * @param title Title of dialog window
     * @param msg Dialog message
     */
    public static void createDialog(JDialog dlg, ActionListener al, String title, String msg) {

        // Create components
        JPanel topUI = new JPanel();
        JPanel botUI = new JPanel();
        JTextArea message = new JTextArea(msg);
        JButton ok = new JButton("OK");

        // Customize components
        Theme.colorThese(new JComponent[]{topUI, botUI, message, ok});
        message.setFont(UI.boldText);
        message.setEditable(false);
        ok.addActionListener(al);

        // Add components
        topUI.add(message);
        botUI.add(ok);
        dlg.add(topUI, BorderLayout.PAGE_START);
        dlg.add(botUI, BorderLayout.PAGE_END);

        // Set window properties
        dlg.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // Retain input from all other windows
        dlg.setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        dlg.setResizable(false);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setTitle(title);
        dlg.pack();
        ok.setPreferredSize(new Dimension(dlg.getWidth() - 5, 40));
        dlg.pack();

    }

}
