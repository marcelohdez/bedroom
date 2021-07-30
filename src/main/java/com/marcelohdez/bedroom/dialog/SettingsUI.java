package com.marcelohdez.bedroom.dialog;

import com.marcelohdez.bedroom.main.Main;
import com.marcelohdez.bedroom.main.UI;
import com.marcelohdez.bedroom.util.Ops;
import com.marcelohdez.bedroom.util.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SettingsUI extends JPanel implements ActionListener, ChangeListener {

    private static final Dimension colorLabelsSize = new Dimension(100, 20);
    private final SettingsDialog parent;

    // RGB values already set
    public int[] textRGB, buttonTextRGB, buttonRGB, bgRGB;

    // Spinners:
    private JSpinner textRed, textGreen, textBlue;          // Text color spinners
    private JSpinner buttonTextRed, buttonTextGreen, buttonTextBlue; // Button text color spinners
    private JSpinner buttonRed, buttonGreen, buttonBlue;    // Button color spinners
    private JSpinner bgRed, bgGreen, bgBlue;                // Background color spinners

    private final JComboBox<String> themeListBox =
            new JComboBox<>(new String[]{"Banana", "Dark", "Demonic Red", "Contrast",
                    "Light", "Pink+White", "Pastel Blue"});

    private final JCheckBox alwaysOnTop = new JCheckBox("Stay on top");
    private final JCheckBox doGC = new JCheckBox("Garbage Collect");

    public SettingsUI(SettingsDialog parent) { // Settings UI constructor
        this.parent = parent;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        loadRGBValues();
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
        add(createButtonRow("Manage Work Apps",
                "Work apps will automatically open along with Bedroom."));
        add(createButtonRow("Set Defaults", "Reset Misc. options, excluding work apps."));

    }

    private void loadRGBValues() {

        // Get already set RGB values
        textRGB = new int[] { // Get text RGB values
                Main.userPrefs.getInt("textRed", 240),
                Main.userPrefs.getInt("textGreen", 240),
                Main.userPrefs.getInt("textBlue", 240)};

        buttonTextRGB = new int[] { // Get button text RGB values
                Main.userPrefs.getInt("buttonTextRed", 240),
                Main.userPrefs.getInt("buttonTextGreen", 240),
                Main.userPrefs.getInt("buttonTextBlue", 240)};

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

        // Button text spinners
        buttonTextRed = new JSpinner(new SpinnerNumberModel(buttonTextRGB[0], 0, 255, 1));
        buttonTextGreen = new JSpinner(new SpinnerNumberModel(buttonTextRGB[1], 0, 255, 1));
        buttonTextBlue = new JSpinner(new SpinnerNumberModel(buttonTextRGB[2], 0, 255, 1));

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
        buttonRed.addChangeListener(this);
        buttonGreen.addChangeListener(this);
        buttonBlue.addChangeListener(this);

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
        bgRed.addChangeListener(this);
        bgGreen.addChangeListener(this);
        bgBlue.addChangeListener(this);

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

    private JPanel createButtonRow(String buttonText, String toolTip) {

        // Create the components
        JPanel row = new JPanel();
        JButton button = new JButton(buttonText);

        // Customize them
        row.setBackground(UI.bg);
        button.setForeground(UI.buttonTextColor);
        button.setBackground(UI.buttonColor);
        button.addActionListener(this);
        button.setToolTipText("<html><b>" + toolTip + "</b></html>");

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

        // Create panel
        JPanel row = new JPanel();

        // Customize stuffs
        row.setBackground(UI.bg);
        Ops.colorThis(alwaysOnTop);
        Ops.colorThis(doGC);

        alwaysOnTop.setToolTipText("<html><b>Keep windows on top even after losing focus.</html></b>");
        doGC.setToolTipText("<html><b>Forcefully remove excess memory every 60 seconds:</b><br>" +
                                    "May reduce the memory footprint of Bedroom but will<br>" +
                                    "cause more CPU overhead.</html>");

        alwaysOnTop.setSelected(Main.userPrefs.getBoolean("alwaysOnTop", false));
        doGC.setSelected(Main.userPrefs.getBoolean("gc", false));

        // Add to panel
        row.add(alwaysOnTop);
        row.add(doGC);

        return  row;

    }

    private void setButtonTextAndText(int value) {

        setButtonTextAll(value);
        setTextColorAll(value);

    }

    private void setTextColorAll(int value) {

        textRed.setValue(value);
        textGreen.setValue(value);
        textBlue.setValue(value);

    }

    private void setButtonTextAll(int value) {

        buttonTextRed.setValue(value);
        buttonTextGreen.setValue(value);
        buttonTextBlue.setValue(value);

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

    private void setTheme(String theme, int index) {

        switch (theme) {
            case "Banana" -> {
                setTextColorAll(0);
                setButtonTextAll(240);
                setButtonRGB(54, 32, 0);
                setBgRGB(240, 224, 53);
            }
            case "Dark" -> {
                setButtonTextAndText(240);
                setButtonColorAll(80);
                setBgColorAll(64);
            }
            case "Demonic Red" -> {
                setButtonTextAndText(240);
                setButtonColorAll(0);
                setBgRGB(72, 0, 0);
            }
            case "Contrast" -> {
                setButtonTextAndText(255);
                setButtonColorAll(0);
                setBgColorAll(0);
            }
            case "Light" -> {
                setButtonTextAndText(0);
                setButtonColorAll(220);
                setBgColorAll(240);
            }
            case "Pink+White" -> {
                setButtonTextAndText(0);
                setButtonColorAll(240);
                setBgRGB(220, 150, 200);
            }
            case "Pastel Blue" -> {
                setButtonTextAndText(255);
                setButtonRGB(100, 160, 240);
                setBgRGB(140, 190, 255);
            }
        }

        Main.userPrefs.putInt("lastTheme", index);

    }

    private void setDefaultMisc() {

        alwaysOnTop.setSelected(false);
        doGC.setSelected(false);

    }

    public void updateValues() {

        textRGB = new int[] {(Integer) textRed.getValue(),
                (Integer) textGreen.getValue(),
                (Integer) textBlue.getValue()};

        buttonTextRGB = new int[] {(Integer) buttonTextRed.getValue(),
                (Integer) buttonTextGreen.getValue(),
                (Integer) buttonTextBlue.getValue()};

        buttonRGB = new int[] {(Integer) buttonRed.getValue(),
                (Integer) buttonGreen.getValue(),
                (Integer) buttonBlue.getValue()};

        bgRGB = new int[] {(Integer) bgRed.getValue(),
                (Integer) bgGreen.getValue(),
                (Integer) bgBlue.getValue()};

        Settings.saveColors(textRGB, buttonTextRGB, buttonRGB, bgRGB);
        Settings.saveMisc(alwaysOnTop.isSelected(), doGC.isSelected());
        Main.updateColors();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "comboBoxChanged" ->
                    setTheme(Objects.requireNonNull(themeListBox.getSelectedItem()).toString(),
                            themeListBox.getSelectedIndex());
            case "Manage Work Apps" -> new WorkAppsManager(parent);
            case "Set Defaults" -> setDefaultMisc();
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateValues();
    }

}