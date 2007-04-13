/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.NotConcreteRegistrationException;

@SuppressWarnings("serial")
public class InjectingComponentAdapter extends
		ConstructorInjectionComponentAdapter {

	public InjectingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters, boolean allowNonPublicClasses, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		super(componentKey, componentImplementation, parameters, allowNonPublicClasses,
				monitor, lifecycleStrategy);
	}

	public InjectingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters, boolean allowNonPublicClasses, ComponentMonitor monitor) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		super(componentKey, componentImplementation, parameters, allowNonPublicClasses,
				monitor);
	}

	public InjectingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters, boolean allowNonPublicClasses) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		super(componentKey, componentImplementation, parameters, allowNonPublicClasses);
	}

	public InjectingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
		super(componentKey, componentImplementation, parameters);
	}

	public InjectingComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		super(componentKey, componentImplementation);
	}
	
	@Override
	protected Object newInstance(Constructor constructor, Object[] parameters) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		Object result = super.newInstance(constructor, parameters);
		return result;
	}

	/**
	 * Should be moved to newInstance
	 */
	@Override
	public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
		Object instance = super.getComponentInstance(container);
		injectDependencies(instance, getComponentImplementation(), container);
		return instance;
	}

	void injectDependencies(Object result, Class clazz, PicoContainer container)  throws PicoInitializationException {
		for (Field field: clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Inject.class)) {
				Object value = container.getComponentInstanceOfType(field.getType());
				try {
					field.setAccessible(true);
					field.set(result, value);
				} catch (IllegalArgumentException e) {
					throw new PicoInitializationException("Error while injecting field " + field.getName() ,e);
				} catch (IllegalAccessException e) {
					throw new PicoInitializationException("Error while injecting field " + field.getName() ,e);
				}
			}
		}
	}		

	
	
}


