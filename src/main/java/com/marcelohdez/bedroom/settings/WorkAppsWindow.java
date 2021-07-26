package com.marcelohdez.bedroom.settings;

import com.marcelohdez.bedroom.main.Main;
import com.marcelohdez.bedroom.main.UI;
import com.marcelohdez.bedroom.dialog.ErrorWindow;
import com.marcelohdez.bedroom.enums.ErrorType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WorkAppsWindow extends JDialog implements ActionListener {

    private DefaultListModel<String> workApps;
    private JList<String> list;

    public WorkAppsWindow() {

        setTitle("Work Apps");
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        JPanel content = new JPanel(); // Content panel to set a background color
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setBackground(UI.bg);

        content.add(createToolsPanel());
        content.add(createList());

        add(content);
        pack();

        // Center on main window
        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));

        setVisible(true);

    }

    private JPanel createToolsPanel() {

        // Create components
        JPanel panel = new JPanel();
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");

        // Customize em
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UI.bg);
        add.setBackground(UI.buttonColor);
        add.setForeground(UI.buttonTextColor);
        add.addActionListener(this);
        remove.setBackground(UI.buttonColor);
        remove.setForeground(UI.buttonTextColor);
        remove.addActionListener(this);

        // Add tools to panel
        panel.add(add);
        panel.add(remove);

        return panel;

    }

    private JPanel createList() {

        // Create panel
        JPanel panel = new JPanel();
        panel.setBackground(UI.buttonColor);

        // Add work apps
        workApps = new DefaultListModel<>();
        workApps.addElement("Program 0");

        // Create list
        list = new JList<>(workApps);
        list.setPreferredSize(new Dimension(180, 140));

        // Customize
        list.setBackground(UI.buttonColor);
        list.setForeground(UI.buttonTextColor);

        // Add to panel
        panel.add(list);

        return panel;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "Add" -> {

                if (workApps.getSize() < 7) { // Add a work app if under limit

                    workApps.addElement("Program " + workApps.getSize());

                } else new ErrorWindow(ErrorType.WORK_APPS_FULL); // Else error

            }
            case "Remove" -> {

                // Check if we have something selected
                if (!list.isSelectionEmpty()) {

                    int selected = list.getSelectedIndex(); // Get selected index
                    workApps.remove(selected);          // Remove it
                    list.setSelectedIndex(selected);    // Keep cursor on same position
                    if (list.isSelectionEmpty())
                        // If where we put the cursor is empty, move it up.
                        list.setSelectedIndex(selected - 1);

                }

            }

        }
    }
}
