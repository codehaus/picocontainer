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

import org.picocontainer.defaults.InstanceComponentAdapter;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.delegate.Delegating;


/**
 * ComponentAdapter, that assimilates a component for a specific type.
 * <p>
 * Allows an instance, or more specifically the <code>componentInstance</code>, to be registered against a
 * <code>componentKey<code> interface that the <code>componentInstance</code>
 * is not assignable from. In other words the componentInstance does NFOT implement the componentKey
 * interface.<p>
 *
 * <p>For Example:</p>
 *
 *<code><pre>
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
 *
 * <p>Notice how Bar does not implement the interface Foo. But Bar does have an identical size() method.</p>
 * @author J&ouml;rg Schaible
 * @author Michael Ward
 * @since 1.0
 */
public class AssimilatingComponentAdapter extends InstanceComponentAdapter {

    public AssimilatingComponentAdapter(final Class componentKey, final Object componentInstance, final ProxyFactory proxyFactory) {
        super(componentKey, Delegating.object(componentKey, componentInstance, proxyFactory));
    }

    public AssimilatingComponentAdapter(final Class componentKey, final Object componentInstance) {
        this(componentKey, componentInstance, new StandardProxyFactory());
    }

}
