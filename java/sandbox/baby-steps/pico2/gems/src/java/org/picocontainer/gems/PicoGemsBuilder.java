package org.picocontainer.gems;

import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.gems.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.DecoratingComponentAdapterFactory;

public class PicoGemsBuilder {

    public static DecoratingComponentAdapterFactory IMPL_HIDING() {
        return new ImplementationHidingComponentAdapterFactory();
    }

    public static ComponentMonitor LOG4J() {
        return new Log4JComponentMonitor();
    }



}
