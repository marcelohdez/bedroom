package com.swiftsatchel.bedroom.dialog.settings;

import com.swiftsatchel.bedroom.components.FloatingSpinner;
import com.swiftsatchel.bedroom.dialog.time.SelectTimeDialog;
import com.swiftsatchel.bedroom.Main;
import com.swiftsatchel.bedroom.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class SettingsUI extends JPanel implements ActionListener, ChangeListener, ItemListener, MouseListener {

    private final SettingsDialog parent;

    public int[] textRGB, buttonTextRGB, buttonRGB, bgRGB; // Component color values
    private int currentlyColoring = // Component(s) currently being colored,
            // 0 = text, 1 = buttonText, 2 = buttons, 3 = background
            Main.userPrefs.getInt("lastColoring", 0);

    private JSlider redSlider, greenSlider, blueSlider; // Color sliders
    public int changeCount = 0; // Amount of color changes, (2 are done on startup, so 3 means colors have changed)
    private JLabel redLabel, greenLabel, blueLabel; // Color labels
    // This used to ignore showing the color values when changing themes/component being edited:
    private boolean showColorValues = true;
    private boolean highContrast = Settings.isContrastEnabled();

    // ======= Combo Boxes: =======
    // Components we can color
    private final JComboBox<String> coloringListBox = new JComboBox<>(new String[]{"Text", "Button Text", "Buttons", "Background"});
    // Themes
    private final JComboBox<String> themeListBox = new JComboBox<>(new String[]{"Dark", "Contrast",
            "Jelly Sandwich", "Midnight", "Light", "Pink+White"});
    // Default shift length in hours.
    private final JComboBox<String> shiftLengthListBox =
            new JComboBox<>(Ops.createNumberList(false, 1, 12, "h "));
    private final JComboBox<String> defTargetListBox =
            new JComboBox<>(Ops.createNumberList(false, 1, 24, "   "));

    // ======= Checkboxes =======
    private final JCheckBox alwaysOnTop = new JCheckBox("Stay on top");
    private final JCheckBox recoverCrash = new JCheckBox("Crash recovery");
    private final JCheckBox askBeforeEarlyClose = new JCheckBox("Ask before clocking out early");

    public SettingsUI(SettingsDialog parent) { // Settings UI constructor
        this.parent = parent;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addKeyListener(parent);

        loadRGBValues();
        // Creates misc. checkboxes and sets their default values. must be done before createColorSliders as
        // that checks if these checkboxes are selected by calling updateValues.
        createMiscOptions();
        createColorSliders();

        // Add rows
        createLabelRow("Colors");

        createListBoxRow("Preset:", themeListBox, Main.userPrefs.getInt("lastTheme", 0));
        createColoringPanel();
        createListBoxRow("Currently editing:", coloringListBox, Main.userPrefs.getInt("lastColoring", 0));
        createLabelRow("Misc.");
        createCheckBoxRow(alwaysOnTop, recoverCrash);
        createCheckBoxRow(askBeforeEarlyClose);
        createListBoxRow("Default shift length:", shiftLengthListBox);
        createListBoxRow("Default target:", defTargetListBox);
        createButtonRow("Manage Startup Items", "Startup items open along with Bedroom.");
        createButtonRow("Set Defaults", "Reset Misc. options, excluding startup items.");

        Ops.setHandCursorOnCompsFrom(this); // Set hand cursor on needed components

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

    private void createColorSliders() {

        // Create color sliders
        redSlider = new JSlider(0, 255);
        greenSlider = new JSlider(0, 255);
        blueSlider = new JSlider(0, 255);

        redSlider.addMouseListener(this);
        redSlider.addKeyListener(parent);
        greenSlider.addMouseListener(this);
        greenSlider.addKeyListener(parent);
        blueSlider.addMouseListener(this);
        blueSlider.addKeyListener(parent);

        updateColorSliders(); // Set their values

    }

    private void updateColorSliders() {

        // Remove change listener to not update unwanted colors, as each slider triggers a change event
        // making the colors get messed up quickly when changing themes or the currently coloring component.
        removeSlidersChangeListener();

        switch (currentlyColoring) {
            case 0 -> {         // Text
                redSlider.setValue(textRGB[0]);
                greenSlider.setValue(textRGB[1]);
                blueSlider.setValue(textRGB[2]);
            }
            case 1 -> {         // Button Text
                redSlider.setValue(buttonTextRGB[0]);
                greenSlider.setValue(buttonTextRGB[1]);
                blueSlider.setValue(buttonTextRGB[2]);
            }
            case 2 -> {         // Buttons
                redSlider.setValue(buttonRGB[0]);
                greenSlider.setValue(buttonRGB[1]);
                blueSlider.setValue(buttonRGB[2]);
            }
            case 3 -> {         // Background
                redSlider.setValue(bgRGB[0]);
                greenSlider.setValue(bgRGB[1]);
                blueSlider.setValue(bgRGB[2]);
            }
        }

        addSlidersChangeListener();                     // Add change listener for user to change color

        redSlider.setValue(redSlider.getValue() + 1);   // Since JSliders only send a change event when their
        redSlider.setValue(redSlider.getValue() - 1);   // value is changed, here we make sure to do so.

        if (!showColorValues) showColorValues = true;   // Allow color values to be seen if they weren't

    }

    private void addSlidersChangeListener() {

        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);

    }

    private void removeSlidersChangeListener() {

        redSlider.removeChangeListener(this);
        greenSlider.removeChangeListener(this);
        blueSlider.removeChangeListener(this);

    }

    private void createMiscOptions() {

        // Stay on top checkbox
        alwaysOnTop.setToolTipText("<html><b>Keep windows on top even after losing focus.</html></b>");
        alwaysOnTop.setSelected(Settings.getAlwaysOnTop());
        // Recover from crashes (closing Bedroom suddenly)
        recoverCrash.setToolTipText("<html><b>Load last shift if currently in it if Bedroom wasn't able<br>" +
                "to clock out</html></b>");
        recoverCrash.setSelected(Settings.isCrashRecoveryEnabled());
        // Default shift length
        shiftLengthListBox.setSelectedIndex(Math.min(Settings.getDefaultShiftLength() - 1, defTargetListBox.getItemCount()));
        shiftLengthListBox.setToolTipText("<html><b>Default amount of hours after clock in time to set<br>" +
                "clock out time.<br></b></html>");
        // Default target value
        defTargetListBox.setSelectedIndex(Math.min(Settings.getDefaultTarget() - 1, defTargetListBox.getItemCount()));
        defTargetListBox.setToolTipText("<html><b>Default target value in clock out time dialog</b></html>");
        // Ask before clocking out early
        askBeforeEarlyClose.setToolTipText("<html><b>When closing Bedroom before your shift ends,<br>" +
                "a dialog asks to input new clock out time</b></html>");
        askBeforeEarlyClose.setSelected(Settings.getAskBeforeEarlyClose());

    }

    private void createColoringPanel() {

        JPanel root = new JPanel();
        JPanel redRow = new JPanel();
        JPanel greenRow = new JPanel();
        JPanel blueRow = new JPanel();
        redLabel = new JLabel("Red:");
        greenLabel = new JLabel("Green:");
        blueLabel = new JLabel("Blue:");

        // Add labels and sliders to their respective rows
        FontMetrics fm = redLabel.getFontMetrics(redLabel.getFont());
        Dimension labelSize = new Dimension(Math.max(fm.stringWidth(redLabel.getText()),
                Math.max(fm.stringWidth(greenLabel.getText()), fm.stringWidth(blueLabel.getText()))), fm.getHeight());
        redRow.add(redLabel);
        redRow.add(redSlider);
        redLabel.setPreferredSize(labelSize);
        greenRow.add(greenLabel);
        greenRow.add(greenSlider);
        greenLabel.setPreferredSize(labelSize);
        blueRow.add(blueLabel);
        blueRow.add(blueSlider);
        blueLabel.setPreferredSize(labelSize);

        // Add them to root panel
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(redRow);
        root.add(greenRow);
        root.add(blueRow);

        add(root);

    }

    private void createLabelRow(String labelText) {

        JPanel row = new JPanel();
        JLabel colorLabel = new JLabel(labelText);

        row.add(colorLabel);
        colorLabel.setFont(Theme.getBoldFont());
        // Color row a bit darker or lighter than bg color:
        row.setBackground(Theme.contrastWithShade(Theme.getBgColor(), 20));
        colorLabel.setForeground(Theme.contrastWithBnW(row.getBackground()));

        add(row);

    }

    private void createButtonRow(String buttonText, String toolTip) {

        // Create the components
        JPanel row = new JPanel();
        JButton button = new JButton(buttonText);

        // Customize them
        button.setToolTipText("<html><b>" + toolTip + "</b></html>");
        button.addActionListener(this);
        button.addKeyListener(parent); // Add KeyListener for when it retains focus on user click

        // Add to row
        row.add(button);

        add(row);

    }

    private void createListBoxRow(String labelText, JComboBox<?> listBox, int selected) {

        // Create the components
        JPanel row = new JPanel();
        JLabel label = new JLabel(labelText);

        // For list boxes not already initialized: make sure the index we want to select is available
        listBox.setSelectedIndex(Math.min(selected, listBox.getItemCount() - 1));
        listBox.addItemListener(this);
        listBox.addKeyListener(parent); // Add KeyListener for when it retains focus on user click

        // Add to row
        row.add(label);
        row.add(listBox);

        add(row);

    }

    private void createListBoxRow(String labelText, JComboBox<?> listBox) {

        // Create the components
        JPanel row = new JPanel();
        JLabel label = new JLabel(labelText);

        // Customize them
        listBox.addItemListener(this);
        listBox.addKeyListener(parent); // Add KeyListener for when it retains focus on user click

        // Add to row
        row.add(label);
        row.add(listBox);

        add(row);

    }

    private void createCheckBoxRow(JCheckBox... comps) { // Creates a row with 1 or more checkboxes

        // Create panel
        JPanel row = new JPanel();

        for (JCheckBox c : comps) {
            // Add to panel
            c.addKeyListener(parent); // Add KeyListener for when it retains focus on user click
            row.add(c);
        }

        add(row);

    }

    private void setColorSliderToolTips() {

        redSlider.setToolTipText("<html><b>" + redSlider.getValue() + "</b></html>");
        greenSlider.setToolTipText("<html><b>" + greenSlider.getValue() + "</b></html>");
        blueSlider.setToolTipText("<html><b>" + blueSlider.getValue() + "</b></html>");

    }

    private void setColorLabelsToValues() {

        if (showColorValues) {

            if (redLabel != null) redLabel.setText(Integer.toString(redSlider.getValue()));
            if (greenLabel != null) greenLabel.setText(Integer.toString(greenSlider.getValue()));
            if (blueLabel != null) blueLabel.setText(Integer.toString(blueSlider.getValue()));

        }

    }

    private void resetColorLabels() {

        if (redLabel != null) redLabel.setText("Red:");
        if (greenLabel != null) greenLabel.setText("Green:");
        if (blueLabel != null) blueLabel.setText("Blue:");

    }

    private void setColoringTo(int index) {

        currentlyColoring = index;
        showColorValues = false;
        updateColorSliders();
        Main.userPrefs.putInt("lastColoring", index);

    }

    private void setCustomSliderValue(MouseEvent e) {

        JSlider source = (JSlider) e.getSource();
        source.setValue(
                new FloatingSpinner(source.getValue(), source.getMinimum(),
                        source.getMaximum()).showSelf());

    }

    private void equalizeSliders(ChangeEvent e) { // Make sliders same value as e

        JSlider source = (JSlider) e.getSource();
        redSlider.setValue(source.getValue());
        greenSlider.setValue(source.getValue());
        blueSlider.setValue(source.getValue());

    }

    private void setTheme(String theme, int index) {

        showColorValues = false;
        int[] newTextRGB = new int[3];
        int[] newButtonTextRGB = new int[3];
        int[] newButtonRGB = new int[3];
        int[] newBgRGB = new int[3];

        highContrast = false; // Disable high contrast by default
        switch (theme) {
            case "Dark" -> {
                newTextRGB = new int[]{240, 240, 240};
                newButtonTextRGB = new int[]{240, 240, 240};
                newButtonRGB = new int[]{80, 80, 80};
                newBgRGB = new int[]{64, 64, 64};
            }
            case "Contrast" -> highContrast = true; // Enable high contrast
            case "Midnight" -> {
                newTextRGB = new int[]{255, 255, 255};
                newButtonTextRGB = new int[]{255, 255, 255};
                newButtonRGB = new int[]{80, 85, 135};
                newBgRGB = new int[]{65, 70, 120};
            }
            case "Light" -> {
                newTextRGB = new int[]{0, 0, 0};
                newButtonTextRGB = new int[]{0, 0, 0};
                newButtonRGB = new int[]{220, 220, 220};
                newBgRGB = new int[]{240, 240, 240};
            }
            case "Pink+White" -> {
                newTextRGB = new int[]{0, 0, 0};
                newButtonTextRGB = new int[]{0, 0, 0};
                newButtonRGB = new int[]{240, 240, 240};
                newBgRGB = new int[]{235, 150, 200};
            }
            case "Jelly Sandwich" -> {
                newTextRGB = new int[]{0, 0, 0};
                newButtonTextRGB = new int[]{255, 255, 255};
                newButtonRGB = new int[]{75, 35, 125};
                newBgRGB = new int[]{215, 180, 155};
            }
        }

        textRGB = newTextRGB;
        buttonTextRGB = newButtonTextRGB;
        buttonRGB = newButtonRGB;
        bgRGB = newBgRGB;
        updateColorSliders();

        Main.userPrefs.putInt("lastTheme", index);

    }

    private void setDefaultMisc() {

        alwaysOnTop.setSelected(true);
        askBeforeEarlyClose.setSelected(true);
        shiftLengthListBox.setSelectedIndex(3);
        defTargetListBox.setSelectedIndex(8);

    }

    public void updateValues() {

        setColorSliderToolTips();
        int[] newRGB = new int[] {redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()};
        switch (currentlyColoring) {
            case 0 -> textRGB = newRGB;
            case 1 -> buttonTextRGB = newRGB;
            case 2 -> buttonRGB = newRGB;
            case 3 -> bgRGB = newRGB;
        }

        // Save settings
        Settings.saveMisc(alwaysOnTop.isSelected(), askBeforeEarlyClose.isSelected(),
                shiftLengthListBox.getSelectedIndex() + 1, recoverCrash.isSelected(),
                defTargetListBox.getSelectedIndex() + 1);
        Settings.setHighContrastTo(highContrast);
        // Since high contrast overwrites colors anyways, only do this if it is false
        if (!highContrast) Settings.saveColors(textRGB, buttonTextRGB, buttonRGB, bgRGB);

        if (parent.getWindowParent() instanceof SelectTimeDialog)
            parent.getWindowParent().reloadSettings();  // If parent window is a SelectTimeDialog, reload
                                                        // its settings too

        Main.updateSettings();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Manage Startup Items" -> new StartupItemsManager(parent);
            case "Set Defaults" -> setDefaultMisc();
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {

        changeCount++;
        updateValues();

        if (e.getSource() instanceof JSlider) {
            setColorLabelsToValues();
            if (parent.isShifting()) equalizeSliders(e); // Make all sliders same value if shifting
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == themeListBox) // If theme list box changed, update theme
            setTheme(Objects.requireNonNull(themeListBox.getSelectedItem()).toString(),
                    themeListBox.getSelectedIndex());
        if (e.getSource() == coloringListBox) // If coloring list box changed, update sliders for current component
            setColoringTo(coloringListBox.getSelectedIndex());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) setCustomSliderValue(e);
        setColorLabelsToValues();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) setCustomSliderValue(e);
        resetColorLabels();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
