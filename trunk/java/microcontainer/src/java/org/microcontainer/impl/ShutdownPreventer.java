package org.microcontainer.impl;

import org.picocontainer.Startable;
import org.microcontainer.Kernel;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ShutdownPreventer implements Startable, Runnable {

    private Kernel kernel;
    private Thread thread;

    public ShutdownPreventer(Kernel kernel) {
        this.kernel = kernel;
    }

    public void start() {
        thread = new Thread(this, "MicroContainer Shutdown Preventer");
        thread.start();
        System.out.println("MicroContainer Started");
    }

    public void stop() {
        thread.interrupt();
        System.out.println("MicroContainer Stopping");        
    }

    public void run() {
        try {
            while(true) {
                Thread.sleep(100 * 1000);
            }
        } catch (InterruptedException e) {
        }
    }
}
