package org.picocontainer.monitors;

import org.picocontainer.ComponentMonitor;

import java.lang.reflect.Constructor;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ConsoleComponentMonitor implements ComponentMonitor {

    public void instantiating(Constructor constructor) {
        ///CLOVER:OFF
        System.out.println("ComonentMonitor: instantiating constructor: " + constructor.toString());
        ///CLOVER:ON
    }

    public void instantiated(Constructor constructor, long beforeTime, long duration) {
        ///CLOVER:OFF
        System.out.println("ComonentMonitor: instantiated constructor: " + constructor.toString() + "[ " + duration + "millis]");
        ///CLOVER:ON
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        ///CLOVER:OFF
        System.out.println("ComonentMonitor: instantiationFailed constructor: " + constructor.toString() + ", reason '" + e.getMessage() + "'");
        ///CLOVER:ON
    }
}
