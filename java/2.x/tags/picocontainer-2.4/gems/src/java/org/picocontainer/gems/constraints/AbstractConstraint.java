/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.parameters.CollectionComponentParameter;

import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Base class for parameter constraints.
 *
 * @author Nick Sieger
 */
public abstract class AbstractConstraint extends CollectionComponentParameter implements Constraint {

    /** Construct an AbstractContraint. */
    protected AbstractConstraint() {
        super(false);
    }

    @Override
	public Object resolveInstance(final PicoContainer container,
                                  final ComponentAdapter adapter,
                                  final Class expectedType,
                                  final NameBinding expectedNameBinding, final boolean useNames, final Annotation binding) throws PicoCompositionException
    {
        final Object[] array =
            (Object[])super.resolveInstance(container, adapter, getArrayType(expectedType), expectedNameBinding,
                                            useNames, null);
        if (array.length == 1) {
            return array[0];
        }
        return null;
    }

    @Override
	public boolean isResolvable(final PicoContainer container,
                                final ComponentAdapter adapter,
                                final Class expectedType,
                                final NameBinding expectedNameBinding, final boolean useNames, final Annotation binding) throws PicoCompositionException
    {
        return super.isResolvable(container, adapter, getArrayType(expectedType), expectedNameBinding, useNames,
                                  binding);
    }

    @Override
	public void verify(final PicoContainer container,
                       final ComponentAdapter adapter,
                       final Class expectedType,
                       final NameBinding expectedNameBinding, final boolean useNames, final Annotation binding) throws PicoCompositionException
    {
        super.verify(container, adapter, getArrayType(expectedType), expectedNameBinding, useNames, binding);
    }

    @Override
	public abstract boolean evaluate(ComponentAdapter adapter);

    @Override
	protected Map<Object, ComponentAdapter<?>> getMatchingComponentAdapters(final PicoContainer container,
                                                                            final ComponentAdapter adapter,
                                                                            final Class keyType,
                                                                            final Class valueType)
    {
        final Map<Object, ComponentAdapter<?>> map =
            super.getMatchingComponentAdapters(container, adapter, keyType, valueType);
        if (map.size() > 1) {
            throw new AbstractInjector.AmbiguousComponentResolutionException(valueType, map.keySet().toArray(new Object[map.size()]));
        }
        return map;
    }

    private Class getArrayType(final Class expectedType) {
        return Array.newInstance(expectedType, 0).getClass();
    }
}
