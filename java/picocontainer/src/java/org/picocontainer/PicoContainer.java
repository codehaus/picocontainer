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
public interface PicoContainer extends Startable, Disposable {

    /**
     * Gets a component instance registered with a specific key.
     *
     * @param componentKey key the component was registered with.
     * @return an instantiated component.
     */
    Object getComponentInstance(Object componentKey);

    /**
     * Finds a component instance matching the type, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentType type of the component.
     * @return the adapter matching the class.
     */
    Object getComponentInstanceOfType(Class componentType);

    /**
     * Gets all the registered component instances in the container, (not including
     * those in the parent container).
     * The components are returned in their order of instantiation, which
     * depends on the dependency order between them.
     *
     * @return all the components.
     */
    List getComponentInstances();

    /**
     * Get the parent container of this container.
     *
     * @return a Collection of {@link PicoContainer}.
     */
    PicoContainer getParent();

    /**
     * Finds a ComponentAdapter matching the key, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentKey key of the component.
     * @return the adapter matching the key.
     */
    ComponentAdapter getComponentAdapter(Object componentKey);

    /**
     * Finds a ComponentAdapter matching the type, looking in parent if
     * not found in self (unless parent is null).
     *
     * @param componentType type of the component.
     * @return the adapter matching the class.
     */
    ComponentAdapter getComponentAdapterOfType(Class componentType);

    /**
     * Returns all adapters (not including the adapters from the parent).
     * @return Collection of {@link ComponentAdapter}.
     */
    Collection getComponentAdapters();

    /**
     * Verifies that the dependencies for all the registered components can be satisfied
     * None of the components are instantiated during the verification process.
     *
     * @throws PicoVerificationException if there are unsatisifiable dependencies.
     */
    void verify() throws PicoVerificationException;

    /**
     * Callback method from the implementation to keep track of the instantiation
     * order. This method is not intended to be called explicitly by clients of the API!
     */
    void addOrderedComponentAdapter(ComponentAdapter componentAdapter);
}
