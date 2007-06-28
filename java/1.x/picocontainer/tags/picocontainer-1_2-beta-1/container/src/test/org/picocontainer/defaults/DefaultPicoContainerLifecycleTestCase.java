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
import junit.framework.TestCase;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */

/**
 * This class tests the lifecycle aspects of DefaultPicoContainer.
 */
public class DefaultPicoContainerLifecycleTestCase extends TestCase {

    public abstract static class RecordingLifecycle implements Startable, Disposable {
        private final StringBuffer recording;

        protected RecordingLifecycle(StringBuffer recording) {
            this.recording = recording;
        }

        public void start() {
            recording.append("<" + code());
        }

        public void stop() {
            recording.append(code() + ">");
        }

        public void dispose() {
            recording.append("!" + code());
        }

        private String code() {
            String name = getClass().getName();
            return name.substring(name.indexOf('$') + 1);
        }
    }

    public static class One extends RecordingLifecycle {
        public One(StringBuffer sb) {
            super(sb);
        }
    }

    public static class Two extends RecordingLifecycle {
        public Two(StringBuffer sb, One one) {
            super(sb);
            assertNotNull(one);
        }
    }

    public static class Three extends RecordingLifecycle {
        public Three(StringBuffer sb, One one, Two two) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
        }
    }

    public static class Four extends RecordingLifecycle {
        public Four(StringBuffer sb, Two two, Three three, One one) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
            assertNotNull(three);
        }
    }

    public static class FiveTriesToBeMalicious extends RecordingLifecycle {
        public FiveTriesToBeMalicious(StringBuffer sb, PicoContainer pc) {
            super(sb);
            assertNotNull(pc);
            sb.append("Whao! Should not get instantiated!!");
        }
    }

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

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", parent.getComponentInstance("recording").toString());
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
        MutablePicoContainer child = parent.makeChildContainer();
        child.registerComponentImplementation(Three.class);
        child.registerComponentImplementation(FiveTriesToBeMalicious.class);
        try {
            parent.start();
            fail();
        } catch (UnsatisfiableDependenciesException expected) {
        }
    }

    public static class NotStartable {
        public NotStartable() {
            Assert.fail("Shouldn't be instantiated");
        }
    }

    public void testOnlyStartableComponentsAreInstantiatedOnStart() {
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



}