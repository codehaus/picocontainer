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
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.NullContainer;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.io.Serializable;

public class StringRegistrationNanoContainerImpl implements StringRegistrationNanoContainer, Serializable {

    private final RegistrationPicoContainer picoContainer;
    private ArrayList classLoaders = new ArrayList();
    private StringToObjectConverter converter;

    public StringRegistrationNanoContainerImpl(PicoContainer parentContainer, ClassLoader classLoader, StringToObjectConverter converter) {
        this.picoContainer = makePicoContainer(parentContainer);
        if (classLoader != null) {
            classLoaders.add(classLoader);
        }
        this.converter = converter;
    }

    protected RegistrationPicoContainer makePicoContainer(PicoContainer parentContainer) {
        return new HierarchicalPicoContainer.WithParentContainer(parentContainer);
    }

    public static class Default extends StringRegistrationNanoContainerImpl {
        public Default() {
            super(new NullContainer(), StringRegistrationNanoContainerImpl.class.getClassLoader(), new StringToObjectConverter());
        }
    }

    public static class WithParentContainer extends StringRegistrationNanoContainerImpl {
        public WithParentContainer(PicoContainer parentContainer) {
            super(parentContainer, StringRegistrationNanoContainerImpl.class.getClassLoader(), new StringToObjectConverter());
        }
    }

    public static class WithClassLoader extends StringRegistrationNanoContainerImpl {
        public WithClassLoader(ClassLoader classLoader) {
            super(new NullContainer(), classLoader, new StringToObjectConverter());
        }
    }

    public void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        picoContainer.registerComponentByClass(StringRegistrationNanoContainerImpl.class.getClassLoader().loadClass(compClassName));
    }

    public void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class typeClass = loadClass(typeClassName);
        Class compClass = loadClass(compClassName);
        picoContainer.registerComponent(typeClass, compClass);
    }

    public void addParameterToComponent(String compClassName, String paramClassName, String valueAsString) throws ClassNotFoundException, PicoIntrospectionException {
        Class compClass = loadClass(compClassName);
        Class paramClass = loadClass(paramClassName);

        Object value = converter.convertTo(paramClass, valueAsString);

        picoContainer.addParameterToComponent(compClass, paramClass, value);

    }

    public void instantiateComponents() throws PicoInitializationException {
        picoContainer.instantiateComponents();
    }

    public boolean hasComponent(Object clazz) {
        return picoContainer.hasComponent(clazz);
    }

    public Object getComponent(Object clazz) {
        return picoContainer.getComponent(clazz);
    }

    public Set getComponents() {
        return picoContainer.getComponents();
    }

    public Object getCompositeComponent()
    {
        return picoContainer.getCompositeComponent();
    }

    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
    {
        return picoContainer.getCompositeComponent(callInInstantiationOrder, callUnmanagedComponents);
    }

    public Set getComponentKeys() {
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
