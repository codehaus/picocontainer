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
 * Interface for disposable Pico components.
 * This interface is part of the standard lifecycle of a 
 * {@link org.picocontainer.defaults.DefaultPicoContainer}. The compontent's
 * method {@link Disposable#dispose()} is called directly after
 * {@link Startable#stop()} (if implemented). 
 * 
 * @since 1.0
 * @version $Revision$
 */
public interface Disposable {
    /**
     * Dispose this component.
     * The contract for this method defines a single call at the end of this component's
     * life.
     */
    void dispose();
}
