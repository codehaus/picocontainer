package org.picocontainer.tck;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.Washable;
import org.picocontainer.testmodel.WashableTouchable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This test tests (at least it should) all the methods in MutablePicoContainer.
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

    public void testNewContainerIsNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull("Are you calling super.setUp() in your setUp method?", createPicoContainerWithTouchableAndDependency());
    }

    public void testRegisteredComponentsExistAndAreTheCorrectTypes() throws PicoException, PicoRegistrationException {
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

    public void testRegistersSingleInstance() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        StringBuffer sb = new StringBuffer();
        pico.registerComponentInstance(sb);
        assertSame(sb, pico.getComponentInstance(StringBuffer.class));
    }

    public void testContainerIsSerializable() throws PicoException, PicoInitializationException,
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

    public void testGettingComponentWithMissingDependencyFails() throws PicoException, PicoRegistrationException {
        try {
            PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();
            picoContainer.getComponentInstance(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (NoSatisfiableConstructorsException e) {
            assertEquals(DependsOnTouchable.class, e.getUnsatisfiableComponentImplementation());
        }
    }

    public void testGettingSameComponentTwiceGivesSameComponent() throws PicoException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Object.class, Object.class);
        assertSame(
                pico.getComponentInstance(Object.class),
                pico.getComponentInstance(Object.class)
        );
    }

    public void testDuplicateRegistration() throws Exception {
        try {
            MutablePicoContainer pico = createPicoContainer();
            pico.registerComponentImplementation(Object.class, Object.class);
            pico.registerComponentImplementation(Object.class, Object.class);
            fail("Should have barfed with duplicate registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Object.class);
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
            pico.getComponentInstance(String.class);
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

        pico.getComponentInstance(ListAdder.class);

        List a = (List) pico.getComponentInstance("a");
        assertTrue(a.contains("something"));

        List l = (List) pico.getComponentInstance("l");
        assertTrue(l.contains("something"));
    }

    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(D.class);

        try {
            pico.getComponentInstance(D.class);
        } catch (NoSatisfiableConstructorsException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(2, unsatisfiableDependencies.size());
            assertTrue(unsatisfiableDependencies.contains(E.class));
            assertTrue(unsatisfiableDependencies.contains(B.class));

            assertTrue(e.getMessage().indexOf("class " + E.class.getName()) != -1);
            assertTrue(e.getMessage().indexOf("class " + B.class.getName()) != -1);
        }
    }

    public void testCyclicDependencyThrowsCyclicDependencyException() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(B.class);
        pico.registerComponentImplementation(D.class);
        pico.registerComponentImplementation(E.class);

        try {
            pico.getComponentInstance(D.class);
            fail();
        } catch (CyclicDependencyException e) {
            final List dDependencies = Arrays.asList(D.class.getConstructors()[0].getParameterTypes());
            final List reportedDependencies = Arrays.asList(e.getDependencies());
            assertEquals(dDependencies, reportedDependencies);
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
        pico.registerComponentImplementation("wt", WashableTouchable.class);
        pico.registerComponentImplementation("nw", NeedsWashable.class);
        pico.registerComponentImplementation("nt", NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable) pico.getComponentInstance("nw");
        NeedsTouchable nt = (NeedsTouchable) pico.getComponentInstance("nt");
        assertSame(nw.washable, nt.touchable);
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();

        try {
            pico.registerComponentInstance(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object.class as Serializable because it is not, " +
                    "it does not implement it, Object.class does not implement much.");
        } catch (AssignabilityRegistrationException e) {
        }

    }

    public static class JMSService {
        public final String serverid;
        public final String path;

        public JMSService(String serverid, String path) {
            this.serverid = serverid;
            this.path = path;
        }
    }

    // http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
    public void testPico52() {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation("foo", JMSService.class, new Parameter[]{
            new ConstantParameter("0"),
            new ConstantParameter("something"),
        });
        JMSService jms = (JMSService) pico.getComponentInstance("foo");
        assertEquals("0", jms.serverid);
        assertEquals("something", jms.path);
    }

    public void testChildContainerCanBeAddedAndRemoved() {
        MutablePicoContainer parent = createPicoContainer();
        MutablePicoContainer child = createPicoContainer();

        assertTrue(parent.addChild(child));
        assertFalse(parent.addChild(child));
        assertTrue(child.getParentContainers().contains(parent));
        assertTrue(parent.getChildContainers().contains(child));

        assertTrue(parent.removeChild(child));
        assertFalse(parent.removeChild(child));
        assertFalse(child.getParentContainers().contains(parent));
        assertFalse(parent.getChildContainers().contains(child));

    }

    public void testParentContainerCanBeAddedAndRemoved() {
        MutablePicoContainer parent = createPicoContainer();
        MutablePicoContainer child = createPicoContainer();

        assertTrue(child.addParent(parent));
        assertFalse(child.addParent(parent));
        assertTrue(child.getParentContainers().contains(parent));
        assertTrue(parent.getChildContainers().contains(child));

        assertTrue(child.removeParent(parent));
        assertFalse(child.removeParent(parent));
        assertFalse(child.getParentContainers().contains(parent));
        assertFalse(parent.getChildContainers().contains(child));

    }

    public static class A {
        public A(B b, C c) {
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
        }
    }

    public static class B {
    }

    public static class C {
    }

    public static class D {
        public D(E e, B b) {
            Assert.assertNotNull(e);
            Assert.assertNotNull(b);
        }
    }

    public static class E {
        public E(D d) {
            Assert.assertNotNull(d);
        }
    }


    public void testAggregatedVerificationException() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(A.class);
        pico.registerComponentImplementation(E.class);
        try {
            pico.verify();
            fail("we expect a PicoVerificationException");
        } catch (PicoVerificationException e) {
            List nested = e.getNestedExceptions();
            assertEquals(2, nested.size());

            Set bc = new HashSet(Arrays.asList(new Class[]{B.class, C.class}));
            assertTrue(nested.contains(new NoSatisfiableConstructorsException(A.class, bc)));

            Set d = new HashSet(Arrays.asList(new Class[]{D.class}));
            assertTrue(nested.contains(new NoSatisfiableConstructorsException(E.class, d)));
        }
    }

}
