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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.sql.SQLException;

import picocontainer.testmodel.UnaccessibleStartComponent;
import picocontainer.PicoInvocationTargetStartException;

public class ReflectionUsingLifecycleManagerTestCase extends TestCase {

    public void testStartInvocation() {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        try {
            lm.startComponent(new Object() {
                public void start() {
                    messages.add("started");
                }
            });
        } catch (PicoStartException e) {
            fail("Should have started just fine");
        }

        assertEquals(1, messages.size());
        assertEquals("started", messages.get(0));
    }

    public void testFailingStartInvocation() throws PicoStartException {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        try {
            lm.startComponent(new Object() {
                public void start() throws SQLException {
                    throw new SQLException();
                }
            });
            fail("Should have barfed");
        } catch (PicoInvocationTargetStartException e) {
            assertEquals(SQLException.class, e.getCause().getClass());
            // expected
        }
    }

    public void testStopInvocation() {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        try {
            lm.stopComponent(new Object() {
                public void stop() {
                    messages.add("stopped");
                }
            });
        } catch (PicoStopException e) {
            fail("Should have stopped just fine");
        }

        assertEquals(1, messages.size());
        assertEquals("stopped", messages.get(0));
    }

    public void testFailingStopInvocation() throws PicoStopException {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        try {
            lm.stopComponent(new Object() {
                public void stop() throws SQLException {
                    throw new SQLException("Hello");
                }
            });
            fail("Should have barfed");
        } catch (PicoInvocationTargetStopException e) {
            assertEquals(SQLException.class, e.getCause().getClass());
            assertTrue(e.getMessage().indexOf("SQLException") > 0 );
            assertTrue(e.getMessage().indexOf("Hello") > 0 );
            // expected
        }
    }

    public void testPrivateStartInvocation() throws PicoStartException {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        lm.startComponent(new UnaccessibleStartComponent(messages));
        assertEquals("Should not have started", 0, messages.size());

    }

    public void testNonPublicStopInvocation() throws PicoStartException {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        lm.startComponent(new Object() {
            protected void stop() {
                messages.add("stopped");
            }
        });
        assertEquals(0, messages.size());

    }

    public void testNoStartOrStopInvocation() {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        try {
            lm.startComponent(new Object());
            lm.stopComponent(new Object());
        } catch (PicoStartException e) {
            fail("Should have started just fine");
        } catch (PicoStopException e) {
            fail("Should have stopped just fine");
        }
    }

    public void testInvocationTargetBasics() {
        try {
            new PicoInvocationTargetStartException(null);
        } catch (IllegalArgumentException iae){
            // expected
        }
        try {
            new PicoInvocationTargetStopException(null);
        } catch (IllegalArgumentException iae){
            // expected
        }

    }


}
