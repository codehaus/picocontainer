package org.nanocontainer;

import org.picocontainer.PicoContainer;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface NanoContainerMonitor {
    void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa);

    void componentsInstantiated(PicoContainer picoContainer);
}
