package org.picocontainer.defaults;

import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.PicoInitializationException;

import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InstanceComponentAdapter implements ComponentAdapter {
    private Object componentKey;
    private Object componentInstance;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) {
        this.componentKey = componentKey;
        this.componentInstance = componentInstance;
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return componentInstance.getClass();
    }

    public Object instantiateComponent(ComponentRegistry componentRegistry) {
        return componentInstance;
    }
}
