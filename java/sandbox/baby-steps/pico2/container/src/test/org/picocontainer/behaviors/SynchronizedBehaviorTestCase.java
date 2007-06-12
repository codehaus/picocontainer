/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.behaviors.SynchronizedBehavior;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public final class SynchronizedBehaviorTestCase extends TestCase {
    private final Runner[] runner = new Runner[3];
    private int blockerCounter = 0;

    final class Runner implements Runnable {
        public RuntimeException exception;
        public Blocker blocker;
        private final PicoContainer pico;

        public Runner(PicoContainer pico) {
            this.pico = pico;
        }

        public void run() {
            try {
                blocker = (Blocker) pico.getComponent("key");
            } catch (RuntimeException e) {
                exception = e;
            }
        }
    }

    public class Blocker {
        public Blocker() throws InterruptedException {
            final Thread thread = Thread.currentThread();
            synchronized (thread) {
                SynchronizedBehaviorTestCase.this.blockerCounter++;
                thread.wait();
            }
        }
    }

    private void initTest(ComponentAdapter componentAdapter) throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(this);
        pico.addAdapter(componentAdapter);
        blockerCounter = 0;

        for(int i = 0; i < runner.length; ++i) {
            runner[i] = new Runner(pico);
        }
        
        Thread racer[] = new Thread[runner.length];
        for(int i = 0; i < racer.length; ++i) {
            racer[i] =  new Thread(runner[i]);
        }

        for (Thread aRacer2 : racer) {
            aRacer2.start();
            Thread.sleep(250);
        }

        for (Thread aRacer : racer) {
            synchronized (aRacer) {
                aRacer.notify();
            }
        }

        for (Thread aRacer1 : racer) {
            aRacer1.join();
        }
    }

    public void testRaceConditionIsHandledBySynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingBehavior(new ConstructorInjector("key", Blocker.class));
        SynchronizedBehavior synchronizedComponentAdapter = new SynchronizedBehavior(componentAdapter);
        initTest(synchronizedComponentAdapter);

        assertEquals(1, blockerCounter);
        for (Runner aRunner1 : runner) {
            assertNull(aRunner1.exception);
        }
        for (Runner aRunner : runner) {
            assertNotNull(aRunner.blocker);
        }
        for(int i = 1; i < runner.length; ++i) {
            assertSame(runner[0].blocker, runner[i].blocker);
        }
    }

    public void testRaceConditionIsNotHandledWithoutSynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingBehavior(new ConstructorInjector("key", Blocker.class));
        initTest(componentAdapter);

        assertNull(runner[0].exception);
        assertEquals(3, blockerCounter);
        for(int i = 1; i < runner.length; ++i) {
            assertNull(runner[i].exception);
        }
    }

    public void THIS_NATURALLY_FAILS_testSingletonCreationRace() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("slow", SlowCtor.class);
        runConcurrencyTest(pico);
    }

    public void THIS_NATURALLY_FAILS_testSingletonCreationWithSynchronizedAdapter() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new CachingBehavior(new SynchronizedBehavior(new ConstructorInjector("slow", SlowCtor.class))));
        runConcurrencyTest(pico);
    }

    // This is overkill - an outer sync adapter is enough
    public void testSingletonCreationWithSynchronizedAdapterAndDoubleLocking() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SynchronizedBehavior(new CachingBehavior(new SynchronizedBehavior(new ConstructorInjector("slow", SlowCtor.class)))));
        runConcurrencyTest(pico);
    }

    public void testSingletonCreationWithSynchronizedAdapterOutside() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SynchronizedBehavior(new CachingBehavior(new ConstructorInjector("slow", SlowCtor.class))));
        runConcurrencyTest(pico);
    }

    public void testSingletonCreationWithSynchronizedAdapterOutsideUsingFactory() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer(
                new SynchronizedBehaviorFactory().forThis(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()))
        );
        pico.addComponent("slow", SlowCtor.class);
        runConcurrencyTest(pico);
    }

    private void runConcurrencyTest(final DefaultPicoContainer pico) throws InterruptedException {
        int size = 10;

        Thread[] threads = new Thread[size];

        final List out = Collections.synchronizedList(new ArrayList());

        for (int i = 0; i < size; i++) {

            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        out.add(pico.getComponent("slow"));
                    } catch (Exception e) {
                        // add ex? is e.equals(anotherEOfTheSameType) == true?
                        out.add(new Date()); // add something else to indicate miss
                    }
                }
            });
        }

        for (Thread thread1 : threads) {
            thread1.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        List differentInstances = new ArrayList();

        for (Object anOut : out) {

            if (!differentInstances.contains(anOut)) {
                differentInstances.add(anOut);
            }
        }

        assertTrue("Only one singleton instance was created [we have " + differentInstances.size() + "]", differentInstances.size() == 1);
    }

    public static class SlowCtor {
        public SlowCtor() throws InterruptedException {
            Thread.sleep(50);
        }
    }
}
