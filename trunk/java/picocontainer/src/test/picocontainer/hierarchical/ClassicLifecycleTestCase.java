package picocontainer.hierarchical;

import junit.framework.TestCase;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.NullContainer;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

import java.util.List;
import java.util.ArrayList;

public class ClassicLifecycleTestCase extends TestCase {

    public static class One implements ClassicStartingUpLifecycle, ClassicShuttonDownLifecycle {

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
            stopping.add(msg);
        }

    }

    public static class Two implements ClassicStartingUpLifecycle, ClassicShuttonDownLifecycle {
        One one;
        public Two(One one) {
            one.instantiation("Two");
            this.one = one;
        }
        public void start() throws Exception {
            one.startCalled("One");
        }
        public void stop() throws Exception {
            one.stopCalled("One");
        }
        public void dispose() throws Exception {
            one.disposeCalled("One");
        }
    }

    public static class Three implements ClassicStartingUpLifecycle, ClassicShuttonDownLifecycle {
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

    public static class Four implements ClassicStartingUpLifecycle, ClassicShuttonDownLifecycle {
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


    public static interface ClassicStartingUpLifecycle {
        void start() throws Exception;
    }
    public static interface ClassicShuttonDownLifecycle {
        void stop() throws Exception;
        void dispose() throws Exception;
    }


    public void testOrderOfInstantiation() throws Exception {

        MorphingHierarchicalPicoContainer pico = new MorphingHierarchicalPicoContainer(new NullContainer(), new DefaultComponentFactory());

        pico.registerComponent(Four.class);
        pico.registerComponent(Two.class);
        pico.registerComponent(One.class);
        pico.registerComponent(Three.class);

        pico.initializeContainer();

        ClassicStartingUpLifecycle startup = (ClassicStartingUpLifecycle) pico.asLifecycle(ClassicStartingUpLifecycle.class, MorphingHierarchicalPicoContainer.INSTANTIATION_ORDER);
        ClassicShuttonDownLifecycle shutdown = (ClassicShuttonDownLifecycle) pico.asLifecycle(ClassicShuttonDownLifecycle.class, MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER);

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

        shutdown.dispose();

        // post instantiation shutdown - REVERSE order.
        assertEquals("Should be four elems", 4, one.getDisposing().size());
        assertEquals("Incorrect Order of Stopping", "Four", one.getDisposing().get(0));
        assertEquals("Incorrect Order of Stopping", "Three", one.getDisposing().get(1));
        assertEquals("Incorrect Order of Stopping", "Two", one.getDisposing().get(2));
        assertEquals("Incorrect Order of Stopping", "One", one.getDisposing().get(3));

    }


    public void testStartStopStartStopAndDispose() throws Exception {

        MorphingHierarchicalPicoContainer pico = new MorphingHierarchicalPicoContainer(null, new DefaultComponentFactory());

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.initializeContainer();

        ClassicStartingUpLifecycle startup = (ClassicStartingUpLifecycle) pico.asLifecycle(ClassicStartingUpLifecycle.class, MorphingHierarchicalPicoContainer.INSTANTIATION_ORDER);
        ClassicShuttonDownLifecycle shutdown = (ClassicShuttonDownLifecycle) pico.asLifecycle(ClassicShuttonDownLifecycle.class, MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER);


        startup.start();
        shutdown.stop();

        startup.start();
        shutdown.stop();

        shutdown.dispose();

    }


//    public void testStartStartCausingBarf() throws PicoInitializationException, PicoRegistrationException{
//        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
//
//        pico.registerComponent(FredImpl.class);
//        pico.registerComponent(WilmaImpl.class);
//
//        pico.initializeContainer();
//        try {
//            pico.initializeContainer();
//            fail("Should have barfed");
//        } catch (IllegalStateException e) {
//            // expected;
//        }
//    }
//
//    public void testStartStopStopCausingBarf() throws PicoInitializationException, PicoRegistrationException {
//        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
//
//        pico.registerComponent(FredImpl.class);
//        pico.registerComponent(WilmaImpl.class);
//
//        pico.initializeContainer();
//        pico.stop();
//        try {
//            pico.stop();
//            fail("Should have barfed");
//        } catch (IllegalStateException e) {
//            // expected;
//        }
//    }
//
//    public void testStartStopDisposeDisposeCausingBarf() throws PicoInitializationException, PicoRegistrationException {
//        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
//
//        pico.registerComponent(FredImpl.class);
//        pico.registerComponent(WilmaImpl.class);
//
//        pico.initializeContainer();
//        pico.stop();
//        pico.dispose();
//        try {
//            pico.dispose();
//            fail("Should have barfed");
//        } catch (IllegalStateException e) {
//            // expected;
//        }
//    }
//
//    public static class Foo implements Runnable {
//        private int runCount;
//        private Thread thread = new Thread();
//        private boolean interrupted;
//
//        public Foo() {
//        }
//
//        public int runCount() {
//            return runCount;
//        }
//
//        public boolean isInterrupted() {
//            return interrupted;
//        }
//
//        public void initializeContainer() {
//            thread = new Thread(this);
//            thread.initializeContainer();
//        }
//
//        public void stop() {
//            thread.interrupt();
//        }
//
//        // this would do something a bit more concrete
//        // than counting in real life !
//        public void run() {
//            runCount++;
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                interrupted = true;
//            }
//        }
//    }
//
//    public void testStartStopOfDaemonizedThread() throws PicoInitializationException, PicoRegistrationException, InterruptedException {
//        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.WithStartableLifecycleManager(new ReflectionUsingLifecycleManager());
//
//        pico.registerComponent(FredImpl.class);
//        pico.registerComponent(WilmaImpl.class);
//        pico.registerComponent(Foo.class);
//
//        pico.initializeContainer();
//        Thread.sleep(100);
//        pico.stop();
//        Foo foo = (Foo) pico.getComponent(Foo.class);
//        assertEquals(1, foo.runCount());
//        pico.initializeContainer();
//        Thread.sleep(100);
//        pico.stop();
//        assertEquals(2, foo.runCount());
//
//    }
//


}
