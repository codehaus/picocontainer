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

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapterTestCase extends TestCase {
    private Runner[] runner = new Runner[3];

    class Runner implements Runnable {
        public CyclicDependencyException exception;
        public Blocker blocker;
        private PicoContainer pico;

        public Runner(PicoContainer pico) {
            this.pico = pico;
        }

        public void run() {
            try {
                blocker = (Blocker) pico.getComponentInstance("key");
            } catch (CyclicDependencyException e) {
                exception = e;
            }
        }
    }

    public static class Blocker {
        public Blocker() throws InterruptedException {
            final Thread thread = Thread.currentThread();
            synchronized (thread) {
                thread.wait();
            }
        }
    }

    private void initTest(ComponentAdapter componentAdapter) throws InterruptedException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(componentAdapter);

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
        
        Thread.sleep(250);
        
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
        for(int i = 1; i < runner.length; ++i) {
            assertNotNull(runner[i].exception);
        }
    }
}
