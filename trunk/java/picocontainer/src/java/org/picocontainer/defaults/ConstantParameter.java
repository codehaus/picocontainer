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

/**
 * A ConstantParameter should be used to pass in "constant" arguments
 * to constructors. This includes {@link String}s, {@link Integer}s or
 * any other object that is not registered in the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConstantParameter implements Parameter {
    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType) throws NotConcreteRegistrationException {
        if(!expectedType.isAssignableFrom(value.getClass())) {
            if (expectedType.isPrimitive()) {
                if(!primitiveMatches(expectedType, value.getClass())) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return new InstanceComponentAdapter(value, value);
    }

    private boolean primitiveMatches(Class expectedPrimitiveType, Class constantClass) {
        if(expectedPrimitiveType.equals(Byte.class) || expectedPrimitiveType.equals(Byte.TYPE)) {
            return constantClass.equals(Byte.class) || constantClass.equals(Byte.TYPE);
        }
        if(expectedPrimitiveType.equals(Short.class) || expectedPrimitiveType.equals(Short.TYPE)) {
            return constantClass.equals(Short.class) || constantClass.equals(Short.TYPE);
        }
        if(expectedPrimitiveType.equals(Integer.class) || expectedPrimitiveType.equals(Integer.TYPE)) {
            return constantClass.equals(Integer.class) || constantClass.equals(Integer.TYPE);
        }
        if(expectedPrimitiveType.equals(Long.class) || expectedPrimitiveType.equals(Long.TYPE)) {
            return constantClass.equals(Long.class) || constantClass.equals(Long.TYPE);
        }
        if(expectedPrimitiveType.equals(Float.class) || expectedPrimitiveType.equals(Float.TYPE)) {
            return constantClass.equals(Float.class) || constantClass.equals(Float.TYPE);
        }
        if(expectedPrimitiveType.equals(Double.class) || expectedPrimitiveType.equals(Double.TYPE)) {
            return constantClass.equals(Double.class) || constantClass.equals(Double.TYPE);
        }
        if(expectedPrimitiveType.equals(Boolean.class) || expectedPrimitiveType.equals(Boolean.TYPE)) {
            return constantClass.equals(Boolean.class) || constantClass.equals(Boolean.TYPE);
        }
        if(expectedPrimitiveType.equals(Character.class) || expectedPrimitiveType.equals(Character.TYPE)) {
            return constantClass.equals(Character.class) || constantClass.equals(Character.TYPE);
        }
        return false;
    }
}
