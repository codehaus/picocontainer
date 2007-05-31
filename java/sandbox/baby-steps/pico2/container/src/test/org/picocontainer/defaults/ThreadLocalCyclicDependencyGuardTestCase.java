/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;

/**
 * Test the CyclicDependecy.
 */
public class ThreadLocalCyclicDependencyGuardTestCase
        extends TestCase {
    private Runnable[] runner = new Runnable[3];
    
    class ThreadLocalRunner implements Runnable {
        public ThreadLocalCyclicDependencyGuard.CyclicDependencyException exception;
        private final Blocker blocker;
        private final ThreadLocalCyclicDependencyGuard guard;

        public ThreadLocalRunner() {
            this.blocker = new Blocker();
            this.guard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    try {
                        blocker.block();
                    } catch (InterruptedException e) {
                    }
                    return null;
                }
            };
        }

        public void run() {
            try {
                guard.observe(ThreadLocalRunner.class);
            } catch (ThreadLocalCyclicDependencyGuard.CyclicDependencyException e) {
                exception = e;
            }
        }
    }

    public class Blocker {
        public void block() throws InterruptedException {
            final Thread thread = Thread.currentThread();
            synchronized (thread) {
                thread.wait();
            }
        }
    }

    private void initTest(final Runnable[] runner) throws InterruptedException {

        Thread racer[] = new Thread[runner.length];
        for(int i = 0; i < racer.length; ++i) {
            racer[i] =  new Thread(runner[i]);
        }

        for (Thread aRacer : racer) {
            aRacer.start();
            Thread.sleep(200);
        }

        for (Thread aRacer : racer) {
            synchronized (aRacer) {
                aRacer.notify();
            }
        }

        for (Thread aRacer : racer) {
            aRacer.join();
        }
    }
    
    public void testCyclicDependencyWithThreadSafeGuard() throws InterruptedException {
        for(int i = 0; i < runner.length; ++i) {
            runner[i] = new ThreadLocalRunner();
        }
        
        initTest(runner);

        for (Runnable aRunner : runner) {
            assertNull(((ThreadLocalRunner) aRunner).exception);
        }
    }
}
