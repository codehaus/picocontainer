/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.LifecycleStrategy;

@SuppressWarnings("serial")
public class DefaultAnnotablePicoContainer extends DefaultPicoContainer implements
		AnnotablePicoContainer {

	public DefaultAnnotablePicoContainer() {
		this(new SingletonComponentAdapterFactory(), null);
	}

	public DefaultAnnotablePicoContainer(
			ComponentAdapterFactory componentAdapterFactory,
			LifecycleStrategy lifecycleStrategyForInstanceRegistrations,
			PicoContainer parent) {
		super(componentAdapterFactory,
				lifecycleStrategyForInstanceRegistrations, parent);
	}

	public DefaultAnnotablePicoContainer(
			ComponentAdapterFactory componentAdapterFactory,
			PicoContainer parent) {
		super(componentAdapterFactory, parent);
	}

	public DefaultAnnotablePicoContainer(
			ComponentAdapterFactory componentAdapterFactory) {
		super(componentAdapterFactory);
	}

	public DefaultAnnotablePicoContainer(ComponentMonitor monitor,
			LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
		super(monitor, lifecycleStrategy, parent);
	}

	public DefaultAnnotablePicoContainer(ComponentMonitor monitor,
			PicoContainer parent) {
		super(monitor, parent);
	}

	public DefaultAnnotablePicoContainer(ComponentMonitor monitor) {
		super(monitor);
	}

	public DefaultAnnotablePicoContainer(LifecycleStrategy lifecycleStrategy,
			PicoContainer parent) {
		super(lifecycleStrategy, parent);
	}

	public DefaultAnnotablePicoContainer(PicoContainer parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	public <T> T getComponent(Class<T> type) {
		return (T) this.getComponentInstanceOfType(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getComponentInstanceOfType(Class componentType) {
		// implicit binding
		if (componentType.isInterface()
				&& componentType.isAnnotationPresent(ImplementedBy.class)) {
			ImplementedBy implementedBy = (ImplementedBy) componentType
					.getAnnotation(ImplementedBy.class);
			// Make sure it's not the same type
			Class<?> implementationType = implementedBy.value();
			if (implementationType == componentType) {
				throw new PicoInitializationException("Invalid annotation");
			}

			// Make sure implementationType extends type.
			if (!componentType.isAssignableFrom(implementationType)) {
				throw new PicoInitializationException("Invalid annotation");
			}
			registerComponentImplementation(componentType, implementationType);
		}
		if (PicoContainer.class.isAssignableFrom(componentType)) {
			PicoContainer cont = this;
			while (cont.getParent() != null) {
				cont = cont.getParent();
			}
			return cont;
		}
		Object res = super.getComponentInstanceOfType(componentType);
		// auto registration
		if (res == null) {
			registerComponentImplementation(componentType);
			res = super.getComponentInstanceOfType(componentType);
		}
		return res;
	}

}
