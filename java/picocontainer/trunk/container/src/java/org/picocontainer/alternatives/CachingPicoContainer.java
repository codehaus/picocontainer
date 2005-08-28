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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.*;

import java.io.Serializable;

/**
 * <p/>
 * The Cacing version of  {@link org.picocontainer.defaults.DefaultPicoContainer}
 * </p>
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class CachingPicoContainer extends AbstractDelegatingMutablePicoContainer implements Serializable {
    private final ComponentAdapterFactory caf;
    private LifecycleManager lifecycleManager;

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent, LifecycleManager lifecycleManager) {
        super(new DefaultPicoContainer(makeComponentAdapterFactory(caf), parent, lifecycleManager));
        this.caf = caf;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * Creates a new container with a parent container.
     */
    private CachingPicoContainer(CachingComponentAdapterFactory caf, PicoContainer parent) {
        this(caf, parent, new DefaultLifecycleManager());
    }

    private static CachingComponentAdapterFactory makeComponentAdapterFactory(ComponentAdapterFactory caf) {
        if (caf instanceof CachingComponentAdapterFactory) {
            return (CachingComponentAdapterFactory) caf;
        }
        return new CachingComponentAdapterFactory(caf);
    }

    public CachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this(makeComponentAdapterFactory(caf), parent);
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
    public CachingPicoContainer(PicoContainer parent, LifecycleManager lifecycleManager) {
        this(makeComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()), parent, lifecycleManager);
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
        CachingPicoContainer pc = new CachingPicoContainer(caf, this, lifecycleManager);
        getDelegate().addChildContainer(pc);
        return pc;
    }

}
