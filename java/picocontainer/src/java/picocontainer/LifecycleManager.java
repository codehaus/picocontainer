/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

public interface LifecycleManager {

    /**
     * Conceptually start a component.  This is called by the PicoContainer
     * to indicate that a component may want to start its daemonized state.
     *
     * This may include lauching a background thread (via new thread,
     * or from a thread pool), or opening of a socket listener.
     *
     * What characterizes 'start' on a component is left to the component
     * implementor. The presence of a 'void start()' method is fine for
     * ReflectionUsingLifecycleManager, but other implementations of
     * this interface may choose harder contracts like if the components
     * implemented say some Startable interface.
     *
     * @param component The component to start
     * @throws PicoStartException if there was a problem with starting
     * the component
     */
    void startComponent(Object component) throws PicoStartException;

    /**
     * Conceptually stop a component.  This is called by the PicoContainer
     * to indicate that a component may want to stop its daemonized state.
     *
     * This may include ending of a background thread, or closing of a 
     * socket listener.
     *
     * See above for characterizations
     *
     * @param component The component to stop
     * @throws PicoStopException if there was a problem with stopping
     * the component
     */
    void stopComponent(Object component) throws PicoStopException;

    /**
     * Conceptually dispose of a component.  This is called by the PicoContainer
     * to indicate a component is (or at least should be) now dead.
     *
     * See above for characterizations
     *
     * @param component The component to dispose of
     * @throws PicoDisposalException if there was a problem with stopping
     * the component
     */
    void disposeOfComponent(Object component) throws PicoDisposalException;
}
