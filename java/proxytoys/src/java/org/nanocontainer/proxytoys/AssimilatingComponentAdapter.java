/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.nanocontainer.proxytoys;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.delegate.Delegating;

import org.picocontainer.defaults.InstanceComponentAdapter;


/**
 * ComponentAdapter, that assimilates a component for a specific type.
 * <p>
 * Allows an instance, or more specifically the <code>componentInstance</code>, to be
 * registered against a <code>componentKey</code> interface that the
 * <code>componentInstance</code> is not assignable from. In other words the componentInstance
 * does NOT implement the componentKey interface.
 * </p>
 * <p>
 * For Example:
 * </p>
 * <code><pre>
 * public interface Foo {
 *     int size();
 * }
 * 
 * public class Bar {
 *     public int size() {
 *         return 1;
 *     }
 * }
 * </pre></code>
 * <p>
 * Notice how Bar does not implement the interface Foo. But Bar does have an identical
 * <code>size()</code> method.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @author Michael Ward
 * @since 1.0
 */
public class AssimilatingComponentAdapter extends InstanceComponentAdapter {

    /**
     * Construct an AssimilatingComponentAdapter. The <code>componentInstance</code> may not
     * implement the class type used as key. If the component instant <b>does</b> implement the
     * interface, no proxy is used though.
     * 
     * @param componentKey The class type used as key.
     * @param componentInstance The component.
     * @param proxyFactory The {@link ProxyFactory}to use.
     */
    public AssimilatingComponentAdapter(final Class componentKey, final Object componentInstance, final ProxyFactory proxyFactory) {
        super(componentKey, componentKey.isAssignableFrom(componentInstance.getClass()) ? componentInstance : Delegating.object(
                componentKey, componentInstance, proxyFactory));
    }

    /**
     * Construct an AssimilatingComponentAdapter. The <code>componentInstance</code> may not
     * implement the class type used as key. The implementation will use JDK
     * {@link java.lang.reflect.Proxy}instances. If the component instant <b>does</b> implement the
     * interface, no proxy is used anyway.
     * 
     * @param componentKey The class type used as key.
     * @param componentInstance The component.
     */
    public AssimilatingComponentAdapter(final Class componentKey, final Object componentInstance) {
        this(componentKey, componentInstance, new StandardProxyFactory());
    }

}
