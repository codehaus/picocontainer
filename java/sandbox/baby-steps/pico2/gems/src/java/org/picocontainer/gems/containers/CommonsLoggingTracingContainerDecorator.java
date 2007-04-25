package org.picocontainer.gems.containers;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

/**
 *  
 * @author Michael Rimov
 */
public class CommonsLoggingTracingContainerDecorator implements MutablePicoContainer {
	
	/**
	 * Wrapped container.
	 */
	private final MutablePicoContainer delegate;
	
	/**
	 * Logger instance used for writing events.
	 */
	private transient Log log;
	
	/**
	 * Serialized log category.
	 */
	private final String logCategory;
	
	/**
	 * Default typical wrapper that wraps another MutablePicoContainer.
	 * @param delegate Container to be decorated.
	 * @throws NullPointerException if delegate is null.
	 */
	public CommonsLoggingTracingContainerDecorator(final MutablePicoContainer delegate) {
		this(delegate, PicoContainer.class.getName());
	}
	
	
	/**
	 * Alternate constructor that allows specification of the Logger to
	 * use.
	 * @param delegate Container to be decorated.
	 * @param loggingCategory specific Log4j Logger to use.
	 * @throws NullPointerException if delegate or log is null.
	 */
	public CommonsLoggingTracingContainerDecorator(final MutablePicoContainer delegate, final String loggingCategory) {
		if (delegate == null) {
			throw new NullPointerException("delegate");
		}
		
		if (loggingCategory == null) {
			throw new NullPointerException("loggingCategory");
		}
		
		log = LogFactory.getLog(loggingCategory);
		
		this.delegate = delegate;
		logCategory = loggingCategory;
	}
	
	
	/**
	 * Standard message handling for cases when a null object is returned
	 * for a given key.
	 * @param componentKey
	 * @param target
	 */
	protected void onKeyDoesntExistInContainer(final Object componentKey, final Log target) {
		log.info("Could not find component "
				+ ((componentKey != null) ? componentKey.toString() : " null ")
				+ " in container or parent container.");
	}

	/**
	 * {@inheritDoc}
	 * @param visitor
	 * @see org.picocontainer.PicoContainer#accept(org.picocontainer.PicoVisitor)
	 */
	public void accept(final PicoVisitor visitor) {
		if (log.isDebugEnabled()) {
			log.debug("Visiting Container " + delegate
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
	public boolean addChildContainer(final PicoContainer child) {
		if (log.isDebugEnabled()) {
			log.debug("Adding child container: " + child + " to container " + delegate);
		}
		return delegate.addChildContainer(child);
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Disposable#dispose()
	 */
	public void dispose() {
		if (log.isDebugEnabled()) {
			log.debug("Disposing container " + delegate);
		}
		delegate.dispose();
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponentAdapter(java.lang.Object)
	 */
	public ComponentAdapter getComponentAdapter(final Object componentKey) {
		if (log.isDebugEnabled()) {
			log.debug("Locating component adapter with key " + componentKey);
		}
		
		ComponentAdapter adapter =  delegate.getComponentAdapter(componentKey);
		if (adapter == null) {
			onKeyDoesntExistInContainer(componentKey, log);
		}
		return adapter;
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return ComponentAdapter or null.
	 * @see org.picocontainer.PicoContainer#getComponentAdapter(java.lang.Class)
	 */
	public ComponentAdapter getComponentAdapter(final Class componentType) {
		if (log.isDebugEnabled()) {
			log.debug("Locating component adapter with type " + componentType);
		}

		ComponentAdapter ca =  delegate.getComponentAdapter(componentType);

		if (ca == null) {
			onKeyDoesntExistInContainer(ca, log);
		}
		return ca;
	}

	/**
	 * {@inheritDoc}
	 * @return Collection or null.
	 * @see org.picocontainer.PicoContainer#getComponentAdapters()
	 */
	public Collection getComponentAdapters() {
		if (log.isDebugEnabled()) {
			log.debug("Grabbing all component adapters for container: " + delegate);
		}
		return delegate.getComponentAdapters();
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return List of ComponentAdapters
	 * @see org.picocontainer.PicoContainer#getComponentAdapters(java.lang.Class)
	 */
	public List getComponentAdapters(final Class componentType) {
		if (log.isDebugEnabled()) {
			log.debug("Grabbing all component adapters for container: " 
					+ delegate + " of type: " + componentType.getName());
		}
		return delegate.getComponentAdapters(componentType);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponent(java.lang.Object)
	 */
	public Object getComponent(final Object componentKey) {
		
		if (log.isDebugEnabled()) {
			log.debug("Attempting to load component instance with key: " 
					+ componentKey
					+ " for container " 
					+ delegate);
			
		}
		
		Object result =  delegate.getComponent(componentKey);
		if (result == null) {
			onKeyDoesntExistInContainer(componentKey, log);
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponent(java.lang.Class)
	 */
	public Object getComponent(final Class componentType) {
		if (log.isDebugEnabled()) {
			log.debug("Attempting to load component instance with type: " 
					+ componentType
					+ " for container " 
					+ delegate);
			
		}
		
		Object result = delegate.getComponent(componentType);
		if (result == null) {
			if (log.isInfoEnabled()) {
				log.info("No component of type " + componentType.getName()
						+ " was found in container: " + delegate);
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponents()
	 */
	public List getComponents() {
		if (log.isDebugEnabled()) {
			log.debug("Retrieving all component instances for container "
					+ delegate);
		}
		return delegate.getComponents();
	}

	/**
	 * {@inheritDoc}
	 * @param componentType
	 * @return
	 * @see org.picocontainer.PicoContainer#getComponents(java.lang.Class)
	 */
	public List getComponents(final Class componentType) {
		if (log.isDebugEnabled()) {
			log.debug("Loading all component instances of type " + componentType
					+ " for container " + delegate);
		}
		List result = delegate.getComponents(componentType);
		if (result == null || result.size() == 0) {
			if (log.isInfoEnabled()) {
			log.info("Could not find any components  "
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
		if (log.isDebugEnabled()) {
			log.debug("Retrieving the parent for container " + delegate);
		}
		
		return delegate.getParent();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#makeChildContainer()
	 */
	public MutablePicoContainer makeChildContainer() {
		if (log.isDebugEnabled()) {
			log.debug("Making child container for container " + delegate);
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
	public ComponentAdapter registerComponent(final ComponentAdapter componentAdapter) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component adapter " + componentAdapter);
		}
		
		return delegate.registerComponent(componentAdapter);
	}

	/**
	 * {@inheritDoc}
	 * @param componentImplementation
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(java.lang.Class)
	 */
	public ComponentAdapter registerComponent(final Class componentImplementation) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component implementation " + componentImplementation.getName());
		}

		return delegate.registerComponent(componentImplementation);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentImplementation
	 * @param parameters
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
	 */
	public ComponentAdapter registerComponent(final Object componentKey, final Class componentImplementation,
			final Parameter[] parameters) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component implementation with key " 
					+ componentKey 
					+ " and implementation " 
					+ componentImplementation.getCanonicalName() 
					+ " using parameters "
					+ parameters);
		}
		
		return delegate.registerComponent(componentKey, componentImplementation, parameters);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentImplementation
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(java.lang.Object, java.lang.Class)
	 */
	public ComponentAdapter registerComponent(final Object componentKey, final Class componentImplementation) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component implementation with key " 
					+ componentKey 
					+ " and implementation " 
					+ componentImplementation.getCanonicalName());
		}
		
		return delegate.registerComponent(componentKey, componentImplementation);
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(java.lang.Object, java.lang.Object)
	 */
	public ComponentAdapter registerComponent(final Object componentKey, final Object componentInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component instance with key " 
					+ componentKey 
					+ " and instance " 
					+ componentInstance + "(class: " 
					+ ((componentInstance != null) ? componentInstance.getClass().getName() : " null "));
		}

		return delegate.registerComponent(componentKey, componentInstance);
	}

	/**
	 * {@inheritDoc}
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#registerComponent(java.lang.Object)
	 */
	public ComponentAdapter registerComponent(final Object componentInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component instance " 
					+ componentInstance + "(class: " 
					+ ((componentInstance != null) ? componentInstance.getClass().getName() : " null "));
		}
		
		return delegate.registerComponent(componentInstance);
	}

	/**
	 * {@inheritDoc}
	 * @param child
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#removeChildContainer(org.picocontainer.PicoContainer)
	 */
	public boolean removeChildContainer(final PicoContainer child) {
		if (log.isDebugEnabled()) {
			log.debug("Removing child container: " + child
					+ " from parent: " + delegate);
		}
		return delegate.removeChildContainer(child);
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Startable#start()
	 */
	public void start() {
		if (log.isInfoEnabled()) {
			log.info("Starting Container " + delegate);
		}
		
		delegate.start();
	}

	/**
	 * {@inheritDoc}
	 * @see org.picocontainer.Startable#stop()
	 */
	public void stop() {
		if (log.isInfoEnabled()) {
			log.info("Stopping Container " + delegate);
		}
		delegate.stop();
	}

	/**
	 * {@inheritDoc}
	 * @param componentKey
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#unregisterComponent(java.lang.Object)
	 */
	public ComponentAdapter unregisterComponent(final Object componentKey) {
		if (log.isDebugEnabled()) {
			log.debug("Unregistering component " + componentKey + " from container " + delegate);
		}
		
		return delegate.unregisterComponent(componentKey);
	}

	/**
	 * {@inheritDoc}
	 * @param componentInstance
	 * @return
	 * @see org.picocontainer.MutablePicoContainer#unregisterComponentByInstance(java.lang.Object)
	 */
	public ComponentAdapter unregisterComponentByInstance(final Object componentInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Unregistering component by instance (" + componentInstance + ") from container " + delegate);
		}
		
		return delegate.unregisterComponentByInstance(componentInstance);
	}

	
	/**
	 * Retrieves the log instance used by this decorator.
	 * @return Logger instance.
	 */
	public Log getLoggerUsed() {
		return this.log;
	}
	
	private void readObject(final java.io.ObjectInputStream s) 
	  	throws java.io.IOException, java.lang.ClassNotFoundException {
	        s.defaultReadObject();
	        log = LogFactory.getLog(this.logCategory);
	}
}
