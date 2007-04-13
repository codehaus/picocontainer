/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.DefaultLifecycleStrategy;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.MonitoringComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.monitors.DefaultComponentMonitor;

@SuppressWarnings("serial")
public class SingletonComponentAdapterFactory extends
		MonitoringComponentAdapterFactory {

	private LifecycleStrategy lifecycleStrategy;

	public SingletonComponentAdapterFactory() {
		super();
		this.lifecycleStrategy = new DefaultLifecycleStrategy(
				new DefaultComponentMonitor());
	}

	public SingletonComponentAdapterFactory(ComponentMonitor monitor) {
		super(monitor);
		this.lifecycleStrategy = new DefaultLifecycleStrategy(monitor);
	}

	public SingletonComponentAdapterFactory(ComponentMonitor monitor,
			LifecycleStrategy lifecycleStrategy) {
		super(monitor);
		this.lifecycleStrategy = lifecycleStrategy;
	}

	public ComponentAdapter createComponentAdapter(Object componentKey,
			Class componentImplementation, Parameter[] parameters)
			throws PicoIntrospectionException,
			AssignabilityRegistrationException,
			NotConcreteRegistrationException {
		if (componentImplementation.isAnnotationPresent(Singleton.class)) {
			return new CachingComponentAdapter(new InjectingComponentAdapter(
					componentKey, componentImplementation, parameters, false,
					currentMonitor(), lifecycleStrategy));
		}
		return new DecoratingComponentAdapter(new InjectingComponentAdapter(
				componentKey, componentImplementation, parameters, false,
				currentMonitor(), lifecycleStrategy));
	}

	@Override
	public void changeMonitor(ComponentMonitor monitor) {
		super.changeMonitor(monitor);
		if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
			((ComponentMonitorStrategy) lifecycleStrategy)
					.changeMonitor(monitor);
		}
	}

}
