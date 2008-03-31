/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.memberToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.*;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.monitors.ComponentMonitorHelper;
import org.picocontainer.monitors.NullComponentMonitor;


/**
 * A {@link ComponentMonitor} which writes to a Commons Logging {@link Log Log} instance.
 * The Log instance can either be injected or, if not set, the {@link LogFactory LogFactory}
 * will be used to retrieve it at every invocation of the monitor.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class CommonsLoggingComponentMonitor implements ComponentMonitor, Serializable {

    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = 5863003718112457388L;

	/**
	 * Commons Logger.
	 */
	private Log log;
    
	/**
	 * Delegate for component monitor chains.
	 */
    private final ComponentMonitor delegate;

    /**
     * Creates a CommonsLoggingComponentMonitor with no Log instance set.
     * The {@link LogFactory LogFactory} will be used to retrieve the Log instance
     * at every invocation of the monitor.
     */
    public CommonsLoggingComponentMonitor() {
        delegate = new NullComponentMonitor();
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance class.
     * The class name is used to retrieve the Log instance.
     * 
     * @param logClass the class of the Log
     */
    public CommonsLoggingComponentMonitor(Class<?> logClass) {
        this(logClass.getName());
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance name. It uses the
     * {@link LogFactory LogFactory} to create the Log instance.
     * 
     * @param logName the name of the Log
     */
    public CommonsLoggingComponentMonitor(String logName) {
        this(LogFactory.getLog(logName));
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance
     * 
     * @param log the Log to write to
     */
    public CommonsLoggingComponentMonitor(Log log) {
        this();
        this.log = log;
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance class.
     * The class name is used to retrieve the Log instance.
     *
     * @param logClass the class of the Log
     * @param delegate the delegate
     */
    public CommonsLoggingComponentMonitor(Class<?> logClass, ComponentMonitor delegate) {
        this(logClass.getName(), delegate);
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance name. It uses the
     * {@link LogFactory LogFactory} to create the Log instance.
     *
     * @param logName the name of the Log
     * @param delegate the delegate
     */
    public CommonsLoggingComponentMonitor(String logName, ComponentMonitor delegate) {
        this(LogFactory.getLog(logName), delegate);
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance
     *
     * @param log the Log to write to
     * @param delegate the delegate
     */
    public CommonsLoggingComponentMonitor(Log log, ComponentMonitor delegate) {
        this.log = log;
        this.delegate = delegate;
    }


    public <T> Constructor<T> instantiating(PicoContainer container, ComponentAdapter<T> componentAdapter,
                                     Constructor<T> constructor
    ) {
        Log log = getLog(constructor);
        if (log.isDebugEnabled()) {
            log.debug(ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
        }
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    public <T> void instantiated(PicoContainer container, ComponentAdapter<T> componentAdapter,
                             Constructor<T> constructor,
                             Object instantiated,
                             Object[] parameters,
                             long duration) {
        Log log = getLog(constructor);
        if (log.isDebugEnabled()) {
            log.debug(ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), duration, instantiated.getClass().getName(), parmsToString(parameters)));
        }
        delegate.instantiated(container, componentAdapter, constructor, instantiated, parameters, duration);
    }

    public <T> void instantiationFailed(PicoContainer container,
                                    ComponentAdapter<T>  componentAdapter,
                                    Constructor<T>  constructor,
                                    Exception cause) {
        Log log = getLog(constructor);
        if (log.isWarnEnabled()) {
            log.warn(ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), cause.getMessage()), cause);
        }
        delegate.instantiationFailed(container, componentAdapter, constructor, cause);
    }

    public void invoking(PicoContainer container,
                         ComponentAdapter<?> componentAdapter,
                         Member member,
                         Object instance) {
        Log log = getLog(member);
        if (log.isDebugEnabled()) {
            log.debug(ComponentMonitorHelper.format(ComponentMonitorHelper.INVOKING, memberToString(member), instance));
        }
        delegate.invoking(container, componentAdapter, member, instance);
    }

    public void invoked(PicoContainer container,
                        ComponentAdapter<?> componentAdapter,
                        Method method,
                        Object instance,
                        long duration) {
        Log log = getLog(method);
        if (log.isDebugEnabled()) {
            log.debug(ComponentMonitorHelper.format(ComponentMonitorHelper.INVOKED, methodToString(method), instance, duration));
        }
        delegate.invoked(container, componentAdapter, method, instance,  duration);
    }

    public void invocationFailed(Member member, Object instance, Exception cause) {
        Log log = getLog(member);
        if (log.isWarnEnabled()) {
            log.warn(ComponentMonitorHelper.format(ComponentMonitorHelper.INVOCATION_FAILED, memberToString(member), instance, cause.getMessage()), cause);
        }
        delegate.invocationFailed(member, instance, cause);
    }

    public void lifecycleInvocationFailed(MutablePicoContainer container,
                                          ComponentAdapter<?> componentAdapter, Method method,
                                          Object instance,
                                          RuntimeException cause) {
        Log log = getLog(method);
        if (log.isWarnEnabled()) {
            log.warn(ComponentMonitorHelper.format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED, methodToString(method), instance, cause.getMessage()), cause);
        }
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
        Log log = this.log != null ? this.log : LogFactory.getLog(ComponentMonitor.class);
        if (log.isWarnEnabled()) {
            log.warn(ComponentMonitorHelper.format(ComponentMonitorHelper.NO_COMPONENT, componentKey));
        }
        return delegate.noComponentFound(container, componentKey);
    }

    public AbstractInjector newInjectionFactory(AbstractInjector abstractInjector) {
        return delegate.newInjectionFactory(abstractInjector); 
    }

    protected Log getLog(Member member) {
        if ( log != null ){
            return log;
        } 
        return LogFactory.getLog(member.getDeclaringClass());
    }

}
