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
// TODO: move to org.nanocontainer.script.reflection ?
// TODO - Naaah - Paul

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;

import java.net.URL;

/**
 * This class adapts a {@link MutablePicoContainer} through a similar API that
 * is based only on Strings. (It uses reflection to look up classes before registering them
 * with the adapted container). This adapter API is used primarily by the various scripting
 * based {@link org.nanocontainer.integrationkit.ContainerComposer}s in the org.nanocontainer.script
 * package.
 */
public interface ReflectionContainerAdapter {

    ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException;

    ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException;

    ComponentAdapter registerComponentImplementation(Object key,
                                                     String componentImplementationClassName,
                                                     String[] parameterTypesAsString,
                                                     String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException;

    ComponentAdapter registerComponentImplementation(String componentImplementationClassName,
                                                     String[] parameterTypesAsString,
                                                     String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException;

    /**
     * Sets what classloader to use. This will reset all previously set URLs.
     * This overrides the ClassLoaders that may have been set by addClassLoaderURL(..)
     *
     * @param classLoader
     * @see #addClassLoaderURL
     */
    void setClassLoader(ClassLoader classLoader);

    /**
     * Adds a new URL.
     * This overrides the ClassLoader that may have been set by setClassLoader(..)
     *
     * @param url
     */
    void addClassLoaderURL(URL url);

    MutablePicoContainer getPicoContainer();

    ClassLoader getComponentClassLoader();

    /**
     * Find a component instance matching the specified type.
     *
     * @param componentType the type of the component.
     * @return the adapter matching the class.
     */
    Object getComponentInstanceOfType(String componentType);


}
