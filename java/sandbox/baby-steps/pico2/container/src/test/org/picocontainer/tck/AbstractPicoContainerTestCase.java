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

import junit.framework.Assert;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Disposable;
import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.Startable;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.componentadapters.InstanceComponentAdapter;
import org.picocontainer.defaults.AbstractPicoVisitor;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.BasicComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.VerifyingVisitor;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.Washable;
import org.picocontainer.testmodel.WashableTouchable;

import org.jmock.MockObjectTestCase;

/**
 * This test tests (at least it should) all the methods in MutablePicoContainer.
 */
public abstract class AbstractPicoContainerTestCase extends MockObjectTestCase {

    protected abstract MutablePicoContainer createPicoContainer(PicoContainer parent);

    protected final MutablePicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponent(DependsOnTouchable.class);
        return pico;

    }

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependsOnTouchable() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainerWithDependsOnTouchableOnly();
        pico.registerComponent(Touchable.class, SimpleTouchable.class);
        return pico;
    }

    public void testBasicInstantiationAndContainment() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();    
        assertTrue("Component should be instance of Touchable", Touchable.class.isAssignableFrom(pico.getComponentAdapter(Touchable.class).getComponentImplementation()));
    }

    public void testRegisteredComponentsExistAndAreTheCorrectTypes() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        assertNotNull("Container should have Touchable component",
                pico.getComponentAdapter(Touchable.class));
        assertNotNull("Container should have DependsOnTouchable component",
                pico.getComponentAdapter(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                pico.getComponent(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                pico.getComponent(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertNull("should not have non existent component", pico.getComponentAdapter(Map.class));
    }

    public void testRegistersSingleInstance() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        StringBuffer sb = new StringBuffer();
        pico.registerComponent(sb);
        assertSame(sb, pico.getComponent(StringBuffer.class));
    }

    public void testContainerIsSerializable() throws PicoException,
            IOException, ClassNotFoundException {

        getTouchableFromSerializedContainer();

    }

    private Touchable getTouchableFromSerializedContainer() throws IOException, ClassNotFoundException {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        // Add a list too, using a constant parameter
        pico.registerComponent("list", ArrayList.class, new ConstantParameter(10));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (MutablePicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponent(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        return (Touchable) pico.getComponent(Touchable.class);
    }

    public void testSerializedContainerCanRetrieveImplementation() throws PicoException,
            IOException, ClassNotFoundException {

        Touchable touchable = getTouchableFromSerializedContainer();

        SimpleTouchable simpleTouchable = (SimpleTouchable) touchable;

        assertTrue(simpleTouchable.wasTouched);
    }


    public void testGettingComponentWithMissingDependencyFails() throws PicoException, PicoRegistrationException {
        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();
        try {
            picoContainer.getComponent(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (UnsatisfiableDependenciesException e) {
            assertSame(picoContainer.getComponentAdapter(DependsOnTouchable.class).getComponentImplementation(), e.getUnsatisfiableComponentAdapter().getComponentImplementation());
            final Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());

            // Touchable.class is now inside a List (the list of unsatisfied parameters) -- mparaz
            List unsatisfied = (List) unsatisfiableDependencies.iterator().next();
            assertEquals(1, unsatisfied.size());
            assertEquals(Touchable.class, unsatisfied.get(0));
        }
    }

    public void testDuplicateRegistration() throws Exception {
        try {
            MutablePicoContainer pico = createPicoContainer(null);
            pico.registerComponent(Object.class);
            pico.registerComponent(Object.class);
            fail("Should have failed with duplicate registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Object.class);
        }
    }

    public void testExternallyInstantiatedObjectsCanBeRegistgeredAndLookedUp() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        final HashMap map = new HashMap();
        pico.registerComponent(Map.class, map);
        assertSame(map, pico.getComponent(Map.class));
    }

    public void testAmbiguousResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponent("ping", String.class);
        pico.registerComponent("pong", "pang");
        try {
            pico.getComponent(String.class);
        } catch (AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testLookupWithUnregisteredKeyReturnsNull() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(String.class));
    }

    public void testLookupWithUnregisteredTypeReturnsNull() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(String.class));
    }

    public static class ListAdder {
        public ListAdder(Collection list) {
            list.add("something");
        }
    }

    public void testUnsatisfiableDependenciesExceptionGivesVerboseEnoughErrorMessage() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponent(ComponentD.class);

        try {
            pico.getComponent(ComponentD.class);
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

    public void testUnsatisfiableDependenciesExceptionGivesUnsatisfiedDependencyTypes() {
        MutablePicoContainer pico = createPicoContainer(null);
        // D depends on E and B
        pico.registerComponent(ComponentD.class);

        // first - do not register any dependency
        // should yield first unsatisfied dependency
        try {
            pico.getComponent(ComponentD.class);
        } catch (UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            List list = (List) unsatisfiableDependencies.iterator().next();
            final List expectedList = new ArrayList(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);
            assertEquals(expectedList, list);

            Class unsatisfiedDependencyType = e.getUnsatisfiedDependencyType();
            assertNotNull(unsatisfiedDependencyType);
            assertEquals(ComponentE.class, unsatisfiedDependencyType);
        }
        
        // now register only first dependency
        // should yield second unsatisfied dependency
        pico.registerComponent(ComponentE.class);
        try {
            pico.getComponent(ComponentD.class);
        } catch (UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            List list = (List) unsatisfiableDependencies.iterator().next();
            final List expectedList = new ArrayList(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);
            assertEquals(expectedList, list);

            Class unsatisfiedDependencyType = e.getUnsatisfiedDependencyType();
            assertNotNull(unsatisfiedDependencyType);
            assertEquals(ComponentB.class, unsatisfiedDependencyType);
        }        
    }
    
    public void testCyclicDependencyThrowsCyclicDependencyException() {
        assertCyclicDependencyThrowsCyclicDependencyException(createPicoContainer(null));
    }

    private static void assertCyclicDependencyThrowsCyclicDependencyException(MutablePicoContainer pico) {
        pico.registerComponent(ComponentB.class);
        pico.registerComponent(ComponentD.class);
        pico.registerComponent(ComponentE.class);

        try {
            pico.getComponent(ComponentD.class);
            fail("CyclicDependencyException expected");
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

    public void testCyclicDependencyThrowsCyclicDependencyExceptionWithParentContainer() {
        MutablePicoContainer pico = createPicoContainer(createPicoContainer(null));
        assertCyclicDependencyThrowsCyclicDependencyException(pico);
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

        picoContainer.getComponents(); // create all the instances at once
        assertFalse("instances should be created in same order as adapters are created",
                picoContainer.getComponents().get(0) instanceof String);
        assertTrue("instances should be created in same order as adapters are created",
                picoContainer.getComponents().get(1) instanceof String);

        MutablePicoContainer reversedPicoContainer = createPicoContainer(null);
        reversedPicoContainer.registerComponent(c2);
        reversedPicoContainer.registerComponent(c1);
        assertEquals("registration order should be maintained",
                Arrays.asList(new Object[]{c2, c1}), reversedPicoContainer.getComponentAdapters());

        reversedPicoContainer.getComponents(); // create all the instances at once
        assertTrue("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponents().get(0) instanceof String);
        assertFalse("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponents().get(1) instanceof String);
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

    public void testSameInstanceCanBeUsedAsDifferentTypeWhenCaching() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponent("wt", WashableTouchable.class);
        pico.registerComponent("nw", NeedsWashable.class);
        pico.registerComponent("nt", NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable) pico.getComponent("nw");
        NeedsTouchable nt = (NeedsTouchable) pico.getComponent("nt");
        assertSame(nw.washable, nt.touchable);
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer(null);

        try {
            pico.registerComponent(Serializable.class, new Object());
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

        pico.registerComponent("foo", JMSService.class, new Parameter[]{
            new ConstantParameter("0"),
            new ConstantParameter("something"),
        });
        JMSService jms = (JMSService) pico.getComponent("foo");
        assertEquals("0", jms.serverid);
        assertEquals("something", jms.path);
    }

    public static class ComponentA {
        public ComponentA(ComponentB b, ComponentC c) {
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
        }
    }

    public static class ComponentB {
    }

    public static class ComponentC {
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
        pico.registerComponent(ComponentA.class);
        pico.registerComponent(ComponentE.class);
        try {
            new VerifyingVisitor().traverse(pico);
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
//        pico.registerComponent(ContainerDependency.class);
//        ContainerDependency dep = (ContainerDependency) pico.getComponent(ContainerDependency.class);
//        assertSame(pico, dep.pico);
//    }

    public void testShouldReturnNullWhenUnregistereingUnmanagedComponent() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.unregisterComponentByInstance("yo"));
    }

    public void testShouldReturnNullForComponentAdapterOfUnregisteredType() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(List.class));
    }

    public void testShouldReturnNonMutableParent() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        final MutablePicoContainer picoContainer = createPicoContainer(parent);
        assertNotSame(parent, picoContainer.getParent());
        assertFalse(picoContainer.getParent() instanceof MutablePicoContainer);
    }

    class Foo implements Startable, Disposable {
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

    public void testContainerCascadesDefaultLifecycle() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        Foo foo = new Foo();
        picoContainer.registerComponent(foo);
        picoContainer.start();
        assertEquals(true, foo.started);
        picoContainer.stop();
        assertEquals(true, foo.stopped);
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

        a.registerComponent("a", ao);
        b.registerComponent("b", bo);
        c.registerComponent("c", co);

        assertEquals(1, a.getComponents().size());
        assertEquals(1, b.getComponents().size());
        assertEquals(1, c.getComponents().size());
    }

    public void testStartStopAndDisposeCascadedtoChildren() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponent(new StringBuffer());
        final MutablePicoContainer child = createPicoContainer(parent);
        parent.addChildContainer(child);
        child.registerComponent(LifeCycleMonitoring.class);
        parent.start();
        try {
            child.start();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        parent.stop();
        try {
            child.stop();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child not started", "Not started", e.getMessage());
        }
        parent.dispose();
        try {
            child.dispose();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already disposed", "Already disposed", e.getMessage());
        }

    }

    public void testMakingOfChildContainer() {
        final MutablePicoContainer parent = createPicoContainer(null);
        MutablePicoContainer child = parent.makeChildContainer();
        assertNotNull(child);
    }

    public void testMakingOfChildContainerPercolatesLifecycleManager() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponent("one", TestLifecycleComponent.class);
        MutablePicoContainer child = parent.makeChildContainer();
        assertNotNull(child);
        child.registerComponent("two", TestLifecycleComponent.class);
        parent.start();
        try {
            child.start();
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        //TODO - The LifecycleManager reference in child containers is not used. Thus is is almost pointless
        // The reason is because DefaultPicoContainer's accept() method visits child containers' on its own.
        // This may be file for visiting components in a tree for general cases, but for lifecycle, we
        // should hand to each LifecycleManager's start(..) at each appropriate node. See mail-list discussion.
    }

    public static class TestLifecycleManager implements LifecycleManager {
        public ArrayList started = new ArrayList();
        public void start(PicoContainer node) {
            started.add(node);
        }

        public void stop(PicoContainer node) {
        }

        public void dispose(PicoContainer node) {
        }

        public boolean hasLifecycle() {
            return true;
        }
    }

    public static class TestLifecycleComponent implements Startable {
        public boolean started;
        public void start() {
            started = true;
        }
        public void stop() {
        }
    }

    public void testStartStopAndDisposeNotCascadedtoRemovedChildren() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponent(new StringBuffer());
        StringBuffer sb = (StringBuffer) parent.getComponents(StringBuffer.class).get(0);

        final MutablePicoContainer child = createPicoContainer(parent);
        assertTrue(parent.addChildContainer(child));
        child.registerComponent(LifeCycleMonitoring.class);
        assertTrue(parent.removeChildContainer(child));
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
        parent.registerComponent(sb);
        parent.registerComponent(Map.class, HashMap.class);

        final MutablePicoContainer child = parent.makeChildContainer();
        child.registerComponent(LifeCycleMonitoring.class);

        Map map = (Map) parent.getComponent(Map.class);
        assertNotNull(map);
        parent.start();
        try {
            child.start();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        parent.stop();
        try {
            child.stop();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child not started", "Not started", e.getMessage());
        }
        parent.dispose();
        try {
            child.dispose();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already disposed", "Already disposed", e.getMessage());
        }
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

    public static class RecordingStrategyVisitor extends AbstractPicoVisitor {

        private final List list;

        public RecordingStrategyVisitor(List list) {
            this.list = list;
        }

        public void visitContainer(PicoContainer pico) {
            list.add(pico);
        }

        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            list.add(componentAdapter);
        }

        public void visitParameter(Parameter parameter) {
            list.add(parameter);
        }

    }

    public void testAcceptImplementsBreadthFirstStrategy() {
        final MutablePicoContainer parent = createPicoContainer(null);
        final MutablePicoContainer child = parent.makeChildContainer();
        ComponentAdapter hashMapAdapter = parent.registerComponent(new ConstructorInjectionComponentAdapter(HashMap.class, HashMap.class));
        ComponentAdapter hashSetAdapter = parent.registerComponent(new ConstructorInjectionComponentAdapter(HashSet.class, HashSet.class));
        ComponentAdapter stringAdapter = parent.registerComponent(new InstanceComponentAdapter(String.class, "foo"));
        ComponentAdapter arrayListAdapter = child.registerComponent(new ConstructorInjectionComponentAdapter(ArrayList.class, ArrayList.class));
        Parameter componentParameter = BasicComponentParameter.BASIC_DEFAULT;
        Parameter throwableParameter = new ConstantParameter(new Throwable("bar"));
        ComponentAdapter exceptionAdapter = child.registerComponent(new ConstructorInjectionComponentAdapter(Exception.class, Exception.class, new Parameter[]{
            componentParameter,
            throwableParameter
        }));

        List expectedList = Arrays.asList(new Object[]{
            parent,
            hashMapAdapter,
            hashSetAdapter,
            stringAdapter,
            child,
            arrayListAdapter,
            exceptionAdapter,
            componentParameter,
            throwableParameter
        });
        List visitedList = new LinkedList();
        PicoVisitor visitor = new RecordingStrategyVisitor(visitedList);
        visitor.traverse(parent);
        assertEquals(expectedList, visitedList);
    }

    public void testAmbiguousDependencies() throws PicoRegistrationException, PicoInitializationException {

        MutablePicoContainer pico = this.createPicoContainer(null);

        // Register two Touchables that Fred will be confused about
        pico.registerComponent(SimpleTouchable.class);
        pico.registerComponent(DerivedTouchable.class);

        // Register a confused DependsOnTouchable
        pico.registerComponent(DependsOnTouchable.class);

        try {
            pico.getComponent(DependsOnTouchable.class);
            fail("DependsOnTouchable should have been confused about the two Touchables");
        } catch (AmbiguousComponentResolutionException e) {
            List componentImplementations = Arrays.asList(e.getAmbiguousComponentKeys());
            assertTrue(componentImplementations.contains(DerivedTouchable.class));
            assertTrue(componentImplementations.contains(SimpleTouchable.class));

            assertTrue(e.getMessage().indexOf(DerivedTouchable.class.getName()) != -1);
        }
    }


    public static class DerivedTouchable extends SimpleTouchable {
        public DerivedTouchable() {
        }    
    }
    
    
    public static class NonGreedyClass {
        
        public int value = 0;
        
        public NonGreedyClass() {
            //Do nothing.
        }
        
        public NonGreedyClass(ComponentA component) {
            fail("Greedy Constructor should never have been called.  Instead got: " + component);
        }
            
        
    };
    
    public void testNoArgConstructorToBeSelected() {
        MutablePicoContainer pico = this.createPicoContainer(null);
        pico.registerComponent(ComponentA.class);
        pico.registerComponent(NonGreedyClass.class, NonGreedyClass.class, Parameter.ZERO);
        

        NonGreedyClass instance = (NonGreedyClass) pico.getComponent(NonGreedyClass.class);
        assertNotNull(instance);
    }

}
