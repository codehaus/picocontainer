/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.ComponentFactory;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;

import java.util.Arrays;

class ComponentSpecification {
    private ComponentFactory componentFactory;
    private final Class compType;
    private final Class comp;
    private Parameter[] parameters;

    public ComponentSpecification(ComponentFactory componentFactory, final Class compType, final Class comp, Parameter[] parameters) {
        this.componentFactory = componentFactory;
        this.compType = compType;
        this.comp = comp;
        this.parameters = parameters;
    }

    public Class getComponentType() {
        return compType;
    }

    public Class getComponentImplementation() {
        return comp;
    }

    public Object instantiateComponent(DefaultPicoContainer picoContainer)
            throws PicoInstantiationException, PicoIntrospectionException {
        Class[] dependencyTypes = componentFactory.getDependencies(comp);
        Object[] dependencies = new Object[dependencyTypes.length];
        for (int i = 0; i < dependencies.length; i++) {
            dependencies[i] = parameters[i].resolve(picoContainer, this, dependencyTypes[i]);
        }
        return componentFactory.createComponent(compType, comp, dependencyTypes, dependencies);
    }

    static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }

        // TODO handle the rest of the primitive types in the same way (Java really sucks concerning this!)

        return actual.isAssignableFrom(requested);
    }

    public void addConstantParameterBasedOnType(Class componentType, Class parameter, Object arg) throws PicoIntrospectionException {
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
}
