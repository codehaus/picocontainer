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
