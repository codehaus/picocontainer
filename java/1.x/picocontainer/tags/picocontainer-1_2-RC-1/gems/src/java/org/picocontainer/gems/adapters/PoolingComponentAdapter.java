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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.LifecycleStrategy;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.NoOperationResetter;
import com.thoughtworks.proxy.kit.Resetter;
import com.thoughtworks.proxy.toys.nullobject.Null;
import com.thoughtworks.proxy.toys.pool.Pool;


/**
 * {@link ComponentAdapter} implementation that pools components.
 * <p>
 * The implementation utilizes a delegated ComponentAdapter to create the instances of the pool. The pool can be
 * configured to grow unlimited or to a maximum size. If a component is requested from this adapter, the implementation
 * returns an availailabe instance from the pool or will create a new one, if the maximum pool size is not reached yet.
 * If none is available, the implementation can wait a defined time for a returned object before it throws a
 * {@link PoolException}.
 * </p>
 * <p>
 * This implementation uses the {@link Pool} toy from the <a href="http://proxytoys.codehaus.org">ProxyToys</a>
 * project. This ensures, that any component, that is out of scope will be automatically returned to the pool by the
 * garbage collector. Additionally will every component instance also implement
 * {@link com.thoughtworks.proxy.toys.pool.Poolable}, that can be used to return the instance manually. After returning
 * an instance it should not be used in client code anymore.
 * </p>
 * <p>
 * Before a returning object is added to the available instances of the pool again, it should be reinitialized to a
 * normalized state. By providing a proper Resetter implementation this can be done automatically. If the object cannot
 * be reused anymore it can also be dropped and the pool may request a new instance.
 * </p>
 * <p>
 * The pool supports components with a lifecylce. If the delegated {@link ComponentAdapter} implements a
 * {@link LifecycleStrategy}, any component retrieved form the pool will be started before and stopped again, when it
 * returns back into the pool. Also if a component cannot be resetted it will automatically be disposed. If the
 * container of the pool is disposed, that any returning object is also disposed and will not return to the pool
 * anymore. Note, that current implementation cannot dispose pooled objects.
 * </p>
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 * @since 1.2
 */
public class PoolingComponentAdapter extends DecoratingComponentAdapter implements LifecycleManager {

    private static final long serialVersionUID = 1L;

    /**
     * Context of the PoolingComponentAdapter used to initialize it.
     * @author J&ouml;rg Schaible
     * @since 1.2
     */
    public static interface Context {
        /**
         * Retrieve the maximum size of the pool. An implementation may return the maximum value or
         * {@link PoolingComponentAdapter#UNLIMITED_SIZE} for <em>unlimited</em> growth.
         * @return the maximum pool size
         * @since 1.2
         */
        int getMaxSize();

        /**
         * Retrieve the maximum number of milliseconds to wait for a returned element. An implementation may return
         * alternatively {@link PoolingComponentAdapter#BLOCK_ON_WAIT} or {@link PoolingComponentAdapter#FAIL_ON_WAIT}.
         * @return the maximum number of milliseconds to wait
         * @since 1.2
         */
        int getMaxWaitInMilliseconds();

        /**
         * Allow the implementation to invoke the garbace collector manually if the pool is exhausted.
         * @return <code>true</code> for an internal call to {@link System#gc()}
         * @since 1.2
         */
        boolean autostartGC();

        /**
         * Retrieve the ProxyFactory to use to create the pooling proxies.
         * @return the {@link ProxyFactory}
         * @since 1.2
         */
        ProxyFactory getProxyFactory();

        /**
         * Retrieve the {@link Resetter} of the objects returning to the pool.
         * @return the Resetter instance
         * @since 1.2
         */
        Resetter getResetter();

        /**
         * Retrieve the serialization mode of the pool. Following values are possible:
         * <ul>
         * <li>{@link Pool#SERIALIZATION_STANDARD}</li>
         * <li>{@link Pool#SERIALIZATION_NONE}</li>
         * <li>{@link Pool#SERIALIZATION_FORCE}</li>
         * </ul>
         * @return the serialization mode
         * @since 1.2
         */
        int getSerializationMode();
    }

    /**
     * The default context for a PoolingComponentAdapter.
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
         * {@inheritDoc} Returns <code>false</code>.
         */
        public boolean autostartGC() {
            return false;
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

        /**
         * {@inheritDoc} Returns {@link Pool#SERIALIZATION_STANDARD}.
         */
        public int getSerializationMode() {
            return Pool.SERIALIZATION_STANDARD;
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
     * <code>DEFAULT_RESETTER</code> is a {@link NoOperationResetter} that is used by default.
     */
    public static final Resetter DEFAULT_RESETTER = new NoOperationResetter();

    private int maxPoolSize;
    private int waitMilliSeconds;
    private Pool pool;
    private int serializationMode;
    private boolean autostartGC;
    private boolean started;
    private boolean disposed;
    private transient List components;

    /**
     * Construct a PoolingComponentAdapter with default settings.
     * @param delegate the delegated ComponentAdapter
     * @since 1.2
     */
    public PoolingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new DefaultContext());
    }

    /**
     * Construct a PoolingComponentAdapter. Remember, that the implementation will request new components from the
     * delegate as long as no component instance is available in the pool and the maximum pool size is not reached.
     * Therefore the delegate may not return the same component instance twice. Ensure, that the used
     * {@link ComponentAdapter} does not cache.
     * @param delegate the delegated ComponentAdapter
     * @param context the {@link Context} of the pool
     * @throws IllegalArgumentException if the maximum pool size or the serialization mode is invalid
     * @since 1.2
     */
    public PoolingComponentAdapter(ComponentAdapter delegate, Context context) {
        super(delegate);
        this.maxPoolSize = context.getMaxSize();
        this.waitMilliSeconds = context.getMaxWaitInMilliseconds();
        this.autostartGC = context.autostartGC();
        this.serializationMode = context.getSerializationMode();
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("Invalid maximum pool size");
        }
        started = false;
        disposed = false;
        components = new ArrayList();
        final Class type = delegate.getComponentKey() instanceof Class ? (Class)delegate.getComponentKey() : delegate
                .getComponentImplementation();
        final Resetter resetter = context.getResetter();
        this.pool = new Pool(
                type, delegateSupportsLifecycle() ? new LifecycleResetter(this, resetter) : resetter, context
                        .getProxyFactory(), serializationMode);
    }

    /**
     * Construct an empty ComponentAdapter, used for serialization with reflection only.
     * @since 1.2
     */
    protected PoolingComponentAdapter() {
        //@todo super class should support standard ctor
        super((ComponentAdapter)Null.object(ComponentAdapter.class));
    }

    /**
     * {@inheritDoc}
     * <p>
     * As long as the maximum size of the pool is not reached and the pool is exhausted, the implementation will request
     * its delegate for a new instance, that will be managed by the pool. Only if the maximum size of the pool is
     * reached, the implementation may wait (depends on the initializing {@link Context}) for a returning object.
     * </p>
     * @throws PoolException if the pool is exhausted or waiting for a returning object timed out or was interrupted
     */
    public Object getComponentInstance(PicoContainer container) {
        final boolean hasLifecycleSupport = delegateSupportsLifecycle();
        if (hasLifecycleSupport) {
            if (disposed) throw new IllegalStateException("Already disposed");
        }
        Object componentInstance = null;
        long now = System.currentTimeMillis();
        boolean gc = autostartGC;
        while (true) {
            synchronized (pool) {
                componentInstance = pool.get();
                if (componentInstance != null) {
                    break;
                }
                if (maxPoolSize > pool.size()) {
                    final Object component = super.getComponentInstance(container);
                    if (hasLifecycleSupport) {
                        components.add(component);
                        if (started) {
                            start(component);
                        }
                    }
                    pool.add(component);
                } else if (!gc) {
                    long after = System.currentTimeMillis();
                    if (waitMilliSeconds < 0) {
                        throw new PoolException("Pool exhausted");
                    }
                    if (waitMilliSeconds > 0 && after - now > waitMilliSeconds) {
                        throw new PoolException("Time out wating for returning object into pool");
                    }
                    try {
                        pool.wait(waitMilliSeconds); // Note, the pool notifies after an object was returned
                    } catch (InterruptedException e) {
                        // give the client code of the current thread a chance to abort also
                        Thread.currentThread().interrupt();
                        throw new PoolException("Interrupted waiting for returning object into the pool", e);
                    }
                } else {
                    System.gc();
                    gc = false;
                }
            }
        }
        return componentInstance;
    }

    /**
     * Retrieve the current size of the pool. The returned value reflects the number of all managed components.
     * @return the number of components.
     * @since 1.2
     */
    public int size() {
        return pool.size();
    }

    private boolean delegateSupportsLifecycle() {
        return getDelegate() instanceof LifecycleStrategy;
    }

    static class LifecycleResetter implements Resetter, Serializable {
        private static final long serialVersionUID = 1L;
        private Resetter delegate;
        private PoolingComponentAdapter adapter;

        LifecycleResetter(final PoolingComponentAdapter adapter, final Resetter delegate) {
            this.adapter = adapter;
            this.delegate = delegate;
        }

        public boolean reset(Object object) {
            final boolean result = delegate.reset(object);
            if (!result || adapter.disposed) {
                if (adapter.started) {
                    adapter.stop(object);
                }
                adapter.components.remove(object);
                if (!adapter.disposed) {
                    adapter.dispose(object);
                }
            }
            return result && !adapter.disposed;
        }

    }

    /**
     * Start of the container ensures that at least one pooled component has been started. Applies only if the delegated
     * {@link ComponentAdapter} supports a lifecylce by implementing {@link LifecycleStrategy}.
     * @throws IllegalStateException if pool was already disposed
     */
    public void start(final PicoContainer container) {
        if (delegateSupportsLifecycle()) {
            if (started) throw new IllegalStateException("Already started");
            if (disposed) throw new IllegalStateException("Already disposed");
            for (final Iterator iter = components.iterator(); iter.hasNext();) {
                start(iter.next());
            }
            started = true;
            if (pool.size() == 0) {
                getComponentInstance(container);
            }
        }
    }

    /**
     * Stop of the container has no effect for the pool. Applies only if the delegated {@link ComponentAdapter} supports
     * a lifecylce by implementing {@link LifecycleStrategy}.
     * @throws IllegalStateException if pool was already disposed
     */
    public void stop(final PicoContainer container) {
        if (delegateSupportsLifecycle()) {
            if (!started) throw new IllegalStateException("Not started yet");
            if (disposed) throw new IllegalStateException("Already disposed");
            started = false;
            for (final Iterator iter = components.iterator(); iter.hasNext();) {
                stop(iter.next());
            }
        }
    }

    /**
     * Dispose of the container will dispose all returning objects. They will not be added to the pool anymore. Applies
     * only if the delegated {@link ComponentAdapter} supports a lifecylce by implementing {@link LifecycleStrategy}.
     * @throws IllegalStateException if pool was already disposed
     */
    public void dispose(final PicoContainer container) {
        if (delegateSupportsLifecycle()) {
            if (started) throw new IllegalStateException("Not stopped yet");
            if (disposed) throw new IllegalStateException("Already disposed");
            disposed = true;
            for (final Iterator iter = components.iterator(); iter.hasNext();) {
                dispose(iter.next());
            }
            // @todo: Release pooled components and clear collection
        }
    }

    private synchronized void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        int mode = serializationMode;
        if (mode == Pool.SERIALIZATION_FORCE && components.size() > 0) {
            try {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final ObjectOutputStream testStream = new ObjectOutputStream(buffer);
                testStream.writeObject(components); // force NotSerializableException
                testStream.close();
            } catch (final NotSerializableException e) {
                mode = Pool.SERIALIZATION_NONE;
            }
        }
        if (mode == Pool.SERIALIZATION_STANDARD) {
            out.writeObject(components);
        } else {
            out.writeObject(new ArrayList());
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        components = (List)in.readObject();
    }
}
