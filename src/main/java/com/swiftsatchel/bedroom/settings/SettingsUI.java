package com.swiftsatchel.bedroom.settings;

import com.swiftsatchel.bedroom.dialog.SelectTimeDialog;
import com.swiftsatchel.bedroom.main.Main;
import com.swiftsatchel.bedroom.main.UI;
import com.swiftsatchel.bedroom.util.FloatingSpinner;
import com.swiftsatchel.bedroom.util.Settings;
import com.swiftsatchel.bedroom.util.Theme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class SettingsUI extends JPanel implements ActionListener, ChangeListener, ItemListener,
        MouseListener, KeyListener {

    private static final Dimension colorLabelsSize = new Dimension(40, 20);
    private final SettingsDialog parent;
    private boolean shifting = false; // Keeps track if user is shifting

    // RGB values already set
    public int[] textRGB, buttonTextRGB, buttonRGB, bgRGB;
    // Keep track of currently coloring red green and blue slider values:
    // 0 = text, 1 = buttonText, 2 = buttons, 3 = background
    private int currentlyColoring = Main.userPrefs.getInt("lastColoring", 0);

    private JSlider redSlider, greenSlider, blueSlider; // Color sliders
    private JLabel redLabel, greenLabel, blueLabel; // Color labels
    // This used to ignore showing the color values when changing themes/component being edited:
    private boolean showColorValues = true;

    private final JComboBox<String> coloringListBox = // Components we can color
            new JComboBox<>(new String[]{"Text", "Button Text", "Buttons", "Background"});

    private final JComboBox<String> themeListBox = // Themes
            new JComboBox<>(new String[]{"Dark", "Demonic Red", "Contrast",
                    "Khaki Green", "Light", "Pink+White", "Pastel Blue"});

    // Misc. settings checkboxes
    private JCheckBox alwaysOnTop;
    private JCheckBox doGC;

    public SettingsUI(SettingsDialog parent) { // Settings UI constructor
        this.parent = parent;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        loadRGBValues();
        // Creates misc. checkboxes and sets their default values. must be done before createColorSliders as
        // that checks if these checkboxes are selected by calling updateValues.
        createMiscOptions();
        createColorSliders();

        // Add rows
        add(createLabelRow("Colors (Red, Green, Blue)"));
        add(createListBoxRow("Theme:", themeListBox, "lastTheme"));
        add(createColoringPanel());
        add(createListBoxRow("Currently editing:", coloringListBox, "lastColoring"));
        add(createLabelRow("Misc."));
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

    private void createColorSliders() {

        // Create color sliders
        redSlider = new JSlider(0, 255);
        greenSlider = new JSlider(0, 255);
        blueSlider = new JSlider(0, 255);

        redSlider.addMouseListener(this);
        redSlider.addKeyListener(this);
        greenSlider.addMouseListener(this);
        greenSlider.addKeyListener(this);
        blueSlider.addMouseListener(this);
        blueSlider.addKeyListener(this);

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

        alwaysOnTop = new JCheckBox("Stay on top");
        doGC = new JCheckBox("Garbage Collect");

        alwaysOnTop.setToolTipText("<html><b>Keep windows on top even after losing focus.</html></b>");
        doGC.setToolTipText("<html><b>Forcefully remove excess memory every 60 seconds:</b><br>" +
                "May reduce the memory footprint of Bedroom but will<br>" +
                "cause more CPU overhead.</html>");

        alwaysOnTop.setSelected(Main.userPrefs.getBoolean("alwaysOnTop", false));
        doGC.setSelected(Main.userPrefs.getBoolean("gc", false));

    }

    private JPanel createColoringPanel() {

        JPanel root = new JPanel();
        JPanel redRow = new JPanel();
        JPanel greenRow = new JPanel();
        JPanel blueRow = new JPanel();
        redLabel = new JLabel("Red:");
        greenLabel = new JLabel("Green:");
        blueLabel = new JLabel("Blue:");

        Theme.colorThese(new JComponent[]{redRow, redLabel, redSlider, greenRow, greenLabel, greenSlider,
                blueRow, blueLabel, blueSlider});

        // Add labels and sliders to their respective rows
        redRow.add(redLabel);
        redRow.add(redSlider);
        redLabel.setPreferredSize(colorLabelsSize);
        greenRow.add(greenLabel);
        greenRow.add(greenSlider);
        greenLabel.setPreferredSize(colorLabelsSize);
        blueRow.add(blueLabel);
        blueRow.add(blueSlider);
        blueLabel.setPreferredSize(colorLabelsSize);

        // Add them to root panel
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(redRow);
        root.add(greenRow);
        root.add(blueRow);

        return root;

    }

    private JPanel createLabelRow(String label) {

        JPanel row = new JPanel();
        JLabel colorLabel = new JLabel(label);

        row.setBackground(Theme.darkenBy(UI.bg, 20));
        row.add(colorLabel);
        colorLabel.setFont(UI.boldText);
        colorLabel.setForeground(Theme.contrastWithBnW(Theme.darkenBy(UI.bg, 20)));

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

    private JPanel createListBoxRow(String labelText, JComboBox<String> listBox, String indexPrefKey) {

        // Create the components
        JPanel row = new JPanel();
        JLabel label = new JLabel(labelText);

        // Customize them
        row.setBackground(UI.bg);
        label.setForeground(UI.textColor);
        Theme.colorThis(listBox);
        listBox.setSelectedIndex(0);
        listBox.setSelectedIndex(Math.min(Main.userPrefs.getInt(indexPrefKey, 0),
                listBox.getItemCount() - 1));
        listBox.addItemListener(this);

        // Add to row
        row.add(label);
        row.add(listBox);

        return row;

    }

    private JPanel createFirstMiscRow() {

        // Create panel
        JPanel row = new JPanel();

        // Customize stuffs
        row.setBackground(UI.bg);
        Theme.colorThis(alwaysOnTop);
        Theme.colorThis(doGC);

        // Add to panel
        row.add(alwaysOnTop);
        row.add(doGC);

        return  row;

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

        switch (theme) {
            case "Dark" -> {
                newTextRGB = new int[]{240, 240, 240};
                newButtonTextRGB = new int[]{240, 240, 240};
                newButtonRGB = new int[]{80, 80, 80};
                newBgRGB = new int[]{64, 64, 64};
            }
            case "Demonic Red" -> {
                newTextRGB = new int[]{240, 240, 240};
                newButtonTextRGB = new int[]{240, 240, 240};
                newButtonRGB = new int[]{0, 0, 0};
                newBgRGB = new int[]{72, 0, 0};
            }
            case "Contrast" -> {
                newTextRGB = new int[]{255, 255, 255};
                newButtonTextRGB = new int[]{255, 255, 255};
                newButtonRGB = new int[]{0, 0, 0};
                newBgRGB = new int[]{0, 0, 0};
            }
            case "Khaki Green" -> {
                newTextRGB = new int[]{240, 240, 240};
                newButtonTextRGB = new int[]{240, 240, 240};
                newButtonRGB = new int[]{0, 0, 0};
                newBgRGB = new int[]{90, 120, 0};
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
                newBgRGB = new int[]{220, 150, 200};
            }
            case "Pastel Blue" -> {
                newTextRGB = new int[]{255, 255, 255};
                newButtonTextRGB = new int[]{255, 255, 255};
                newButtonRGB = new int[]{100, 160, 240};
                newBgRGB = new int[]{140, 190, 255};
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

        alwaysOnTop.setSelected(false);
        doGC.setSelected(false);

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
        Settings.saveColors(textRGB, buttonTextRGB, buttonRGB, bgRGB);
        Settings.saveMisc(alwaysOnTop.isSelected(), doGC.isSelected());

        if (parent.getWindowParent() instanceof SelectTimeDialog)
            parent.getWindowParent().reloadSettings();  // If parent window is a SelectTimeDialog, reload
                                                        // its settings too

        Main.updateSettings();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Manage Work Apps" -> new WorkAppsManager(parent);
            case "Set Defaults" -> setDefaultMisc();
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {

        updateValues();

        if (e.getSource() instanceof JSlider) {
            setColorLabelsToValues();
            if (shifting) equalizeSliders(e); // Make all sliders same value if shifting
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
    public void mouseClicked(MouseEvent e) {}

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
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        shifting = e.getKeyCode() == KeyEvent.VK_SHIFT; // If shift is pressed, we are shifting
    }

    @Override
    public void keyReleased(KeyEvent e) {
        shifting = false;
    }
}