/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy & Joerg Schaible                                       *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.pool.Pool;
import com.thoughtworks.proxy.toys.pool.Resetter;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DecoratingComponentAdapter;


/**
 * @todo Auto-generated JavaDoc
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 * @since 1.2
 */
public class PoolingComponentAdapter extends DecoratingComponentAdapter {

    /**
     * Context of the PoolingComponentAdapter used to initialize it.
     * 
     * @author J&ouml;rg Schaible
     * @since 1.2
     */
    public static interface Context {
        /**
         * Retrieve the maximum size of the pool. An implementation may return the maximum value or
         * {@link PoolingComponentAdapter#UNLIMITED_SIZE} for <em>unlimited</em> growth.
         * 
         * @return the maximum pool size
         * @since 1.2
         */
        int getMaxSize();

        /**
         * Retrieve the maximum number of milliseconds to wait for a returned element. An implementation may return
         * alternatively {@link PoolingComponentAdapter#BLOCK_ON_WAIT} or {@link PoolingComponentAdapter#FAIL_ON_WAIT}.
         * 
         * @return the maximum number of milliseconds to wait
         * @since 1.2
         */
        int getMaxWaitInMilliseconds();

        /**
         * Retrieve the ProxyFactory to use to create the pooling proxies.
         * 
         * @return the {@link ProxyFactory}
         * @since 1.2
         */
        ProxyFactory getProxyFactory();

        /**
         * Retrieve the {@link Resetter} of the objects returning to the pool.
         * 
         * @return the Resetter instance
         * @since 1.2
         */
        Resetter getResetter();
    }

    /**
     * The default context for a PoolingComponentAdapter.
     * 
     * @author J&ouml;rg Schaible
     * @since 1.2
     */
    public static class DefaultContext implements Context {

        /**
         * {@inheritDoc} Returns {@link PoolingComponentAdapter#DEFAULT_MAX_SIZE}.
         */
        public int getMaxSize() {
            return DEFAULT_MAX_SIZE;
        }

        /**
         * {@inheritDoc} Returns {@link PoolingComponentAdapter#FAIL_ON_WAIT}.
         */
        public int getMaxWaitInMilliseconds() {
            return FAIL_ON_WAIT;
        }

        /**
         * {@inheritDoc} Returns a {@link StandardProxyFactory}.
         */
        public ProxyFactory getProxyFactory() {
            return new StandardProxyFactory();
        }

        /**
         * {@inheritDoc} Returns the {@link PoolingComponentAdapter#DEFAULT_RESETTER}.
         */
        public Resetter getResetter() {
            return DEFAULT_RESETTER;
        }

    }

    /**
     * <code>UNLIMITED_SIZE</code> is the value to set the maximum size of the pool to unlimited ({@link Integer#MAX_VALUE}
     * in fact).
     */
    public static final int UNLIMITED_SIZE = Integer.MAX_VALUE;
    /**
     * <code>DEFAULT_MAX_SIZE</code> is the default size of the pool.
     */
    public static final int DEFAULT_MAX_SIZE = 8;
    /**
     * <code>BLOCK_ON_WAIT</code> forces the pool to wait until an object of the pool is returning in case none is
     * immediately available.
     */
    public static final int BLOCK_ON_WAIT = 0;
    /**
     * <code>FAIL_ON_WAIT</code> forces the pool to fail none is immediately available.
     */
    public static final int FAIL_ON_WAIT = -1;
    /**
     * <code>DEFAULT_RESETTER</code> is the Resetter used by default.
     * 
     * @todo Use a NoOperationResetter from ptoys.
     */
    public static final Resetter DEFAULT_RESETTER = new Resetter() {

        public boolean reset(Object object) {
            return true;
        }

    };

    private final int maxPoolSize;
    private final int waitMilliSeconds;
    private final Pool pool;

    /**
     * Construct a PoolingComponentAdapter with default settings.
     * 
     * @param delegate the delegated ComponentAdapter
     * @since 1.2
     */
    public PoolingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new DefaultContext());
    }

    /**
     * Construct a PoolingComponentAdapter.
     * 
     * @param delegate the delegated ComponentAdapter
     * @param context the {@link Context} of the pool
     * @since 1.2
     */
    public PoolingComponentAdapter(ComponentAdapter delegate, Context context) {
        super(delegate);
        this.maxPoolSize = context.getMaxSize();
        this.waitMilliSeconds = context.getMaxWaitInMilliseconds();
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("Invalid maximum pool size");
        }
        Class type = delegate.getComponentKey() instanceof Class ? (Class)delegate.getComponentKey() : delegate
                .getComponentImplementation();
        this.pool = new Pool(type, context.getResetter(), context.getProxyFactory());
    }

    public Object getComponentInstance(PicoContainer container)
            throws PicoInitializationException, PicoIntrospectionException {
        Object componentInstance = null;
        long now = System.currentTimeMillis();
        synchronized (pool) {
            while ((componentInstance = pool.get()) == null) {
                if (maxPoolSize > pool.size()) {
                    pool.add(super.getComponentInstance(container));
                } else {
                    long after = System.currentTimeMillis();
                    if (waitMilliSeconds < 0) {
                        // @todo: Use correct exception
                        throw new RuntimeException("EXHAUSTED!");
                    }
                    if (waitMilliSeconds > 0 && after - now > waitMilliSeconds) {
                        // @todo: Use correct exception
                        throw new RuntimeException("TIMED OUT!");
                    }
                    try {
                        wait(waitMilliSeconds);
                    } catch (InterruptedException e) {
                        // give the client code of the current thread a chance to abort also
                        Thread.currentThread().interrupt();
                        // @todo: Use correct exception
                        throw new RuntimeException("INTERRUPTED!");
                    }
                }
            }
        }
        return componentInstance;
    }
}
