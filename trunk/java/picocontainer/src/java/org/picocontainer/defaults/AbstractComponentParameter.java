/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Abstract base class for Parameter implementations. The class synchonizes the behaviour of the
 * methods {@link #isResolvable(PicoContainer, ComponentAdapter, Class)}and
 * {@link Parameter#verify(PicoContainer, ComponentAdapter, Class)}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public abstract class AbstractComponentParameter
        implements Parameter, Serializable {

    /**
     * Check wether the given Parameter can be statisfied by the container.
     * 
     * @return <code>true</code> if the Parameter can be verified.
     * @see org.picocontainer.Parameter#isResolvable(org.picocontainer.PicoContainer,
     *           org.picocontainer.ComponentAdapter, java.lang.Class)
     */
    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        final ComponentAdapter[] adapters = getResolvingAdapters(container, adapter, expectedType);
        return adapters != null && adapters.length == 1;
    }

    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        final ComponentAdapter[] adapters = getResolvingAdapters(container, adapter, expectedType);
        if (adapters == null) {
            throw new PicoIntrospectionException(expectedType.getName() +  " is not resolvable");
        }
        if (adapters.length > 1) {
            final List keyList = new ArrayList();
            for (int i = 0; i < adapters.length; i++) {
                final Object key = adapters[i].getComponentKey();
                keyList.add(key);
            }
            throw new AmbiguousComponentResolutionException(expectedType, keyList.toArray());
        }
        adapters[0].verify(container);
    }

    /**
     * Visit the current {@link Parameter}.
     * 
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    /**
     * Retrieve an array of ComponentAdapters, that can satisfy the expected type using the
     * container.
     * 
     * @param container the container from which dependencies are resolved.
     * @param adapter the container that should be searched
     * @param expectedType the required type
     * @return the array with the satisfying CompoenntAdapters.
     */
    protected abstract ComponentAdapter[] getResolvingAdapters(PicoContainer container, ComponentAdapter adapter, Class expectedType);

}
