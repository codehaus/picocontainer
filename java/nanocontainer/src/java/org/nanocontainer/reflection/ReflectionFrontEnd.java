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
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;

import java.net.URL;

/**
 * This class is a front-end to {@link MutablePicoContainer} that uses reflection
 * to register components.
 *
 * This class replaces the former StringRegistrationNanoContainer.
 */
public interface ReflectionFrontEnd {

    void registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException;

    void registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException;

    void registerComponentImplementation(
            Object key,
            String componentImplementationClassName,
            String[] parameterTypesAsString,
            String[] parameterValuesAsString
            ) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException;

    /**
     * Sets what classloader to use. This will reset all previously set URLs.
     * This overrides the ClassLoaders that may have been set by addClassLoaderURL(..)
     * @see #addClassLoaderURL
     * @param classLoader
     */
    void setClassLoader(ClassLoader classLoader);

    /**
     * Adds a new URL.
     * This overrides the ClassLoader that may have been set by setClassLoader(..)
     * @param url
     */
    void addClassLoaderURL(URL url);

    MutablePicoContainer getPicoContainer();
    
    ClassLoader getClassLoader();
}
