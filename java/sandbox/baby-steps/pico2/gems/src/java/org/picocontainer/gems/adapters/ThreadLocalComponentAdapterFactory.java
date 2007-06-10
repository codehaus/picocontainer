/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.CachingBehaviorAdapter;
import org.picocontainer.adapters.AbstractBehaviorFactory;
import org.picocontainer.ComponentFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoRegistrationException;


/**
 * A {@link ComponentFactory} for components kept in {@link ThreadLocal} instances.
 * <p>
 * This factory has two operating modes. By default it ensures, that every thread uses its own addComponent at any time.
 * This mode ({@link #ENSURE_THREAD_LOCALITY}) makes internal usage of a {@link ThreadLocalComponentAdapter}. If the
 * application architecture ensures, that the thread that creates the addComponent is always also the thread that is th
 * only user, you can set the mode {@link #THREAD_ENSURES_LOCALITY}. In this mode the factory uses a simple
 * {@link org.picocontainer.adapters.CachingBehaviorAdapter} that uses a {@link ThreadLocalReference} to cache the addComponent.
 * </p>
 * <p>
 * See the use cases for the subtile difference:
 * </p>
 * <p>
 * <code>THREAD_ENSURES_LOCALITY</code> is applicable, if the pico container is requested for a thread local addComponent
 * from the working thread e.g. in a web application for a request. In this environment it is ensured, that the request
 * is processed from the same thread and the thread local addComponent is reused, if a previous request was handled in the
 * same thread. Note that thi scenario fails badly, if the thread local addComponent is created because of another cached
 * addComponent indirectly by a dependecy. In this case the cached addComponent already have an instance of the thread local
 * addComponent, that may have been created in another thread, since only the addComponent addAdapter for the thread local
 * addComponent can ensure a unique addComponent for each thread.
 * </p>
 * <p>
 * <code>ENSURES_THREAD_LOCALITY</code> solves this problem. In this case the returned addComponent is just a proxy for
 * the thread local addComponent and this proxy ensures, that a new addComponent is created for each thread. Even if another
 * cached addComponent has an indirect dependency on the thread local addComponent, the proxy ensures unique instances. This
 * is vital for a multithreaded application that uses EJBs.
 * </p>
 * @author J&ouml;rg Schaible
 */
public final class ThreadLocalComponentAdapterFactory extends AbstractBehaviorFactory {

    /**
     * <code>ENSURE_THREAD_LOCALITY</code> is the constant for created {@link ComponentAdapter} instances, that ensure
     * unique instances of the component by delivering a proxy for the addComponent.
     */
    public static final boolean ENSURE_THREAD_LOCALITY = true;
    /**
     * <code>THREAD_ENSURES_LOCALITY</code> is the constant for created {@link ComponentAdapter} instances, that
     * create for the current thread a new addComponent.
     */
    public static final boolean THREAD_ENSURES_LOCALITY = false;

    private final boolean ensureThreadLocal;
    private final ProxyFactory proxyFactory;

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory, that ensures the usage of the ThreadLocal. The Proxy
     * instances are generated by the JDK.
     */
    public ThreadLocalComponentAdapterFactory() {
        this(new StandardProxyFactory());
    }

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory, that ensures the usage of the ThreadLocal.
     * @param proxyFactory The {@link ProxyFactory} to use.
     */
    public ThreadLocalComponentAdapterFactory(final ProxyFactory proxyFactory) {
        this(ENSURE_THREAD_LOCALITY, proxyFactory);
    }

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory.
     * @param ensure {@link #ENSURE_THREAD_LOCALITY} or {@link #THREAD_ENSURES_LOCALITY}.
     */
    public ThreadLocalComponentAdapterFactory(final boolean ensure) {
        this(ensure, new StandardProxyFactory());
    }

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory.
     * @param ensure {@link #ENSURE_THREAD_LOCALITY} or {@link #THREAD_ENSURES_LOCALITY}.
     * @param factory The {@link ProxyFactory} to use.
     */
    protected ThreadLocalComponentAdapterFactory(
            final boolean ensure, final ProxyFactory factory) {
        ensureThreadLocal = ensure;
        proxyFactory = factory;
    }

    public ComponentAdapter createComponentAdapter(
            ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, PicoRegistrationException {
        final ComponentAdapter componentAdapter;
        if (ensureThreadLocal) {
            componentAdapter = new ThreadLocalComponentAdapter(super.createComponentAdapter(
                    componentMonitor, lifecycleStrategy, null, componentKey, componentImplementation, parameters), proxyFactory);
        } else {
            componentAdapter = new CachingBehaviorAdapter(super.createComponentAdapter(
                    componentMonitor, lifecycleStrategy, null, componentKey, componentImplementation, parameters), new ThreadLocalReference());
        }
        return componentAdapter;
    }

}
