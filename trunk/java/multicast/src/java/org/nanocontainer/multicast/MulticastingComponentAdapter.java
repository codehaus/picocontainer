/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.multicast;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Will be replaced by {@link org.nanocontainer.proxytoys.Multicaster}
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastingComponentAdapter extends AbstractComponentAdapter {
    private final List componentInstances = new ArrayList();

    private final Invoker invoker;
    private final MulticasterFactory multicasterFactory;

    public MulticastingComponentAdapter(Object key, Class componentImplementation, Invoker invoker, MulticasterFactory multicasterFactory) {
        super(key, componentImplementation);
        this.invoker = invoker;
        this.multicasterFactory = multicasterFactory;
    }

    public MulticastingComponentAdapter(Object key, Class componentImplementation) {
        this(key, componentImplementation, new MulticastInvoker(), new MulticasterFactory());
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return multicasterFactory.createComponentMulticaster(null, null, componentInstances,
                true,
                invoker
        );
    }

    public void verify() throws PicoIntrospectionException {
    }

    /**
     * Adds an object this component adapter will multicast to.
     * @param componentInstance instance to multicast to.
     */
    public void addComponentInstance(Object componentInstance) {
        componentInstances.add(componentInstance);
    }
}
