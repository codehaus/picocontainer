/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb;

import org.picocontainer.PicoException;

import junit.framework.TestCase;


/**
 * Test the ServiceUnavailableException
 */
public class ServiceUnavailableExceptionTest extends TestCase {

    /**
     * Test the constructors
     */
    public void testConstructors() {
        final Throwable throwable = new Throwable();
        PicoException exception = new ServiceUnavailableException("junit", throwable);
        assertEquals("junit", exception.getMessage());
        assertSame(throwable, exception.getCause());
        exception = new ServiceUnavailableException(throwable);
        assertNull(exception.getMessage());
        assertSame(throwable, exception.getCause());
    }
}
