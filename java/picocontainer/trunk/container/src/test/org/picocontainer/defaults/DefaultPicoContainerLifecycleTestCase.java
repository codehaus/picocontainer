/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.Assert;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.monitors.LifecycleComponentMonitor;
import org.picocontainer.monitors.LifecycleComponentMonitor.LifecycleFailuresException;
import org.picocontainer.testmodel.RecordingLifecycle.FiveTriesToBeMalicious;
import org.picocontainer.testmodel.RecordingLifecycle.Four;
import org.picocontainer.testmodel.RecordingLifecycle.One;
import org.picocontainer.testmodel.RecordingLifecycle.Three;
import org.picocontainer.testmodel.RecordingLifecycle.Two;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class tests the lifecycle aspects of DefaultPicoContainer.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class DefaultPicoContainerLifecycleTestCase extends MockObjectTestCase {


    public void testOrderOfInstantiationShouldBeDependencyOrder() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("recording", StringBuffer.class);
        pico.registerComponentImplementation(Four.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);
        final List componentInstances = pico.getComponentInstances();

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Incorrect Order of Instantiation", One.class, componentInstances.get(1).getClass());
        assertEquals("Incorrect Order of Instantiation", Two.class, componentInstances.get(2).getClass());
        assertEquals("Incorrect Order of Instantiation", Three.class, componentInstances.get(3).getClass());
        assertEquals("Incorrect Order of Instantiation", Four.class, componentInstances.get(4).getClass());
    }

    public void testOrderOfStartShouldBeDependencyOrderAndStopAndDisposeTheOpposite() throws Exception {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = parent.makeChildContainer();

        parent.registerComponentImplementation("recording", StringBuffer.class);
        child.registerComponentImplementation(Four.class);
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation(One.class);
        child.registerComponentImplementation(Three.class);

        parent.start();
        parent.stop();
        parent.dispose();

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One",
                parent.getComponentInstance("recording").toString());
    }


    public void testLifecycleIsIgnoredIfAdaptersAreNotLifecycleManagers() {
        DefaultPicoContainer parent = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        MutablePicoContainer child = parent.makeChildContainer();

        parent.registerComponentImplementation("recording", StringBuffer.class);
        child.registerComponentImplementation(Four.class);
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation(One.class);
        child.registerComponentImplementation(Three.class);

        parent.start();
        parent.stop();
        parent.dispose();

        assertEquals("",
                parent.getComponentInstance("recording").toString());
    }

    public void testStartStartShouldFail() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.start();
        try {
            pico.start();
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public void testStartStopStopShouldFail() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.start();
        pico.stop();
        try {
            pico.stop();
            fail("Should have failed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public void testStartStopDisposeDisposeShouldFail() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.start();
        pico.stop();
        pico.dispose();
        try {
            pico.dispose();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public static class FooRunnable implements Runnable, Startable {
        private int runCount;
        private Thread thread = new Thread();
        private boolean interrupted;

        public FooRunnable() {
        }

        public int runCount() {
            return runCount;
        }

        public boolean isInterrupted() {
            return interrupted;
        }

        public void start() {
            thread = new Thread(this);
            thread.start();
        }

        public void stop() {
            thread.interrupt();
        }

        // this would do something a bit more concrete
        // than counting in real life !
        public void run() {
            runCount++;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
    }

    public void testStartStopOfDaemonizedThread() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(FooRunnable.class);

        pico.getComponentInstances();
        pico.start();
        Thread.sleep(100);
        pico.stop();

        FooRunnable foo = (FooRunnable) pico.getComponentInstance(FooRunnable.class);
        assertEquals(1, foo.runCount());
        pico.start();
        Thread.sleep(100);
        pico.stop();
        assertEquals(2, foo.runCount());
    }

    public void testGetComponentInstancesOnParentContainerHostedChildContainerDoesntReturnParentAdapter() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = parent.makeChildContainer();
        assertEquals(0, child.getComponentInstances().size());
    }

    public void testComponentsAreStartedBreadthFirstAndStoppedAndDisposedDepthFirst() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation("recording", StringBuffer.class);
        parent.registerComponentImplementation(One.class);
        MutablePicoContainer child = parent.makeChildContainer();
        child.registerComponentImplementation(Three.class);
        parent.start();
        parent.stop();
        parent.dispose();

        assertEquals("<One<Two<ThreeThree>Two>One>!Three!Two!One", parent.getComponentInstance("recording").toString());
    }

    public void testMaliciousComponentCannotExistInAChildContainerAndSeeAnyElementOfContainerHierarchy() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation("recording", StringBuffer.class);
        parent.registerComponentImplementation(One.class);
        parent.registerComponentImplementation(Three.class);
        MutablePicoContainer child = parent.makeChildContainer();
        child.registerComponentImplementation(FiveTriesToBeMalicious.class);
        try {
            parent.start();
            fail("Thrown " + UnsatisfiableDependenciesException.class.getName() + " expected");
        } catch ( UnsatisfiableDependenciesException e) {
            // FiveTriesToBeMalicious can't get instantiated as there is no PicoContainer in any component set
        }
        String recording = parent.getComponentInstance("recording").toString();
        assertEquals("<One<Two<Three", recording);
        try {
            child.getComponentInstanceOfType(FiveTriesToBeMalicious.class);
            fail("Thrown " + UnsatisfiableDependenciesException.class.getName() + " expected");
        } catch (final UnsatisfiableDependenciesException e) {
            // can't get instantiated as there is no PicoContainer in any component set
        }
        recording = parent.getComponentInstance("recording").toString();
        assertEquals("<One<Two<Three", recording); // still the same
    }


    public static class NotStartable {
         public void start(){
            Assert.fail("start() should not get invoked on NonStartable");
        }
    }

    public void testOnlyStartableComponentsAreStartedOnStart() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("recording", StringBuffer.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(NotStartable.class);
        pico.start();
        pico.stop();
        pico.dispose();
        assertEquals("<OneOne>!One", pico.getComponentInstance("recording").toString());
    }

    public void testShouldFailOnStartAfterDispose() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.dispose();
        try {
            pico.start();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testShouldFailOnStopAfterDispose() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.dispose();
        try {
            pico.stop();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testShouldStackContainersLast() {
        // this is merely a code coverage test - but it doesn't seem to cover the StackContainersAtEndComparator
        // fully. oh well.
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(ArrayList.class);
        pico.registerComponentImplementation(DefaultPicoContainer.class);
        pico.registerComponentImplementation(HashMap.class);
        pico.start();
        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance(DefaultPicoContainer.class);
        // it should be started too
        try {
            childContainer.start();
            fail();
        } catch (IllegalStateException e) {
        }
    }

    public void testCanSpecifyLifeCycleStrategyForInstanceRegistrationWhenSpecifyingComponentAdapterFactory()
        throws Exception
    {
        LifecycleStrategy strategy = new LifecycleStrategy() {
            public void start(Object component) {
                ((StringBuffer)component).append("start>");
            }

            public void stop(Object component) {
                ((StringBuffer)component).append("stop>");
            }

            public void dispose(Object component) {
                ((StringBuffer)component).append("dispose>");
            }

            public boolean hasLifecycle(Class type) {
                return true;
            }
        };
        MutablePicoContainer pico = new DefaultPicoContainer( new DefaultComponentAdapterFactory(), strategy, null );

        StringBuffer sb = new StringBuffer();

        pico.registerComponentInstance(sb);

        pico.start();
        pico.stop();
        pico.dispose();

        assertEquals("start>stop>dispose>", sb.toString());
    }

    public void testLifeCycleStrategyForInstanceRegistrationPassedToChildContainers()
        throws Exception
    {
        LifecycleStrategy strategy = new LifecycleStrategy() {
            public void start(Object component) {
                ((StringBuffer)component).append("start>");
            }

            public void stop(Object component) {
                ((StringBuffer)component).append("stop>");
            }

            public void dispose(Object component) {
                ((StringBuffer)component).append("dispose>");
            }

            public boolean hasLifecycle(Class type) {
                return true;
            }
        };
        MutablePicoContainer parent = new DefaultPicoContainer(strategy, null);
        MutablePicoContainer pico = parent.makeChildContainer();

        StringBuffer sb = new StringBuffer();

        pico.registerComponentInstance(sb);

        pico.start();
        pico.stop();
        pico.dispose();

        assertEquals("start>stop>dispose>", sb.toString());
    }


    public void testLifecycleDoesNotRecoverwithDefaultComponentMonitor() throws NoSuchMethodException {

        Mock s1 = mock(Startable.class, "s1");
        s1.expects(once()).method("start").will(throwException(new RuntimeException("I do not want to start myself")));

        Mock s2 = mock(Startable.class, "s2");

        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.registerComponentInstance("foo", s1.proxy());
        dpc.registerComponentInstance("bar", s2.proxy());
        try {
            dpc.start();
        } catch (RuntimeException e) {
            assertEquals("I do not want to start myself", e.getCause().getMessage());
        }
        try {
            dpc.stop();
        } catch (Exception e) {
            assertEquals("Not started",e.getMessage());
        }
    }

    public void testLifecycleCanRecoverWithCustomComponentMonitor() throws NoSuchMethodException {

        Mock s1 = mock(Startable.class, "s1");
        s1.expects(once()).method("start").will(throwException(new RuntimeException("I do not want to start myself")));
        s1.expects(once()).method("stop");

        Mock s2 = mock(Startable.class, "s2");
        s2.expects(once()).method("start");
        s2.expects(once()).method("stop");

        Mock cm = mock(ComponentMonitor.class);

        // s1 expectations
        cm.expects(once()).method("invoking").with(eq(Startable.class.getMethod("start", (Class[])null)), same(s1.proxy()));
        cm.expects(once()).method("lifecycleInvocationFailed").with(isA(Method.class),same(s1.proxy()), isA(RuntimeException.class) );
        cm.expects(once()).method("invoking").with(eq(Startable.class.getMethod("stop", (Class[])null)), same(s1.proxy()));
        cm.expects(once()).method("invoked").with(eq(Startable.class.getMethod("stop", (Class[])null)), same(s1.proxy()), ANYTHING);

        // s2 expectations
        cm.expects(once()).method("invoking").with(eq(Startable.class.getMethod("start", (Class[])null)), same(s2.proxy()));
        cm.expects(once()).method("invoked").with(eq(Startable.class.getMethod("start", (Class[])null)), same(s2.proxy()), ANYTHING);
        cm.expects(once()).method("invoking").with(eq(Startable.class.getMethod("stop", (Class[])null)), same(s2.proxy()));
        cm.expects(once()).method("invoked").with(eq(Startable.class.getMethod("stop", (Class[])null)), same(s2.proxy()), ANYTHING);

        DefaultPicoContainer dpc = new DefaultPicoContainer((ComponentMonitor) cm.proxy());
        dpc.registerComponentInstance("foo", s1.proxy());
        dpc.registerComponentInstance("bar", s2.proxy());
        dpc.start();
        dpc.stop();
    }

    public void testLifecycleFailuresCanBePickedUpAfterTheEvent() throws NoSuchMethodException {

        Mock s1 = mock(Startable.class, "s1");
        s1.expects(once()).method("start").will(throwException(new RuntimeException("I do not want to start myself")));
        s1.expects(once()).method("stop");

        Mock s2 = mock(Startable.class, "s2");
        s2.expects(once()).method("start");
        s2.expects(once()).method("stop");

        LifecycleComponentMonitor lifecycleComponentMonitor = new LifecycleComponentMonitor();

        DefaultPicoContainer dpc = new DefaultPicoContainer(lifecycleComponentMonitor);
        dpc.registerComponentInstance("foo", s1.proxy());
        dpc.registerComponentInstance("bar", s2.proxy());

        dpc.start();

        try {
            lifecycleComponentMonitor.rethrowLifecycleFailuresException();
        } catch (LifecycleFailuresException e) {
            dpc.stop();
            assertEquals(1, e.getFailures().size());
        }

    }

    public void testStartedComponentsCanBeStoppedIfSomeComponentsFailToStart() throws NoSuchMethodException {

        Mock s1 = mock(Startable.class, "s1");
        s1.expects(once()).method("start");
        s1.expects(once()).method("stop");

        Mock s2 = mock(Startable.class, "s2");
        s2.expects(once()).method("start").will(throwException(new RuntimeException("I do not want to start myself")));
        // s2 does not expect stop().

        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.registerComponentInstance("foo", s1.proxy());
        dpc.registerComponentInstance("bar", s2.proxy());

        try {
            dpc.start();
        } catch (RuntimeException e) {
            dpc.stop();
        }

    }

}