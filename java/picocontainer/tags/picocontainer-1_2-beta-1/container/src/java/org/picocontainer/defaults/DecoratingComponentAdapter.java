/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class DecoratingComponentAdapter implements ComponentAdapter, Serializable {

    private ComponentAdapter delegate;

    public DecoratingComponentAdapter(ComponentAdapter delegate) {
        this.delegate = delegate;
    }

    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
        return delegate.getComponentInstance(container);
    }

    public void verify(PicoContainer container) throws PicoIntrospectionException {
        delegate.verify(container);
    }

    public ComponentAdapter getDelegate() {
        return delegate;
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
        delegate.accept(visitor);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        buffer.append("[");
        buffer.append(delegate.toString());
        buffer.append("]");
        return buffer.toString();
    }
}
