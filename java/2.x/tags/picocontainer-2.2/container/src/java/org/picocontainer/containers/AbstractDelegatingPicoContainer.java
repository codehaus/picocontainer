package org.picocontainer.containers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.annotation.Annotation;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;

/**
 * abstract base class for immutable delegation to pico
 * 
 * @author k.pribluda
 * 
 */
public abstract class AbstractDelegatingPicoContainer implements PicoContainer, Serializable{

	private PicoContainer delegate;
    private static final long serialVersionUID = 6444995393199283475L;

    public AbstractDelegatingPicoContainer(PicoContainer delegate) {
		if (delegate == null) {
			throw new NullPointerException(
					"PicoContainer delegate must not be null");
		}
		this.delegate = delegate;
	}

	public void accept(PicoVisitor visitor) {
		delegate.accept(visitor);
	}


	public boolean equals(Object obj) {
		// required to make it pass on both jdk 1.3 and jdk 1.4. Btw, what about
		// overriding hashCode()? (AH)
		return delegate.equals(obj) || this == obj;
	}

	public <T> T getComponent(Class<T> componentType) {
		return componentType.cast(getComponent((Object) componentType));
	}

    public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
        return delegate.getComponent(componentType, binding);
    }

    public Object getComponent(Object componentKeyOrType) {
		return delegate.getComponent(componentKeyOrType);
	}

	public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType,
			NameBinding componentNameBinding) {
		return delegate.getComponentAdapter(componentType,
                                            componentNameBinding);
	}

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
        return delegate.getComponentAdapter(componentType, binding);
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
		return delegate.getComponentAdapter(componentKey);
	}

	public Collection<ComponentAdapter<?>> getComponentAdapters() {
		return delegate.getComponentAdapters();
	}

	public <T> List<ComponentAdapter<T>> getComponentAdapters(
			Class<T> componentType) {
		return delegate.getComponentAdapters(componentType);
	}

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType, Class<? extends Annotation> binding) {
        return delegate.getComponentAdapters(componentType, binding);
    }    

    public List<Object> getComponents() {
		return delegate.getComponents();
	}

	public <T> List<T> getComponents(Class<T> type) throws PicoException {
		return delegate.getComponents(type);
	}

	public PicoContainer getDelegate() {
		return delegate;
	}

	public PicoContainer getParent() {
		return delegate.getParent();
	}
	
}
