/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ComponentFactory {

    /**
     * Create a component. Used by the internals of applicable PicoContainers
     * to instantiate a component.
     * @param compType The component type to instantiate
     * @param constructor The constructor to use to create the component.
     * @param args The arguments to pass in to the constructor
     * @return The component
     * @throws PicoInvocationTargetStartException If a problem creating the component.
     */
    Object createComponent(Class compType, Constructor constructor, Object[] args) throws PicoInvocationTargetStartException;

}
