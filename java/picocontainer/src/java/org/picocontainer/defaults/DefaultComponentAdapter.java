/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.*;
import org.picocontainer.defaults.NoPicoSuitableConstructorException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;

import java.io.Serializable;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultComponentAdapter implements Serializable, ComponentAdapter {

    private final Object componentKey;
    private final Class componentImplementation;
    private Parameter[] parameters;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     * @param parameters
     * @throws org.picocontainer.PicoIntrospectionException
     */
    public DefaultComponentAdapter(final Object componentKey,
                                   final Class componentImplementation,
                                   Parameter[] parameters) throws PicoIntrospectionException {
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        this.parameters = parameters;

        if (this.parameters == null) {
            this.parameters = getDefaultParameters();
        }
    }

    /**
     * Use default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     * @throws org.picocontainer.PicoIntrospectionException
     */
    public DefaultComponentAdapter(Object componentKey,
                                   Class componentImplementation)
            throws PicoIntrospectionException {
        this(componentKey, componentImplementation, null);
    }

    protected Parameter[] getDefaultParameters() throws PicoIntrospectionException {
        Parameter[] parameters = new Parameter[getDependencies().length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = createDefaultParameter();
        }
        return parameters;
    }

    public Class[] getDependencies() throws PicoIntrospectionException {
        Constructor constructor = getConstructor();
        return constructor.getParameterTypes();
    }

    /**
     * This is now IoC 2.5 compatible.  Multi ctors next.
     * @return
     * @throws org.picocontainer.defaults.NoPicoSuitableConstructorException
     */
    private Constructor getConstructor() throws NoPicoSuitableConstructorException {
        Constructor[] constructors = componentImplementation.getConstructors();
        Constructor picoConstructor = null;
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];
            if (constructor.getParameterTypes().length != 0 || constructors.length == 1) {
                if (picoConstructor != null) {
                    throw new NoPicoSuitableConstructorException(componentImplementation);
                }
                picoConstructor = constructor;
            }
        }
        if (picoConstructor == null) {
            throw new NoPicoSuitableConstructorException(componentImplementation);
        }
        // Get the pico enabled constructor
        return picoConstructor;
    }

    public Parameter createDefaultParameter() {
        return new ComponentParameter();
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }

    public Object instantiateComponent(ComponentRegistry componentRegistry)
            throws PicoInitializationException {
        Class[] dependencyTypes = getDependencies();
        Object[] dependencies = new Object[dependencyTypes.length];
        if (dependencyTypes.length != parameters.length) {
            throw new RuntimeException("Incorrect number of parameters specified for " + getComponentImplementation().getName() + " component with key " + getComponentKey().toString() );
        }
        for (int i = 0; i < dependencies.length; i++) {
            dependencies[i] = parameters[i].resolve(componentRegistry, this, dependencyTypes[i]);
        }
        return createComponent(this, dependencies);
    }

    public Object createComponent(ComponentAdapter componentAdapter, Object[] instanceDependencies) throws PicoInvocationTargetInitializationException, NoPicoSuitableConstructorException {
        try {
            Constructor constructor = getConstructor();
            return constructor.newInstance(instanceDependencies);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetInitializationException(e.getCause());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }

        // TODO handle the rest of the primitive types in the same way (Java really sucks concerning this!)

        return actual.isAssignableFrom(requested);
    }

    public void addConstantParameterBasedOnType(Class parameter, Object arg) throws PicoIntrospectionException {
        // TODO this is an ugly hack and the feature should simply be removed
        Class[] dependencies = getDependencies();
        for (int i = 0; i < dependencies.length; i++) {

            if (isAssignableFrom(dependencies[i], parameter) && !(parameters[i] instanceof ConstantParameter)) {
                parameters[i] = new ConstantParameter(arg);
                return;
            }
        }

        throw new RuntimeException("No such parameter " + parameter + " in " + Arrays.asList(dependencies));
    }

    public Parameter[] getParameters() {
        return parameters;
    }

	public boolean equals(Object object) {
		if (object == null || !getClass().equals(object.getClass())) {
			return false;
		}
		DefaultComponentAdapter other = (DefaultComponentAdapter) object;

		return getComponentKey().equals(other.getComponentKey()) &&
			getComponentImplementation().equals(other.getComponentImplementation()) &&
			Arrays.asList(getParameters()).equals(Arrays.asList(other.getParameters()));
	}
}
