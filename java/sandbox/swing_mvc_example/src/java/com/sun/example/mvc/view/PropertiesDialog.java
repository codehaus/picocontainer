package com.sun.example.mvc.view;

import org.picocontainer.Startable;

import javax.swing.*;
import java.awt.*;

public class PropertiesDialog extends JDialog implements Startable {
    public PropertiesDialog(DisplayFrame displayFrame, PropertiesViewPanel propertiesViewPanel) {
        super(displayFrame, "Properties (View 2)");
        setModal(false);
        getContentPane().add(propertiesViewPanel, BorderLayout.CENTER);
        pack();
    }

    public void start() {
        setVisible(true);
    }

    public void stop() {
    }
}
