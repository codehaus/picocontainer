/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;

import java.util.Set;

/**
 * PicoContainer - guaranteed to resolve the needs of components
 * as it instantiates them.
 *
 */
public interface PicoContainer {

    /**
     * Does the internals have a partilcilar component type?
     * @param componentKey The component type to look for.
     * @return true if it does have the component type
     */
    boolean hasComponent(Object componentKey);

    /**
     * Get a component for a component type.
     * @param componentKey The component type to look for.
     * @return the component, or null of no such component.
     */
    Object getComponent(Object componentKey);

    /**
     * Get all components (random order).
     * @return A collection of components.
     */
    Set getComponents();

    /**
     * Get all component types (random order).
     * @return A collection of component types.
     */
    Set getComponentKeys();

    /**
     * Initialize the internals.
     */
    void instantiateComponents() throws PicoInitializationException;

    /**
     * Shorthand for {@link #getCompositeComponent(boolean, boolean)}(true, true).
     * @return a proxy.
     */
    Object getCompositeComponent();

    /**
     * Returns a proxy that implements the union of all the components'
     * interfaces.
     * Calling a method on the returned Object will call the
     * method on all components in the internals that implement
     * that interface.
     *
     * @param callInInstantiationOrder whether to call the methods in the order of instantiation (true) or reverse (false)
     * @param callUnmanagedComponents whether to exclude components registered via instance rather than class
     */
    Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents);


}
