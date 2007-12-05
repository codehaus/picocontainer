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
 * has accessor methods (in addition to the {@link #accept(PicoVisitor)} method). In order to register components in a
 * PicoContainer, use a {@link MutablePicoContainer}, such as {@link DefaultPicoContainer}.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @see <a href="package-summary.html#package_description">See package description for basic overview how to use
 *      PicoContainer.</a>
 */
public interface PicoContainer {

    /**
     * Retrieve a component instance registered with a specific key or type. If a component cannot be found in this container,
     * the parent container (if one exists) will be searched.
     *
     * @param componentKeyOrType the key or Type that the component was registered with.
     * @return an instantiated component, or <code>null</code> if no component has been registered for the specified
     *         key.
     */
    Object getComponent(Object componentKeyOrType);

    /**
     * Retrieve a component instance of desired type. will delegate to 
     * parent container (if any) 
     * @param componentType 	type of desirec component
     * @return an instantiated component  or <code>null</code> if no component of specified type 
     * was registered. 
     * @throws AmbiguousComponentResolutionException  if more than one component is available from
     * 					container
     * @TODO  maybe this exception can be factored out of AbstractInjector? 
     */
    <T> T getComponent(Class<T> componentType);


    /**
     * Retrieve all the registered component instances in the container, (not including those in the parent container).
     * The components are returned in their order of instantiation, which depends on the dependency order between them.
     *
     * @return all the components.
     * @throws PicoException if the instantiation of the component fails
     */
    List getComponents();

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
     * @return the component adapter associated with this key, or <code>null</code> if no component has been
     *         registered for the specified key.
     */
    ComponentAdapter<?> getComponentAdapter(Object componentKey);

    /**
     * Find a component adapter associated with the specified type. If a component adapter cannot be found in this
     * container, the parent container (if one exists) will be searched.
     *
     * @param componentType the type of the component.
     * @return the component adapter associated with this class, or <code>null</code> if no component has been
     *         registered for the specified key.
     * @deprecated this functionality is also in lookup by class. not sure this is
     * interesting for users
     */

    <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType);

    /**
     * Retrieve all the component adapters inside this container. The component adapters from the parent container are
     * not returned.
     *
     * @return a collection containing all the {@link ComponentAdapter}s inside this container. The collection will not
     *         be modifiable.
     * @see #getComponentAdapters(Class) a variant of this method which returns the component adapters inside this
     *      container that are associated with the specified type.
     */
    Collection<ComponentAdapter<?>> getComponentAdapters();

    /**
     * retrieve all the keys of component adapters registered in this container. 
     * parent containers are not consulted. this collection is unimodifable
     * to prevent occasional out-of-bound modifcation of container content
     * @return collection containing all the keys of components registered in this container 
     */
    Collection getComponentKeys();
    
    /**
     * Retrieve all component adapters inside this container that are associated with the specified type. The addComponent
     * adapters from the parent container are not returned.
     *
     * @param componentType the type of the components.
     * @return a collection containing all the {@link ComponentAdapter}s inside this container that are associated with
     *         the specified type. Changes to this collection will not be reflected in the container itself.
     */
    <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType);

    /**
     * Returns a List of components of a certain componentType. The list is ordered by instantiation order, starting
     * with the components instantiated first at the beginning.
     *
     * @param componentType the searched type.
     * @return a List of components.
     * @throws PicoException if the instantiation of a component fails
     */
    <T> List<T> getComponents(Class<T> componentType);

    /**
     * Accepts a visitor that should visit the child containers, component adapters and component instances.
     *
     * @param visitor the visitor
     */
    void accept(PicoVisitor visitor);

}
