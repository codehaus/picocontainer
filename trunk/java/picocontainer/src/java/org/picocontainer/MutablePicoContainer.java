/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.picocontainer;

import java.util.List;

/**
 * This is the core interface for registration of components.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface MutablePicoContainer extends PicoContainer {

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentImplementation the concrete component class.
     * @param parameters an array of parameters that gives the container hints about what arguments
     *    to pass to the constructor when it is instantiated.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException;

    /**
     * Registers a component using the componentImplementation as key.
     *
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object, using itself as a key.
     *
     * @param componentInstance
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object as a compoent in the container. This is
     * handy when other components in the same container have dependencies on this
     * kind of object, but where letting the container manage and instantiate it
     * is impossible.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentInstance an arbitrary object.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException;

    /**
     * Registers a component via a ComponentAdapter. Use this if you need fine grained control over what ComponentAdapter
     * to use for a specific component.
     *
     * @param componentAdapter the adapter
     * @throws PicoRegistrationException if registration fails.
     */
    void registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException;

    /**
     * Unregisters a component.
     *
     * @param componentKey key of the component to unregister.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter unregisterComponent(Object componentKey);

    /**
     * Registers an ordered component adapter candidate for a component. Subsequent calls to
     * {@link #addOrderedComponentAdapter(ComponentAdapter)} will be ignored for
     * all adapters, except the first caller of this method for each component key.
     * 
     * You don't need to call this mehtod, unless you are implementing custom component adapters.
     * Use {@link #registerComponentInstance(Object)} and {@link #registerComponentInstance(Object, Object)}
     * to register externally instantiated objects.
     *
     * @param componentAdapter adapter of the component.
     */
    void registerOrderedComponentAdapter(ComponentAdapter componentAdapter);

    /**
     * Adds a component instance to the instantiation order list. Calls to this method are ignored
     * for all adapters except the first caller of {@link #registerOrderedComponentAdapter(ComponentAdapter)}
     * for each component key. 
     * 
     * You don't need to call this mehtod, unless you are implementing custom component adapters.
     * Use {@link #registerComponentInstance(Object)} and {@link #registerComponentInstance(Object, Object)}
     * to register externally instantiated objects.
     *
     * @param componentAdapter adapter of the component.
     */
    void addOrderedComponentAdapter(ComponentAdapter componentAdapter);

    /**
     * Adds a Child container. Will also add this instance as a parent to child,
     * so calling {@link #addParent} is not necessary.
     *
     * @param child child container.
     * @return true if the child was actually added.
     */
    boolean addChild(MutablePicoContainer child);

    /**
     * Adds a Parent container. Will also add this instance as a child to parent,
     * so calling {@link #addChild} is not necessary.
     *
     * @param parent parent container.
     * @return true if the parent was actually added.
     */
    boolean addParent(MutablePicoContainer parent);

    /**
     * Adds a Child container. Will also remove this instance as a parent to child,
     * so calling {@link #removeParent} is not necessary.
     *
     * @param child child container.
     * @return true if the child was actually removed.
     */
    boolean removeChild(MutablePicoContainer child);

    /**
     * Adds a Parent container. Will also remove this instance as a child to parent,
     * so calling {@link #removeChild} is not necessary.
     *
     * @param parent parent container.
     * @return true if the parent was actually removed.
     */
    boolean removeParent(MutablePicoContainer parent);

    List getComponentAdapters();
}
