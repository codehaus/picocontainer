/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.defaults;

import org.nanocontainer.aop.AspectsApplicator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.AbstractBehaviorDecoratorFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.LifecycleStrategy;

/**
 * Produces addComponent adapters that apply aspects to components.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public class AspectsComponentAdapterFactory extends AbstractBehaviorDecoratorFactory {

    private final AspectsApplicator aspectsApplicator;

    /**
     * Creates a new <code>AspectsComponentAdapterFactory</code>. The factory
     * will produce <code>AspectsComponentAdapter</code> objects that will use
     * <code>aspectsApplicator</code> to apply aspects to components produced
     * by <code>delegate</code>.
     *
     * @param aspectsApplicator used to apply the aspects.
     */
    public AspectsComponentAdapterFactory(AspectsApplicator aspectsApplicator) {
        this.aspectsApplicator = aspectsApplicator;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new AspectsComponentAdapter(aspectsApplicator, super.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey,
                componentImplementation, parameters));
    }

}