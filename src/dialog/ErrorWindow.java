package dialog;

import bedroom.Main;
import bedroom.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorWindow extends JDialog implements ActionListener {

    public ErrorWindow(Main.ERROR e) {

        // Create components
        JPanel topUI = new JPanel();
        JPanel botUI = new JPanel();
        JTextArea message = new JTextArea(getErrorMessage(e));
        JButton ok = new JButton("OK");

        // Set component properties
        topUI.setBackground(UI.bg);
        botUI.setBackground(UI.bg);
        message.setBackground(UI.bg);
        message.setForeground(UI.textColor);
        message.setFont(UI.boldText);
        ok.addActionListener(this);
        ok.setBackground(UI.buttonColor);
        ok.setForeground(UI.textColor);

        // Add components
        topUI.add(message);
        botUI.add(ok);
        add(topUI, BorderLayout.PAGE_START);
        add(botUI, BorderLayout.PAGE_END);

        // Set window properties
        setModalityType(ModalityType.APPLICATION_MODAL); // Retain input from all other windows
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Error");
        pack();
        ok.setPreferredSize(new Dimension(this.getWidth() - 5, 40));
        pack();

        // Center on main window
        setLocation(Main.wnd.getX() + ((Main.wnd.getWidth()/2) - (this.getWidth()/2)),
                Main.wnd.getY() + ((Main.wnd.getHeight()/2) - (this.getHeight()/2)));

        setVisible(true);

    }

    // Get error message per error type
    private String getErrorMessage(Main.ERROR e) {

        switch(e) { // (Limit of 6 words per line)
            case BREAK_OUT_OF_SHIFT -> {
                return "Breaks can not start or end\noutside of shifts.";
            }
            case NEGATIVE_BREAK_TIME -> {
                return "Chosen break end time is before\nthe break's start time.";
            }
            case NEGATIVE_SHIFT_TIME -> {
                return "Clock out time has to be\nafter your clock in time.";
            }
        }

        // If type is not recognized return the type itself
        return e.toString();

    }

    @Override
    public void actionPerformed(ActionEvent e) { // Do sum when OK is pressed
        if (e.getActionCommand().equals("OK")) {
            this.dispose();
        }
    }
}
