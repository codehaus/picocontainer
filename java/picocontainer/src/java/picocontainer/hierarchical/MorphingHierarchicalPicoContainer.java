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

import picocontainer.ComponentFactory;
import picocontainer.PicoContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class MorphingHierarchicalPicoContainer extends HierarchicalPicoContainer {

    public static final int REVERSE_INSTANTIATION_ORDER = 11;
    public static final int INSTANTIATION_ORDER = 22;

    public MorphingHierarchicalPicoContainer(PicoContainer parentContainer, ComponentFactory componentFactory) {
        super(parentContainer, componentFactory);
    }

    /**
     * Returns a proxy that impements the specified interface.
     * Calling a method on the returned Object will call the
     * method on all components in the container that implement
     * that interface.
     *
     * This version will call the method on all comonents in this container
     * (ie those returned by {@see getComponents()})
     */
    public Object as(Class theInterface) {
        return as(new Class[]{theInterface});
    }

    /**
     * Returns a proxy that impements the specified set of interfaces.
     * @see #as(Class)
     */
    public Object as(Class[] interfaces) {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                interfaces,
                new AsInvocationHandler(this));
    }

    /**
     * Generates a proxy that can be used to call the interfaces methods
     * on all components managed by this container, ie. those components
     * that were instantiated by this container. It is assumed that
     * compoenents passed in already instantiated are managed externally.
     *
     * Lifecycle methods can be called in wither {@link INSTANTIATION_ORDER},
     * that is the order in which the container created them, or
     * {@link REVERSE_INSTANTIATION_ORDER}.
     */
    public Object asLifecycle(Class theInterface, int direction) {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {theInterface},
                new AsLifecycleInvocationHandler(direction));
    }

    class AsLifecycleInvocationHandler implements InvocationHandler {
        private int direction;

        AsLifecycleInvocationHandler(int direction) {
            if (direction != MorphingHierarchicalPicoContainer.INSTANTIATION_ORDER && direction != MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER) {
                throw new IllegalArgumentException("Illegal argument - direction whould be one of REVERSE_INSTANTIATION_ORDER or INSTANTIATION_ORDER");
            }
            this.direction = direction;

        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            switch (direction) {
                case MorphingHierarchicalPicoContainer.INSTANTIATION_ORDER:
                    for (int i = 0; i < orderedComponents.size(); i++) {
                        invokeOnComponent(orderedComponents.get(i), method, args);
                    }
                    break;
                case MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER:
                    for (int i = orderedComponents.size() - 1; i >= 0; i--) {
                        invokeOnComponent(orderedComponents.get(i), method, args);
                    }
                    break;
            }

            return null;
        }

        private void invokeOnComponent(Object component, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            Class declaringInterface = method.getDeclaringClass();
            if ( component instanceof MorphingHierarchicalPicoContainer) {
                MorphingHierarchicalPicoContainer childContainer = (MorphingHierarchicalPicoContainer)component;
                Object childContainerAs = childContainer.asLifecycle(declaringInterface, direction);
                method.invoke(childContainerAs, args);
            }
            else {
                if (declaringInterface.isAssignableFrom(component.getClass())) {
                    method.invoke(component, args);
                }
            }
        }

   }

    static class AsInvocationHandler implements InvocationHandler {
        private PicoContainer container;

        AsInvocationHandler(PicoContainer container) {
            this.container = container;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            invokeOnComponents(container.getComponents(), method, args);
            return null;
        }

        private void invokeOnComponents(Object[] components, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            for (int i = 0; i < components.length; i++) {
                invokeOnComponent(components[i], method, args);
            }
        }

        private void invokeOnComponent(Object component, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            Class declaringInterface = method.getDeclaringClass();
            if (declaringInterface.isAssignableFrom(component.getClass())) {
                method.invoke(component, args);
            }
            else if (component instanceof PicoContainer) {
                invokeOnComponents( ((PicoContainer)component).getComponents(), method, args );
            }
        }

    }

}