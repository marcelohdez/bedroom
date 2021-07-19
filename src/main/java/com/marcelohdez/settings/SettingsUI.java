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

    JComboBox<String> themeListBox;
    String[] themes = {"Current", "Dark", "Light", "Pink+White", "Pink+Black"};

    public SettingsUI() { // Settings UI constructor

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        getTextRGBValues();
        setColorSpinners();

        // Add rows
        add(createLabelRow("Colors (in RGB)"));
        add(createTextSettings());
        add(createButtonSettings());
        add(createBgSettings());
        add(createThemesRow("Set to theme:"));
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

    private JPanel createThemesRow(String text) {

        // Create the components
        JPanel row = new JPanel();
        JLabel label = new JLabel(text);
        themeListBox = new JComboBox<>(themes);
        JButton button = new JButton("Apply");

        // Customize them
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);
        themeListBox.setBackground(UI.buttonColor);
        themeListBox.setForeground(UI.textColor);
        themeListBox.setSelectedIndex(0);
        button.setForeground(UI.textColor);
        button.setBackground(UI.buttonColor);

        // Add them to the row
        row.add(label);
        row.add(themeListBox);
        row.add(button);

        button.addActionListener(this);

        return  row;

    }

    private void setTextColor(int amount) {

        textRed.setValue(amount);
        textGreen.setValue(amount);
        textBlue.setValue(amount);

    }

    private void setButtonColor(int amount) {

        buttonRed.setValue(amount);
        buttonGreen.setValue(amount);
        buttonBlue.setValue(amount);

    }

    private void setBgColor(int amount) {

        bgRed.setValue(amount);
        bgGreen.setValue(amount);
        bgBlue.setValue(amount);

    }

    private void setTheme(String theme) {

        switch (theme) {
            case "Dark" -> {
                setTextColor(240);
                setButtonColor(80);
                setBgColor(64);
            }
            case "Light" -> {
                setTextColor(0);
                setButtonColor(220);
                setBgColor(240);
            }
            case "Pink+White" -> {
                setTextColor(0);
                setButtonColor(240);
                bgRed.setValue(220);
                bgGreen.setValue(150);
                bgBlue.setValue(200);
            }
            case "Pink+Black" -> {
                setTextColor(255);
                setButtonColor(0);
                bgRed.setValue(220);
                bgGreen.setValue(150);
                bgBlue.setValue(200);
            }
        }

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
        Main.ui.reloadColors();

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
            case "Apply" -> {

                String theme = themeListBox.getSelectedItem().toString();

                if (theme.equals("Current"))  {
                    updateValues();
                } else setTheme(theme);

                updateValues();

            }
            case "bruh" -> {
                int x;
            }
        }

    }
}