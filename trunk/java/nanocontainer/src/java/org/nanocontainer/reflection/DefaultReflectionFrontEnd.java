/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.reflection;

import org.picocontainer.*;
import org.picocontainer.defaults.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class is a front-end to {@link MutablePicoContainer} that uses reflection
 * to register components.
 *
 * This class replaces the former StringRegistrationNanoContainer.
 */
public class DefaultReflectionFrontEnd implements ReflectionFrontEnd {
    private final List urls = new ArrayList();

    private final StringToObjectConverter converter = new StringToObjectConverter();

    private final MutablePicoContainer picoContainer;

    // Either supplied specifically or built up by added URLs.
    private ClassLoader classLoader;
    private ReflectionFrontEnd parent;

    public DefaultReflectionFrontEnd(ClassLoader classLoader, MutablePicoContainer picoContainer) {
        this.classLoader = classLoader;
        this.picoContainer = picoContainer;
    }

    public DefaultReflectionFrontEnd(ClassLoader classLoader) {
        this(
                classLoader,
                new DefaultPicoContainer()
        );
    }

    public DefaultReflectionFrontEnd(MutablePicoContainer picoContainer) {
        this(
                (DefaultReflectionFrontEnd) null,
                picoContainer
        );
    }

    public DefaultReflectionFrontEnd(ReflectionFrontEnd parent, MutablePicoContainer picoContainer) {
        this.parent = parent;
        this.picoContainer = picoContainer;
        if (parent != null) {
            MutablePicoContainer parentContainer = parent.getPicoContainer();
            picoContainer.addParent(parentContainer);
        }
    }

    public DefaultReflectionFrontEnd(ReflectionFrontEnd parent) {
        this(parent, new DefaultPicoContainer());
    }

    public DefaultReflectionFrontEnd() {
        this(
                (DefaultReflectionFrontEnd) null,
                new DefaultPicoContainer()
        );
    }

    public void registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        picoContainer.registerComponentImplementation(getClassLoader().loadClass(componentImplementationClassName));
    }

    public void registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        Class componentImplementation = getClassLoader().loadClass(componentImplementationClassName);
        picoContainer.registerComponentImplementation(key, componentImplementation);
    }

    public void registerComponentImplementation(
            Object key,
            String componentImplementationClassName,
            String[] parameterTypesAsString,
            String[] parameterValuesAsString
            ) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getClassLoader().loadClass(componentImplementationClassName);
        Parameter[] parameters = new Parameter[parameterTypesAsString.length];
        for (int i = 0; i < parameters.length; i++) {
            Class paramTypeClass = getClassLoader().loadClass(parameterTypesAsString[i]);
            Object value = converter.convertTo(paramTypeClass, parameterValuesAsString[i]);
            parameters[i] = new ConstantParameter(value);
        }
        picoContainer.registerComponentImplementation(key, componentImplementation, parameters);
    }

    /**
     * Sets what classloader to use. This will reset all previously set URLs.
     * This overrides the ClassLoaders that may have been set by addClassLoaderURL(..)
     * @see #addClassLoaderURL
     * @param classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        urls.clear();
    }

    /**
     * Adds a new URL.
     * This overrides the ClassLoader that may have been set by setClassLoader(..)
     * @param url
     */
    public void addClassLoaderURL(URL url) {
        classLoader = null;
        urls.add(url);
    }

    private ClassLoader getParentClassLoader() {
        if (parent != null) {
            return parent.getClassLoader();
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            URL[] urlz = (URL[]) urls.toArray(new URL[urls.size()]);
            classLoader = new URLClassLoader(urlz, getParentClassLoader());
        }
        return classLoader;
    }

    public MutablePicoContainer getPicoContainer() {
        return picoContainer;
    }
}
