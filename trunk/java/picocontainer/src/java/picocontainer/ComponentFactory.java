/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

public interface ComponentFactory {

    /**
     * Create a component. Used by the internals of applicable PicoContainers
     * to instantiate a component.
     * @param componentType the type the component will be registered as.
     * @param componentImplementation concrete component class.
     * @param instanceDependencies The component instances the created component will depend on.
     * @return The component
     * @throws picocontainer.defaults.PicoInvocationTargetInitializationException If a problem creating the component.
     */
    Object createComponent(Class componentType, Class componentImplementation, Class[] dependencies, Object[] instanceDependencies) throws PicoInstantiationException, PicoIntrospectionException;

    /**
     * Return the types the componentImplementation component depends on.
     * @param componentImplementation concrete component class.
     * @return an array of dependent types.
     */
    Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException;
}
