/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.hierarchical;

import junit.framework.TestCase;
import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentSpecification;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.NullContainer;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.UnsatisfiedDependencyInstantiationException;
import org.picocontainer.defaults.NoPicoSuitableConstructorException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.testmodel.FlintstonesImpl;
import org.picocontainer.testmodel.FredImpl;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.WilmaImpl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class HierarchicalPicoContainerTestCase extends TestCase {

    public void testBasicContainerAsserts() {
        try {
            new HierarchicalPicoContainer(new DefaultComponentFactory(), null, new DefaultComponentRegistry());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            new HierarchicalPicoContainer(null, new NullContainer(), new DefaultComponentRegistry());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void testBasicRegAndStart() throws PicoInitializationException, PicoRegistrationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponentByClass(WilmaImpl.class);

        pico.instantiateComponents();

        assertEquals("There should be two comps in the container", 2, pico.getComponents().length);

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", pico.hasComponent(WilmaImpl.class));
    }

    public void testTooFewComponents() throws PicoInitializationException, PicoRegistrationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);

        try {
            pico.instantiateComponents();
            fail("should need a wilma");
        } catch (UnsatisfiedDependencyInstantiationException e) {
            // expected
            assertTrue(e.getClassThatNeedsDeps() == FredImpl.class);
            assertTrue(e.getMessage().indexOf(FredImpl.class.getName()) > 0);

        }
    }

//    public void testSharedImplementationOfTwoTypes() throws PicoInitializationException {
//        List messages = new ArrayList();
//        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
//        try {
//            pico.registerComponent(List.class, messages);
//            pico.registerComponent(Dictionary.class, Webster.class);
//            pico.registerComponent(Thesaurus.class, Webster.class);
//            pico.instantiateComponents();
//
//            assertEquals("Should only have one instance of Webster", 1, messages.size());
//            Object dictionary = pico.getComponent(Dictionary.class);
//            Object thesaurus = pico.getComponent(Thesaurus.class);
//            assertSame("The dictionary and the thesaurus should have been the same object", dictionary, thesaurus);
//
//        } catch (PicoRegistrationException e) {
//            fail("Should not have barfed for any reason");
//        }
//    }

    public void testDuplicateRegistration() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(WilmaImpl.class);
        try {
            pico.registerComponentByClass(WilmaImpl.class);
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == WilmaImpl.class);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }
    }

    public void testDuplicateRegistrationWithTypeAndObject() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(WilmaImpl.class);
        try {
            pico.registerComponent(WilmaImpl.class, new WilmaImpl());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == WilmaImpl.class);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }
    }

    public static class DerivedWilma extends WilmaImpl {
        public DerivedWilma() {
        }
    }

    public void testAmbiguousHierarchy() throws PicoRegistrationException, PicoInitializationException {

        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        // Register two Wilmas that Fred will be confused about
        pico.registerComponentByClass(WilmaImpl.class);
        pico.registerComponentByClass(DerivedWilma.class);

        // Register a confused Fred
        pico.registerComponentByClass(FredImpl.class);

        try {
            pico.instantiateComponents();
            fail("Fred should have been confused about the two Wilmas");
        } catch (AmbiguousComponentResolutionException e) {
            // expected

            List ambiguous = Arrays.asList(e.getResultingKeys());
            assertTrue(ambiguous.contains(DerivedWilma.class));
            assertTrue(ambiguous.contains(WilmaImpl.class));
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(DerivedWilma.class.getName()) > 0);
        }
    }

    public void testRegisterComponentWithObject() throws PicoRegistrationException, PicoInitializationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponentByInstance(new WilmaImpl());

        pico.instantiateComponents();

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", pico.hasComponent(WilmaImpl.class));
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponent(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object as Serializable");
        } catch (PicoRegistrationException e) {

        }

    }

    public void testComponentRegistrationMismatch() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();


        try {
            pico.registerComponent(List.class, WilmaImpl.class);
        } catch (AssignabilityRegistrationException e) {
            // not worded in message
            assertTrue(e.getMessage().indexOf(List.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }

    }

    public void testMultipleArgumentConstructor() throws Throwable /* fixme */ {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponentByClass(FlintstonesImpl.class);

        pico.instantiateComponents();

        assertTrue("There should have been a FlintstonesImpl in the container", pico.hasComponent(FlintstonesImpl.class));
    }

    public void testGetComponentTypes() throws PicoRegistrationException, PicoInitializationException {
        // ASLAK: don't declare as Impl. For IDEA jumps only
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);

        // You might have thought that starting the container shouldn't be necessary
        // just to get the types, but it is. The map holding the types->component instances
        // doesn't receive anything until the components are instantiated.
        pico.instantiateComponents();

        List types = Arrays.asList(pico.getComponentKeys());
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a FredImpl type", types.contains(FredImpl.class));
        assertTrue("There should be a Wilma type", types.contains(Wilma.class));
        assertTrue("There should not be a WilmaImpl type", !types.contains(WilmaImpl.class));
    }

    public void testParentContainer() throws PicoRegistrationException, PicoInitializationException {

        final Wilma wilma = new WilmaImpl();

        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.WithParentContainer(new PicoContainer() {
            public boolean hasComponent(Object componentKey) {
                return componentKey == Wilma.class;
            }

            public Object getComponent(Object componentKey) {
                return componentKey == Wilma.class ? wilma : null;
            }

            public Object[] getComponents() {
                return new Object[]{wilma};
            }

            public Object[] getComponentKeys() {
                return new Class[]{Wilma.class};
            }

            public void instantiateComponents() throws PicoInstantiationException {
            }

            public Object getCompositeComponent()
            {
                return null;
            }

            public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
            {
                return null;
            }
        });

        pico.registerComponentByClass(FredImpl.class);

        pico.instantiateComponents();

        assertEquals("The parent should return 2 components (one from the parent)", 2, pico.getComponents().length);
        assertTrue("Wilma should have been passed through the parent container", pico.hasComponent(Wilma.class));
        assertTrue("There should have been a FredImpl in the container", pico.hasComponent(FredImpl.class));

    }

    // TODO move this test. It doesn't test hierarchical behaviour
    public void testTooManyContructors() throws PicoRegistrationException, PicoInitializationException {

        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponentByClass(Vector.class);
            fail("Should fail because there are more than one constructors");
        } catch (NoPicoSuitableConstructorException e) {
            // expected;
            assertEquals("Should be right class", Vector.class,  e.getForImplementationClass());
        }

    }

    public void testRegisterAbstractShouldFail() throws PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        try {
            pico.registerComponentByClass(Runnable.class);
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

    public void testWithComponentFactory() throws PicoRegistrationException, PicoInitializationException {
        final WilmaImpl wilma = new WilmaImpl();
        HierarchicalPicoContainer pc = new HierarchicalPicoContainer.WithComponentFactory(new ComponentFactory() {
            public Object createComponent(ComponentSpecification componentSpec, Object[] args) {
                return wilma;
            }

            public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
                return new Class[0];
            }
        });
        pc.registerComponentByClass(WilmaImpl.class);
        pc.instantiateComponents();
        assertEquals(pc.getComponent(WilmaImpl.class), wilma);
    }

    public static class Barney {
        public Barney() {
            throw new RuntimeException("Whoa!");
        }
    }

    public void testInvocationTargetException() throws PicoRegistrationException, PicoInitializationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponentByClass(Barney.class);
        try {
            pico.instantiateComponents();
        } catch (PicoInvocationTargetInitializationException e) {
            assertEquals("Whoa!", e.getCause().getMessage());
            assertTrue(e.getMessage().indexOf("Whoa!") > 0);
        }
    }

    public static class BamBam {
        public BamBam() {
        }
    }

    // TODO uncomment this and make it pass
    private void tAestCircularDependencyShouldFail() throws PicoRegistrationException, PicoInitializationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        //     try {
        pico.registerComponentByClass(A.class);
        pico.registerComponentByClass(B.class);
        pico.registerComponentByClass(C.class);
        pico.registerComponentByClass(D.class);

        pico.instantiateComponents();
        fail("Should have gotten a CircularDependencyRegistrationException");
        //     } catch (CircularDependencyRegistrationException e) {
        // ok
        //     }
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
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino.class);
        pico.addParameterToComponent(Dino.class, String.class, "bones");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public static class Dino2 extends Dino {
        public Dino2(int number) {
            super(String.valueOf(number));
        }
    }

    public void testParameterCanBePrimitive() throws Exception {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino2.class);
        pico.addParameterToComponent(Dino2.class, Integer.class, new Integer(22));
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public static class Dino3 extends Dino {
        public Dino3(String a, String b) {
            super(a + b);
        }
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino3.class);
        pico.addParameterToComponent(Dino3.class, String.class, "a");
        pico.addParameterToComponent(Dino3.class, String.class, "b");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public static class Dino4 extends Dino {
        public Dino4(String a, int n, String b, Wilma wilma) {
            super(a + n + b + " " + wilma.getClass().getName());
        }
    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino4.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.addParameterToComponent(Dino4.class, String.class, "a");
        pico.addParameterToComponent(Dino4.class, Integer.class, new Integer(3));
        pico.addParameterToComponent(Dino4.class, String.class, "b");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b org.picocontainer.testmodel.WilmaImpl", animal.getFood());
    }


    public static final class Donkey implements Animal {
        private final Wilma wilma;
        private final String name;

        public Donkey(final Wilma wilma, final String name) {
            this.wilma = wilma;
            this.name = name;
        }

        public String getFood() {
            return "tin cans";
        }
    }

    public static final class Monkey implements Animal {
        public Monkey() {
        }

        public String getFood() {
            return "bananas";
        }
    }

    public void testCannotMixLookupTypesWithKeyFirst() throws PicoRegistrationException {
        //final RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
        //pico.registerComponent(Animal.class, Donkey.class, "donkey");
        //pico.registerComponent(Wilma.class, WilmaImpl.class);
        //try {
        //pico.registerComponent(Animal.class, Monkey.class);
        //fail("Expected InconsistentLookupTypeRegistrationException");
        //} catch (InconsistentLookupTypeRegistrationException e) {
        //}
    }

    public void testCannotMixLookupTypesWithTypeFirst() throws PicoRegistrationException {
        //final PicoContainer pico = new HierarchicalPicoContainer.Default();
        //pico.registerComponent(Animal.class, Donkey.class);
        //pico.registerComponent(Wilma.class, WilmaImpl.class);
        //try {
        //pico.registerComponent(Animal.class, Monkey.class, "monkey");
        //fail("Expected InconsistentLookupTypeRegistrationException");
        //} catch (InconsistentLookupTypeRegistrationException e) {
        //}
    }

    public void testKeepingLookupTypesConsistentWorks() throws PicoRegistrationException {
        //final PicoContainer pico = new HierarchicalPicoContainer.Default();
        //pico.registerComponent(Animal.class, Donkey.class, "donkey");
        //pico.registerComponent(Wilma.class, WilmaImpl.class);
        //pico.registerComponent(Animal.class, Monkey.class, "monkey");
    }

    public void testCanPassMultipleImplsAsArray() throws PicoRegistrationException, PicoInstantiationException {
        //final PicoContainer pico = new HierarchicalPicoContainer.Default();

        //pico.registerComponent(Animal.class, Donkey.class, "donkey");
        //pico.registerComponent(Animal.class, Monkey.class, "monkey");
        //pico.registerComponent(Wilma.class, WilmaImpl.class);
        //pico.registerComponent(AnimalConsumer.class);
        //pico.addParameterToComponent(Donkey.class, String.class,"neddy");

        //pico.instantiateComponents();
        //final Monkey monkey = (Monkey) pico.getComponent(Animal.class, "monkey");
        //assertNotNull(monkey);
        //final Donkey donkey = (Donkey) pico.getComponent(Animal.class, "donkey");
        //assertNotNull(donkey);
        //assertNotNull(donkey.wilma);
        //assertEquals("neddy",donkey.name);
        //final AnimalConsumer animalConsumer = (AnimalConsumer) pico.getComponent(AnimalConsumer.class);
        //assertSame(monkey,animalConsumer.getAnimal("monkey"));
        //assertSame(donkey,animalConsumer.getAnimal("donkey"));
    }


    public static interface I {
    }

    public static class AA implements I {
        public AA(I i) {
        }
    }

    public static class BB implements I {
        public BB(I i) {
        }
    }

    public void testExtendAndDependOnSameType() throws PicoRegistrationException, PicoInitializationException {
        HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

        pico.registerComponentByClass(AA.class);
        pico.registerComponentByClass(BB.class);

        try {
            pico.instantiateComponents();
            fail("Should have barfed");
        } catch (AmbiguousComponentResolutionException e) {
            // Neither can be instantiated without the other.
        }
    }
}
