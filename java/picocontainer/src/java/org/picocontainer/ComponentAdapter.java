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

import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * A component adapter is responsible for providing
 * a specific component instance.
 *
 * @author Jon Tirs&eacute;n
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public interface ComponentAdapter {
    /**
     * @return the component's key.
     */
    Object getComponentKey();

    /**
     * @return the component's implementation class.
     */
    Class getComponentImplementation();

    /**
     * Gets the component instance. This method will usually create
     * a new instance for each call (an exception is {@link org.picocontainer.defaults.CachingComponentAdapter}).
     *
     * @return the component instance.
     * @throws PicoInitializationException if the component couldn't be instantiated
     */
    Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException;

    /**
     * Verify that all dependencies for this adapter can be satisifed.
     *
     * @throws PicoIntrospectionException if the dependencies cannot be resolved.
     */
    void verify() throws UnsatisfiableDependenciesException;

    PicoContainer getContainer();

    /**
     * Sets the container in which this instance is registered, called by the container upon
     * registration.
     * @param picoContainer
     */
    void setContainer(PicoContainer picoContainer);
}
