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


/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 */
public interface MutablePicoContainer extends PicoContainer {

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     */
    void registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentImplementation the concrete component class.
     * @param parameters an array of parameters that gives the container hints about what arguments
     *    to pass to the constructor when it is instantiated.
     * @throws PicoRegistrationException if registration fails.
     */
    void registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException;

    /**
     * Registers a component using the componentImplementation as key.
     *
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     */
    void registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object, using itself as a key.
     *
     * @param componentInstance
     * @throws PicoRegistrationException
     */
    void registerComponentInstance(Object componentInstance) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object as a compoent in the container. This is
     * handy when other components in the same container have dependencies on this
     * kind of object, but where letting the container manage and instantiate it
     * is impossible.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * @param componentInstance an arbitrary object.
     * @throws PicoRegistrationException if registration fails.
     */
    void registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException;

    /**
     * Unregisters a component.
     *
     * @param componentKey key of the component to unregister.
     */
    void unregisterComponent(Object componentKey);

    /**
     * Adds a component instance to the container. Do not call this method
     * explicitly. It is used by the internals. Use {@link #registerComponentInstance}
     * instead if you wish to register externally instantiated objects.
     *
     * @param componentAdapter key of the component.
     */
    void addOrderedComponentAdapter(ComponentAdapter componentAdapter);

    /**
     * Adds a child container. <b>Don't call this method explicitly.</b>
     * In order to create connected graphs and hierarchies of containers,
     * use the {@link #addParent(PicoContainer)
     * method.
     *
     * @param child child container to add
     */
    void addChild(PicoContainer child);
    void addParent(PicoContainer parent);
}
