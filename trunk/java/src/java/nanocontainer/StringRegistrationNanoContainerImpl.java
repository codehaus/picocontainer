/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import picocontainer.PicoContainer;
import picocontainer.Container;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;
import picocontainer.HierarchicalPicoContainer;
import picocontainer.PicoStopException;
import picocontainer.NullContainer;
import java.util.ArrayList;

import nanocontainer.reflection.StringToObjectConverter;

public class StringRegistrationNanoContainerImpl implements StringRegistrationNanoContainer {

    private final PicoContainer picoContainer;
    private ArrayList classLoaders = new ArrayList();
    private StringToObjectConverter converter;

    public StringRegistrationNanoContainerImpl(Container parentContainer, ClassLoader classLoader, StringToObjectConverter converter) {
        this.picoContainer = makePicoContainer(parentContainer);
        if (classLoader != null) {
            classLoaders.add(classLoader);
        }
        this.converter = converter;
    }

    protected PicoContainer makePicoContainer(Container parentContainer) {
        return new HierarchicalPicoContainer.WithParentContainer(parentContainer);
    }

    public static class Default extends StringRegistrationNanoContainerImpl {
        public Default() {
            super(new NullContainer(), StringRegistrationNanoContainerImpl.class.getClassLoader(), new StringToObjectConverter());
        }
    }

    public static class WithParentContainer extends StringRegistrationNanoContainerImpl {
        public WithParentContainer(Container parentContainer) {
            super(parentContainer, StringRegistrationNanoContainerImpl.class.getClassLoader(), new StringToObjectConverter());
        }
    }

    public static class WithClassLoader extends StringRegistrationNanoContainerImpl {
        public WithClassLoader(ClassLoader classLoader) {
            super(new NullContainer(), classLoader, new StringToObjectConverter());
        }
    }

    public void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException {
        picoContainer.registerComponent(StringRegistrationNanoContainerImpl.class.getClassLoader().loadClass(compClassName));
    }

    public void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException {
        Class typeClass = loadClass(typeClassName);
        Class compClass = loadClass(compClassName);
        picoContainer.registerComponent(typeClass, compClass);
    }

    public void addParameterToComponent(String compClassName, String paramClassName, String valueAsString) throws ClassNotFoundException {
        Class compClass = loadClass(compClassName);
        Class paramClass = loadClass(paramClassName);

        Object value = converter.convertTo(paramClass, valueAsString);

        picoContainer.addParameterToComponent(compClass, paramClass, value);

    }

    public void start() throws PicoStartException {
        picoContainer.start();
    }

    public void stop() throws PicoStopException {
        picoContainer.stop();
    }

    public boolean hasComponent(Class clazz) {
        return picoContainer.hasComponent(clazz);
    }

    public Object getComponent(Class clazz) {
        return picoContainer.getComponent(clazz);
    }

    public Object[] getComponents() {
        return picoContainer.getComponents();
    }

    public Class[] getComponentTypes() {
        return picoContainer.getComponentTypes();
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
