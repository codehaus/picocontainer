/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original Code By Centerline Computers, Inc.                               *
 *****************************************************************************/

package org.picocontainer.gems.containers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

/**
 * Decorates a MutablePicoContainer to provide extensive tracing
 * capabilities for all function calls into the Picocontainers.
 * <p>
 * By default, this class uses <tt>org.picocontainer.PicoContainer</tt>
 * as its logging category, however, this may be changed by providing
 * the logger in its alternate constructor.
 * </p>
 * <p>Start and Stop events are logged under <tt>info</tt> priority,
 * as are all conditions where querying for an object returns a null object
 * (e.g., getComponentAdapter(Object) returns null).  All other
 * functions use <tt>debug</tt> priority.</p>
 * <p>If used in nanocontainer, you can add wrap your PicoContainer with the
 *  Log4jTracingContainerDecorator: (Groovy Example)</p>
 * <pre>
 * 		pico = builder.container(parent: parent) {
 * 			//component(.....)
 * 			//And others.
 * 		}
 * 
 * 		//Wrap the underlying NanoContainer with a Decorated Pico.
 * 		pico = new org.picocontainer.gems.containers.Log4jTracingContainerDecorator (pico.getPico())
 * </pre>
 * 
 * @author Michael Rimov
 * @since Version 1.3 
 */
public class Log4jTracingContainerDecorator implements MutablePicoContainer, Serializable {

	/**
	 * Wrapped container.
	 */
	private final MutablePicoContainer delegate;
	
	/**
	 * Logger instance used for writing events.
	 */
	private transient Logger logger;
	
	/**
	 * Default typical wrapper that wraps another MutablePicoContainer.
	 * @param delegate Container to be decorated.
	 * @throws NullPointerException if delegate is null.
	 */
	public Log4jTracingContainerDecorator(MutablePicoContainer delegate) {
		this(delegate, Logger.getLogger(PicoContainer.class));
	}
	
	
	/**
	 * Alternate constructor that allows specification of the Logger to
	 * use.
	 * @param delegate Container to be decorated.
	 * @param logger specific Log4j Logger to use.
	 * @throws NullPointerException if delegate or logger is null.
	 */
	public Log4jTracingContainerDecorator(MutablePicoContainer delegate, Logger logger) {
		if (delegate == null) {
			throw new NullPointerException("delegate");
		}
		
		if (logger == null) {
			throw new NullPointerException("logger");
		}
		
		this.delegate = delegate;
		this.logger = logger;
	}
	
	
	/**
	 * Standard message handling for cases when a null object is returned
	 * for a given key.
	 * @param componentKey
	 * @param target
	 */
	protected void onKeyDoesntExistInContainer(Object componentKey, Logger target) {
		logger.info("Could not find component "
				+ ((componentKey != null) ? componentKey.toString() : " null ")
				+ " in container or parent container.");
	}

	/**
	 * {@inheritDoc}
	 * @param visitor
	 * @see org.picocontainer.PicoContainer#accept(org.picocontainer.PicoVisitor)
	 */
	public void accept(PicoVisitor visitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("Visiting Container " + delegate
					+ " with visitor " + visitor);
		}
		delegate.accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 * @param child
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#addChildContainer(org.picocontainer.PicoContainer)
	 */
	public boolean addChildContainer(PicoContainer child) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding child container: " + child + " to container " + delegate);
		}
		return delegate.addChildContainer(child);
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Disposable#dispose()
	 */
	public void dispose() {
		if (logger.isDebugEnabled()) {
			logger.debug("Disposing container " + delegate);
		}
		delegate.dispose();
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentAdapter(java.lang.Object)
	 */
	public ComponentAdapter getComponentAdapter(Object componentKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("Locating component adapter with key " + componentKey);
		}
		
		ComponentAdapter adapter =  delegate.getComponentAdapter(componentKey);
		if (adapter == null) {
			onKeyDoesntExistInContainer(componentKey, logger);
		}
		return adapter;
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return ComponentAdapter or null.
	 * @see org.picocontainer.PicoContainer#getComponentAdapterOfType(java.lang.Class)
	 */
	public ComponentAdapter getComponentAdapterOfType(Class componentType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Locating component adapter with type " + componentType);
		}

		ComponentAdapter ca =  delegate.getComponentAdapterOfType(componentType);

		if (ca == null) {
			onKeyDoesntExistInContainer(ca, logger);
		}
		return ca;
	}

	/**
	 * {@inheritDoc}
	 * @return Collection or null.
	 * @see org.picocontainer.PicoContainer#getComponentAdapters()
	 */
	public Collection getComponentAdapters() {
		if (logger.isDebugEnabled()) {
			logger.debug("Grabbing all component adapters for container: " + delegate);
		}
		return delegate.getComponentAdapters();
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return List of ComponentAdapters
	 * @see org.picocontainer.PicoContainer#getComponentAdaptersOfType(java.lang.Class)
	 */
	public List getComponentAdaptersOfType(Class componentType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Grabbing all component adapters for container: " 
					+ delegate + " of type: " + componentType.getName());
		}
		return delegate.getComponentAdaptersOfType(componentType);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentInstance(java.lang.Object)
	 */
	public Object getComponentInstance(Object componentKey) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to load component instance with key: " 
					+ componentKey
					+ " for container " 
					+ delegate);
			
		}
		
		Object result =  delegate.getComponentInstance(componentKey);
		if (result == null) {
			onKeyDoesntExistInContainer(componentKey, logger);
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentInstanceOfType(java.lang.Class)
	 */
	public Object getComponentInstanceOfType(Class componentType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to load component instance with type: " 
					+ componentType
					+ " for container " 
					+ delegate);
			
		}
		
		Object result = delegate.getComponentInstanceOfType(componentType);
		if (result == null) {
			if (logger.isInfoEnabled()) {
				logger.info("No component of type " + componentType.getName()
						+ " was found in container: " + delegate);
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentInstances()
	 */
	public List getComponentInstances() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving all component instances for container "
					+ delegate);
		}
		return delegate.getComponentInstances();
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentInstancesOfType(java.lang.Class)
	 */
	public List getComponentInstancesOfType(Class componentType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading all component instances of type " + componentType
					+ " for container " + delegate);
		}
		List result = delegate.getComponentInstancesOfType(componentType);
		if (result == null || result.size() == 0) {
			if (logger.isInfoEnabled()) {
			logger.info("Could not find any components  "
					+ " in container or parent container.");	
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see org.picocontainer.PicoContainer#getParent()
	 */
	public PicoContainer getParent() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving the parent for container " + delegate);
		}
		
		return delegate.getParent();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#makeChildContainer()
	 */
	public MutablePicoContainer makeChildContainer() {
		if (logger.isDebugEnabled()) {
			logger.debug("Making child container for container " + delegate);
		}
		
		//Wrap the new delegate
		return new Log4jTracingContainerDecorator( delegate.makeChildContainer());
	}

	/**
	 * {@inheritDoc}
	 * @param componentAdapter
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(org.picocontainer.ComponentAdapter)
	 */
	public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component adapter " + componentAdapter);
		}
		
		return delegate.registerComponent(componentAdapter);
	}

	/**
	 * {@inheritDoc}
	 * @param componentImplementation
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponentImplementation(java.lang.Class)
	 */
	public ComponentAdapter registerComponentImplementation(Class componentImplementation) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component implementation " + componentImplementation.getName());
		}

		return delegate.registerComponentImplementation(componentImplementation);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentImplementation
	 * @param parameters
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponentImplementation(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
	 */
	public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component implementation with key " 
					+ componentKey 
					+ " and implementation " 
					+ componentImplementation.getCanonicalName() 
					+ " using parameters "
					+ parameters);
		}
		
		return delegate.registerComponentImplementation(componentKey, componentImplementation, parameters);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentImplementation
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponentImplementation(java.lang.Object, java.lang.Class)
	 */
	public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component implementation with key " 
					+ componentKey 
					+ " and implementation " 
					+ componentImplementation.getCanonicalName());
		}
		
		return delegate.registerComponentImplementation(componentKey, componentImplementation);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponentInstance(java.lang.Object, java.lang.Object)
	 */
	public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component instance with key " 
					+ componentKey 
					+ " and instance " 
					+ componentInstance + "(class: " 
					+ ((componentInstance != null) ? componentInstance.getClass().getName() : " null "));
		}

		return delegate.registerComponentInstance(componentKey, componentInstance);
	}

	/**
	 * {@inheritDoc}
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponentInstance(java.lang.Object)
	 */
	public ComponentAdapter registerComponentInstance(Object componentInstance) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering component instance " 
					+ componentInstance + "(class: " 
					+ ((componentInstance != null) ? componentInstance.getClass().getName() : " null "));
		}
		
		return delegate.registerComponentInstance(componentInstance);
	}

	/**
	 * {@inheritDoc}
	 * @param child
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#removeChildContainer(org.picocontainer.PicoContainer)
	 */
	public boolean removeChildContainer(PicoContainer child) {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing child container: " + child
					+ " from parent: " + delegate);
		}
		return delegate.removeChildContainer(child);
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Startable#start()
	 */
	public void start() {
		if (logger.isInfoEnabled()) {
			logger.info("Starting Container " + delegate);
		}
		
		delegate.start();
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Startable#stop()
	 */
	public void stop() {
		if (logger.isInfoEnabled()) {
			logger.info("Stopping Container " + delegate);
		}
		delegate.stop();
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#unregisterComponent(java.lang.Object)
	 */
	public ComponentAdapter unregisterComponent(Object componentKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("Unregistering component " + componentKey + " from container " + delegate);
		}
		
		return delegate.unregisterComponent(componentKey);
	}

	/**
	 * {@inheritDoc}
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#unregisterComponentByInstance(java.lang.Object)
	 */
	public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
		if (logger.isDebugEnabled()) {
			logger.debug("Unregistering component by instance (" + componentInstance + ") from container " + delegate);
		}
		
		return delegate.unregisterComponentByInstance(componentInstance);
	}

	/**
	 * {@inheritDoc}
	 * @throws PicoVerificationException
	 * @deprecated
	 * @see org.picocontainer.PicoContainer#verify()
	 */
	public void verify() throws PicoVerificationException {
		logger.info("Verifying container");
		logger.warn("Using deprecated function PicoContainer.verify().  Please use VerifyingVisitor instead.");
		delegate.verify();
	}
	
	
	/**
	 * Retrieves the logger instance used by this decorator.
	 * @return Logger instance.
	 */
	public Logger getLoggerUsed() {
		return this.logger;
	}
	
	  private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, java.lang.ClassNotFoundException {
	        s.defaultReadObject();
	        String loggerName = s.readUTF();
	        logger = Logger.getLogger(loggerName);
	    }

	    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	        s.defaultWriteObject();
	        s.writeUTF(logger.getName());
	    }
	
	
	
}
