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

import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.Parameter;

import java.util.Arrays;

public class ComponentSpecification {
    private final ComponentFactory componentFactory;
    private final Object componentKey;
    private final Class comp;
    private Parameter[] parameters;

    public ComponentSpecification(ComponentFactory componentFactory, final Object componentKey, final Class comp, Parameter[] parameters) {
        this.componentFactory = componentFactory;
        this.componentKey = componentKey;
        this.comp = comp;
        this.parameters = parameters;
    }

    public ComponentSpecification(ComponentFactory componentFactory, Object componentKey, Class comp) throws PicoIntrospectionException {
        this.componentFactory = componentFactory;
        this.componentKey = componentKey;
        this.comp = comp;

        parameters = new Parameter[componentFactory.getDependencies(comp).length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = createDefaultParameter();
        }
    }

    protected Parameter createDefaultParameter() {
        return new ComponentParameter();
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return comp;
    }

    public Object instantiateComponent(DefaultPicoContainer picoContainer)
            throws PicoInitializationException {
        Class[] dependencyTypes = componentFactory.getDependencies(comp);
        Object[] dependencies = new Object[dependencyTypes.length];
        for (int i = 0; i < dependencies.length; i++) {
            dependencies[i] = parameters[i].resolve(picoContainer, this, dependencyTypes[i]);
        }
        return componentFactory.createComponent(this, dependencies);
    }

    static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }

        // TODO handle the rest of the primitive types in the same way (Java really sucks concerning this!)

        return actual.isAssignableFrom(requested);
    }

    public void addConstantParameterBasedOnType(Class parameter, Object arg) throws PicoIntrospectionException {
        // TODO this is an ugly hack and the feature should simply be removed
        Class[] dependencies = componentFactory.getDependencies(comp);
        for (int i = 0; i < dependencies.length; i++) {

            if (isAssignableFrom(dependencies[i], parameter) && !(parameters[i] instanceof ConstantParameter)) {
                parameters[i] = new ConstantParameter(arg);
                return;
            }
        }

        throw new RuntimeException("No such parameter " + parameter + " in " + Arrays.asList(dependencies));
    }

    public Object newInstance() {
        try {
            return getComponentImplementation().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("#3 Can we have a concerted effort to try to force these excptions?");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("#4 Can we have a concerted effort to try to force these excptions?");
        }
    }

    public Parameter[] getParameters() {
        return parameters;
    }
}
