/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.junit.Test;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoException;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.monitors.AbstractComponentMonitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the several PicoException classes.
 */
@SuppressWarnings("serial")
public class PicoExceptionsTestCase {

    final static public String MESSAGE = "Message of the exception";
    final static public Throwable THROWABLE = new Throwable();

    @SuppressWarnings({ "unchecked" })
    final void executeTestOfStandardException(final Class clazz) {
        final ComponentAdapter componentAdapter = new ConstructorInjector(clazz, clazz, null, new AbstractComponentMonitor(), false, false);
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(MESSAGE);
        Exception exception = (Exception) componentAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
            assertEquals(MESSAGE, exception.getMessage());
        pico = new DefaultPicoContainer();
        pico.addComponent(THROWABLE);
        exception = (PicoException) componentAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
        assertSame(THROWABLE, exception.getCause());
        pico.addComponent(MESSAGE);
        exception = (PicoException) componentAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
        assertEquals(MESSAGE, exception.getMessage());
        assertSame(THROWABLE, exception.getCause());
    }

    @Test public void testPicoInitializationException() {
        executeTestOfStandardException(PicoCompositionException.class);
    }

    @Test public void testPicoInitializationExceptionWithDefaultConstructor() {
        TestException e = new TestException(null);
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    private static class TestException extends PicoCompositionException {
        public TestException(final String message) {
            super(message);
        }
    }

    @Test public void testPrintStackTrace() throws IOException {
        PicoException nestedException = new PicoException("Outer", new Exception("Inner")) {
        };
        PicoException simpleException = new PicoException("Outer") {
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        nestedException.printStackTrace(printStream);
        simpleException.printStackTrace(printStream);
        out.close();
        assertTrue(out.toString().indexOf("Caused by:") > 0);
        out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        nestedException.printStackTrace(writer);
        simpleException.printStackTrace(writer);
        writer.flush();
        out.close();
        assertTrue(out.toString().indexOf("Caused by:") > 0);
        //simpleException.printStackTrace();
    }
}
