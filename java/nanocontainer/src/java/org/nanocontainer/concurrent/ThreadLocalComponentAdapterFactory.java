/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.concurrent;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;


/**
 * A {@link ComponentAdapterFactory}for components kept in {@link ThreadLocal}instances.
 * <p>
 * This factory has two operating modes. By default it ensures, that every thread uses its own component at any time. This mode (
 * {@link #ENSURE_THREAD_LOCALITY}) makes internal usage of a {@link ThreadLocalComponentAdapter}. If the application
 * architecture ensures, that the thread that creates the component is always also the thread that is th only user, you can set
 * the mode {@link #THREAD_ENSURES_LOCALITY}. In this mode the factory uses a simple {@link CachingComponentAdapter}that uses
 * a {@link ThreadLocalReference}to cache the component.
 * </p>
 * <p>
 * See the use cases for the subtile difference:
 * </p>
 * <p>
 * <code>THREAD_ENSURES_LOCALITY</code> is applicable, if the pico container is requested for a thread local component from
 * the working thread e.g. in a web application for a request. In this environment it is ensured, that the request is processed
 * from the same thread and the thread local component is reused, if a previous request was handled in the same thread. Note
 * that thi scenario fails badly, if the thread local component is created because of another cached component indirectly by a
 * dependecy. In this case the cached component already have an instance of the thread local component, that may have been
 * created in another thread, since only the component adapter for the thread local component can ensure a unique component for
 * each thread.
 * </p>
 * <p>
 * <code>ENSURES_THREAD_LOCALITY</code> solves this problem. In this case the returned component is just a proxy for the
 * thread local component and this proxy ensures, that a new component is created for each thread. Even if another cached
 * component has an indirect dependency on the thread local component, the proxy ensures unique instances. This is vital for a
 * multithreaded application that uses EJBs.
 * </p>
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    /**
     * <code>ENSURE_THREAD_LOCALITY</code> is the constant for created {@link ComponentAdapter}instances, that ensure unique
     * instances of the component by delivering a proxy for the component.
     */
    public static final boolean ENSURE_THREAD_LOCALITY = true;
    /**
     * <code>THREAD_ENSURES_LOCALITY</code> is the constant for created {@link ComponentAdapter}instances, that create for
     * the current thread a new component.
     */
    public static final boolean THREAD_ENSURES_LOCALITY = false;

    private final boolean ensureThreadLocal;

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory, that ensures the usage of the ThreadLocal.
     * @param delegate The delegated ComponentAdapterFactory.
     */
    public ThreadLocalComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this(delegate, ENSURE_THREAD_LOCALITY);
    }

    /**
     * Constructs a wrapping ThreadLocalComponentAdapterFactory.
     * @param delegate The delegated ComponentAdapterFactory.
     * @param ensure {@link #ENSURE_THREAD_LOCALITY} or {@link #THREAD_ENSURES_LOCALITY}.
     */
    public ThreadLocalComponentAdapterFactory(ComponentAdapterFactory delegate, boolean ensure) {
        super(delegate);
        ensureThreadLocal = ensure;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.defaults.DecoratingComponentAdapterFactory#createComponentAdapter(java.lang.Object,
     *           java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final ComponentAdapter componentAdapter;
        if (ensureThreadLocal) {
            componentAdapter = new ThreadLocalComponentAdapter(super.createComponentAdapter(
                    componentKey, componentImplementation, parameters));
        } else {
            componentAdapter = new CachingComponentAdapter(super.createComponentAdapter(
                    componentKey, componentImplementation, parameters), new ThreadLocalReference());
        }
        return componentAdapter;
    }

}
