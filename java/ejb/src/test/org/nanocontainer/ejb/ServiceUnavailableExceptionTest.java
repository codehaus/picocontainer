/*
 * Copyright (C) 2004 Elsag-Solutions AG.
 * Created on 30.11.2004 by Jörg Schaible.
 */
package org.nanocontainer.ejb;

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
