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
public interface PicoContainer extends Lifecycle {
    /**
     * Checks for the presence of a particular component.
     *
     * @param componentKey key of the component to look for.
     * @return true if there is a component for this key.
     * @deprecated We don't need this. Can be determined with
     * {@link #getComponentInstance(Object)} != null.
     */
    boolean hasComponent(Object componentKey);

    /**
     * Get all the component keys.
     * @return all the component keys.
     * @deprecated We don't need to expose this. The collection can be constructed outside
     * with data from {@link #getComponentAdapters()}.
     */
    Collection getComponentKeys();

    /**
     * Gets a component instance registered with a specific key.
     *
     * @param componentKey key the component was registered with.
     * @return an instantiated component.
     * @throws PicoException if the component could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    Object getComponentInstance(Object componentKey) throws PicoException;

    /**
     * Finds a component instance matching the type, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentType type of the component.
     * @return the adapter matching the class.
     */
    Object getComponentInstanceOfType(Class componentType);

    /**
     * Gets all the registered component instances in the container, including
     * those in the parent (unless it is null).
     * The components are returned in their order of instantiation, which
     * depends on the dependency order between components.
     *
     * @return all the components.
     * @throws PicoException if one of the components could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    List getComponentInstances() throws PicoException;

    /**
     * Get the parent container of this container.
     *
     * @return a Collection of {@link PicoContainer}.
     */
    PicoContainer getParent();

    /**
     * Verifies that the dependencies for all the registered components can be satisfied
     * None of the components are instantiated during the verification process.
     *
     * @throws PicoVerificationException if there are unsatisifiable dependencies.
     */
    void verify() throws PicoVerificationException;

    /**
     * Finds a ComponentAdapter matching the key, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentKey key of the component.
     * @return the adapter matching the key.
     */
    ComponentAdapter getComponentAdapter(Object componentKey) throws PicoIntrospectionException;

    /**
     * Finds a ComponentAdapter matching the type, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentType type of the component.
     * @return the adapter matching the class.
     */
    ComponentAdapter getComponentAdapterOfType(Class componentType);

    /**
     * Return all adapters (not including the adapters from the parent).
     * @return Collection of {@link ComponentAdapter}.
     */
    Collection getComponentAdapters();
}
