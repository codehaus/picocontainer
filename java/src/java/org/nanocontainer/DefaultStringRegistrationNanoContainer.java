/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer;

import org.nanocontainer.reflection.StringToObjectConverter;
import org.picocontainer.*;
import org.picocontainer.defaults.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultStringRegistrationNanoContainer implements StringRegistrationNanoContainer, Serializable {
    private final List classLoaders = new ArrayList();
    private final StringToObjectConverter converter = new StringToObjectConverter();

    private final MutablePicoContainer picoContainer;

    public DefaultStringRegistrationNanoContainer(ClassLoader classLoader, MutablePicoContainer picoContainer) {
        this.picoContainer = picoContainer;
        classLoaders.add(classLoader);
    }

    public DefaultStringRegistrationNanoContainer() {
        this(
                DefaultStringRegistrationNanoContainer.class.getClassLoader(),
                new DefaultPicoContainer()
        );
    }

    public DefaultStringRegistrationNanoContainer(ClassLoader classLoader) {
        this(
                classLoader,
                new DefaultPicoContainer()
        );
    }

    public void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        if (compClassName == null) {
            throw new NullPointerException("compClassName can't be null");
        }
        picoContainer.registerComponentImplementation(DefaultStringRegistrationNanoContainer.class.getClassLoader().loadClass(compClassName));
    }

    public void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class typeClass = loadClass(typeClassName);
        Class compClass = loadClass(compClassName);
        picoContainer.registerComponentImplementation(typeClass, compClass);
    }

    public void registerComponent(
            String typeClassName,
            String compClassName,
            String[] parameterTypesAsString,
            String[] parameterValuesAsString
            ) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class typeClass = loadClass(typeClassName);
        Class compClass = loadClass(compClassName);
        Parameter[] parameters = new Parameter[parameterTypesAsString.length];
        for (int i = 0; i < parameters.length; i++) {
            Class paramTypeClass = loadClass(parameterTypesAsString[i]);
            Object value = converter.convertTo(paramTypeClass, parameterValuesAsString[i]);
            parameters[i] = new ConstantParameter(value);
        }
        picoContainer.registerComponentImplementation(typeClass, compClass, parameters);
    }

    public boolean hasComponent(Object clazz) {
        return picoContainer.hasComponent(clazz);
    }

    public Object getComponentInstance(Object clazz) throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return picoContainer.getComponentInstance(clazz);
    }

    public Collection getComponentInstances() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return picoContainer.getComponentInstances();
    }

    public Object getComponentMulticaster() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return picoContainer.getComponentMulticaster();
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return picoContainer.getComponentMulticaster(callInInstantiationOrder, callUnmanagedComponents);
    }

    public Collection getComponentKeys() {
        return picoContainer.getComponentKeys();
    }

    public void addComponentClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    private Class loadClass(String name) throws ClassNotFoundException {
        for (int i = 0; i < classLoaders.size(); i++) {
            ClassLoader classLoader = (ClassLoader) classLoaders.get(i);
            try {
                Class result = classLoader.loadClass(name);
                if (result != null) {
                    return result;
                }
            } catch (ClassNotFoundException e) {
                // continue...
            }
        }
        throw new ClassNotFoundException(name);
    }
}
