package org.picocontainer.gems;

import org.picocontainer.gems.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.BehaviorDecorator;

public class PicoGemsBuilder {

    public static BehaviorDecorator IMPL_HIDING() {
        return new ImplementationHidingComponentAdapterFactory();
    }

    public static ComponentMonitor LOG4J() {
        return new Log4JComponentMonitor();
    }



}
