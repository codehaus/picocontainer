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
 * A component adapter is responsible for providing a specific component instance.
 * An instance of an implementation of this interface is used in a {@link PicoContainer}
 * for every registered component or instance.  Each ComponentAdapter instance has to 
 * support unique key for a single PicoContainer.  The key itself is either a class type
 * (normally an interface) or an identifier.
 * 
 * @see MutablePicoContainer
 * 
 * @author Jon Tirs&eacute;n
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * 
 * @since 1.0
 * @version $Revision$
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
     * Gets the component instance.  This method will usually create
     * a new instance for each call (an exception is {@link org.picocontainer.defaults.CachingComponentAdapter}).
     *
     * @return the component instance.
     * @throws PicoInitializationException if the component couldn't be instantiated
     * @throws PicoIntrospectionException if the component's dependencies could not be 
     * solved or the instantiation lead to an ambigous situation
     */
    Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException;

    /**
     * Verifies that all dependencies for this adapter can be satisifed.
     *
     * @throws UnsatisfiableDependenciesException if the dependencies cannot be resolved.
     */
    void verify() throws UnsatisfiableDependenciesException;

    /**
     * @return the container in which this instance is registered. 
     */
    PicoContainer getContainer();

    /**
     * Sets the container in which this instance is registered. Method is called by the container upon
     * registration.
     * @param picoContainer
     */
    void setContainer(PicoContainer picoContainer);
}
