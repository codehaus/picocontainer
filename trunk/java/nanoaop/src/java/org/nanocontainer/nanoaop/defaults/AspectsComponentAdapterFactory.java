/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import org.nanocontainer.nanoaop.AspectsApplicator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Stephen Molitor
 */
public class AspectsComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    private final AspectsApplicator aspectsApplicator;

    public AspectsComponentAdapterFactory(AspectsApplicator aspectsApplicator, ComponentAdapterFactory delegate) {
        super(delegate);
        this.aspectsApplicator = aspectsApplicator;
    }

    public AspectsComponentAdapterFactory(AspectsApplicator aspectsApplicator) {
        this(aspectsApplicator, new DefaultComponentAdapterFactory());
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation,
            Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return new AspectsComponentAdapter(aspectsApplicator, super.createComponentAdapter(componentKey,
                componentImplementation, parameters));
    }

}