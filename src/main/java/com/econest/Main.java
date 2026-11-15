package com.econest;

import com.econest.controller.MainController;
import com.econest.dao.DatabaseSetup;
import com.econest.view.MainFrame;
import com.econest.view.components.Theme;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // smooth text on every computer
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            Theme.install();   // colours and fonts

            // create the tables (and sample data) the first time the program runs
            try {
                DatabaseSetup.initialise();
            } catch (SQLException e) {
                // without a database the program can't work, so show the error and stop
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Could not open the database.\n" + e.getMessage(),
                        "EcoNest", JOptionPane.ERROR_MESSAGE);
                return;
            }
            MainFrame frame = new MainFrame();
            MainController controller = new MainController(frame);
            controller.open("dashboard");
            frame.setVisible(true);
        });
    }
}
