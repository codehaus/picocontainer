/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer;

/**
 * This class provides control over the arguments that will be passed to a constructor. It can be used for finer control
 * over what arguments are passed to a particular constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Thomas Heller
 * @see MutablePicoContainer#registerComponentImplementation(Object,Class,Parameter[]) a method on the {@link
 *      MutablePicoContainer} interface which allows passing in of an array of <code>Parameter</code>s.
 * @see org.picocontainer.defaults.ComponentParameter an implementation of this interface that allows you to specify the
 *      key used for resolving the parameter.
 * @see org.picocontainer.defaults.ConstantParameter an implementation of this interface that allows you to specify a
 *      constant that will be used for resolving the parameter.
 * @since 1.0
 */
public interface Parameter {
    /**
     * Retrieve the component parameter that statisfies the expected type.
     *
     * @param container the container from which dependencies are resolved.
     * @param adapter the ComponentAdapter that is asking for the instance
     * @param expectedType  the type that the returned instance needs to match.
     * @return the instance or <code>null</code> if no suitable instance can be found.
     * @throws PicoInitializationException if a referenced component could not be instantiated.
     * @since 1.1
     */
    Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoInitializationException;

    /**
     * Check wether the given Parameter can be statisfied by the container
     *
     * @param container the container from which dependencies are resolved.
     * @param adapter     the container that should be searched
     * @param expectedType the required type
     * @return <code>true</code> if the component parameter can be resolved.
     * @since 1.1
     */
    boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType);

    /**
     * Verify that the given Parameter can be statisfied by the container
     *
     * @param container the container from which dependencies are resolved.
     * @param adapter     the container that should be searched
     * @param expectedType the required type
     * @throws PicoVerificationException if parameter and its dependencies cannot be resolved
     * @since 1.1
     */
    void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoVerificationException;

    /**
     * Accepts a visitor for this Parameter. The method is normally called by visiting a {@link ComponentAdapter}, that 
     * cascades the visitor also down to all its Parameters.
     * 
     * @param visitor the visitor.
     * @since 1.1
     */
    void accept(PicoVisitor visitor);
}
