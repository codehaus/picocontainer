package org.picocontainer;

import org.picocontainer.lifecycle.Lifecycle;

import java.util.Collection;
import java.util.List;

/**
 * This is the core interface for PicoContainer. It only has accessor methods.
 * In order to register components in a PicoContainer, use a {@link MutablePicoContainer},
 * such as {@link org.picocontainer.defaults.DefaultPicoContainer}.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface PicoContainer {
    /**
     * Gets a component instance.
     *
     * @param componentKey key the component was registered with.
     * @return an instantiated component.
     * @throws PicoException if the component could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    Object getComponentInstance(Object componentKey) throws PicoException;

    /**
     * Gets all the registered component instances in the container.
     * The components are returned in their order of instantiation, which
     * depends on the dependency order between components.
     *
     * @return all the components.
     * @throws PicoException if one of the components could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    List getComponentInstances() throws PicoException;

    /**
     * Gets component instances that are registered, but not managed by the container.
     *
     * @return unmanaged components.
     */
    List getUnmanagedComponentInstances() throws PicoException;

    /**
     * Checks for the presence of a particular component.
     *
     * @param componentKey key of the component to look for.
     * @return true if there is a component for this key.
     */
    boolean hasComponent(Object componentKey);

    /**
     * Get all the component keys.
     * @return all the component keys.
     */
    List getComponentKeys();

    /**
     * Get the child containers of this container. Any given container instance should not use
     * the child containers to resolve components, but rahter their parents. This method
     * is available merely to be able to traverse trees of containers, and is not used by the
     * container itself.
     * @return a Collection of {@link PicoContainer}.
     * @see #getParentContainers()
     */
    Collection getChildContainers();

    /**
     * Get the parent containers of this container. In a purely hierarchical (tree structure) container,
     * there will be 0..1 parents. However, it is possible to have several parents to construct graphs.
     * A container will look in its parents if a component can be found in self.
     *
     * @return a Collection of {@link PicoContainer}.
     */
    List getParentContainers();

    /**
     * Finds a ComponentAdapter matching the key.
     *
     * @param componentKey key of the component.
     */
    ComponentAdapter findComponentAdapter(Object componentKey) throws PicoIntrospectionException;

    /**
     * Verifies that the dependencies for all the registered components can be satisfied
     * None of the components are instantiated during the verification process.
     *
     * @throws PicoVerificationException if there are unsatisifiable dependencies.
     */
    void verify() throws PicoVerificationException;
}
