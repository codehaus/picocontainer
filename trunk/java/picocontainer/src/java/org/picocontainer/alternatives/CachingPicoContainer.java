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
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

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

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(CachingComponentAdapterFactory caf, PicoContainer parent) {
        super(new DefaultPicoContainer(caf, parent));
    }

    public CachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this(new CachingComponentAdapterFactory(caf), parent);
    }

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }

    /**
     * Creates a new container with a parent container.
     */
    public CachingPicoContainer(ComponentAdapterFactory caf) {
        this(caf, null);
    }


    /**
     * Creates a new container with no parent container.
     */
    public CachingPicoContainer() {
        this((PicoContainer) null);
    }


    public MutablePicoContainer makeChildContainer() {
        ImplementationHidingCachingPicoContainer pc = new ImplementationHidingCachingPicoContainer(this);
        getDelegate().addChildContainer(pc);
        return pc;
    }

}
