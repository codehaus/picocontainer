package org.picocontainer;

/**
 * A component adapter is responsible for instantiating and caching
 * a specific component instance.
 *
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
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
     * with the same arguments should return the same object (it should
     * be caching).
     * <p>
     * This method should also register the instantiated component with the
     * pico container.
     *
     * @param picoContainer registries where the adapter can look for
     *  dependent component instances
     * @return the component instance
     * @throws PicoInitializationException if the component couldn't be instantiated
     */
    Object getComponentInstance(MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException;

}
