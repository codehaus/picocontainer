package org.picocontainer.internals;

import org.picocontainer.PicoInitializationException;

import java.util.List;

/**
 * A component adapter is responsible for instantiating and caching
 * a specific component.
 */
public interface ComponentAdapter {
    /**
     * The component's key.
     * @return
     */
    Object getComponentKey();

    /**
     * The component's implementation class.
     * @return
     */
    Class getComponentImplementation();

    /**
     * Gets the component instance. Subsequent calls to this method
     * with the same arguments should return the same object.
     *
     * @param componentRegistry registries where the adapter can look for
     *  dependent component instances
     * @return the component instance
     * @throws PicoInitializationException if the component couldn't be instantiated
     */
    Object instantiateComponent(ComponentRegistry componentRegistry) throws PicoInitializationException;

}
