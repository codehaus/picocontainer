/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import junit.framework.TestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.Stoppable;
import org.picocontainer.lifecycle.Disposable;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.ArrayList;
import java.util.List;

public class DefaultLifecyclePicoAdaptorTestCase extends TestCase {

    public static class One implements Startable, Stoppable, Disposable {

        List instantiating = new ArrayList();
        List starting = new ArrayList();
        List stopping = new ArrayList();
        List disposing = new ArrayList();

        public One() {
            instantiation("One");
        }

        public void instantiation(String s) {
            instantiating.add(s);
        }

        public List getInstantiating() {
            return instantiating;
        }

        public List getStarting() {
            return starting;
        }

        public List getStopping() {
            return stopping;
        }

        public List getDisposing() {
            return disposing;
        }

        public void start() {
            startCalled("One");
        }

        public void stop() {
            stopCalled("One");
        }

        public void dispose() {
            disposeCalled("One");
        }

        public void startCalled(String msg) {
            starting.add(msg);
        }

        public void stopCalled(String msg) {
            stopping.add(msg);
        }

        public void disposeCalled(String msg) {
            disposing.add(msg);
        }

    }

    public static class Two implements Startable, Stoppable, Disposable {
        One one;

        public Two(One one) {
            one.instantiation("Two");
            this.one = one;
        }

        public void start() {
            one.startCalled("Two");
        }

        public void stop() {
            one.stopCalled("Two");
        }

        public void dispose() {
            one.disposeCalled("Two");
        }
    }

    public static class Three implements Startable, Stoppable, Disposable {
        One one;

        public Three(One one, Two two) {
            one.instantiation("Three");
            this.one = one;
        }

        public void start() {
            one.startCalled("Three");
        }

        public void stop() {
            one.stopCalled("Three");
        }

        public void dispose() {
            one.disposeCalled("Three");
        }
    }

    public static class Four implements Startable, Stoppable, Disposable {
        One one;

        public Four(Two two, Three three, One one) {
            one.instantiation("Four");
            this.one = one;
        }

        public void start() {
            one.startCalled("Four");
        }

        public void stop() {
            one.stopCalled("Four");
        }

        public void dispose() {
            one.disposeCalled("Four");
        }
    }


    public void testOrderOfInstantiationWithoutAdapter() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(Four.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);

        assertEquals(4, pico.getComponentInstances().size());

        Startable startup = (Startable) pico.getComponentMulticaster(true, false);
        Stoppable shutdown = (Stoppable) pico.getComponentMulticaster(false, false);
        Disposable disposal = (Disposable) pico.getComponentMulticaster(false, false);

        assertTrue("There should have been a 'One' in the internals", pico.hasComponent(One.class));

        One one = (One) pico.getComponentInstance(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems", 4, one.getInstantiating().size());
        assertEquals("Incorrect Order of Instantiation", "One", one.getInstantiating().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getInstantiating().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getInstantiating().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getInstantiating().get(3));

        startStopDisposeLifecycleComps(startup, shutdown, disposal, one);

    }


    public void testStartStopStartStopAndDispose() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);

        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(Three.class);
        pico.registerComponentImplementation(Four.class);

        pico.getComponentInstances();

        One one = (One) pico.getComponentInstance(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems", 4, one.getInstantiating().size());
        assertEquals("Incorrect Order of Instantiation", "One", one.getInstantiating().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getInstantiating().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getInstantiating().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getInstantiating().get(3));

        startStopDisposeLifecycleComps(lifecycle, lifecycle, lifecycle, one);

    }

    private void startStopDisposeLifecycleComps(Startable start, Stoppable stop, Disposable disp, One one) throws Exception {
        start.start();

        // post instantiation startup
        assertEquals("Should be four elems", 4, one.getStarting().size());
        assertEquals("Incorrect Order of Starting", "One", one.getStarting().get(0));
        assertEquals("Incorrect Order of Starting", "Two", one.getStarting().get(1));
        assertEquals("Incorrect Order of Starting", "Three", one.getStarting().get(2));
        assertEquals("Incorrect Order of Starting", "Four", one.getStarting().get(3));

        stop.stop();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, one.getStopping().size());
        assertEquals("Incorrect Order of Stopping", "Four", one.getStopping().get(0));
        assertEquals("Incorrect Order of Stopping", "Three", one.getStopping().get(1));
        assertEquals("Incorrect Order of Stopping", "Two", one.getStopping().get(2));
        assertEquals("Incorrect Order of Stopping", "One", one.getStopping().get(3));

        disp.dispose();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, one.getDisposing().size());
        assertEquals("Incorrect Order of Stopping", "Four", one.getDisposing().get(0));
        assertEquals("Incorrect Order of Stopping", "Three", one.getDisposing().get(1));
        assertEquals("Incorrect Order of Stopping", "Two", one.getDisposing().get(2));
        assertEquals("Incorrect Order of Stopping", "One", one.getDisposing().get(3));
    }


    public void testStartStartCausingBarf() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(SimpleTouchable.class);

        pico.getComponentInstances();

        assertTrue(lifecycle.isStopped());
        lifecycle.start();
        assertTrue(lifecycle.isStarted());
        try {
            lifecycle.start();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
            assertTrue(lifecycle.isStarted());
        }
    }

    public void testStartStopStopCausingBarf() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(SimpleTouchable.class);

        pico.getComponentInstances();
        assertTrue(lifecycle.isStopped());
        lifecycle.start();
        assertTrue(lifecycle.isStarted());
        lifecycle.stop();
        assertTrue(lifecycle.isStopped());
        try {
            lifecycle.stop();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
            assertTrue(lifecycle.isStopped());
        }
    }

    public void testDisposeDisposeCausingBarf() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(SimpleTouchable.class);

        pico.getComponentInstances();
        lifecycle.start();
        lifecycle.stop();
        assertFalse(lifecycle.isDisposed());
        lifecycle.dispose();
        assertTrue(lifecycle.isDisposed());
        try {
            lifecycle.dispose();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
            assertTrue(lifecycle.isDisposed());
        }
    }


    public void testStartStopDisposeDisposeCausingBarf() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(SimpleTouchable.class);

        pico.getComponentInstances();
        lifecycle.start();
        lifecycle.stop();
        lifecycle.dispose();
        try {
            lifecycle.dispose();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public static class FooRunnable implements Runnable, Startable, Stoppable {
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
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(FooRunnable.class);

        pico.getComponentInstances();
        lifecycle.start();
        assertTrue(lifecycle.isStarted());
        Thread.sleep(100);
        lifecycle.stop();
        assertTrue(lifecycle.isStopped());

        FooRunnable foo = (FooRunnable) pico.getComponentInstance(FooRunnable.class);
        assertEquals(1, foo.runCount());
        lifecycle.start();
        assertTrue(lifecycle.isStarted());
        Thread.sleep(100);
        lifecycle.stop();
        assertTrue(lifecycle.isStopped());
        assertEquals(2, foo.runCount());

    }

    public void testForgivingNatureOfLifecycleAdapter() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        // Touchable is not Startable (etc). This internals should be able to handle the
        // fact that none of the comps are Startable (etc).
        pico.registerComponentImplementation(SimpleTouchable.class);

        pico.getComponentInstances();

        assertTrue(lifecycle.isStopped());
        lifecycle.start();
        assertTrue(lifecycle.isStarted());

    }

}
