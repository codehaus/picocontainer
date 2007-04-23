/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SynchronizedComponentAdapterTestCase extends TestCase {
    private Runner[] runner = new Runner[3];
    private int blockerCounter = 0;

    class Runner implements Runnable {
        public RuntimeException exception;
        public Blocker blocker;
        private PicoContainer pico;

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
                SynchronizedComponentAdapterTestCase.this.blockerCounter++;
                thread.wait();
            }
        }
    }

    private void initTest(ComponentAdapter componentAdapter) throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(this);
        pico.registerComponent(componentAdapter);
        blockerCounter = 0;

        for(int i = 0; i < runner.length; ++i) {
            runner[i] = new Runner(pico);
        }
        
        Thread racer[] = new Thread[runner.length];
        for(int i = 0; i < racer.length; ++i) {
            racer[i] =  new Thread(runner[i]);
        }

        for(int i = 0; i < racer.length; ++i) {
            racer[i].start();
            Thread.sleep(250);
        }
        
        for(int i = 0; i < racer.length; ++i) {
            synchronized (racer[i]) {
                racer[i].notify();
            }
        }

        for(int i = 0; i < racer.length; ++i) {
            racer[i].join();
        }
    }

    public void testRaceConditionIsHandledBySynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter("key", Blocker.class));
        SynchronizedComponentAdapter synchronizedComponentAdapter = new SynchronizedComponentAdapter(componentAdapter);
        initTest(synchronizedComponentAdapter);

        assertEquals(1, blockerCounter);
        for(int i = 0; i < runner.length; ++i) {
            assertNull(runner[i].exception);
        }
        for(int i = 0; i < runner.length; ++i) {
            assertNotNull(runner[i].blocker);
        }
        for(int i = 1; i < runner.length; ++i) {
            assertSame(runner[0].blocker, runner[i].blocker);
        }
    }

    public void testRaceConditionIsNotHandledWithoutSynchronizedComponentAdapter() throws InterruptedException {
        ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter("key", Blocker.class));
        initTest(componentAdapter);

        assertNull(runner[0].exception);
        assertEquals(3, blockerCounter);
        for(int i = 1; i < runner.length; ++i) {
            assertNull(runner[i].exception);
        }
    }

    public void THIS_NATURALLY_FAILS_testSingletonCreationRace() throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent("slow", SlowCtor.class);
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
        pico.registerComponent("slow", SlowCtor.class);
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

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

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
