/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;

import java.util.*;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import picocontainer.testmodel.FlintstonesImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.testmodel.*;
import picocontainer.testmodel.Dictionary;

public class HierarchicalPicoContainerTestCase extends TestCase {

    public void testBasicContainerAsserts() {
        try {
            new HierarchicalPicoContainer(null, new NullStartableLifecycleManager(), new DefaultComponentFactory());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            new HierarchicalPicoContainer(new NullContainer(), null, new DefaultComponentFactory());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            new HierarchicalPicoContainer(new NullContainer(), new NullStartableLifecycleManager(), null);
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }

    }

    public void testBasicRegAndStart() throws PicoStartException, PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.start();

        assertEquals("There should be two comps in the container", 2, pico.getComponents().length);

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", pico.hasComponent(WilmaImpl.class));
    }

    public void testTooFewComponents() throws PicoStartException, PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(FredImpl.class);

        try {
            pico.start();
            fail("should need a wilma");
        } catch (UnsatisfiedDependencyStartupException e) {
            // expected
            assertTrue(e.getClassThatNeedsDeps() == FredImpl.class);
            assertTrue(e.getMessage().indexOf(FredImpl.class.getName()) > 0);

        }
    }

    public void testDupeImplementationsOfComponents() throws PicoStartException {

        List messages = new ArrayList();
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        try {
            pico.registerComponent(List.class, messages);
            pico.registerComponent(Dictionary.class, Webster.class);
            pico.registerComponent(Thesaurus.class, Webster.class);
            pico.start();

            assertEquals("Should only have one instance of Webster", 1,messages.size());
            Object dictionary = pico.getComponent(Dictionary.class);
            Object thesaurus = pico.getComponent(Thesaurus.class);
            assertSame("The dictionary and the thesaurus should heve been the same object", dictionary, thesaurus);

        } catch (PicoRegistrationException e) {
            fail("Should not have barfed with dupe registration");
        }
    }

    public void testDupeTypesWithClass() throws PicoStartException, PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(WilmaImpl.class);
        try {
            pico.registerComponent(WilmaImpl.class);
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentTypeRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateClass() == WilmaImpl.class);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }
    }

    public void testDupeTypesWithObject() throws PicoStartException, PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(WilmaImpl.class);
        try {
            pico.registerComponent(WilmaImpl.class, new WilmaImpl());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentTypeRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateClass() == WilmaImpl.class);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }
    }

    private static class DerivedWilma extends WilmaImpl {
        public DerivedWilma() {
        }
    }

    public void testAmbiguousHierarchy() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new HierarchicalPicoContainer.Default();

        // Register two Wilmas that Fred will be confused about
        pico.registerComponent(WilmaImpl.class);
        pico.registerComponent(DerivedWilma.class);

        // Register a confused Fred
        pico.registerComponent(FredImpl.class);

        try {
            pico.start();
            fail("Fred should have been confused about the two Wilmas");
        } catch (AmbiguousComponentResolutionException e) {
            // expected

            List ambiguous = Arrays.asList(e.getAmbiguousClasses());
            assertTrue(ambiguous.contains(DerivedWilma.class));
            assertTrue(ambiguous.contains(WilmaImpl.class));
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(DerivedWilma.class.getName()) > 0);
        }
    }

    public void testRegisterComponentWithObject() throws PicoRegistrationException, PicoStartException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(new WilmaImpl());

        pico.start();

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", pico.hasComponent(WilmaImpl.class));
    }

    public void testRegisterComponentWithObjectBadType() {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponent(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object as Serializable");
        } catch (PicoRegistrationException e) {

        }

    }

    public void testComponentRegistrationMismatch() throws PicoStartException, PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();


        try {
            pico.registerComponent(List.class, WilmaImpl.class);
        } catch (AssignabilityRegistrationException e) {
            // not worded in message
            assertTrue(e.getMessage().indexOf(List.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }

    }

    public void testMultipleArgumentConstructor() throws Throwable /* fixme */ {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent(FlintstonesImpl.class);

        pico.start();

        assertTrue("There should have been a FlintstonesImpl in the container", pico.hasComponent(FlintstonesImpl.class));
    }

    public void testGetComponentTypes() throws PicoRegistrationException, PicoStartException {
        // ASLAK: don't declare as Impl. For IDEA jumps only
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);

        // You might have thought that starting the container shouldn't be necessary
        // just to get the types, but it is. The map holding the types->component instances
        // doesn't receive anything until the components are instantiated.
        pico.start();

        List types = Arrays.asList(pico.getComponentTypes());
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a FredImpl type", types.contains(FredImpl.class));
        assertTrue("There should be a Wilma type", types.contains(Wilma.class));
        assertTrue("There should not be a WilmaImpl type", !types.contains(WilmaImpl.class));
    }

    public void testParentContainer() throws PicoRegistrationException, PicoStartException {

        final Wilma wilma = new WilmaImpl();

        PicoContainer pico = new HierarchicalPicoContainer.WithParentContainer(new Container() {
            public boolean hasComponent(Class componentType) {
                return componentType == Wilma.class;
            }

            public Object getComponent(Class componentType) {
                return componentType == Wilma.class ? wilma : null;
            }

            public Object[] getComponents() {
                return new Object[]{wilma};
            }

            public Class[] getComponentTypes() {
                return new Class[]{Wilma.class};
            }
        });

        pico.registerComponent(FredImpl.class);

        pico.start();

        assertEquals("The parent should return 2 components (one from the parent)", 2, pico.getComponents().length);
        assertTrue("Wilma should have been passed through the parent container", pico.hasComponent(Wilma.class));
        assertTrue("There should have been a FredImpl in the container", pico.hasComponent(FredImpl.class));

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

        assertTrue("There should have been a 'One' in the container", pico.hasComponent(One.class));

        One one = (One) pico.getComponent(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems", 4, one.getMsgs().size());
        assertEquals("Incorrect Order of Instantiation", "One", one.getMsgs().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getMsgs().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getMsgs().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getMsgs().get(3));

        // post instantiation startup
        assertEquals("Should be four elems", 4, osm.getStarted().size());
        assertEquals("Incorrect Order of Starting", One.class, osm.getStarted().get(0));
        assertEquals("Incorrect Order of Starting", Two.class, osm.getStarted().get(1));
        assertEquals("Incorrect Order of Starting", Three.class, osm.getStarted().get(2));
        assertEquals("Incorrect Order of Starting", Four.class, osm.getStarted().get(3));

        pico.stop();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, osm.getStopped().size());
        assertEquals("Incorrect Order of Stopping", Four.class, osm.getStopped().get(0));
        assertEquals("Incorrect Order of Stopping", Three.class, osm.getStopped().get(1));
        assertEquals("Incorrect Order of Stopping", Two.class, osm.getStopped().get(2));
        assertEquals("Incorrect Order of Stopping", One.class, osm.getStopped().get(3));

    }

    public void testTooManyContructors() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponent(Vector.class);
            fail("Should fail because there are more than one constructors");
        } catch (WrongNumberOfConstructorsRegistrationException e) {
            assertTrue(e.getMessage().indexOf("4") > 0);            //expected
            // expected;
        }

    }

    public void testRegisterAbstractShouldFail() throws PicoRegistrationException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponent(Runnable.class);
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (NotConcreteRegistrationException e) {
            assertEquals(Runnable.class, e.getComponentImplementation());
            assertTrue(e.getMessage().indexOf(Runnable.class.getName()) > 0);
        }
    }


    public static class A {
        public A(B b) {
        }
    }

    public static class B {
        public B(C c, D d) {
        }
    }

    public static class C {
        public C(A a, B b) {
        }
    }

    public static class D {
        public D() {
        }
    }

    public void testWithComponentFactory() throws PicoRegistrationException, PicoStartException {
        final WilmaImpl wilma = new WilmaImpl();
        PicoContainer pc = new HierarchicalPicoContainer.WithComponentFactory(new ComponentFactory() {
            public Object createComponent(Class componentType, Constructor constructor, Object[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
                return wilma;
            }
        });
        pc.registerComponent(WilmaImpl.class);
        pc.start();
        assertEquals(pc.getComponent(WilmaImpl.class), wilma);
    }

    static class Barney {
        public Barney() {
            throw new RuntimeException("Whoa!");
        }
    }

    public void testInvocationTargetException() throws PicoRegistrationException, PicoStartException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Barney.class);
        try {
            pico.start();
        } catch (PicoInvocationTargetStartException e) {
            assertEquals("Whoa!", e.getCause().getMessage());
            assertTrue(e.getMessage().indexOf("Whoa!") > 0);
        }
    }

    public static class BamBam {
        public BamBam() {
        }
    }

    // TODO uncomment this and make it pass
    private void tAestCircularDependencyShouldFail() throws PicoRegistrationException, PicoStartException {
        PicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponent(A.class);
            pico.registerComponent(B.class);
            pico.registerComponent(C.class);
            pico.registerComponent(D.class);

            pico.start();
            fail("Should have gotten a CircularDependencyRegistrationException");
        } catch (CircularDependencyRegistrationException e) {
            // ok
        }
    }

    interface Animal {

        String getFood();
    }

    public static class Dino implements Animal {
        String food;

        public Dino(String food) {
            this.food = food;
        }

        public String getFood() {
            return food;
        }
    }

    public void testParameterCanBePassedToConstructor() throws Exception {
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino.class);
        pico.addParameterToComponent(Dino.class, String.class, "bones");
        pico.start();

        Animal animal = (Animal)pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public static class Dino2 extends Dino {
        public Dino2(int number) {
            super(String.valueOf(number));
        }
    }

    public void testParameterCanBePrimitive() throws Exception {
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino2.class);
        pico.addParameterToComponent(Dino2.class, Integer.class, new Integer(22));
        pico.start();

        Animal animal = (Animal)pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public static class Dino3 extends Dino {
        public Dino3(String a, String b) {
            super(a + b);
        }
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino3.class);
        pico.addParameterToComponent(Dino3.class, String.class, "a");
        pico.addParameterToComponent(Dino3.class, String.class, "b");
        pico.start();

        Animal animal = (Animal)pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public static class Dino4 extends Dino {
        public Dino4(String a, int n, String b, Wilma wilma) {
            super(a + n + b + " " + wilma.getClass().getName());
        }
    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        PicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino4.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.addParameterToComponent(Dino4.class, String.class, "a");
        pico.addParameterToComponent(Dino4.class, Integer.class, new Integer(3));
        pico.addParameterToComponent(Dino4.class, String.class, "b");
        pico.start();

        Animal animal = (Animal)pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b picocontainer.testmodel.WilmaImpl", animal.getFood());
    }


}
