/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import java.io.Serializable;

import org.nanocontainer.NanoPicoContainer;
import org.nanocontainer.AbstractNanoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.componentadapters.CachingAndConstructorComponentAdapterFactory;

/**
 * This is a MutablePicoContainer that supports soft composition and hides implementations where it can.
 * <p/>
 * In terms of implementation it adopts the behaviour of ImplementationHidingPicoContainer
 * and DefaulNanoContainer
 *
 * @author Paul Hammant
 * @author Michael Rimov
 * @version $Revision$
 */
public class ImplementationHidingNanoContainer extends AbstractNanoContainer implements NanoPicoContainer, Serializable {

    public ImplementationHidingNanoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        super(new ImplementationHidingPicoContainer(caf, parent), classLoader);
    }


    public ImplementationHidingNanoContainer(ClassLoader classLoader, PicoContainer parent) {
        super(new ImplementationHidingPicoContainer(new CachingAndConstructorComponentAdapterFactory(), parent), classLoader);
    }

    public ImplementationHidingNanoContainer(PicoContainer pc) {
        this(ImplementationHidingNanoContainer.class.getClassLoader(), pc);
    }

    public ImplementationHidingNanoContainer(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ImplementationHidingNanoContainer() {
        this(ImplementationHidingNanoContainer.class.getClassLoader(), null);
    }

    /**
     * Copy Constructor.  Makes a new ImplementationHidingNanoContainer with the same
     * attributes - ClassLoader, child PicoContainer type, ComponentAdapterFactory -
     * as the parent.
     * <p><tt>Note:</tt> This constructor is protected because are existing scripts
     * that call <tt>new ImplementationHidingNanoContainer(PicoContainer)</tt>, and they get this
     * constructor instead (which has different behavior).</p>
     * @param parent ImplementationHidingNanoContainer
     */
    protected ImplementationHidingNanoContainer(final ImplementationHidingNanoContainer parent) {
        super(parent.getDelegate().makeChildContainer(), parent.getComponentClassLoader());
    }


    protected AbstractNanoContainer createChildContainer() {
        return new ImplementationHidingNanoContainer(this);
    }
}
