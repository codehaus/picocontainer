package org.nanocontainer;

import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ConsoleNanoContainerMonitor implements NanoContainerMonitor {

    public void componentsInstantiated(PicoContainer picoContainer) {
        System.out.println("Components Instantiated For Container " + picoContainer);
    }

    public void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa) {
        System.out.println("ComponentEvent '" + eventName + "'" + lpa);
    }
}
