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

import java.lang.reflect.Field;
import java.io.Serializable;

/**
 * A ConstantParameter should be used to pass in "constant" arguments
 * to constructors. This includes {@link String}s, {@link Integer}s or
 * any other object that is not registered in the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ConstantParameter implements Parameter, Serializable {
    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter result = null;
        if (expectedType.isAssignableFrom(value.getClass())) {
            result = new InstanceComponentAdapter(uniqueishKey(), value);
        } else if (expectedType.isPrimitive()) {
            try {
                Field field = value.getClass().getField("TYPE");
                Class type = (Class) field.get(value);
                if (expectedType.isAssignableFrom(type)) {
                    result = new InstanceComponentAdapter(uniqueishKey(), value);
                } else {
                    result = null;
                }
            } catch (NoSuchFieldException e) {
                result = null;
            } catch (IllegalArgumentException e) {
                result = null;
            } catch (IllegalAccessException e) {
                result = null;
            } catch (ClassCastException e) {
                result = null;
            }
        }
        return result;
    }

    private Integer uniqueishKey() {
        return new Integer(System.identityHashCode(value));
    }

}
