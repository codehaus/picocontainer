/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import picocontainer.PicoContainer;
import picocontainer.ComponentFactory;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class MorphingHierarchicalPicoContainer extends HierarchicalPicoContainer {

    public MorphingHierarchicalPicoContainer(PicoContainer parentContainer, ComponentFactory componentFactory) {
        super(parentContainer, componentFactory);
    }

    /**
     * Shorthand for {@link #getMultipleInheritanceProxy(boolean, boolean)}(true, true).
     * @return a proxy.
     */
    public Object getMultipleInheritanceProxy() {
        return getMultipleInheritanceProxy( true, true);
    }

    /**
     * Returns a proxy that implements the union of all the components'
     * interfaces.
     * Calling a method on the returned Object will call the
     * method on all components in the container that implement
     * that interface.
     *
     * @param callInInstantiationOrder whether to call the methods in the order of instantiation
     * @param callInInstantiationOrder whether to exclude components registered with {@link #registerComponent(Class, Object)}
     * or {@link #registerComponent(Object)}
     */
    public Object getMultipleInheritanceProxy(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                getComponentInterfaces(),
                new ComponentsInvocationHandler(callInInstantiationOrder, callUnmanagedComponents));
    }

    private class ComponentsInvocationHandler implements InvocationHandler {
        private boolean callInInstantiationOrder;
        private boolean callUnmanagedComponents;

        public ComponentsInvocationHandler(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
            this.callInInstantiationOrder = callInInstantiationOrder;
            this.callUnmanagedComponents = callUnmanagedComponents;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            List orderedComponentsCopy = new ArrayList(orderedComponents);

            if( !callInInstantiationOrder ) {
                // reverse the list
                Collections.reverse(orderedComponentsCopy);
            }
            Object[] components = orderedComponentsCopy.toArray();
            return invokeOnComponents(components, method, args);
        }

        private Object invokeOnComponents(Object[] components, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            Object result = null;
            int invokeCount = 0;
            for (int i = 0; i < components.length; i++) {
                Class declarer = method.getDeclaringClass();
                boolean isValidType = declarer.isAssignableFrom(components[i].getClass());
                boolean isUnmanaged = unmanagedComponents.contains(components[i]);
                boolean exclude = !callUnmanagedComponents && isUnmanaged;
                if (isValidType && !exclude) {
                    // It's ok to call the method on this one
                    Object resultCandidate = method.invoke(components[i], args);
                    invokeCount++;
                    if( invokeCount == 1 ) {
                        result = resultCandidate;
                    } else {
                        result = null;
                    }
                }
            }
            return result;
        }
    }

    /**
     * Get all the interfaces implemented by the registered component instances.
     * @return an array of interfaces implemented by the concrete component instances.
     */
    private final Class[] getComponentInterfaces() {
        Set interfaces = new HashSet();
        Object[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Class componentClass = components[i].getClass();
            // Strangely enough Class.getInterfaces() does not include the interfaces
            // implemented by superclasses. So we must loop up the hierarchy.
            while( componentClass != null ) {
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
                componentClass = componentClass.getSuperclass();
            }
        }

        Class[] result = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return result;
    }
}