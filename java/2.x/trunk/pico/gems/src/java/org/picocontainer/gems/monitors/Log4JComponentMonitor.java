/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.format;
import static org.picocontainer.monitors.ComponentMonitorHelper.memberToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.monitors.ComponentMonitorHelper;
import org.picocontainer.monitors.NullComponentMonitor;


/**
 * A {@link org.picocontainer.ComponentMonitor} which writes to a Log4J {@link org.apache.log4j.Logger} instance.
 * The Logger instance can either be injected or, if not set, the {@link LogManager LogManager}
 * will be used to retrieve it at every invocation of the monitor.
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class Log4JComponentMonitor implements ComponentMonitor, Serializable {

	/**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = 6974859796813059085L;

	/**
	 * Log4j Logger.
	 */
    private transient Logger logger;
    
    /**
     * Delegate Monitor.
     */
    private final ComponentMonitor delegate;

    /**
     * Creates a Log4JComponentMonitor with no Logger instance set.
     * The {@link LogManager LogManager} will be used to retrieve the Logger instance
     * at every invocation of the monitor.
     */
    public Log4JComponentMonitor() {
        delegate = new NullComponentMonitor();
        
    }
    
    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Log4JComponentMonitor(final Class<?> loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Log4JComponentMonitor(final String loggerName) {
        this(LogManager.getLogger(loggerName));
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Log4JComponentMonitor(final Logger logger) {
        this();
        this.logger = logger;
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(final Class<?> loggerClass, final ComponentMonitor delegate) {
        this(loggerClass.getName(), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(final String loggerName, final ComponentMonitor delegate) {
        this(LogManager.getLogger(loggerName), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     * @param delegate the delegate
     */
    public Log4JComponentMonitor(final Logger logger, final ComponentMonitor delegate) {
        this(delegate);
        this.logger = logger;
    }

    public Log4JComponentMonitor(final ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} **/
    public <T> Constructor<T> instantiating(final PicoContainer container, final ComponentAdapter<T> componentAdapter,
                                     final Constructor<T> constructor
    ) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
        }
        return delegate.instantiating(container, componentAdapter, constructor);
    }

    /** {@inheritDoc} **/
    public <T> void instantiated(final PicoContainer container, final ComponentAdapter<T> componentAdapter,
                             final Constructor<T> constructor,
                             final Object instantiated,
                             final Object[] parameters,
                             final long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), duration, instantiated.getClass().getName(), parmsToString(parameters)));
        }
        delegate.instantiated(container, componentAdapter, constructor, instantiated, parameters, duration);
    }

    /** {@inheritDoc} **/
    public <T> void instantiationFailed(final PicoContainer container,
                                    final ComponentAdapter<T> componentAdapter,
                                    final Constructor<T> constructor,
                                    final Exception cause) {
        Logger logger = getLogger(constructor);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), cause.getMessage()), cause);
        }
        delegate.instantiationFailed(container, componentAdapter, constructor, cause);
    }

    /** {@inheritDoc} **/
    public void invoking(final PicoContainer container,
                         final ComponentAdapter<?> componentAdapter,
                         final Member member,
                         final Object instance) {
        Logger logger = getLogger(member);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKING, memberToString(member), instance));
        }
        delegate.invoking(container, componentAdapter, member, instance);
    }

    /** {@inheritDoc} **/
    public void invoked(final PicoContainer container,
                        final ComponentAdapter<?> componentAdapter,
                        final Method method,
                        final Object instance,
                        final long duration) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(ComponentMonitorHelper.INVOKED, methodToString(method), instance, duration));
        }
        delegate.invoked(container, componentAdapter, method, instance, duration);
    }

    /** {@inheritDoc} **/
    public void invocationFailed(final Member member, final Object instance, final Exception cause) {
        Logger logger = getLogger(member);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.INVOCATION_FAILED, memberToString(member), instance, cause.getMessage()), cause);
        }
        delegate.invocationFailed(member, instance, cause);
    }

    /** {@inheritDoc} **/
    public void lifecycleInvocationFailed(final MutablePicoContainer container,
                                          final ComponentAdapter<?> componentAdapter, final Method method,
                                          final Object instance,
                                          final RuntimeException cause) {
        Logger logger = getLogger(method);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED, methodToString(method), instance, cause.getMessage()), cause);
        }
        delegate.lifecycleInvocationFailed(container, componentAdapter, method, instance, cause);
    }

    /** {@inheritDoc} **/
    public Object noComponentFound(final MutablePicoContainer container, final Object componentKey) {
        Logger logger = this.logger != null ? this.logger : LogManager.getLogger(ComponentMonitor.class);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(ComponentMonitorHelper.NO_COMPONENT, componentKey));
        }
        return delegate.noComponentFound(container, componentKey);

    }

    /** {@inheritDoc} **/
    public AbstractInjector newInjectionFactory(final AbstractInjector abstractInjector) {
        return delegate.newInjectionFactory(abstractInjector);
    }

    protected Logger getLogger(final Member member) {
        if ( logger != null ){
            return logger;
        } 
        return LogManager.getLogger(member.getDeclaringClass());
    }

    
    /**
     * Serializes the monitor.
     * @param oos object output stream.
     * @throws IOException
     */
    private void writeObject(final ObjectOutputStream oos) throws IOException {
    	oos.defaultWriteObject();
    	if (logger != null) {
    		oos.writeBoolean(true);
    		oos.writeUTF(logger.getName());
    	} else {
    		oos.writeBoolean(false);    			
    	}
    }
    
    /**
     * Manually creates a new logger instance if it was defined earlier.
     * @param ois
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
    	ois.defaultReadObject();
    	boolean hasDefaultLogger = ois.readBoolean();
    	if (hasDefaultLogger) {
	    	String defaultLoggerCategory = ois.readUTF();
	    	assert defaultLoggerCategory != null : "Serialization indicated default logger, "
	    		+"but no logger category found in input stream.";
    		logger = LogManager.getLogger(defaultLoggerCategory);
    	}
    }

}
