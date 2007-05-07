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
 * This is the core interface for PicoContainer. It is used to retrieve addComponent instances from the container; it only
 * has accessor methods (in addition to the {@link #accept(PicoVisitor)} method). In order to register components in a
 * PicoContainer, use a {@link MutablePicoContainer}, such as {@link org.picocontainer.defaults.DefaultPicoContainer}.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @see <a href="package-summary.html#package_description">See package description for basic overview how to use
 *      PicoContainer.</a>
 * @since 1.0
 */
public interface PicoContainer {

    /**
     * Retrieve a addComponent instance registered with a specific key or type. If a addComponent cannot be found in this container,
     * the parent container (if one exists) will be searched.
     * 
     * @param componentKeyOrType the key or Type that the addComponent was registered with.
     * @return an instantiated addComponent, or <code>null</code> if no addComponent has been registered for the specified
     *         key.
     */
    Object getComponent(Object componentKeyOrType);

    <T> T getComponent(Class<T> componentType);


    /**
     * Retrieve all the registered addComponent instances in the container, (not including those in the parent container).
     * The components are returned in their order of instantiation, which depends on the dependency order between them.
     * 
     * @return all the components.
     * @throws PicoException if the instantiation of the addComponent fails
     */
    List getComponents();

    /**
     * Retrieve the parent container of this container.
     * 
     * @return a {@link PicoContainer} instance, or <code>null</code> if this container does not have a parent.
     */
    PicoContainer getParent();

    /**
     * Find a addComponent addAdapter associated with the specified key. If a addComponent addAdapter cannot be found in this
     * container, the parent container (if one exists) will be searched.
     * 
     * @param componentKey the key that the addComponent was registered with.
     * @return the addComponent addAdapter associated with this key, or <code>null</code> if no addComponent has been
     *         registered for the specified key.
     */
    ComponentAdapter getComponentAdapter(Object componentKey);

    /**
     * Find a addComponent addAdapter associated with the specified type. If a addComponent addAdapter cannot be found in this
     * container, the parent container (if one exists) will be searched.
     * 
     * @param componentType the type of the addComponent.
     * @return the addComponent addAdapter associated with this class, or <code>null</code> if no addComponent has been
     *         registered for the specified key.
     */
    ComponentAdapter getComponentAdapter(Class componentType);

    /**
     * Retrieve all the addComponent adapters inside this container. The addComponent adapters from the parent container are
     * not returned.
     * 
     * @return a collection containing all the {@link ComponentAdapter}s inside this container. The collection will not
     *         be modifiable.
     * @see #getComponentAdapters(Class) a variant of this method which returns the addComponent adapters inside this
     *      container that are associated with the specified type.
     */
    Collection<ComponentAdapter> getComponentAdapters();

    /**
     * Retrieve all addComponent adapters inside this container that are associated with the specified type. The addComponent
     * adapters from the parent container are not returned.
     * 
     * @param componentType the type of the components.
     * @return a collection containing all the {@link ComponentAdapter}s inside this container that are associated with
     *         the specified type. Changes to this collection will not be reflected in the container itself.
     */
    List<ComponentAdapter> getComponentAdapters(Class componentType);

    /**
     * Returns a List of components of a certain componentType. The list is ordered by instantiation order, starting
     * with the components instantiated first at the beginning.
     * 
     * @param componentType the searched type.
     * @return a List of components.
     * @throws PicoException if the instantiation of a addComponent fails
     * @since 1.1
     */
    List getComponents(Class componentType);

    /**
     * Accepts a visitor that should visit the child containers, addComponent adapters and addComponent instances.
     * 
     * @param visitor the visitor
     * @since 1.1
     */
    void accept(PicoVisitor visitor);
}
