/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;

/**
 * <p>
 * Component adapter which wraps a component instance.
 * </p>
 * <p>
 * This component adapter is a {@link LifecycleManager LifecycleManager}
 * which uses the {@link LifecycleStrategy LifecycleStrategy} to control the lifecycle
 * of the component.
 * </p>
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision$
 */
public class InstanceComponentAdapter extends AbstractComponentAdapter implements LifecycleManager {
    private Object componentInstance;
    private LifecycleStrategy lifecycleStrategy;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentInstance, new DefaultLifecycleStrategy());
    }

    public InstanceComponentAdapter(Object componentKey, Object componentInstance, LifecycleStrategy lifecycleStrategy) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentInstance.getClass());
        this.componentInstance = componentInstance;
        this.lifecycleStrategy = lifecycleStrategy;
    }
    
    public Object getComponentInstance(PicoContainer container) {
        return componentInstance;
    }

    public void verify(PicoContainer container) {
    }

    public void start(PicoContainer container) {
        lifecycleStrategy.start(componentInstance);
    }

    public void stop(PicoContainer container) {
        lifecycleStrategy.stop(componentInstance);
    }

    public void dispose(PicoContainer container) {
        lifecycleStrategy.dispose(componentInstance);
    }

    public LifecycleStrategy currentLifecycleStrategy() {
        return lifecycleStrategy;
    }
}
