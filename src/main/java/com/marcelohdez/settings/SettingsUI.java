package com.marcelohdez.settings;

import com.marcelohdez.bedroom.Main;
import com.marcelohdez.bedroom.UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SettingsUI extends JPanel implements ActionListener {

    private static final Dimension colorLabelsSize = new Dimension(100, 20);

    // RGB values already set
    public int[] textRGB, buttonRGB, bgRGB;

    // Spinners:
    private JSpinner textRed, textGreen, textBlue;          // Text color spinners
    private JSpinner buttonTextRed, buttonTextGreen, buttonTextBlue; // Button text color spinners
    private JSpinner buttonRed, buttonGreen, buttonBlue;    // Button color spinners
    private JSpinner bgRed, bgGreen, bgBlue;                // Background color spinners

    private final JComboBox<String> themeListBox =
            new JComboBox<>(new String[]{"Banana", "Dark", "Demonic Red", "Contrast",
                    "Light", "Pink+White", "Sky Blue"});

    private final JCheckBox alwaysOnTop = new JCheckBox("Stay on top");

    public SettingsUI() { // Settings UI constructor

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        getTextRGBValues();
        setColorSpinners();

        // Add rows
        add(createLabelRow("Colors (Red, Green, Blue)"));
        add(createThemeRow());
        add(createTextSettings());
        add(createButtonTextSettings());
        add(createButtonSettings());
        add(createBgSettings());
        add(createLabelRow("Misc. (Changes need restart)"));
        add(createFirstMiscRow());
        add(createButtonRow("Manage Work Apps"));
        add(createButtonRow("Set Defaults"));

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

    private JPanel createTextSettings() {

        // Create thingies
        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Text:");
        label.setPreferredSize(colorLabelsSize);

        // Customize thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);
        textRed.addChangeListener(this);
        textGreen.addChangeListener(this);
        textBlue.addChangeListener(this);

        // Add thingies to row
        row.add(label);
        row.add(textRed);
        row.add(textGreen);
        row.add(textBlue);

        return row;

    }

    private JPanel createButtonTextSettings() {

        // Create thingies
        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Button Text:");
        label.setPreferredSize(colorLabelsSize);

        // Customize thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);
        buttonTextRed.addChangeListener(this);
        buttonTextGreen.addChangeListener(this);
        buttonTextBlue.addChangeListener(this);

        // Add thingies to row
        row.add(label);
        row.add(buttonTextRed);
        row.add(buttonTextGreen);
        row.add(buttonTextBlue);

        return row;

    }

    private JPanel createButtonSettings() {

        // Create thingies
        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Buttons:");
        label.setPreferredSize(colorLabelsSize);

        // Customize thingies
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);

        // Add thingies to row
        row.add(label);
        row.add(buttonRed);
        row.add(buttonGreen);
        row.add(buttonBlue);

        return row;

    }

    private JPanel createBgSettings() {

        // Create thingies
        JPanel row = new JPanel(); // Create panel to hold row of settings
        JLabel label = new JLabel("Background:");
        label.setPreferredSize(colorLabelsSize);

        // Customize thingies
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

    private JPanel createButtonRow(String buttonText) {

        // Create the components
        JPanel row = new JPanel();
        JButton button = new JButton(buttonText);

        // Customize them
        row.setBackground(UI.bg);
        row.add(button);
        button.setForeground(UI.textColor);
        button.setBackground(UI.buttonColor);
        button.addActionListener(this);

        // Add to row
        row.add(button);

        return  row;

    }

    private JPanel createThemeRow() {

        // Create the components
        JPanel row = new JPanel();
        JLabel label = new JLabel("Theme:");

        // Customize them
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);
        themeListBox.setBackground(UI.buttonColor);
        themeListBox.setForeground(UI.buttonTextColor);
        themeListBox.setSelectedIndex(0);
        themeListBox.addActionListener(this);
        themeListBox.setSelectedIndex(Main.userPrefs.getInt("lastTheme", 0));

        // Add to row
        row.add(label);
        row.add(themeListBox);

        return row;

    }

    private JPanel createFirstMiscRow() {

        JPanel row = new JPanel();

        row.setBackground(UI.bg);
        row.add(alwaysOnTop);
        alwaysOnTop.setForeground(UI.textColor);
        alwaysOnTop.setBackground(UI.bg);
        alwaysOnTop.setSelected(Main.userPrefs.getBoolean("alwaysOnTop", false));

        return  row;

    }

    private void setTextColorAll(int value) {

        textRed.setValue(value);
        textGreen.setValue(value);
        textBlue.setValue(value);

    }

    private void setTextRGB(int r, int g, int b) {

        textRed.setValue(r);
        textGreen.setValue(g);
        textBlue.setValue(b);

    }

    private void setButtonColorAll(int value) {

        buttonRed.setValue(value);
        buttonGreen.setValue(value);
        buttonBlue.setValue(value);

    }

    private void setButtonRGB(int r, int g, int b) {

        buttonRed.setValue(r);
        buttonGreen.setValue(g);
        buttonBlue.setValue(b);

    }

    private void setBgColorAll(int value) {

        bgRed.setValue(value);
        bgGreen.setValue(value);
        bgBlue.setValue(value);

    }

    private void setBgRGB(int r, int g, int b) {

        bgRed.setValue(r);
        bgGreen.setValue(g);
        bgBlue.setValue(b);

    }

    private void setTheme(String theme) {

        switch (theme) {
            case "Dark" -> {
                setTextColorAll(240);
                setButtonColorAll(80);
                setBgColorAll(64);
            }
            case "Demonic Red" -> {
                setTextColorAll(255);
                setButtonColorAll(0);
                setBgRGB(72, 0, 0);
            }
            case "Light" -> {
                setTextColorAll(0);
                setButtonColorAll(220);
                setBgColorAll(240);
            }
            case "Pink+White" -> {
                setTextColorAll(0);
                setButtonColorAll(240);
                setBgRGB(220, 150, 200);
            }
            case "Pastel Blue" -> {
                setTextColorAll(255);
                setButtonRGB(92, 153, 255);
                setBgRGB(128, 176, 255);
            }
        }

    }

    private void setDefaultMisc() {

        alwaysOnTop.setSelected(false);

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
        Main.updateColors();

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

        // Misc options
        Main.userPrefs.putBoolean("alwaysOnTop", alwaysOnTop.isSelected());

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Apply" -> {

                String theme = Objects.requireNonNull(themeListBox.getSelectedItem()).toString();

                if (theme.equals("Current"))  {
                    updateValues();
                } else setTheme(theme);

                updateValues();

            }
            case "Set Defaults" -> {

            }
        }

    }
}