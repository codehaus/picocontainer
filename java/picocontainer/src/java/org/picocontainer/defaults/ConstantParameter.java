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
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

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
    
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        return value;
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.Parameter#verify(org.picocontainer.PicoContainer, org.picocontainer.ComponentAdapter, java.lang.Class)
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoVerificationException {
        try {
            if (!(expectedType.isPrimitive() && checkPrimitive(expectedType)) && !expectedType.isInstance(value)) {
                throw new ClassCastException(value.toString()
                        + " does not match the type "
                        + expectedType.getClass().getName());
            }
        } catch(PicoVerificationException e) {
            throw e;
        } catch(Exception e) {
            final List list = new LinkedList();
            list.add(e);
            throw new PicoVerificationException(list);
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(PicoVisitor visitor) {
        visitor.visitParameter(this);
    }
    
    private boolean checkPrimitive(Class expectedType) throws NoSuchFieldException, IllegalAccessException {
        final Field field = value.getClass().getField("TYPE");
        final Class type = (Class)field.get(value);
        return expectedType.isAssignableFrom(type);
    }


}
