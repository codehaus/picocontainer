/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.io.Serializable;

import picocontainer.testmodel.FlintstonesImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;

public class DefaultPicoContainerTestCase extends TestCase {

    public void testBasic() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.start();

        assertTrue( "There should have been a Fred in the container", pico.hasComponent( FredImpl.class ) );
        assertTrue( "There should have been a Wilma in the container", pico.hasComponent( WilmaImpl.class ) );

    }

    public void testTooFew() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(FredImpl.class);

        try
        {
            pico.start();
            fail("should need a wilma");
        }
        catch (UnsatisfiedDependencyStartupException e)
        {
            // expected
            assertTrue(e.getClassThatNeedsDeps() == FredImpl.class);

        }

    }

    public void testDupesWithClass() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(WilmaImpl.class);
        try
        {
            pico.registerComponent(WilmaImpl.class);
            fail("Should have barfed with dupe registration");
        }
        catch (DuplicateComponentRegistrationException e)
        {
            // expected
            assertTrue(e.getDuplicateClass() == WilmaImpl.class);
        }
    }

    public void testDupesWithObject() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(WilmaImpl.class);
        try
        {
            pico.registerComponent(WilmaImpl.class, new WilmaImpl());
            fail("Should have barfed with dupe registration");
        }
        catch (DuplicateComponentRegistrationException e)
        {
            // expected
            assertTrue(e.getDuplicateClass() == WilmaImpl.class);
        }
    }

    private static class DerivedWilma extends WilmaImpl {
        public DerivedWilma() {
        }
    }

    public void testAmbiguousHierarchy() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new PicoContainerImpl.Default();

        // Register two Wilmas that Fred will be confused about
        pico.registerComponent(WilmaImpl.class);
        pico.registerComponent(DerivedWilma.class);

        // Register a confused Fred
        pico.registerComponent(FredImpl.class);

        try
        {
            pico.start();
            fail("Fred should have been confused about the two Wilmas");
        }
        catch (AmbiguousComponentResolutionException e)
        {
            // expected

            List ambiguous = Arrays.asList( e.getAmbiguousClasses() );
            assertTrue( ambiguous.contains( DerivedWilma.class ));
            assertTrue( ambiguous.contains( WilmaImpl.class ));
        }
    }

    public void testRegisterComponentWithObject() throws PicoRegistrationException, PicoStartException {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(new WilmaImpl());

        pico.start();

        assertTrue( "There should have been a Fred in the container", pico.hasComponent( FredImpl.class ) );
        assertTrue( "There should have been a Wilma in the container", pico.hasComponent( WilmaImpl.class ) );
    }

    public void testRegisterComponentWithObjectBadType() {
        PicoContainer pico = new PicoContainerImpl.Default();

        try {
            pico.registerComponent(Serializable.class, new Object());
            fail( "Shouldn't be able to register an Object as Serializable" );
        } catch (PicoRegistrationException e) {

        }

    }

    public void testRegistrationMismatch() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        try
        {
            pico.registerComponent(Collection.class, WilmaImpl.class);
        }
        catch (AssignabilityRegistrationException e)
        {
            //expected
        }

    }

    public void testMultipleArgumentConstructor() throws Throwable /* fixme */ {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent(FlintstonesImpl.class);

        pico.start();

        assertTrue( "There should have been a FlintstonesImpl in the container", pico.hasComponent( FlintstonesImpl.class ) );
	}

    public void testParentContainer() throws PicoRegistrationException, PicoStartException {

        final Wilma wilma = new WilmaImpl();

        PicoContainer pico = new PicoContainerImpl.WithParentContainer(new Container() {
            public boolean hasComponent(Class compType) {
                return compType == Wilma.class;
            }

            public Object getComponent(Class compType) {
                return wilma;
            }
        });

        pico.registerComponent(FredImpl.class);

        pico.start();

        assertFalse( "Wilma should not have been passed through the parent container", pico.hasComponent( Wilma.class ) );

        assertTrue( "There should have been a FredImpl in the container", pico.hasComponent( FredImpl.class ) );

    }

    public static class One {
        List msgs = new ArrayList();
        public One() {
            ping("One");
        }
        public void ping(String s) {
            msgs.add(s);
        }
        public List getMsgs() {
            return msgs;
        }
    }
    public static class Two {
        public Two(One one) {
            one.ping("Two");
        }
    }
    public static class Three {
        public Three(One one, Two two) {
            one.ping("Three");
        }
    }
    public static class Four {
        public Four(Two two, Three three, One one) {
            one.ping("Four");
        }
    }

    public void testOrderOfInstantiation() throws PicoRegistrationException, PicoStartException, PicoStopException {

        OveriddenPicoTestContainer pico = new OveriddenPicoTestContainer(null);

        pico.registerComponent(Four.class);
        pico.registerComponent(Two.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Three.class);

        pico.start();

        assertTrue( "There should have been a 'One' in the container", pico.hasComponent( One.class ) );

        One one = (One) pico.getComponent(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems",one.getMsgs().size(),4);
        assertEquals("Incorrect Order of Instantiation", "One", one.getMsgs().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getMsgs().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getMsgs().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getMsgs().get(3));

        // post instantiation startup
        assertEquals("Should be four elems",pico.getStarted().size(),4);
        assertEquals("Incorrect Order of Starting", One.class, pico.getStarted().get(0));
        assertEquals("Incorrect Order of Starting", Two.class, pico.getStarted().get(1));
        assertEquals("Incorrect Order of Starting", Three.class, pico.getStarted().get(2));
        assertEquals("Incorrect Order of Starting", Four.class, pico.getStarted().get(3));

        pico.stop();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems",pico.getStopped().size(),4);
        assertEquals("Incorrect Order of Stopping", Four.class, pico.getStopped().get(0));
        assertEquals("Incorrect Order of Stopping", Three.class, pico.getStopped().get(1));
        assertEquals("Incorrect Order of Stopping", Two.class, pico.getStopped().get(2));
        assertEquals("Incorrect Order of Stopping", One.class, pico.getStopped().get(3));

    }

    public static class Horse {
        public Horse() {
        }
        public Horse(Vector v) {
        }
    }

    public void testTooManyContructors() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(Vector.class);
        pico.registerComponent(Horse.class);

        try {
            pico.start();
            fail("Should only have one constructor");
        } catch (WrongNumberOfConstructorsStartException e) {
            // expected;
        }

    }


}
