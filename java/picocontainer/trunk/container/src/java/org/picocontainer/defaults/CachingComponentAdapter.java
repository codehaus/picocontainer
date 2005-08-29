/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * <p>
 * This ComponentAdapter caches the instance
 * </p>
 * <p>
 * This adapter is also a {@link LifecycleManager lifecycle manager} which will apply
 * its  {@link LifecycleStrategy lifecycle strategy} to the cached component instance.
 * </p>
 * 
 * @version $Revision$
 */
public class CachingComponentAdapter extends DecoratingComponentAdapter implements LifecycleManager {

    private ObjectReference instanceReference;

    public CachingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new SimpleReference());
    }

    public CachingComponentAdapter(ComponentAdapter delegate, ObjectReference instanceReference) {
        super(delegate);
        this.instanceReference = instanceReference;
    }

    public CachingComponentAdapter(ComponentAdapter delegate, ObjectReference instanceReference, LifecycleStrategy lifecycleStrategy) {
        super(delegate);
        this.instanceReference = instanceReference;
    }
    
    public Object getComponentInstance(PicoContainer container)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (instanceReference.get() == null) {
            instanceReference.set(super.getComponentInstance(container));
        }
        return instanceReference.get();
    }

    public void flush() {
        instanceReference.set(null);
    }

    /**
     * Starts the cached component instance
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        currentLifecycleStrategy().start(getComponentInstance(container));
    }

    /**
     * Stops the cached component instance
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        currentLifecycleStrategy().stop(getComponentInstance(container));
    }

    /**
     * Disposes the cached component instance
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        currentLifecycleStrategy().dispose(getComponentInstance(container));
    }

}
