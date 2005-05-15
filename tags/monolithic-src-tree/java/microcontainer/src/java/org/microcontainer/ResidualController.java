/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.microcontainer;

import org.picocontainer.Startable;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public final class ResidualController implements Startable {

    private Thread shutdownHookThread, thread;
    private final PicoContainer rootContainer;

    public ResidualController(PicoContainer rootContainer) {
        this.rootContainer = rootContainer;
    }

    public void start() {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(100 * 1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        Runnable shutdownHook = new Runnable() {
            public void run() {
                shuttingDown();
            }
        };
        shutdownHookThread = new Thread(shutdownHook);
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);

    }

    public void stop() {
        Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
        thread.interrupt();
        shuttingDown();
    }

    private void shuttingDown() {
        rootContainer.stop();
        rootContainer.dispose();
    }

}
