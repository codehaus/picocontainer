package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.*;
import org.picocontainer.testmodel.*;
import org.picocontainer.defaults.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * This test tests (at least it should) test all the method in MutablePicoContainer
 */
public abstract class AbstractPicoContainerTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependency() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;
    }

    protected final PicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;

    }

    public void testNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull("Are you calling super.setUp() in your setUp method?", createPicoContainerWithTouchableAndDependency());
    }

    public void testBasicInstantiationAndContainment() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependency();

        assertTrue("Container should have Touchable component",
                pico.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                pico.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                pico.getComponentInstance(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                pico.getComponentInstance(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertTrue("should not have non existent component", !pico.hasComponent(Map.class));
    }

    public void testInstanceRegistration() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        StringBuffer sb = new StringBuffer();
        pico.registerComponentInstance(sb);
        assertSame(sb, pico.getComponentInstance(StringBuffer.class));
    }

    public void testSerializabilityOfContainer() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer pico = createPicoContainerWithTouchableAndDependency();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);

        // yeah yeah, is not needed.
        pico = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (PicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstance(Touchable.class);

        assertTrue("hello should have been called in Touchable", touchable.wasTouched);
    }

    public void testTooFewComponents() throws PicoException, PicoRegistrationException {

        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();

        try {
            picoContainer.getComponentInstance(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (NoSatisfiableConstructorsException e) {
            assertEquals(DependsOnTouchable.class, e.getUnsatisfiableComponentImplementation());
        }
    }

    public void testDoubleInstantiation() throws PicoException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependency();
        assertSame(
                pico.getComponentInstance(DependsOnTouchable.class),
                pico.getComponentInstance(DependsOnTouchable.class)
        );
    }

    public void testDuplicateRegistration() throws Exception {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
        try {
            pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Touchable.class);
        }
    }

    public void testByInstanceRegistration() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
        pico.registerComponentInstance(Map.class, new HashMap());
        assertEquals("Wrong number of comps in the internals", 3, pico.getComponentInstances().size());
        assertEquals("Key - Map, Impl - HashMap should be in internals", HashMap.class, pico.getComponentInstance(Map.class).getClass());
    }

    public void testAmbiguousResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation("ping", String.class);
        pico.registerComponentInstance("pong", "pang");
        try {
            Object huh = pico.getComponentInstance(String.class);
        } catch (AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testNoResolution() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        assertNull(pico.getComponentInstance(String.class));
    }

    public static class ListAdder {
        public ListAdder(Collection list) {
            list.add("something");
        }
    }

    public void TODOtestMulticasterResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation(ListAdder.class);
        pico.registerComponentImplementation("a", ArrayList.class);
        pico.registerComponentImplementation("l", LinkedList.class);

        ListAdder adder = (ListAdder) pico.getComponentInstance(ListAdder.class);

        List a = (List) pico.getComponentInstance("a");
        assertTrue(a.contains("something"));

        List l = (List) pico.getComponentInstance("l");
        assertTrue(l.contains("something"));
    }

    public static class A {
        public A(B b, C c){}
    };
    public static class B {};
    public static class C {};

    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(A.class);
        pico.registerComponentImplementation(B.class);

        try {
            pico.getComponentInstance(A.class);
        } catch (NoSatisfiableConstructorsException e) {
            assertEquals( A.class.getName() + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + C.class.getName() + "]", e.getMessage() );
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            assertTrue(unsatisfiableDependencies.contains(C.class));
        }
    }

    public static class D {
        public D(E e, B b){}
    };
    public static class E {
        public E(D d){}
    };

    public void testCyclicDependencyThrowsCyclicDependencyException() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(B.class);
        pico.registerComponentImplementation(D.class);
        pico.registerComponentImplementation(E.class);

        try {
            pico.getComponentInstance(D.class);
            fail();
        } catch (CyclicDependencyException e) {
            assertEquals("Cyclic dependency: " + D.class.getConstructors()[0].getName() + "(" + E.class.getName() + "," + B.class.getName() + ")", e.getMessage());
            assertEquals(D.class.getConstructors()[0], e.getConstructor());
        } catch (StackOverflowError e) {
            fail();
        }
    }

    public static class NeedsTouchable {
        public Touchable touchable;

        public NeedsTouchable(Touchable touchable) {
            this.touchable = touchable;
        }
    }

    public static class NeedsWashable {
        public Washable washable;

        public NeedsWashable(Washable washable) {
            this.washable = washable;
        }
    }

    public void testSameInstanceCanBeUsedAsDifferentType() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(WashableTouchable.class);
        pico.registerComponentImplementation(NeedsWashable.class);
        pico.registerComponentImplementation(NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable) pico.getComponentInstance(NeedsWashable.class);
        NeedsTouchable nt = (NeedsTouchable) pico.getComponentInstance(NeedsTouchable.class);
        assertSame(nw.washable, nt.touchable);
    }

}

