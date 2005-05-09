/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.TooManySatisfiableConstructorsException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractMultipleConstructorTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    public static class Multi {
        public String message;

        public Multi(One one, Two two, Three three) {
            message = "one two three";
        }

        public Multi(One one, Two two) {
            message = "one two";
        }

        public Multi(Two two, One one) {
            message = "two one";
        }

        public Multi(Two two, Three three) {
            message = "two three";
        }

        public Multi(Three three, One one) {
            message = "three one";
        }

        public Multi(One one, String string) {
            message = "one string";
        }

        public Multi(One one, int i) {
            message = "one int";
        }

        public Multi() {
            message = "none";
        }
    }

    public static class One {
    }

    public static class Two {
    }

    public static class Three {
    }


    public void testStringWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(String.class);
        assertEquals("", pico.getComponentInstance(String.class));
    }

    public void testMultiWithOnlySmallSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);

        Multi multi = (Multi) pico.getComponentInstance(Multi.class);
        assertEquals("three one", multi.message);
    }

    public void testMultiWithBothSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(Three.class);

        Multi multi = (Multi) pico.getComponentInstance(Multi.class);
        assertEquals("one two three", multi.message);
    }

    public void testMultiWithTwoEquallyBigSatisfiedDependenciesFails() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Two.class);

        try {
            pico.getComponentInstance(Multi.class);
            fail();
        } catch (TooManySatisfiableConstructorsException e) {
            assertTrue(e.getMessage().indexOf("Three") == -1);
            assertEquals(3, e.getConstructors().size());
            assertEquals(Multi.class, e.getForImplementationClass());
        }
    }

    public void testMultiWithSatisfyingDependencyAndParametersWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation("MultiOneTwo", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ComponentParameter("Two"),
        });
        pico.registerComponentImplementation("MultiTwoOne", Multi.class, new Parameter[]{
            new ComponentParameter("Two"),
            ComponentParameter.DEFAULT,
        });
        pico.registerComponentImplementation("MultiOneString", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ConstantParameter(""),
        });
        pico.registerComponentImplementation("MultiOneInt", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ConstantParameter(new Integer(5)),
        });
        pico.registerComponentImplementation("MultiNone", Multi.class, new Parameter[]{});
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation("Two", Two.class);

        Multi multiOneTwo = (Multi) pico.getComponentInstance("MultiOneTwo");
        assertEquals("one two", multiOneTwo.message);
        Multi multiTwoOne = (Multi) pico.getComponentInstance("MultiTwoOne");
        assertEquals("two one", multiTwoOne.message);
        Multi multiOneString = (Multi) pico.getComponentInstance("MultiOneString");
        assertEquals("one string", multiOneString.message);
        Multi multiOneInt = (Multi) pico.getComponentInstance("MultiOneInt");
        assertEquals("one int", multiOneInt.message);
        Multi multiNone = (Multi) pico.getComponentInstance("MultiNone");
        assertEquals("none", multiNone.message);
    }
}
