/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImmutableComponentAdapter implements ComponentAdapter, Serializable {

    private ComponentAdapter delegate;
    private PicoContainer container;

    public ImmutableComponentAdapter(ComponentAdapter delegate) {
        this.delegate = delegate;
    }

    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return delegate.getComponentInstance();
    }

    public synchronized PicoContainer getContainer() {
        if (container == null) {
            container = new ImmutablePicoContainer(delegate.getContainer());
        }
        return container;
    }

    public void setContainer(PicoContainer picoContainer) {
        throw new UnsupportedOperationException("setContainer() not supported for ImmutableComponentAdapter");
    }

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }
}
