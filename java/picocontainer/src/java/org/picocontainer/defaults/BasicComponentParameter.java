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
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;


/**
 * A BasicComponentParameter should be used to pass in a particular component as argument to a
 * different component's constructor. This is particularly useful in cases where several
 * components of the same type have been registered, but with a different key. Passing a
 * ComponentParameter as a parameter when registering a component will give PicoContainer a hint
 * about what other component to use in the constructor. This Parameter will never resolve
 * against a collecting type, that is not directly registered in the PicoContainer itself.
 * 
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @version $Revision$
 */
public class BasicComponentParameter
        implements Parameter, Serializable {

    private Object componentKey;

    /**
     * Expect a parameter matching a component of a specific key.
     * 
     * @param componentKey the key of the desired component
     */
    public BasicComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /**
     * Expect any paramter of the appropriate type.
     */
    public BasicComponentParameter() {
    }

    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType)
            throws PicoInstantiationException {
        // type check is done in isResolvable
        Object result = null;
        if (componentKey != null) {
            result = container.getComponentInstance(componentKey);
        } else {
            result = container.getComponentInstanceOfType(expectedType);
        }
        return result;
    }

    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        return resolveAdapter(container, adapter, expectedType) != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.Parameter#verify(org.picocontainer.PicoContainer,
     *           org.picocontainer.ComponentAdapter, java.lang.Class)
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        final ComponentAdapter result = resolveAdapter(container, adapter, expectedType);
        if (result == null) {
            final List list = new LinkedList();
            list.add(new PicoIntrospectionException(expectedType.getName() + " not resolvable"));
            throw new PicoVerificationException(list);
        }
        result.verify(container);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private ComponentAdapter resolveAdapter(PicoContainer container, ComponentAdapter adapter, Class expectedType) {

        final ComponentAdapter result = getTargetAdapter(container, expectedType);
        if (result == null) {
            return null;
        }

        // can't depend on ourselves
        if (adapter.getComponentKey().equals(result.getComponentKey())) {
            return null;
        }

        if (!expectedType.isAssignableFrom(result.getComponentImplementation())) {
            // check for primitive value
            if (expectedType.isPrimitive()) {
                try {
                    final Field field = result.getComponentImplementation().getField("TYPE");
                    final Class type = (Class) field.get(result.getComponentInstance(null));
                    if (expectedType.isAssignableFrom(type)) {
                        return result;
                    }
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                } catch (ClassCastException e) {
                }
            }
            return null;
        }
        return result;
    }

    private ComponentAdapter getTargetAdapter(PicoContainer container, Class expectedType) {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return container.getComponentAdapter(componentKey);
        } else {
            return container.getComponentAdapterOfType(expectedType);
        }
    }
}
