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

import org.picocontainer.internals.Parameter;

public interface RegistrationPicoContainer extends PicoContainer {

    /**
     * Registers a component. Same as calling {@link #registerComponent(Object, Class)}
     * with the componentImplementation as key.
     *
     * @param componentImplementation The class of the component to instantiate
     * @throws PicoRegistrationException If a registration problem
     */
    void registerComponentByClass(Class componentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException;

    /**
     * Alternate way of registering components with additional
     * component type.
     *
     * @param componentKey Component type
     * @param componentImplementation The class of the component to instantiate
     * @throws PicoRegistrationException If a registration problem
     */
    void registerComponent(Object componentKey, Class componentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException;

    /**
     * Registers a component that is instantiated and configured outside
     * the internals. Useful in cases where pico doesn't have sufficient
     * knowledge to instantiate a component.
     *
     * @param componentKey Component type
     * @param componentInstance preinstantiated component
     * @throws PicoRegistrationException If a registration problem
     */
    void registerComponent(Object componentKey, Object componentInstance)
            throws PicoRegistrationException, PicoIntrospectionException;

    /**
     * Registers an instantiated component.  This might be because you are
     * creating trees of Pico containers or if you have a class that you want treated
     * as a component, but is not Pico component compatible. Will use the components class as key.
     *
     * @param componentInstance The pre instantiated component to register
     * @throws PicoRegistrationException
     */
    void registerComponentByInstance(Object componentInstance)
            throws PicoRegistrationException, PicoIntrospectionException;

    /**
     * Register component with key, implementation and bindings for its parameters.
     *
     * @param componentKey Component type
     * @param componentImplementation The class of the component to instantiate
     * @throws PicoRegistrationException If a registration problem
     */
    void registerComponent(Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoRegistrationException, PicoIntrospectionException;

    void unregisterComponent(Object componentKey);
}
