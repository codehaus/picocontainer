/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

/**
 * PicoContainer - guaranteed to resolve the needs of components
 * as it instantiates them.
 *
 */
public interface PicoContainer {

    /**
     * Does the container have a partilcilar component type?
     * @param componentType The component type to look for.
     * @return true if it does have the component type
     */
    boolean hasComponent(Class componentType);

    /**
     * Get a component for a component type.
     * @param componentType The component type to look for.
     * @return the component, or null of no such component.
     */
    Object getComponent(Class componentType);

    /**
     * Get all components (random order).
     * @return An array of components.
     */
    Object[] getComponents();

    /**
     * Get all component types (random order).
     * @return An Array of component types.
     */
    Class[] getComponentTypes();

    /**
     * Initialize the container.
     */
    void initializeContainer() throws PicoInitializationException;

}
