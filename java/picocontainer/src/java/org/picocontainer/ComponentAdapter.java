package org.picocontainer;

import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * A component adapter is responsible for instantiating and caching
 * a specific component instance. It is used internally by PicoContainer,
 * and is not meant to be used directly by clients of the PicoContainer API.
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
     * @param dependencyContainer container where the adapter can look for
     *  dependent component instances
     * @return the component instance
     * @throws PicoInitializationException if the component couldn't be instantiated
     */
    Object getComponentInstance(MutablePicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException;

    /**
     *  Verify that all dependencies for this adapter can be satisifed given picoConatiner
     * @param picoContainer container where the dependencied fot this adapter will be resolved
     * @throws NoSatisfiableConstructorsException 
     */ 
    void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException;

}
