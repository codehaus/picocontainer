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

import java.net.URL;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.BeanPropertyComponentAdapter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.CustomPermissionsURLClassLoader;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * The default implementation of {@link NanoContainer}.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public class DefaultNanoContainer implements NanoContainer {
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

    private final List classPathElements = new ArrayList();
    private MutablePicoContainer picoContainer;
    private final ClassLoader parentClassLoader;

    private ClassLoader componentClassLoader;
    private boolean componentClassLoaderLocked;

    private static String getClassName(String primitiveOrClass) {
        String fromMap = (String) primitiveNameToBoxedName.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }

    public DefaultNanoContainer(ClassLoader parentClassLoader, MutablePicoContainer picoContainer) {
        this.parentClassLoader = parentClassLoader;
        if (picoContainer == null) {
            throw new NullPointerException("picoContainer");
        }
        this.picoContainer = picoContainer;
    }

    public DefaultNanoContainer(ClassLoader parentClassLoader) {
        this(parentClassLoader, new DefaultPicoContainer());
    }

    public DefaultNanoContainer(MutablePicoContainer picoContainer) {
        this(Thread.currentThread().getContextClassLoader(), picoContainer);
    }

    public DefaultNanoContainer(NanoContainer parent) {
        this(parent.getComponentClassLoader(), new DefaultPicoContainer(parent.getPico()));
    }

    /**
     * Beware - no parent container and no parent classloader.
     */
    public DefaultNanoContainer() {
        this(Thread.currentThread().getContextClassLoader(), new DefaultPicoContainer());
    }

    public ComponentAdapter registerComponent(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return picoContainer.registerComponent(loadClass(componentImplementationClassName));
    }

    public ComponentAdapter registerComponent(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        Class componentImplementation = loadClass(componentImplementationClassName);
        if (key instanceof ClassNameKey) {
            key = loadClass(((ClassNameKey) key).getClassName());
        }
        return picoContainer.registerComponent(key, componentImplementation);
    }


    public ComponentAdapter registerComponent(Object key, String componentImplementationClassName, Parameter[] parameters) throws ClassNotFoundException {
        Class componentImplementation = loadClass(componentImplementationClassName);
        if (key instanceof ClassNameKey) {
            key = loadClass(((ClassNameKey) key).getClassName());

        }
        return picoContainer.registerComponent(key, componentImplementation, parameters);
    }

    public ComponentAdapter registerComponent(Object key,
                                                            String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getComponentClassLoader().loadClass(componentImplementationClassName);
        if (key instanceof ClassNameKey) {
            key = loadClass(((ClassNameKey) key).getClassName());

        }
        return registerComponent(parameterTypesAsString, parameterValuesAsString, key, componentImplementation);
    }

    public ComponentAdapter registerComponent(String componentImplementationClassName,
                                                            String[] parameterTypesAsString,
                                                            String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getComponentClassLoader().loadClass(componentImplementationClassName);
        return registerComponent(parameterTypesAsString, parameterValuesAsString, componentImplementation, componentImplementation);
    }

    private ComponentAdapter registerComponent(String[] parameterTypesAsString, String[] parameterValuesAsString, Object key, Class componentImplementation) throws ClassNotFoundException {
        Parameter[] parameters = new Parameter[parameterTypesAsString.length];
        for (int i = 0; i < parameters.length; i++) {
            Object value = BeanPropertyComponentAdapter.convert(parameterTypesAsString[i], parameterValuesAsString[i], getComponentClassLoader());
            parameters[i] = new ConstantParameter(value);
        }
        return picoContainer.registerComponent(key, componentImplementation, parameters);
    }

    private Class loadClass(final String className) throws ClassNotFoundException {
        ClassLoader classLoader = getComponentClassLoader();
        String cn = getClassName(className);
        return classLoader.loadClass(cn);
    }

    public ClassPathElement addClassLoaderURL(URL url) {
        if (componentClassLoaderLocked) throw new IllegalStateException("ClassLoader URLs cannot be added once this instance is locked");

        ClassPathElement classPathElement = new ClassPathElement(url);
        classPathElements.add(classPathElement);
        return classPathElement;
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            componentClassLoaderLocked = true;
            componentClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new CustomPermissionsURLClassLoader(getURLs(classPathElements), makePermissions(), parentClassLoader);
                }
            });
        }
        return componentClassLoader;
    }

    public MutablePicoContainer getPico() {
        return picoContainer;
    }

    private Map makePermissions() {
        Map permissionsMap = new HashMap();
        for (int i = 0; i < classPathElements.size(); i++) {
            ClassPathElement cpe = (ClassPathElement) classPathElements.get(i);
            PermissionCollection permissionCollection = cpe.getPermissionCollection();
            permissionsMap.put(cpe.getUrl(), permissionCollection);
        }
        return permissionsMap;
    }

    private URL[] getURLs(List classPathElemelements) {
        final URL[] urls = new URL[classPathElemelements.size()];
        for(int i = 0; i < urls.length; i++) {
            urls[i] = ((ClassPathElement) classPathElemelements.get(i)).getUrl();
        }
        return urls;
    }

    public Object getComponent(String componentType) {
        try {
            Class compType = getComponentClassLoader().loadClass(componentType);
            return picoContainer.getComponent(compType);
        } catch (ClassNotFoundException e) {
            // ugly hack as Rhino cannot differentiate between getComponent(String) and getComponent(Object) and
            // it is likely that there is no way to cast between the two.
            // moot if we merge all the getComponent() classes ?
            Object retval = picoContainer.getComponent(componentType);
            if (retval == null) {
                throw new NanoContainerMarkupException("Can't resolve class as type '" + componentType + "'");
            } else {
                return retval;
            }
        }
    }

    public MutablePicoContainer addDecoratingPicoContainer(Class picoContainerClass) {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(MutablePicoContainer.class, picoContainerClass, new Parameter[] { new ConstantParameter(picoContainer) });
        picoContainer = (MutablePicoContainer) pico.getComponent(MutablePicoContainer.class);
        return picoContainer;
    }

}
