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
 * Basic lifecycle interface for Pico components implemented by the 
 * {@link org.picocontainer.defaults.DefaultPicoContainer}.
 * 
 * <p>Components implementing this interface can be started and stopped. The
 * contract defines, that the methods can only be called in turn.</p>  
 * 
 * <p>For more advanced and pluggable lifecycle support, see the functionality 
 * offered by the nanocontainer-multicast subproject.</p>
 * 
 * @since 1.0
 * @version $Revision$
 */
public interface Startable {
    /**
     * Start this component.
     * Called initially at the begin of the lifecycle and can be called again after
     * a stop.
     */
    void start();
    /**
     * Stop this component.
     * Called to the end the lifecycle and can be called again after
     * a further start. Implement {@link Disposable} if you need a single call at
     * the definite end of the lifecycle. 
     */
    void stop();
}
