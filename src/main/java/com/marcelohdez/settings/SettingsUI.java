package com.marcelohdez.settings;

import com.marcelohdez.bedroom.Main;
import com.marcelohdez.bedroom.UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsUI extends JPanel implements ActionListener {

    private static final Dimension sizeOfSideLabels = new Dimension(100, 30);

    // RGB values already set
    public int[] textRGB, buttonRGB, bgRGB;

    JSpinner textRed, textGreen, textBlue;          // Text color spinners
    JSpinner buttonRed, buttonGreen, buttonBlue;    // Button color spinners
    JSpinner bgRed, bgGreen, bgBlue;                // Background color spinners

    public SettingsUI() { // Settings UI constructor

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        getTextRGBValues();
        setColorSpinners();

        // Add rows
        add(createLabelRow("Colors (in RGB)"));
        add(createTextSettings());
        add(createButtonSettings());
        add(createBgSettings());
        add(createButtonRow("Default Colors"));
        add(createLabelRow("Misc."));

    }

    private void getTextRGBValues() {

        // Get already set RGB values
        textRGB = new int[] { // Get text RGB values
                Main.userPrefs.getInt("textRed", 240),
                Main.userPrefs.getInt("textGreen", 240),
                Main.userPrefs.getInt("textBlue", 240)};

        buttonRGB = new int[] { // Get button RGB values
                Main.userPrefs.getInt("buttonRed", 80),
                Main.userPrefs.getInt("buttonGreen", 80),
                Main.userPrefs.getInt("buttonBlue", 80)};

        bgRGB = new int[] { // Get background RGB values
                Main.userPrefs.getInt("bgRed", 64),
                Main.userPrefs.getInt("bgGreen", 64),
                Main.userPrefs.getInt("bgBlue", 64)};

    }

    private void setColorSpinners() {

        // Text spinners
        textRed = new JSpinner(new SpinnerNumberModel(textRGB[0], 0, 255, 1));
        textGreen = new JSpinner(new SpinnerNumberModel(textRGB[1], 0, 255, 1));
        textBlue = new JSpinner(new SpinnerNumberModel(textRGB[2], 0, 255, 1));

        // Button spinners
        buttonRed = new JSpinner(new SpinnerNumberModel(buttonRGB[0], 0, 255, 1));
        buttonGreen = new JSpinner(new SpinnerNumberModel(buttonRGB[1], 0, 255, 1));
        buttonBlue = new JSpinner(new SpinnerNumberModel(buttonRGB[2], 0, 255, 1));

        // Background spinners
        bgRed = new JSpinner(new SpinnerNumberModel(bgRGB[0], 0, 255, 1));
        bgGreen = new JSpinner(new SpinnerNumberModel(bgRGB[1], 0, 255, 1));
        bgBlue = new JSpinner(new SpinnerNumberModel(bgRGB[2], 0, 255, 1));

    }

    private JPanel createTextSettings() { // Create text color settings row

        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Text:");
        label.setPreferredSize(sizeOfSideLabels);

        // Color thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);

        row.add(label);
        row.add(textRed);
        row.add(textGreen);
        row.add(textBlue);

        return row;

    }

    private JPanel createButtonSettings() { // Create text color settings row

        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Buttons:");
        label.setPreferredSize(sizeOfSideLabels);

        // Color thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);

        row.add(label);
        row.add(buttonRed);
        row.add(buttonGreen);
        row.add(buttonBlue);

        return row;

    }

    private JPanel createBgSettings() { // Create text color settings row

        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Background:");
        label.setPreferredSize(sizeOfSideLabels);

        // Color thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);

        row.add(label);
        row.add(bgRed);
        row.add(bgGreen);
        row.add(bgBlue);

        return row;

    }

    private JPanel createLabelRow(String label) {

        JPanel row = new JPanel();
        JLabel colorLabel = new JLabel(label);

        row.setBackground(UI.bg);
        row.add(colorLabel);
        colorLabel.setFont(UI.boldText);
        colorLabel.setForeground(UI.textColor);

        return row;

    }

    private JPanel createButtonRow(String text) {

        JPanel row = new JPanel();
        JButton button = new JButton(text);

        row.setBackground(UI.bg);
        row.add(button);
        button.setForeground(UI.textColor);
        button.setBackground(UI.buttonColor);

        button.addActionListener(this);

        return  row;

    }

    private void setDefaultColors() {

        // Text colors
        textRed.setValue(240);
        textGreen.setValue(240);
        textBlue.setValue(240);

        // Button colors
        buttonRed.setValue(80);
        buttonGreen.setValue(80);
        buttonBlue.setValue(80);

        // Background colors
        bgRed.setValue(64);
        bgGreen.setValue(64);
        bgBlue.setValue(64);

    }

    public void updateValues() {

        textRGB = new int[] {(Integer) textRed.getValue(),
                (Integer) textGreen.getValue(),
                (Integer) textBlue.getValue()};

        buttonRGB = new int[] {(Integer) buttonRed.getValue(),
                (Integer) buttonGreen.getValue(),
                (Integer) buttonBlue.getValue()};

        bgRGB = new int[] {(Integer) bgRed.getValue(),
                (Integer) bgGreen.getValue(),
                (Integer) bgBlue.getValue()};

        saveSettings();
        UI.reloadColors();

    }

    private void saveSettings() {

        // Save text colors
        Main.userPrefs.putInt("textRed", textRGB[0]);
        Main.userPrefs.putInt("textGreen", textRGB[1]);
        Main.userPrefs.putInt("textBlue", textRGB[2]);

        // Save button colors
        Main.userPrefs.putInt("buttonRed", buttonRGB[0]);
        Main.userPrefs.putInt("buttonGreen", buttonRGB[1]);
        Main.userPrefs.putInt("buttonBlue", buttonRGB[2]);

        // Save background colors
        Main.userPrefs.putInt("bgRed", bgRGB[0]);
        Main.userPrefs.putInt("bgGreen", bgRGB[1]);
        Main.userPrefs.putInt("bgBlue", bgRGB[2]);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Default Colors" -> setDefaultColors();
            case "Defaults" -> {
                int x; // Really do nothing right now, just shutting up warning of having only one case.
            }
        }

    }
}
