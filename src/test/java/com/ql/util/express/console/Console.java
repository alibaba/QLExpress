package com.ql.util.express.console;

import java.awt.*;

import javax.swing.*;

public class Console {
    final boolean packFrame = false;

    /**
     * Construct and show the application.
     */
    public Console() {
        ConsoleFrame frame = new ConsoleFrame();
        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frameSize.height = screenSize.height;
        frameSize.width = screenSize.width;
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            new Console();
        });
    }
}
