package org.microcontainer.impl;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class Standalone {

    public static void main(String[] args) {

        final DefaultKernel kernel = new DefaultKernel();

		// add a shutdown hook that will tell the builder to kill it.
        Runnable shutdownHook = new Runnable() {
            public void run() {
                System.out.println("Shutting Down MicroContainer");
                try {
                    kernel.stop();
                    kernel.dispose();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Exiting JVM");
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));

    }
}
