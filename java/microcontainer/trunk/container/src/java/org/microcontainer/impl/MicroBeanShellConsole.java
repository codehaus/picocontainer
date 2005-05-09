/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.microcontainer.impl;

import org.microcontainer.Kernel;
import org.picocontainer.Startable;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;

import javax.swing.*;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class MicroBeanShellConsole implements Startable {

    private Kernel kernel;
    private JFrame frame;
    private Thread thread;

    public MicroBeanShellConsole(Kernel kernel) {
        this.kernel = kernel;
    }

    public void start() {

        try {
            JConsole console = new JConsole();
            Interpreter interpreter = new Interpreter(console);
            interpreter.set("kernel", kernel);
            frame = new JFrame("MicroContainer");
            frame.getContentPane().add(new JScrollPane(console,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
            frame.setSize(500, 400);
            frame.setVisible(true);
            thread = new Thread(interpreter);
            thread.run();

        } catch (EvalError ex) {
            ex.printStackTrace();
        }

    }

    public void stop() {
        frame.setVisible(false);
        frame.dispose();
        thread.interrupt();
    }
}
