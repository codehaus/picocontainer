/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * A ConstantParameter should be used to pass in "constant" arguments
 * to constructors. This includes {@link String}s, {@link Integer}s or
 * any other object that is not registered in the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @version $Revision$
 */
public class ConstantParameter implements Parameter, Serializable {
    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        if (expectedType.isPrimitive()) {
            try {
                return checkPrimitive(expectedType);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassCastException e) {
            }
            return false;
        }
        return expectedType.isInstance(value);
    }
    
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        return value;
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.Parameter#verify(org.picocontainer.PicoContainer, org.picocontainer.ComponentAdapter, java.lang.Class)
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        try {
            if (!(expectedType.isPrimitive() && checkPrimitive(expectedType)) && !expectedType.isInstance(value)) {
                throw new PicoIntrospectionException(value.toString() + " does not match the type " + expectedType.getClass().getName()) {};
            }
        } catch (NoSuchFieldException e) {
            throw new PicoIntrospectionException(e) {};
        } catch (IllegalAccessException e) {
            throw new PicoIntrospectionException(e) {};
        }
    }
    
    private boolean checkPrimitive(Class expectedType) throws NoSuchFieldException, IllegalAccessException {
        final Field field = value.getClass().getField("TYPE");
        final Class type = (Class)field.get(value);
        return expectedType.isAssignableFrom(type);
    }
}
