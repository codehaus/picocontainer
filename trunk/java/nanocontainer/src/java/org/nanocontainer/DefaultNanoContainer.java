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
import org.nanocontainer.script.NanoContainerMarkupException;
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
public class DefaultNanoContainer implements NanoContainer {
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

    public DefaultNanoContainer(ClassLoader parentClassLoader, MutablePicoContainer picoContainer) {
        this.parentClassLoader = parentClassLoader;
        if (picoContainer == null) {
            throw new NullPointerException("picoContainer");
        }
        this.picoContainer = picoContainer;
    }

    public DefaultNanoContainer(ClassLoader classLoader) {
        this(classLoader, new DefaultPicoContainer());
    }

    public DefaultNanoContainer(MutablePicoContainer picoContainer) {
        this(DefaultNanoContainer.class.getClassLoader(), picoContainer);
    }

    public DefaultNanoContainer(NanoContainer parent) {
        this(parent.getComponentClassLoader(), new DefaultPicoContainer(parent.getPico()));
    }

    /**
     * Beware - no parent container and no parent classloader.
     */
    public DefaultNanoContainer() {
        this(DefaultNanoContainer.class.getClassLoader(), new DefaultPicoContainer());
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
            componentClassLoader = new URLPrintingClassLoader(urlz, parentClassLoader);
        }
        return componentClassLoader;
    }

    public MutablePicoContainer getPico() {
        return picoContainer;
    }

    public static class URLPrintingClassLoader extends URLClassLoader {
        public URLPrintingClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException(getPrettyURLs(), e);
            }
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException(getPrettyURLs(), e);
            }
        }

        private String getPrettyURLs() {
            String result = "Classloader URLs (classpath):\n";
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
            throw new NanoContainerMarkupException("Can't resolve class as type '" + componentType + "'");
        }
    }

}
