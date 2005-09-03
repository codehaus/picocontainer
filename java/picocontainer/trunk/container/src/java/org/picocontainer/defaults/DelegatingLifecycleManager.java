/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;


/**
 * <p>
 * Implementation of lifecycle manager which delegates to the container's component adapters. 
 * This LifecycleManager will delegate calls on the lifecycle methods to the component adapters
 * if these are themselves LifecycleManagers.
 * </p>
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public class DelegatingLifecycleManager implements LifecycleManager, Serializable {
        
    /**
     * {@inheritDoc} 
     * Loops over all component adapters and invokes 
     * start(PicoContainer) method on the ones which are LifecycleManagers
     */
    public void start(PicoContainer node) {
        List adapters = new ArrayList(node.getComponentAdapters());
        for ( int i = 0; i < adapters.size(); i++ ){
            Object adapter = adapters.get(i);
            if ( adapter instanceof LifecycleManager ){
                LifecycleManager manager = (LifecycleManager)adapter;
                manager.start(node);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Loops over all component adapters (in inverse order) and invokes 
     * stop(PicoContainer) method on the ones which are LifecycleManagers
     */
    public void stop(PicoContainer node) {
        List adapters = new ArrayList(node.getComponentAdapters());
        for (int i = adapters.size() - 1; 0 <= i; i--) {
            Object adapter = adapters.get(i);
            if ( adapter instanceof LifecycleManager ){
                LifecycleManager manager = (LifecycleManager)adapter;
                manager.stop(node);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Loops over all component adapters (in inverse order) and invokes 
     * dispose(PicoContainer) method on the ones which are LifecycleManagers
     */
    public void dispose(PicoContainer node) {
        List adapters = new ArrayList(node.getComponentAdapters());
        for (int i = adapters.size() - 1; 0 <= i; i--) {
            Object adapter = adapters.get(i);
            if ( adapter instanceof LifecycleManager ){
                LifecycleManager manager = (LifecycleManager)adapter;
                manager.dispose(node);
            }
        }
    }
}
