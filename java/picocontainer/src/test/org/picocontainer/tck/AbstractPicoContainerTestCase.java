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
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This test tests (at least it should) all the methods in MutablePicoContainer.
 */
public abstract class AbstractPicoContainerTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer(PicoContainer parent);

    protected final MutablePicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;

    }

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependsOnTouchable() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainerWithDependsOnTouchableOnly();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        return pico;
    }

    public void testRegisteredComponentsExistAndAreTheCorrectTypes() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();

        assertNotNull("Container should have Touchable component",
                pico.getComponentAdapter(Touchable.class));
        assertNotNull("Container should have DependsOnTouchable component",
                pico.getComponentAdapter(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                pico.getComponentInstance(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                pico.getComponentInstance(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertNull("should not have non existent component", pico.getComponentAdapter(Map.class));
    }

    public void testRegistersSingleInstance() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        StringBuffer sb = new StringBuffer();
        pico.registerComponentInstance(sb);
        assertSame(sb, pico.getComponentInstance(StringBuffer.class));
    }

    public void testContainerIsSerializable() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (PicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstance(Touchable.class);

        assertTrue(touchable.wasTouched);
    }

    public void testGettingComponentWithMissingDependencyFails() throws PicoException, PicoRegistrationException {
        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();
        try {
            picoContainer.getComponentInstance(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (UnsatisfiableDependenciesException e) {
            assertSame(picoContainer.getComponentAdapterOfType(DependsOnTouchable.class).getComponentImplementation(), e.getUnsatisfiableComponentAdapter().getComponentImplementation());
            final Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());

            // Touchable.class is now inside a List (the list of unsatisfied parameters) -- mparaz
            Object unstaisifed = unsatisfiableDependencies.iterator().next();
            assertEquals(Collections.singletonList(Touchable.class), unstaisifed);
        }
    }

    public void testDuplicateRegistration() throws Exception {
        try {
            MutablePicoContainer pico = createPicoContainer(null);
            pico.registerComponentImplementation(Object.class);
            pico.registerComponentImplementation(Object.class);
            fail("Should have failed with duplicate registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Object.class);
        }
    }

    public void testExternallyInstantiatedObjectsCanBeRegistgeredAndLookeUp() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        final HashMap map = new HashMap();
        pico.registerComponentInstance(Map.class, map);
        assertSame(map, pico.getComponentInstance(Map.class));
    }

    public void testAmbiguousResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation("ping", String.class);
        pico.registerComponentInstance("pong", "pang");
        try {
            pico.getComponentInstance(String.class);
        } catch (AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testLookupWithUnregisteredKeyReturnsNull() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponentInstance(String.class));
    }

    public static class ListAdder {
        public ListAdder(Collection list) {
            list.add("something");
        }
    }

    public void TODOtestMulticasterResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);

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
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(ComponentD.class);

        try {
            pico.getComponentInstance(ComponentD.class);
        } catch (UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            // The set now contains a list containing the two dependencies in
            // order. Therefore, we can't use the original code. - mparaz

            assertEquals(1, unsatisfiableDependencies.size());

            final List expectedList = new ArrayList(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);

            // Convert the Set to a List and assert that its first and only
            // element is the expected list. This is a stronger check than
            // contains().
            // - mparaz
            assertEquals(new ArrayList(unsatisfiableDependencies).get(0),
                    expectedList);
        }
    }

    public void testCyclicDependencyThrowsCyclicDependencyException() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(ComponentB.class);
        pico.registerComponentImplementation(ComponentD.class);
        pico.registerComponentImplementation(ComponentE.class);

        try {
            pico.getComponentInstance(ComponentD.class);
            fail();
        } catch (CyclicDependencyException e) {
            final List dDependencies = Arrays.asList(ComponentD.class.getConstructors()[0].getParameterTypes());
            final List reportedDependencies = Arrays.asList(e.getDependencies());
            assertEquals(dDependencies, reportedDependencies);
        } catch (StackOverflowError e) {
            fail();
        }
    }

    public void testRemovalNonRegisteredComponentAdapterWorksAndReturnsNull() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        assertNull(picoContainer.unregisterComponent("COMPONENT DOES NOT EXIST"));
    }

    /**
     * Important! Nanning really, really depends on this!
     */
    public void testComponentAdapterRegistrationOrderIsMaintained() {
        ConstructorComponentAdapter c1 = new ConstructorComponentAdapter("1", Object.class);
        ConstructorComponentAdapter c2 = new ConstructorComponentAdapter("2", String.class);

        MutablePicoContainer picoContainer = createPicoContainer(null);
        picoContainer.registerComponent(c1);
        picoContainer.registerComponent(c2);
        assertEquals("registration order should be maintained",
                Arrays.asList(new Object[]{c1, c2}), picoContainer.getComponentAdapters());

        picoContainer.getComponentInstances(); // create all the instances at once
        assertFalse("instances should be created in same order as adapters are created",
                picoContainer.getComponentInstances().get(0) instanceof String);
        assertTrue("instances should be created in same order as adapters are created",
                picoContainer.getComponentInstances().get(1) instanceof String);

        MutablePicoContainer reversedPicoContainer = createPicoContainer(null);
        reversedPicoContainer.registerComponent(c2);
        reversedPicoContainer.registerComponent(c1);
        assertEquals("registration order should be maintained",
                Arrays.asList(new Object[]{c2, c1}), reversedPicoContainer.getComponentAdapters());

        reversedPicoContainer.getComponentInstances(); // create all the instances at once
        assertTrue("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponentInstances().get(0) instanceof String);
        assertFalse("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponentInstances().get(1) instanceof String);
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
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation("wt", WashableTouchable.class);
        pico.registerComponentImplementation("nw", NeedsWashable.class);
        pico.registerComponentImplementation("nt", NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable) pico.getComponentInstance("nw");
        NeedsTouchable nt = (NeedsTouchable) pico.getComponentInstance("nt");
        assertSame(nw.washable, nt.touchable);
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer(null);

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
        MutablePicoContainer pico = createPicoContainer(null);

        pico.registerComponentImplementation("foo", JMSService.class, new Parameter[]{
            new ConstantParameter("0"),
            new ConstantParameter("something"),
        });
        JMSService jms = (JMSService) pico.getComponentInstance("foo");
        assertEquals("0", jms.serverid);
        assertEquals("something", jms.path);
    }

    public static class ComponentA {
        public ComponentA(ComponentB b, c c) {
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
        }
    }

    public static class ComponentB {
    }

    public static class c {
    }

    public static class ComponentD {
        public ComponentD(ComponentE e, ComponentB b) {
            Assert.assertNotNull(e);
            Assert.assertNotNull(b);
        }
    }

    public static class ComponentE {
        public ComponentE(ComponentD d) {
            Assert.assertNotNull(d);
        }
    }

    public static class ComponentF {
        public ComponentF(ComponentA a) {
            Assert.assertNotNull(a);
        }
    }

    public void testAggregatedVerificationException() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(ComponentA.class);
        pico.registerComponentImplementation(ComponentE.class);
        try {
            pico.verify();
            fail("we expect a PicoVerificationException");
        } catch (PicoVerificationException e) {
            List nested = e.getNestedExceptions();
            assertEquals(2, nested.size());
        }
    }

    public void testRegistrationOfAdapterSetsHostingContainerAsSelf() {
        final InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter("", new Object());
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        picoContainer.registerComponent(componentAdapter);
        assertSame(picoContainer, componentAdapter.getContainer());
    }

    public static class ContainerDependency {
        private PicoContainer pico;

        public ContainerDependency(PicoContainer container) {
            this.pico = container;
        }
    }

    public void testImplicitPicoContainerInjection() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(ContainerDependency.class);
        ContainerDependency dep = (ContainerDependency) pico.getComponentInstance(ContainerDependency.class);
        assertSame(pico, dep.pico);
    }

    public void testSelfRegistryThrowsIllegalArgument() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        try {
            pico.registerComponentInstance(pico);
            fail("Should not be able to register a container to itself");
        } catch (PicoRegistrationException e) {
        }
    }

}
