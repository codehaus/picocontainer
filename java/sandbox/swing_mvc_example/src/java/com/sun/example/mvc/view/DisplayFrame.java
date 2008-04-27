package com.sun.example.mvc.view;

import org.picocontainer.Startable;

import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JFrame implements Startable {

    public DisplayFrame(DisplayViewPanel displayViewPanel) {
        setTitle("Display (View 1)");
        getContentPane().add(displayViewPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public void start() {
        setVisible(true);
    }

    public void stop() {
    }
}
