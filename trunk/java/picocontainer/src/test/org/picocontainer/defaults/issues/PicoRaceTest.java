package org.picocontainer.defaults.issues;

import EDU.oswego.cs.dl.util.concurrent.CountDown;
import junit.framework.TestCase;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.SynchronizedComponentAdapter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.SynchronizedComponentAdapterFactory;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Collections;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y (testSingletonCreationWithSynchronizedAdapterOutside())
 */
public class PicoRaceTest extends TestCase {

    public void THIS_NATURALLY_FAILS_testSingletonCreationRace() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("slow", SlowCtor.class);
        runConcurrencyTest(pico);
    }

    public void THIS_NATURALLY_FAILS_testSingletonCreationWithSynchronizedAdapter() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new CachingComponentAdapter(new SynchronizedComponentAdapter(new ConstructorInjectionComponentAdapter("slow", SlowCtor.class))));
        runConcurrencyTest(pico);
    }

    // This is overkill - an outer sync adapter is enough
    public void testSingletonCreationWithSynchronizedAdapterAndDoubleLocking() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new SynchronizedComponentAdapter(new CachingComponentAdapter(new SynchronizedComponentAdapter(new ConstructorInjectionComponentAdapter("slow", SlowCtor.class)))));
        runConcurrencyTest(pico);
    }

    public void testSingletonCreationWithSynchronizedAdapterOutside() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new SynchronizedComponentAdapter(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter("slow", SlowCtor.class))));
        runConcurrencyTest(pico);
    }

    public void testSingletonCreationWithSynchronizedAdapterOutsideUsingFactory() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer(
                new SynchronizedComponentAdapterFactory(
                        new CachingComponentAdapterFactory(
                                new ConstructorInjectionComponentAdapterFactory()
                        )
                )
        );
        pico.registerComponentImplementation("slow", SlowCtor.class);
        runConcurrencyTest(pico);
    }

    private void runConcurrencyTest(final DefaultPicoContainer pico) throws InterruptedException {
        int size = 10;

        Thread[] threads = new Thread[size];

        final List out = Collections.synchronizedList(new ArrayList());

        final CountDown countDown = new CountDown(size);

        for (int i = 0; i < size; i++) {

            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        out.add(pico.getComponentInstance("slow"));
                    } catch (Exception e) {
                        // add ex? is e.equals(anotherEOfTheSameType) == true?
                        out.add(new Date()); // add something else to indicate miss
                    } finally {
                        countDown.release();
                    }
                }
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }


        countDown.acquire();

        List differentInstances = new ArrayList();

        for (int i = 0; i < out.size(); i++) {
            Object o =  out.get(i);

            if (!differentInstances.contains(o))
                differentInstances.add(o);
        }

        assertTrue("Only one singleton instance was created [we have " + differentInstances.size() + "]", differentInstances.size() == 1);
    }

    public static class SlowCtor {
        public SlowCtor() throws InterruptedException {
            Thread.sleep(50);
        }
    }
}