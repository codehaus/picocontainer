package org.picocontainer;

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
     * Checks for the presence of a particular component.
     *
     * @param componentKey key of the component to look for.
     * @return true if there is a component for this key.
     */
    boolean hasComponent(Object componentKey);

    /**
     * Returns an object (in fact, a dynamic proxy) that implements the union
     * of all the interfaces of the currently registered components.
     * <p>
     * Casting this object to any of those interfaces and then calling a method
     * on it will result in that call being multicast to all the components implementing
     * that given interface.
     * <p>
     * This is a simple yet extremely powerful way to handle lifecycle of components.
     * Component writers can invent their own lifecycle interfaces, and then use the multicaster
     * to invoke the method in one go.
     *
     * @param callInInstantiationOrder whether or not to call the method in the order of instantiation,
     *    which depends on the components' inter-dependencies.
     * @param callUnmanagedComponents whether or not to multicast to components that are not managed
     *    by this container.
     * @return a multicaster object.
     */
    Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException;

    /**
     * Shorthand for {@link #getComponentMulticaster(boolean, boolean)}<pre>(true, false)</pre>,
     * which is the most common usage scenario.
     *
     * @return a multicaster object.
     * @throws PicoException
     */
    Object getComponentMulticaster() throws PicoException;

    /**
     * Get all the component keys.
     * @return all the component keys.
     */
    Collection getComponentKeys();

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
     * there will be 0..1 parents. However, it is possible to have several parents.
     * A container will look in its parents if a component can be found in self.
     *
     * @return a Collection of {@link PicoContainer}.
     */
    List getParentContainers();

    /**
     * Finds a ComponentAdapter matching the key. This method is an "expert" method, and should
     * normally not be called by clients of this API. (It is called by the implementation).
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
