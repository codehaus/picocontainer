/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.*;

import java.util.*;

/**
 * DelegatingPicoContainer is a special PicoContainer that will first look inside itself
 * for a component, and if it can't find it, it will ask the delegate(s).
 * <p>
 * This can be used to create trees or graphs of connected containers.
 * <p>
 * It replaces the old HierarchicalComponentRegistry and
 * CompositePicoContainer.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DelegatingPicoContainer extends DefaultPicoContainer {

    private final List delegates;

    public DelegatingPicoContainer(ComponentAdapterFactory componentAdapterFactory, MutablePicoContainer[] delegates) {
        super(componentAdapterFactory);
        this.delegates = Arrays.asList(delegates);
    }

    public DelegatingPicoContainer(ComponentAdapterFactory componentAdapterFactory, MutablePicoContainer delegate) {
        this(componentAdapterFactory, new MutablePicoContainer[]{delegate});
    }

    public DelegatingPicoContainer(MutablePicoContainer[] delegates) {
        this(new DefaultComponentAdapterFactory(), delegates);
    }

    public DelegatingPicoContainer(MutablePicoContainer delegate) {
        this(new DefaultComponentAdapterFactory(), delegate);
    }

    public Collection getComponentKeys() {
        Set result = new HashSet();
        result.addAll(super.getComponentKeys());
        for (Iterator iterator = delegates.iterator(); iterator.hasNext();) {
            MutablePicoContainer delegate = (AbstractPicoContainer) iterator.next();
            result.addAll(delegate.getComponentKeys());
        }
        return result;
    }

    public ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        ComponentAdapter result = super.findComponentAdapter(componentKey);
        if(result != null) {
            return result;
        } else {
            for (Iterator iterator = delegates.iterator(); iterator.hasNext();) {
                MutablePicoContainer delegate = (AbstractPicoContainer) iterator.next();
                ComponentAdapter componentAdapter = delegate.findComponentAdapter(componentKey);
                if(componentAdapter != null) {
                    return componentAdapter;
                }
            }
            return null;
        }
    }
}
