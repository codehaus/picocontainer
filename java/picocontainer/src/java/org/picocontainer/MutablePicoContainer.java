/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.picocontainer;

import org.picocontainer.defaults.ComponentAdapter;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 */
public interface MutablePicoContainer extends PicoContainer {

    void registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException;
    void registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException;
    void registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException;

    void registerComponentInstance(Object component) throws PicoRegistrationException;
    void registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException;

    void unregisterComponent(Object componentKey);

    ///// This is implementation specific, and should ideally go away
    public ComponentAdapter findComponentAdapter(Object componentKey) throws PicoIntrospectionException;
}
