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
 * Basic lifecycle interface for Pico components. For more advanced and pluggable lifecycle
 * support, see the functionality offered by the nanocontainer-multicast subproject.
 */
public interface Startable {
    void start();
    void stop();
}
