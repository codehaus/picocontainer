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

import java.util.*;
import java.io.Serializable;

import picocontainer.testmodel.FlintstonesImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.testmodel.*;
import picocontainer.testmodel.Dictionary;

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

    public void testDupeImplementations() throws PicoRegistrationException {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(Dictionary.class, Webster.class);
        try
        {
            pico.registerComponent(Thesaurus.class, Webster.class);
            fail("Should have barfed with dupe registration");
        }
        catch (DuplicateComponentClassRegistrationException e)
        {
            // expected
            assertTrue(e.getDuplicateClass() == Webster.class);
        }
    }

    public void testDupeTypesWithClass() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(WilmaImpl.class);
        try
        {
            pico.registerComponent(WilmaImpl.class);
            fail("Should have barfed with dupe registration");
        }
        catch (DuplicateComponentTypeRegistrationException e)
        {
            // expected
            assertTrue(e.getDuplicateClass() == WilmaImpl.class);
        }
    }

    public void testDupeTypesWithObject() throws PicoStartException, PicoRegistrationException
    {
        PicoContainer pico = new PicoContainerImpl.Default();

        pico.registerComponent(WilmaImpl.class);
        try
        {
            pico.registerComponent(WilmaImpl.class, new WilmaImpl());
            fail("Should have barfed with dupe registration");
        }
        catch (DuplicateComponentTypeRegistrationException e)
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

            public Object[] getComponents() {
                // Won't be called here
                return null;
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

        OverriddenStartableLifecycleManager osm = new OverriddenStartableLifecycleManager();
        OverriddenPicoTestContainer pico = new OverriddenPicoTestContainer(null, osm);

        pico.registerComponent(Four.class);
        pico.registerComponent(Two.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Three.class);

        pico.start();

        assertTrue( "There should have been a 'One' in the container", pico.hasComponent( One.class ) );

        One one = (One) pico.getComponent(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems",4,one.getMsgs().size());
        assertEquals("Incorrect Order of Instantiation", "One", one.getMsgs().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getMsgs().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getMsgs().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getMsgs().get(3));

        // post instantiation startup
        assertEquals("Should be four elems",4,osm.getStarted().size());
        assertEquals("Incorrect Order of Starting", One.class, osm.getStarted().get(0));
        assertEquals("Incorrect Order of Starting", Two.class, osm.getStarted().get(1));
        assertEquals("Incorrect Order of Starting", Three.class, osm.getStarted().get(2));
        assertEquals("Incorrect Order of Starting", Four.class, osm.getStarted().get(3));

        pico.stop();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems",4,osm.getStopped().size());
        assertEquals("Incorrect Order of Stopping", Four.class, osm.getStopped().get(0));
        assertEquals("Incorrect Order of Stopping", Three.class, osm.getStopped().get(1));
        assertEquals("Incorrect Order of Stopping", Two.class, osm.getStopped().get(2));
        assertEquals("Incorrect Order of Stopping", One.class, osm.getStopped().get(3));

    }

    public void testTooManyContructors() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new PicoContainerImpl.Default();

        try {
            pico.registerComponent(Vector.class);
            fail("Should fail because there are more than one constructors");
        } catch (WrongNumberOfConstructorsRegistrationException e) {
            // expected;
        }

    }

    public void testRegisterAbstractShouldFail() {
        PicoContainer pico = new PicoContainerImpl.Default();

        try {
            pico.registerComponent( Runnable.class );
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (PicoRegistrationException e) {
            // ok
        }
    }


    public static class A {
        public A(B b){}
    }
    public static class B {
        public B(C c, D d){}
    }
    public static class C {
        public C(A a, B b) {}
    }
    public static class D {
        public D() {System.out.println("D instantiated");}
    }

    // TODO uncomment this and make it pass
    private void tAestCircularDependencyShouldFail() throws PicoRegistrationException, PicoStartException {
        PicoContainer pico = new PicoContainerImpl.Default();

        try {
            pico.registerComponent(A.class);
            pico.registerComponent(B.class);
            pico.registerComponent(C.class);
            pico.registerComponent(D.class);

            pico.start();
            fail( "Should have gotten a CircularDependencyRegistrationException" );
        } catch (CircularDependencyRegistrationException e) {
            // ok
        }
    }
}
