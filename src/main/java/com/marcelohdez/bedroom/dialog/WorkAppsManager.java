package com.marcelohdez.bedroom.dialog;

import com.marcelohdez.bedroom.main.Main;
import com.marcelohdez.bedroom.main.UI;
import com.marcelohdez.bedroom.enums.ErrorType;
import com.marcelohdez.bedroom.util.Ops;
import com.marcelohdez.bedroom.util.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WorkAppsManager extends JDialog implements ActionListener, WindowListener {

    private DefaultListModel<String> workApps;
    private JList<String> list;
    private JFileChooser fc;

    public WorkAppsManager() {

        setTitle("Work Apps");
        setAlwaysOnTop(Main.userPrefs.getBoolean("alwaysOnTop", false));
        addWindowListener(this);
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

        // Create stuffs
        JPanel panel = new JPanel();

        // Add work apps
        workApps = new DefaultListModel<>();
        for (String app : Main.loadedWorkApps)
            if (!app.equals("")) workApps.addElement(app);

        // Create list
        list = new JList<>(workApps);
        list.setVisibleRowCount(7);

        // Customize
        Ops.colorThis(list, true);
        panel.setBackground(UI.buttonColor);
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(180, 140));

        // Add to panel
        panel.add(sp);
        pack();

        return panel;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {

            case "Add" -> {

                if (workApps.getSize() < 7) { // Add a work app if under limit

                    fc = new JFileChooser();
                    fc.setFileFilter(new FileNameExtensionFilter("Programs", "exe", "app", "lnk"));
                    fc.setApproveButtonText("Add");
                    int returnVal = fc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION)
                        workApps.addElement(fc.getSelectedFile().toString());

                } else new ErrorDialog(ErrorType.WORK_APPS_FULL); // Else error

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

    @Override
    public void windowClosing(WindowEvent e) {
        Settings.saveWorkApps(workApps.toString());
    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
}
