/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.reflection;

import org.nanocontainer.script.groovy.PicoBuilderException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */

public class DefaultReflectionContainerAdapter implements ReflectionContainerAdapter {
    private static final Map primitiveNameToBoxedName = new HashMap();
    private boolean componentClassLoaderLocked;
    private ClassLoader componentClassLoader;

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

    private final ClassLoader parentClassLoader;

    public DefaultReflectionContainerAdapter(ClassLoader classLoader, MutablePicoContainer picoContainer) {
        this.parentClassLoader = classLoader;
        this.picoContainer = picoContainer;
    }

    public DefaultReflectionContainerAdapter(ClassLoader classLoader) {
        this(classLoader, new DefaultPicoContainer());
    }

    public DefaultReflectionContainerAdapter(MutablePicoContainer picoContainer) {
        parentClassLoader = DefaultReflectionContainerAdapter.class.getClassLoader();
        this.picoContainer = picoContainer;
    }

    public DefaultReflectionContainerAdapter(ReflectionContainerAdapter parent) {
        parentClassLoader = parent.getComponentClassLoader();
        picoContainer = new DefaultPicoContainer(parent.getPicoContainer());
        parent.getPicoContainer().addChildContainer(picoContainer);
    }

    /**
     * Beware - no parent container and no parent classloader.
     */
    public DefaultReflectionContainerAdapter() {
        this(DefaultReflectionContainerAdapter.class.getClassLoader(), new DefaultPicoContainer());
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return picoContainer.registerComponentImplementation(loadClass(componentImplementationClassName));
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        Class componentImplementation = loadClass(componentImplementationClassName);
        return picoContainer.registerComponentImplementation(key, componentImplementation);
    }


    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, Parameter[] parameters) throws ClassNotFoundException {
        Class componentImplementation = loadClass(componentImplementationClassName);
        return picoContainer.registerComponentImplementation(key, componentImplementation, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Object key,
                                                            String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getComponentClassLoader().loadClass(componentImplementationClassName);
        return registerComponentImplementation(parameterTypesAsString, parameterValuesAsString, key, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getComponentClassLoader().loadClass(componentImplementationClassName);
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
        ClassLoader classLoader = getComponentClassLoader();
        return classLoader.loadClass(cn);
    }

    /**
     * Adds a new URL that will be used in classloading
     *
     * @param url
     */
    public void addClassLoaderURL(URL url) {
        if (componentClassLoaderLocked) throw new IllegalStateException("ClassLoader URLs cannot be added once this ContainerAdapter is locked");
        urls.add(url);
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            URL[] urlz = (URL[]) urls.toArray(new URL[urls.size()]);
            componentClassLoaderLocked = true;
            componentClassLoader = new DRCAClassLoader(urlz, parentClassLoader);
        }
        return componentClassLoader;
    }

    public MutablePicoContainer getPicoContainer() {
        return picoContainer;
    }

    public static class DRCAClassLoader extends URLClassLoader {
        public DRCAClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public String toString() {
            return "FCL(parent:" + (getParent() != null ? "" + System.identityHashCode(getParent()) : "x") + " - URLS(" + getPrettyURLs() + ")";
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            return super.loadClass(name);
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }

        private String getPrettyURLs() {
            String result = "";
            URL[] urls = getURLs();
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                result = result + url.toString() + "\n";
            }
            return result;
        }
    }

    public Object getComponentInstanceOfType(String componentType) {
        try {
            Class compType = getComponentClassLoader().loadClass(componentType);
            Object componentInstance = picoContainer.getComponentInstanceOfType(compType);
            return componentInstance;
        } catch (ClassNotFoundException e) {
            throw new PicoBuilderException("Can't resolve class as type '" + componentType + "'");
        }
    }

}
