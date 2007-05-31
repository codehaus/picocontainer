/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ParameterName;
import org.picocontainer.parameters.CollectionComponentParameter;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Base class for parameter constraints.
 *
 * @author Nick Sieger
 * @version 1.1
 */
public abstract class AbstractConstraint extends CollectionComponentParameter implements Constraint {

    /**
     * Construct an AbstractContraint.
     */
    protected AbstractConstraint() {
        super(false);
    }
    
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) throws PicoIntrospectionException {
        final Object[] array = (Object[]) super.resolveInstance(container, adapter, getArrayType(expectedType), expectedParameterName);
        if (array.length == 1) {
            return array[0];
        }
        return null;
    }

    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) throws PicoIntrospectionException {
        return super.isResolvable(container, adapter, getArrayType(expectedType), expectedParameterName);
    }

    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) throws PicoIntrospectionException {
        super.verify(container, adapter, getArrayType(expectedType), expectedParameterName);
    }
    
    public abstract boolean evaluate(ComponentAdapter adapter);

    protected Map getMatchingComponentAdapters(PicoContainer container, ComponentAdapter adapter, Class keyType, Class valueType) {
        final Map map = super.getMatchingComponentAdapters(container, adapter, keyType, valueType);
        if (map.size() > 1) {
            throw new AmbiguousComponentResolutionException(valueType, map.keySet().toArray(new Object[map.size()]));
        }
        return map;
    }
    
    private Class getArrayType(Class expectedType) {
        return Array.newInstance(expectedType, 0).getClass();
    }
}
