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
import org.jmock.MockObjectTestCase;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.Startable;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This test tests (at least it should) all the methods in MutablePicoContainer.
 */
public abstract class AbstractPicoContainerTestCase extends MockObjectTestCase {

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

        getTouchableFromSerializedContainer();

    }

    private Touchable getTouchableFromSerializedContainer() throws IOException, ClassNotFoundException {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        // Add a list too, using a constant parameter
        pico.registerComponentImplementation("list", ArrayList.class, new Parameter[] {new ConstantParameter(new Integer(10))});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (MutablePicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        return (Touchable) pico.getComponentInstance(Touchable.class);
    }

    public void testSerializedContainerCanRetrieveImplementation() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        Touchable touchable = getTouchableFromSerializedContainer();

        SimpleTouchable simpleTouchable = (SimpleTouchable) touchable;

        assertTrue(simpleTouchable.wasTouched);
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
            List unstaisifed = (List) unsatisfiableDependencies.iterator().next();
            assertEquals(1, unstaisifed.size());
            assertEquals(Touchable.class, unstaisifed.get(0));
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

    public void testExternallyInstantiatedObjectsCanBeRegistgeredAndLookedUp() throws PicoException, PicoInitializationException {
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
            assertEquals(1, unsatisfiableDependencies.size());

            List list = (List) unsatisfiableDependencies.iterator().next();

            final List expectedList = new ArrayList(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);

            assertEquals(expectedList, list);
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
            // CyclicDependencyException reports now the stack.
            //final List dependencies = Arrays.asList(ComponentD.class.getConstructors()[0].getParameterTypes());
            final List dependencies = Arrays.asList(new Class[]{ComponentD.class, ComponentE.class, ComponentD.class});
            final List reportedDependencies = Arrays.asList(e.getDependencies());
            assertEquals(dependencies, reportedDependencies);
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

        ConstructorInjectionComponentAdapter c1 = new ConstructorInjectionComponentAdapter("1", Object.class);
        ConstructorInjectionComponentAdapter c2 = new ConstructorInjectionComponentAdapter("2", String.class);

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
            assertTrue(-1 != e.getMessage().indexOf(ComponentA.class.getName()));
            assertTrue(-1 != e.getMessage().indexOf(ComponentE.class.getName()));
        }
    }

    // An adapter has no longer a hosting container.
    
//    public void testRegistrationOfAdapterSetsHostingContainerAsSelf() {
//        final InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter("", new Object());
//        final MutablePicoContainer picoContainer = createPicoContainer(null);
//        picoContainer.registerComponent(componentAdapter);
//        assertSame(picoContainer, componentAdapter.getContainer());
//    }

    public static class ContainerDependency {
        public ContainerDependency(PicoContainer container) {
            assertNotNull(container);
        }
    }

    // ImplicitPicoContainer injection is bad. It is an open door for hackers. Developers with
    // special PicoContainer needs should specifically register() a comtainer they want components to
    // be able to pick up on.

//    public void testImplicitPicoContainerInjection() {
//        MutablePicoContainer pico = createPicoContainer(null);
//        pico.registerComponentImplementation(ContainerDependency.class);
//        ContainerDependency dep = (ContainerDependency) pico.getComponentInstance(ContainerDependency.class);
//        assertSame(pico, dep.pico);
//    }

    public void testSelfRegistryThrowsIllegalArgument() {
        final MutablePicoContainer pico = createPicoContainer(null);
        try {
            pico.registerComponentInstance(pico);
            fail("Should not be able to register a container to itself");
        } catch (PicoRegistrationException e) {
        }
    }

    public void testShouldReturnNullWhenUnregistereingUnmanagedComponent() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.unregisterComponentByInstance("yo"));
    }

    public void testShouldReturnNullForComponentAdapterOfUnregisteredType() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponentInstanceOfType(List.class));
    }

    public void testShouldReturnNonMutableParent() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        final MutablePicoContainer picoContainer = createPicoContainer(parent);
        assertNotSame(parent, picoContainer.getParent());
        assertFalse(picoContainer.getParent() instanceof MutablePicoContainer);
    }

    class Foo implements Startable, Disposable{
        public boolean started;
        public boolean stopped;
        public boolean disposed;
        public void start() {
            started = true;
        }
        public void stop() {
            stopped = true;
        }
        public void dispose() {
            disposed = true;
        }

    }

    public void testContainerCascadesStart() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        Foo foo = new Foo();
        picoContainer.registerComponentInstance(foo);
        picoContainer.start();
        assertEquals(true, foo.started);
    }

    public void testContainerCascadesStop() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        Foo foo = new Foo();
        picoContainer.registerComponentInstance(foo);
        picoContainer.start();
        picoContainer.stop();
        assertEquals(true, foo.stopped);
    }

    public void testContainerCascadesDispose() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        Foo foo = new Foo();
        picoContainer.registerComponentInstance(foo);
        picoContainer.dispose();
        assertEquals(true, foo.disposed);
    }

    public void testComponentInstancesFromParentsAreNotDirectlyAccessible2() {
        final MutablePicoContainer a = createPicoContainer(null);
        final MutablePicoContainer b = createPicoContainer(a);
        final MutablePicoContainer c = createPicoContainer(b);

        Object ao = new Object();
        Object bo = new Object();
        Object co = new Object();

        a.registerComponentInstance("a", ao);
        b.registerComponentInstance("b", bo);
        c.registerComponentInstance("c", co);

        assertEquals(1, a.getComponentInstances().size());
        assertEquals(1, b.getComponentInstances().size());
        assertEquals(1, c.getComponentInstances().size());
    }

    public void testStartStopAndDisposeCascadedtoChildren() {
        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponentInstance(sb);
        final MutablePicoContainer child = createPicoContainer(parent);
        parent.addChildContainer(child);
        child.registerComponentImplementation(LifeCycleMonitoring.class);
        parent.start();
        assertTrue(sb.toString().indexOf("-started") != -1);
        parent.stop();
        assertTrue(sb.toString().indexOf("-stopped") != -1);
        parent.dispose();
        assertTrue(sb.toString().indexOf("-disposed") != -1);

    }

    public void testStartStopAndDisposeNotCascadedtoRemovedChildren() {
        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponentInstance(sb);
        final MutablePicoContainer child = createPicoContainer(parent);
        parent.addChildContainer(child);
        child.registerComponentImplementation(LifeCycleMonitoring.class);
        parent.removeChildContainer(child);
        parent.start();
        assertTrue(sb.toString().indexOf("-started") == -1);
        parent.stop();
        assertTrue(sb.toString().indexOf("-stopped") == -1);
        parent.dispose();
        assertTrue(sb.toString().indexOf("-disposed") == -1);

    }

    public void testShouldCascadeStartStopAndDisposeToChild() {
        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponentInstance(sb);
        parent.registerComponentImplementation(Map.class, HashMap.class);

        final MutablePicoContainer child = parent.makeChildContainer();
        child.registerComponentImplementation(LifeCycleMonitoring.class);

        Map map = (Map) parent.getComponentInstance(Map.class);
        assertNotNull(map);
        assertTrue(sb.toString().indexOf("-started") == -1);

        parent.start();
        assertTrue(sb.toString().indexOf("-started") != -1);
        parent.stop();
        assertTrue(sb.toString().indexOf("-stopped") != -1);
        parent.dispose();
        assertTrue(sb.toString().indexOf("-disposed") != -1);
    }

    public static class LifeCycleMonitoring implements Startable, Disposable {
        StringBuffer sb;

        public LifeCycleMonitoring(StringBuffer sb) {
            this.sb = sb;
            sb.append("-instantiated");
        }
        public void start() {
            sb.append("-started");
        }
        public void stop() {
            sb.append("-stopped");
        }
        public void dispose() {
            sb.append("-disposed");
        }
    }

}
