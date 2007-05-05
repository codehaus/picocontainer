/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ObjectReference;
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.proxy.toys.delegate.Delegating;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.DecoratingComponentAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * This component adapter makes it possible to hide the implementation of a real subject (behind a proxy). If the key of the
 * component is of type {@link Class} and that class represents an interface, the proxy will only implement the interface
 * represented by that Class. Otherwise (if the key is something else), the proxy will implement all the interfaces of the
 * underlying subject. In any case, the proxy will also implement {@link com.thoughtworks.proxy.toys.hotswap.Swappable}, making
 * it possible to swap out the underlying subject at runtime. <p/> <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link org.picocontainer.adapters.CachingComponentAdapter} around this one.
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
        private Object componentInstance;
        private final PicoContainer container;

        public ImplementationHidingReference(ComponentAdapter delegate, PicoContainer container) {
            this.delegate = delegate;
            this.container = container;
        }

        public Object get() {
            if (componentInstance == null) {
                componentInstance = delegate.getComponentInstance(container);
            }
            return componentInstance;
        }

        public void set(Object item) {
            componentInstance = item;
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
        final Class[] proxyTypes;
        if (getComponentKey() instanceof Class && proxyFactory.canProxy((Class)getComponentKey())) {
            proxyTypes = new Class[]{(Class)getComponentKey()};
        } else {
            Set types = new HashSet(Arrays.asList(getComponentImplementation().getInterfaces()));
            ReflectionUtils.addIfClassProxyingSupportedAndNotObject(getComponentImplementation(), types, proxyFactory);
            proxyTypes = (Class[])types.toArray(new Class[types.size()]);
        }
        ObjectReference reference = new ImplementationHidingReference(getDelegate(), container);
        return HotSwapping.object(proxyTypes, proxyFactory, reference, Delegating.MODE_DIRECT);
    }
}
