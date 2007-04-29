/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import java.io.Serializable;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.componentadapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * The Caching version of  {@link org.picocontainer.defaults.DefaultPicoContainer}
 *
 * @see ImplementationHidingCachingPicoContainer
 * @see ImplementationHidingPicoContainer
 * @author Paul Hammant
 * @version $Revision$
 */
public class CachingPicoContainer extends AbstractDelegatingMutablePicoContainer implements Serializable {
    private final ComponentAdapterFactory caf;

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        super(new DefaultPicoContainer(makeComponentAdapterFactory(caf), parent));
        this.caf = caf;
    }

    private static CachingComponentAdapterFactory makeComponentAdapterFactory(ComponentAdapterFactory caf) {
        if (caf instanceof CachingComponentAdapterFactory) {
            return (CachingComponentAdapterFactory) caf;
        }
        return new CachingComponentAdapterFactory(caf);
    }

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(PicoContainer parent) {
        this(makeComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()), parent);
    }
    
    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(ComponentAdapterFactory caf) {
        this(makeComponentAdapterFactory(caf), null);
    }


    /**
     * Creates a new container with no parent container.
     */
    public CachingPicoContainer() {
        this((PicoContainer) null);
    }


    public MutablePicoContainer makeChildContainer() {
        CachingPicoContainer pc = new CachingPicoContainer(caf, this);
        getDelegate().addChildContainer(pc);
        return pc;
    }

}
