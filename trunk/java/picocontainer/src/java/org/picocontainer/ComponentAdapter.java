/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * A component adapter is responsible for providing a specific component instance. An instance of an implementation of
 * this interface is used inside a {@link PicoContainer} for every registered component or instance.  Each
 * <code>ComponentAdapter</code> instance has to have a key which is unique within that container. The key itself is
 * either a class type (normally an interface) or an identifier.
 * 
 * @see MutablePicoContainer an extension of the PicoContainer interface which allows you to modify the contents of the
 *      container.

 * @author Jon Tirs&eacute;n
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public interface ComponentAdapter {
    /**
     * Retrieve the key associated with the component.
     * 
     * @return the component's key. Should either be a class type (normally an interface) or an identifier that is
     *         unique (within the scope of the current PicoContainer).
     */
    Object getComponentKey();

    /**
     * Retrieve the class of the component.
     * 
     * @return the component's implementation class. Should normally be a concrete class (ie, a class that can be
     *         instantiated).
     */
    Class getComponentImplementation();

    /**
     * Retrieve the component instance. This method will usually create a new instance each time it is called, but that
     * is not required. For example, {@link org.picocontainer.defaults.CachingComponentAdapter} will always return the
     * same instance.
     * 
     * @return the component instance.
     * @throws PicoInitializationException if the component could not be instantiated.
     * @throws PicoIntrospectionException  if the component has dependencies which could not be resolved, or
     *                                     instantiation of the component lead to an ambigous situation within the
     *                                     container.
     */
    Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException;

    /**
     * Retrieve the container in which the component is registered.
     * 
     * @return the container in which the component is registered.
     */
    PicoContainer getContainer();

    /**
     * Set the container in which this adapter is registered. This method will be called once by the container when the
     * adapter is registered in that container. It should usually not be called directly.
     * 
     * @param picoContainer the container in which this adapter is registered.
     */
    void setContainer(PicoContainer picoContainer);

    /**
     * Verify that all dependencies for this adapter can be satisifed. Normally, the adapter should verify this by
     * checking that the associated PicoContainer contains all the needed dependnecies.
     * 
     * @throws UnsatisfiableDependenciesException
     *          if one or more dependencies cannot be resolved.
     */
    void verify() throws UnsatisfiableDependenciesException;
}
