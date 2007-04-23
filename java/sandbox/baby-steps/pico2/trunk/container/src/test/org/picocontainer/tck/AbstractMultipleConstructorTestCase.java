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
        pico.registerComponent(String.class);
        assertEquals("", pico.getComponent(String.class));
    }

    public void testMultiWithOnlySmallSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponent(Multi.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Three.class);

        Multi multi = (Multi) pico.getComponent(Multi.class);
        assertEquals("three one", multi.message);
    }

    public void testMultiWithBothSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponent(Multi.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Two.class);
        pico.registerComponent(Three.class);

        Multi multi = (Multi) pico.getComponent(Multi.class);
        assertEquals("one two three", multi.message);
    }

    public void testMultiWithTwoEquallyBigSatisfiedDependenciesFails() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponent(Multi.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Two.class);

        try {
            pico.getComponent(Multi.class);
            fail();
        } catch (TooManySatisfiableConstructorsException e) {
            assertTrue(e.getMessage().indexOf("Three") == -1);
            assertEquals(3, e.getConstructors().size());
            assertEquals(Multi.class, e.getForImplementationClass());
        }
    }

    public void testMultiWithSatisfyingDependencyAndParametersWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponent("MultiOneTwo", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ComponentParameter("Two"),
        });
        pico.registerComponent("MultiTwoOne", Multi.class, new Parameter[]{
            new ComponentParameter("Two"),
            ComponentParameter.DEFAULT,
        });
        pico.registerComponent("MultiOneString", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ConstantParameter(""),
        });
        pico.registerComponent("MultiOneInt", Multi.class, new Parameter[]{
            ComponentParameter.DEFAULT,
            new ConstantParameter(new Integer(5)),
        });
        pico.registerComponent("MultiNone", Multi.class, new Parameter[]{});
        pico.registerComponent(One.class);
        pico.registerComponent("Two", Two.class);

        Multi multiOneTwo = (Multi) pico.getComponent("MultiOneTwo");
        assertEquals("one two", multiOneTwo.message);
        Multi multiTwoOne = (Multi) pico.getComponent("MultiTwoOne");
        assertEquals("two one", multiTwoOne.message);
        Multi multiOneString = (Multi) pico.getComponent("MultiOneString");
        assertEquals("one string", multiOneString.message);
        Multi multiOneInt = (Multi) pico.getComponent("MultiOneInt");
        assertEquals("one int", multiOneInt.message);
        Multi multiNone = (Multi) pico.getComponent("MultiNone");
        assertEquals("none", multiNone.message);
    }
}
