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
import org.picocontainer.PicoVerificationException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * A ComponentParameter should be used to pass in a particular component
 * as argument to a different component's constructor. This is particularly
 * useful in cases where several components of the same type have been registered,
 * but with a different key. Passing a ComponentParameter as a parameter
 * when registering a component will give PicoContainer a hint about what
 * other component to use in the constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @version $Revision$
 */
public class ComponentParameter implements Parameter, Serializable {

    private Object componentKey;

    /**
     * Expect a parameter matching a component of a specific key.
     *
     * @param componentKey the key of the desired component
     */
    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /**
     * Expect any paramter of the appropriate type.
     */
    public ComponentParameter() {
    }

    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        // type check is done in isResolvable
        Object result = null;
        if (componentKey != null) {
            result =  container.getComponentInstance(componentKey);
        } else {
            result = container.getComponentInstanceOfType(expectedType);
            // TODO expectedType can be Map or Collection with generics
            if (result == null) {
                ComponentAdapter genericCA = getGenericCollectionComponentAdapter(container, expectedType);
                if (genericCA != null) {
                    result = genericCA.getComponentInstance(container);
                }
            }
        }
        return result;
    }

    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        return resolveAdapter(container, adapter, expectedType) != null;
    }
    
    private ComponentAdapter resolveAdapter(PicoContainer container, ComponentAdapter adapter, Class expectedType) {

        final ComponentAdapter result = getTargetAdapter(container,  expectedType);
        if (result == null) {
            return null;
        }

        // can't depend on ourselves
        if (adapter.getComponentKey().equals(result.getComponentKey())) {
            return  null;
        }

        if (!expectedType.isAssignableFrom(result.getComponentImplementation())) {
            // check for primitive value
            if (expectedType.isPrimitive()) {
                try {
                    final Field field = result.getComponentImplementation().getField("TYPE");
                    final Class type = (Class)field.get(result.getComponentInstance(null));
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

    /**
     * {@inheritDoc}
     * @see org.picocontainer.Parameter#verify(org.picocontainer.PicoContainer, org.picocontainer.ComponentAdapter, java.lang.Class)
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        final ComponentAdapter result = resolveAdapter(container, adapter, expectedType);
        if (result == null) {
            final List list = new LinkedList();
            list.add(new PicoIntrospectionException(expectedType.getClass().getName() + "not resolvable"));
            throw new PicoVerificationException(list);
        }
        result.verify(container);
    }
    
    private ComponentAdapter getTargetAdapter(PicoContainer container, Class expectedType) {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return container.getComponentAdapter(componentKey);
        } else {
            ComponentAdapter result =  container.getComponentAdapterOfType(expectedType);
            if (result == null) {
                result = getGenericCollectionComponentAdapter(container, expectedType);
            }
            return result;
        }
    }
    
    private ComponentAdapter getGenericCollectionComponentAdapter(PicoContainer container, Class expectedType) {
        Class componentType = expectedType.getComponentType();
        if (container.getComponentAdaptersOfType(componentType).size() == 0) {
            return null;
        } else {
            Object componentKey = new Object[]{this, componentType};
            return new GenericCollectionComponentAdapter(componentKey, null, componentType, expectedType);
        }
    }

//    private ComponentAdapter getGenericCollectionComponentAdapter(Class parameterType, Type genericType) {
//        ComponentAdapter result = null;
//
//        boolean isMap = Map.class.isAssignableFrom(parameterType);
//        boolean isCollection = Collection.class.isAssignableFrom(parameterType);
//        if((isMap || isCollection) && genericType instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) genericType;
//            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//            Class keyType = null;
//            Class valueType = null;
//            if(isMap) {
//                keyType = (Class) actualTypeArguments[0];
//                valueType = (Class) actualTypeArguments[1];
//            } else {
//                valueType = (Class) actualTypeArguments[0];
//            }
//            Object componentKey = new Object[]{this, genericType};
//            result = new GenericCollectionComponentAdapter(componentKey, keyType, valueType, parameterType);
//        }
//        return result;
//    }

}
