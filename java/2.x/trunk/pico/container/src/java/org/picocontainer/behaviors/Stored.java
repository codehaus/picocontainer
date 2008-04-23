package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.lang.reflect.Type;

/**
 * behaviour for all behaviours wishing to store
 * their component in "awkward places" ( object references ) 
 * @author Konstantin Pribluda
 *
 * @param <T>
 */
public class Stored<T> extends AbstractBehavior<T> {

	    
	/**
	 * Serialization UUID. 
	 */
	private static final long serialVersionUID = -155678172730744032L;
	
	protected final boolean delegateHasLifecylce;
	protected boolean disposed;
	protected final ObjectReference<T> instanceReference;

	protected boolean started;

	public Stored(ComponentAdapter<T> delegate, ObjectReference<T> reference) {
		super(delegate);
		instanceReference = reference;
        this.disposed = false;
        this.started = false;
        this.delegateHasLifecylce = delegate instanceof LifecycleStrategy
        && ((LifecycleStrategy) delegate).hasLifecycle(delegate.getComponentImplementation());

	}

	public boolean componentHasLifecycle() {
	    return delegateHasLifecylce;
	}

	/**
	 * Disposes the cached component instance
	 * {@inheritDoc}
	 */
	public void dispose(PicoContainer container) {
	    if ( delegateHasLifecylce ){
	        if (disposed) throw new IllegalStateException("Already disposed");
	        dispose(getComponentInstance(container, null));
	        disposed = true;
	    }
	}

	/**
	 * Retrieves the stored reference.  May be null if it has
	 * never been set, or possibly if the reference has been
	 * flushed.
	 * @return the stored object or null.
	 */
	public T getStoredObject() {
		return instanceReference.get();
	}
	
	/**
	 * Flushes the cache.
	 * If the component instance is started is will stop and dispose it before
	 * flushing the cache.
	 */
	public void flush() {
	    Object instance = instanceReference.get();
	    if ( instance != null && delegateHasLifecylce && started ) {
	        stop(instance);
	        dispose(instance);
	    }
	    instanceReference.set(null);
	}

	public T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
	    T instance = instanceReference.get();
	    if (instance == null) {
	        instance = super.getComponentInstance(container, into);
	        instanceReference.set(instance);
	    }
	    return instance;
	}

    public String getDescriptor() {
        return "Stored";
    }

    /**
	 * Starts the cached component instance
	 * {@inheritDoc}
	 */
	public void start(PicoContainer container) {
	    if ( delegateHasLifecylce ){
	        if (disposed) throw new IllegalStateException("Already disposed");
	        if (started) throw new IllegalStateException("Already started");
	        start(getComponentInstance(container, null));
	        started = true;
	    }
	}

	/**
	 * Stops the cached component instance
	 * {@inheritDoc}
	 */
	public void stop(PicoContainer container) {
	    if ( delegateHasLifecylce ){
	        if (disposed) throw new IllegalStateException("Already disposed");
	        if (!started) throw new IllegalStateException("Not started");
	        stop(getComponentInstance(container, null));
	        started = false;
	    }
	}

}
