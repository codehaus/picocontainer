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

import picocontainer.PicoRegistrationException;
import picocontainer.PicoContainer;

public interface StringRegistrationNanoContainer extends PicoContainer {


    /**
     * Register a component by its classname.
     * @param compClassName The class name (fully qualified) for the component.
     * @throws PicoRegistrationException If a problem registereing the component.
     * @throws ClassNotFoundException If th eclass could npot be found in any
     * visible classloader.
     */
    void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException;

    /**
     * Register a component by the clasnames of its type and implementation.
     * @param typeClassName The classname of the component type.
     * @param compClassName The class name (fully qualified) for the component.
     * @throws PicoRegistrationException If a problem registereing the component.
     * @throws ClassNotFoundException If th eclass could npot be found in any
     * visible classloader.
     */
    void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException;

    /**
     * (Subject to change ... Joe? )
     * @param compClassName
     * @param paramClassName
     * @param value
     * @throws ClassNotFoundException
     */
    void addParameterToComponent(String compClassName, String paramClassName, String value) throws ClassNotFoundException;

    /**
     * Used for classloader juggling. The container will look in each classloader in
     * turn to look for classes it wants to instnatiate.
     *
     * This is entirely option as the classloader that the
     * StringRegistrationNanoContainer class is in automatically in the
     * list of classloaders to iterate through. As are all parent containers
     * of course.
     *
     * It is important to point out that one component in say classloader A will
     * not be able to depend on another component is say classloader B if the
     * componentType (and associated classes mentioned directly in that interface)
     * are not mutually visible.  I.e. in a classloader that is visible to both.
     * Thus, if you are going to this level of effort, you might want to separate
     * classes in jars to such an extent that the classes pertaining to type are
     * in a jar/clasloader of their own and already mounted in the  scope of
     * this container. Mounted by the parent pico container perhaps (if
     * applicable)
     *
     * You may use this if you are doing separation of components
     * for security and management reasons.
     *
     * @param classLoader The classloader to add.
     */
    void addComponentClassLoader(ClassLoader classLoader);



}
