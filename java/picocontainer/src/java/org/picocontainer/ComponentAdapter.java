package org.picocontainer;

/**
 * A component adapter is responsible for providing
 * a specific component instance.
 *
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 */
public interface ComponentAdapter {
    /**
     * @return the component's key.
     */
    Object getComponentKey();

    /**
     * @return the component's implementation class.
     */
    Class getComponentImplementation();

    /**
     * Gets the component instance. This method will usually create
     * a new instance for each call (an exception is {@link org.picocontainer.defaults.CachingComponentAdapter}).
     *
     * @param dependencyContainer container where the adapter can look for
     *  dependent component instances.
     * @return the component instance.
     * @throws PicoInitializationException if the component couldn't be instantiated
     */
    Object getComponentInstance(MutablePicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException;

    /**
     * Verify that all dependencies for this adapter can be satisifed.
     * 
     * @param picoContainer container where the dependencied fot this adapter will be resolved.
     * @throws PicoIntrospectionException if the dependencies cannot be resolved.
     */
    void verify(PicoContainer picoContainer) throws PicoIntrospectionException;

}
