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
import org.picocontainer.monitors.DefaultComponentMonitor;

/**
 * <p>
 * Component adapter which wraps a component instance.
 * </p>
 * <p>
 * This component adapter supports both a {@link LifecycleManager LifecycleManager} and a 
 * {@link LifecycleStrategy LifecycleStrategy} to control the lifecycle of the component.
 * The lifecycle manager methods simply delegate to the lifecycle strategy methods 
 * on the component instance.
 * </p>
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision$
 */
public class InstanceComponentAdapter extends AbstractComponentAdapter implements LifecycleManager, LifecycleStrategy {
    private Object componentInstance;
    private LifecycleStrategy lifecycleStrategy;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentInstance, new DefaultLifecycleStrategy(new DefaultComponentMonitor()));
    }

    public InstanceComponentAdapter(Object componentKey, Object componentInstance, LifecycleStrategy lifecycleStrategy) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, getInstanceClass(componentInstance));
        this.componentInstance = componentInstance;
        this.lifecycleStrategy = lifecycleStrategy;
    }

    private static Class getInstanceClass(Object componentInstance) {
        if (componentInstance == null) {
            throw new NullPointerException("componentInstance cannot be null");
        }
        return componentInstance.getClass();
    }

    public Object getComponentInstance(PicoContainer container) {
        return componentInstance;
    }
    
    public void verify(PicoContainer container) {
    }

    // ~~~~~~~~ LifecylceManager ~~~~~~~~

    public void start(PicoContainer container) {
        start(componentInstance);
    }

    public void stop(PicoContainer container) {
        stop(componentInstance);
    }

    public void dispose(PicoContainer container) {
        dispose(componentInstance);
    }

    public boolean hasLifecycle() {
        return hasLifecycle(componentInstance.getClass());
    }

    // ~~~~~~~~ LifecylceStrategy ~~~~~~~~
    
    public void start(Object component) {
        lifecycleStrategy.start(componentInstance);
    }

    public void stop(Object component) {
        lifecycleStrategy.stop(componentInstance);
    }

    public void dispose(Object component) {
        lifecycleStrategy.dispose(componentInstance);
    }

    public boolean hasLifecycle(Class type) {
        return lifecycleStrategy.hasLifecycle(type);
    }

}
