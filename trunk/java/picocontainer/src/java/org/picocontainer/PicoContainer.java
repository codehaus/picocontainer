/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;


import java.util.Collection;
import java.util.List;

/**
 * This is the core interface for PicoContainer. It is used to retrieve component instances from the container; it only
 * has accessor methods (in addition to  the {@link #verify()} method). In order to register components in a
 * PicoContainer, use a {@link MutablePicoContainer}, such as {@link org.picocontainer.defaults.DefaultPicoContainer}.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @see "The <a href='package-summary.html#package_description'>The package description</a> has a basic overview of how to use the picocontainer package."
 * @since 1.0
 */
public interface PicoContainer extends Startable, Disposable {

    /**
     * Retrieve a component instance registered with a specific key. If a component cannot be found in this container,
     * the parent container (if one exists) will be searched.
     * 
     * @param componentKey the key that the component was registered with.
     * @return an instantiated component, or <code>null</code> if no component has been registered for the specified
     *         key.
     */
    Object getComponentInstance(Object componentKey);

//    Object getComponentInstanceFromPath(Object componentKey);

    /**
     * Find a component instance matching the specified type.
     * 
     * @param componentType the type of the component.
     * @return the adapter matching the class.
     */
    Object getComponentInstanceOfType(Class componentType);

    /**
     * Retrieve all the registered component instances in the container, (not including those in the parent container).
     * The components are returned in their order of instantiation, which depends on the dependency order between them.
     * 
     * @return all the components.
     */
    List getComponentInstances();

    /**
     * Retrieve the parent container of this container.
     * 
     * @return a {@link PicoContainer} instance, or <code>null</code> if this container does not have a parent.
     */
    PicoContainer getParent();

    /**
     * Find a component adapter associated with the specified key. If a component adapter cannot be found in this
     * container, the parent container (if one exists) will be searched.
     * 
     * @param componentKey the key that the component was registered with.
     * @return the component adapter associated with this key, or <code>null</code> if no component has been registered
     *         for the specified key.
     */
    ComponentAdapter getComponentAdapter(Object componentKey);

    /**
     * Find a component adapter associated with the specified type. If a component adapter cannot be found in this
     * container, the parent container (if one exists) will be searched.
     * 
     * @param componentType the type of the component.
     * @return the component adapter associated with this class, or <code>null</code> if no component has been
     *         registered for the specified key.
     */
    ComponentAdapter getComponentAdapterOfType(Class componentType);

    /**
     * Retrieve all the component adapters inside this container. The component adapters from the parent container are
     * not returned.
     *
     * @return a collection containing all the {@link ComponentAdapter}s inside this container. The collection will
     *         not be modifiable.
     * @see #getComponentAdaptersOfType(Class) a variant of this method which returns the component adapters inside this
     *      container that are associated with the specified type.
     */
    Collection getComponentAdapters();

    /**
     * Retrieve all component adapters inside this container that are associated with the specified type. The component
     * adapters from the parent container are not returned.
     * 
     * @param componentType the type of the components.
     * @return a collection containing all the {@link ComponentAdapter}s inside this container that are associated with
     *         the specified type. Changes to this collection will not be reflected in the container itself.
     */
    List getComponentAdaptersOfType(Class componentType);

    /**
     * Verify that the dependencies for all the registered components can be satisfied. No components are
     * instantiated during the verification process.
     * 
     * @throws PicoVerificationException if there are unsatisifiable dependencies.
     */
    void verify() throws PicoVerificationException;

    /**
     * Callback method from the implementation to keep track of the instantiation order. <b>This method is not intended
     * to be called explicitly by clients of the API!</b>
     * 
     * @param componentAdapter the freshly added {@link ComponentAdapter}
     */
    void addOrderedComponentAdapter(ComponentAdapter componentAdapter);

}
