package org.picocontainer.extras;

import junit.framework.TestCase;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapterTestCase extends TestCase {
    private Runner runner1;
    private Runner runner2;

    class Runner implements Runnable {
        public Exception exception;
        public Blocker blocker;
        private PicoContainer pico;

        public Runner(PicoContainer pico) {
            this.pico = pico;
        }

        public void run() {
            try {
                blocker = (Blocker) pico.getComponentInstance("key");
            } catch (Exception e) {
                exception = e;
            }
        }
    }

    public static class Blocker {
        public Blocker() throws InterruptedException {
            // Yes I know sleeping in tests is bad, but it's also simple :-)
            Thread.sleep(300);
        }
    }

    private void initTest(ComponentAdapter componentAdapter) throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(componentAdapter);

        runner1 = new Runner(pico);
        runner2 = new Runner(pico);

        Thread racer1 = new Thread(runner1);
        Thread racer2 = new Thread(runner2);

        racer1.start();
        racer2.start();

        racer1.join();
        racer2.join();
    }

    public void testRaceConditionIsHandledBySynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorComponentAdapter("key", Blocker.class));
        SynchronizedComponentAdapter synchronizedComponentAdapter = new SynchronizedComponentAdapter(componentAdapter);
        initTest(synchronizedComponentAdapter);

        assertNull(runner1.exception);
        assertNull(runner2.exception);

        assertNotNull(runner1.blocker);
        assertSame(runner1.blocker, runner2.blocker);
    }

    public void testRaceConditionIsNotHandledWithoutSynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorComponentAdapter("key", Blocker.class));
        initTest(componentAdapter);

        assertTrue(runner1.exception != null || runner2.exception != null);
    }
}
