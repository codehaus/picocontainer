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

public interface LifecycleContainer extends PicoContainer {

    /**
     * Instantiates (if not done already) and starts all
     * registered components.
     *
     * @throws PicoStartException if one or more components couldn't be instantiated/started.
     */
    void start() throws PicoStartException;

    /**
     * stops all applicable registered components.
     *
     * @throws PicoStopException if one or more components couldn't be started.
     */
    void stop() throws PicoStopException;

    /**
     * disposes of all registered components.
     *
     * @throws PicoDisposalException if one or more components couldn't be disposed of.
     */
    void dispose() throws PicoDisposalException;

}
