/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.*;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class DecoratingComponentAdapter implements ComponentAdapter {

    private final ComponentAdapter delegate;

    public DecoratingComponentAdapter(ComponentAdapter decoratedComponentAdapter) {
        this.delegate = decoratedComponentAdapter;
    }

    public Parameter createDefaultParameter() {
        return delegate.createDefaultParameter();
    }

    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object instantiateComponent(ComponentRegistry componentRegistry)
            throws PicoInitializationException {
        return delegate.instantiateComponent(componentRegistry);
    }

    public void addConstantParameterBasedOnType(Class parameter, Object arg) throws PicoIntrospectionException {
        delegate.addConstantParameterBasedOnType(parameter, arg);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecoratingComponentAdapter)) return false;

        final DecoratingComponentAdapter decoratingComponentAdapter = (DecoratingComponentAdapter) o;

        if (!delegate.equals(decoratingComponentAdapter.delegate)) return false;

        return true;
    }

    public int hashCode() {
        return delegate.hashCode();
    }
}
