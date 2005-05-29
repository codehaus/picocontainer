/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.delegate.ObjectReference;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DecoratingComponentAdapter;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy).
 * If the key of the component is of type {@link Class} and that class represents an interface, the proxy
 * will only implement the interface represented by that Class. Otherwise (if the key is
 * something else), the proxy will implement all the interfaces of the underlying subject.
 * In any case, the proxy will also implement
 * {@link com.thoughtworks.proxy.toys.hotswap.Swappable}, making it possible to swap out the underlying
 * subject at runtime.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link org.picocontainer.defaults.CachingComponentAdapter} around this one.
 * </em>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class HotSwappingComponentAdapter extends DecoratingComponentAdapter {
    private final ProxyFactory proxyFactory;

    private static class ImplementationHidingReference implements ObjectReference {
        private final ComponentAdapter delegate;
        private Object value;
        private final PicoContainer container;

        public ImplementationHidingReference(ComponentAdapter delegate, PicoContainer container) {
            this.delegate = delegate;
            this.container = container;
        }

        public Object get() {
            if (value == null) {
                value = delegate.getComponentInstance(container);
            }
            return value;
        }

        public void set(Object item) {
            value = item;
        }
    }

    public HotSwappingComponentAdapter(final ComponentAdapter delegate, ProxyFactory proxyFactory) {
        super(delegate);
        this.proxyFactory = proxyFactory;
    }

    public HotSwappingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new StandardProxyFactory());
    }

    public Object getComponentInstance(final PicoContainer container) {
        Class[] proxyTypes;
        if (getComponentKey() instanceof Class && proxyFactory.canProxy((Class) getComponentKey())) {
            proxyTypes = new Class[]{(Class) getComponentKey()};
        } else {
            proxyTypes = ClassHierarchyIntrospector.addIfClassProxyingSupportedAndNotObject(getComponentImplementation(), getComponentImplementation().getInterfaces(), proxyFactory);
        }
        ObjectReference reference = new ImplementationHidingReference(getDelegate(), container);
        return HotSwapping.object(proxyTypes, proxyFactory, reference, true);
    }
}
