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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.adapters.ConstructorInjectionAdapter;

/**
 * Unit tests for the several PicoException classes.
 */
public class PicoExceptionsTestCase
        extends TestCase {

    final static public String MESSAGE = "Message of the exception";
    final static public Throwable THROWABLE = new Throwable();

    final void executeTestOfStandardException(final Class clazz) {
        final ComponentAdapter componentAdapter = new ConstructorInjectionAdapter(clazz, clazz, null, new DelegatingComponentMonitor());
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(MESSAGE);
        try {
            final Exception exception = (Exception) componentAdapter.getComponentInstance(pico);
            assertEquals(MESSAGE, exception.getMessage());
        } catch (final UnsatisfiableDependenciesException ex) {
            final Set set = new HashSet();
            for (final Iterator iter = ex.getUnsatisfiableDependencies().iterator(); iter.hasNext();) {
                final List list = (List) iter.next();
                set.addAll(list);
            }
            assertTrue(set.contains(Throwable.class));
        }
        pico = new DefaultPicoContainer();
        pico.addComponent(THROWABLE);
        try {
            final PicoException exception = (PicoException) componentAdapter.getComponentInstance(pico);
            assertSame(THROWABLE, exception.getCause());
        } catch (final UnsatisfiableDependenciesException ex) {
            final Set set = new HashSet();
            for (final Iterator iter = ex.getUnsatisfiableDependencies().iterator(); iter.hasNext();) {
                final List list = (List) iter.next();
                set.addAll(list);
            }
            assertTrue(set.contains(String.class));
        }
        pico.addComponent(MESSAGE);
        final PicoException exception = (PicoException) componentAdapter.getComponentInstance(pico);
        assertEquals(MESSAGE, exception.getMessage());
        assertSame(THROWABLE, exception.getCause());
    }

    public void testPicoInitializationException() {
        executeTestOfStandardException(PicoInitializationException.class);
    }

    public void testPicoInitializationExceptionWithDefaultConstructor() {
        TestException e = new TestException(null);
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }
    
    private static class TestException extends PicoInitializationException {
        public TestException(final String message) {
            super(message);
        }
    }

    public void testPicoIntrospectionException() {
        executeTestOfStandardException(PicoIntrospectionException.class);
    }
    
    public void testPicoRegistrationException() {
        executeTestOfStandardException(PicoRegistrationException.class);
    }

    public void testCyclicDependencyException() {
        final CyclicDependencyException cdEx = new CyclicDependencyException(getClass());
        cdEx.push(String.class);
        final Class[] classes = cdEx.getDependencies();
        assertEquals(2, classes.length);
        assertSame(getClass(), classes[0]);
        assertSame(String.class, classes[1]);
        assertTrue(cdEx.getMessage().indexOf(getClass().getName()) >= 0);
    }

    public void testPrintStackTrace() throws IOException {
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
