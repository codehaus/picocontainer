package picocontainer.hierarchical;

import junit.framework.TestCase;
import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.lifecycle.Disposable;
import picocontainer.lifecycle.LifecyclePicoContainer;
import picocontainer.lifecycle.Startable;
import picocontainer.lifecycle.Stoppable;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

import java.util.ArrayList;
import java.util.List;

public class ClassicLifecycleTestCase extends TestCase {

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

        public void start() throws Exception {
            startCalled("One");
        }

        public void stop() throws Exception {
            stopCalled("One");
        }

        public void dispose() throws Exception {
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

        public void start() throws Exception {
            one.startCalled("Two");
        }

        public void stop() throws Exception {
            one.stopCalled("Two");
        }

        public void dispose() throws Exception {
            one.disposeCalled("Two");
        }
    }

    // TODO-Chris - try to remove 'implements Startable, Stoppable, Disposable'
    // Thus making this comp accidentally start()able.
    // We should be able to get this to work.
    public static class Three implements Startable, Stoppable, Disposable {
        One one;

        public Three(One one, Two two) {
            one.instantiation("Three");
            this.one = one;
        }

        public void start() throws Exception {
            one.startCalled("Three");
        }

        public void stop() throws Exception {
            one.stopCalled("Three");
        }

        public void dispose() throws Exception {
            one.disposeCalled("Three");
        }
    }

    public static class Four implements Startable, Stoppable, Disposable {
        One one;

        public Four(Two two, Three three, One one) {
            one.instantiation("Four");
            this.one = one;
        }

        public void start() throws Exception {
            one.startCalled("Four");
        }

        public void stop() throws Exception {
            one.stopCalled("Four");
        }

        public void dispose() throws Exception {
            one.disposeCalled("Four");
        }
    }








//    public static interface StartStopDisposable extends Startable, Stoppable, Disposable {
  //  }



    public void testOrderOfInstantiation() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(Four.class);
        pico.registerComponent(Two.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Three.class);

        pico.instantiateComponents();

        Startable startup = (Startable) pico.getAggregateComponentProxy(true, false);
        Stoppable shutdown = (Stoppable) pico.getAggregateComponentProxy(false, false);
        Disposable disposal = (Disposable) pico.getAggregateComponentProxy(false, false);

        assertTrue("There should have been a 'One' in the container", pico.hasComponent(One.class));

        One one = (One) pico.getComponent(One.class);

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Should be four elems", 4, one.getInstantiating().size());
        assertEquals("Incorrect Order of Instantiation", "One", one.getInstantiating().get(0));
        assertEquals("Incorrect Order of Instantiation", "Two", one.getInstantiating().get(1));
        assertEquals("Incorrect Order of Instantiation", "Three", one.getInstantiating().get(2));
        assertEquals("Incorrect Order of Instantiation", "Four", one.getInstantiating().get(3));

        startup.start();

        // post instantiation startup
        assertEquals("Should be four elems", 4, one.getStarting().size());
        assertEquals("Incorrect Order of Starting", "One", one.getStarting().get(0));
        assertEquals("Incorrect Order of Starting", "Two", one.getStarting().get(1));
        assertEquals("Incorrect Order of Starting", "Three", one.getStarting().get(2));
        assertEquals("Incorrect Order of Starting", "Four", one.getStarting().get(3));

        shutdown.stop();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, one.getStopping().size());
        assertEquals("Incorrect Order of Stopping", "Four", one.getStopping().get(0));
        assertEquals("Incorrect Order of Stopping", "Three", one.getStopping().get(1));
        assertEquals("Incorrect Order of Stopping", "Two", one.getStopping().get(2));
        assertEquals("Incorrect Order of Stopping", "One", one.getStopping().get(3));

        disposal.dispose();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, one.getDisposing().size());
        assertEquals("Incorrect Order of Stopping", "Four", one.getDisposing().get(0));
        assertEquals("Incorrect Order of Stopping", "Three", one.getDisposing().get(1));
        assertEquals("Incorrect Order of Stopping", "Two", one.getDisposing().get(2));
        assertEquals("Incorrect Order of Stopping", "One", one.getDisposing().get(3));

    }



    public void testStartStopStartStopAndDispose() throws Exception {

        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        pico.start();
        pico.stop();

        pico.start();
        pico.stop();

        pico.dispose();

    }

    public void testStartStartCausingBarf() throws Exception {

        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        pico.start();
        try {
            pico.start();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public void testStartStopStopCausingBarf() throws Exception {
        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();
        pico.start();
        pico.stop();
        try {
            pico.stop();
            fail("Should have barfed");
        } catch (IllegalStateException e) {
            // expected;
        }
    }

    public void testDisposeDisposeCausingBarf() throws Exception {
        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();
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


    public void testStartStopDisposeDisposeCausingBarf() throws Exception {
        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();
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
        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);
        pico.registerComponent(FooRunnable.class);

        pico.instantiateComponents();
        pico.start();
        Thread.sleep(100);
        pico.stop();

        FooRunnable foo = (FooRunnable) pico.getComponent(FooRunnable.class);
        assertEquals(1, foo.runCount());
        pico.start();
        Thread.sleep(100);
        pico.stop();
        assertEquals(2, foo.runCount());

    }

    public void testForgivingNatureOfSoNamedContainer() throws Exception {

        LifecyclePicoContainer pico = new LifecyclePicoContainer.Default();

        // Wilma is not Startable (etc). This container should be able to handle the
        // fact that none of the comps are Startable (etc).
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        pico.start();

    }



}
