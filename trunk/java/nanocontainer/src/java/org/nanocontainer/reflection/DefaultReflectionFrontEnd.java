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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class is a front-end to {@link MutablePicoContainer} that uses reflection
 * to register components.
 * <p/>
 *
 * @author Aslak Helles&oslash;y
 */
public class DefaultReflectionFrontEnd implements ReflectionFrontEnd {
    private static final Map primitiveNameToBoxedName = new HashMap();
    static {
        primitiveNameToBoxedName.put("int", Integer.class.getName());
        primitiveNameToBoxedName.put("byte", Byte.class.getName());
        primitiveNameToBoxedName.put("short", Short.class.getName());
        primitiveNameToBoxedName.put("long", Long.class.getName());
        primitiveNameToBoxedName.put("float", Float.class.getName());
        primitiveNameToBoxedName.put("double", Double.class.getName());
        primitiveNameToBoxedName.put("boolean", Boolean.class.getName());
    }
    private static String getClassName(String primitiveOrClass) {
        String fromMap = (String) primitiveNameToBoxedName.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }

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
        this(classLoader,
                new DefaultPicoContainer());
    }

    public DefaultReflectionFrontEnd(MutablePicoContainer picoContainer) {
        this((DefaultReflectionFrontEnd) null,
                picoContainer);
    }

    public DefaultReflectionFrontEnd(ReflectionFrontEnd parent, MutablePicoContainer picoContainer) {
        this.parent = parent;
        this.picoContainer = picoContainer;
        if (parent != null) {
            MutablePicoContainer parentContainer = parent.getPicoContainer();
            picoContainer.setParent(parentContainer);
        }
    }

    public DefaultReflectionFrontEnd(ReflectionFrontEnd parent) {
        this(parent, new DefaultPicoContainer());
    }

    public DefaultReflectionFrontEnd() {
        this((DefaultReflectionFrontEnd) null,
                new DefaultPicoContainer());
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return picoContainer.registerComponentImplementation(loadClass(componentImplementationClassName));
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        Class componentImplementation = loadClass(componentImplementationClassName);
        return picoContainer.registerComponentImplementation(key, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object key,
                                                            String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getClassLoader().loadClass(componentImplementationClassName);
        return registerComponentImplementation(parameterTypesAsString, parameterValuesAsString, key, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getClassLoader().loadClass(componentImplementationClassName);
        return registerComponentImplementation(parameterTypesAsString, parameterValuesAsString, componentImplementation, componentImplementation);
    }

    private ComponentAdapter registerComponentImplementation(String[] parameterTypesAsString, String[] parameterValuesAsString, Object key, Class componentImplementation) throws ClassNotFoundException {
        Parameter[] parameters = new Parameter[parameterTypesAsString.length];
        for (int i = 0; i < parameters.length; i++) {
            Class paramTypeClass = loadClass(parameterTypesAsString[i]);
            Object value = converter.convertTo(paramTypeClass, parameterValuesAsString[i]);
            parameters[i] = new ConstantParameter(value);
        }
        return picoContainer.registerComponentImplementation(key, componentImplementation, parameters);
    }

    private Class loadClass(final String componentImplementationClassName) throws ClassNotFoundException {
        String cn = getClassName(componentImplementationClassName);
        return getClassLoader().loadClass(cn);
    }

    /**
     * Sets what classloader to use. This will reset all previously set URLs.
     * This overrides the ClassLoaders that may have been set by addClassLoaderURL(..)
     * 
     * @param classLoader 
     * @see #addClassLoaderURL
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        urls.clear();
    }

    /**
     * Adds a new URL.
     * This overrides the ClassLoader that may have been set by setClassLoader(..)
     * 
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
