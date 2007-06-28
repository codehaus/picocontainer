/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer;

/**
 * An interface which is implemented by components that need to dispose of resources during the shutdown of that
 * component. The {@link Disposable#dispose()} must be called once during shutdown, directly after {@link
 * Startable#stop()} (if the component implements the {@link Startable} interface).
 * @version $Revision$
 * @see org.picocontainer.Startable the Startable interface if you need to <code>start()</code> and
 *      <code>stop()</code> semantics.
 * @see org.picocontainer.PicoContainer the main PicoContainer interface (and hence its subinterfaces and
 *      implementations like {@link org.picocontainer.defaults.DefaultPicoContainer}) implement this interface.
 * @since 1.0
 */
public interface Disposable {
    /**
     * Dispose this component. The component should deallocate all resources. The contract for this method defines a
     * single call at the end of this component's life.
     */
    void dispose();
}
