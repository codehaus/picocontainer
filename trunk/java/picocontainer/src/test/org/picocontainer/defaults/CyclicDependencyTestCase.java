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
public class CyclicDependencyTestCase
        extends TestCase {
    private Runner[] runner = new Runner[3];

    class Runner implements Runnable {
        public CyclicDependencyException exception;
        private final Blocker blocker;
        private final ObjectReference guard;

        public Runner(Blocker blocker, ObjectReference guard) {
            this.blocker = blocker;
            this.guard = guard;
        }

        public void run() {
            try {
                CyclicDependency.observe(guard, Runner.class, new CyclicDependency() {
                    public Object run() {
                        try {
                            blocker.block();
                        } catch (InterruptedException e) {
                        }
                        return null;
                    }
                });
            } catch (CyclicDependencyException e) {
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

    private void initTest(ObjectReference guard) throws InterruptedException {

        final Blocker blocker = new Blocker();
        
        for(int i = 0; i < runner.length; ++i) {
            runner[i] = new Runner(blocker, guard);
        }
        
        Thread racer[] = new Thread[runner.length];
        for(int i = 0; i < racer.length; ++i) {
            racer[i] =  new Thread(runner[i]);
        }

        for(int i = 0; i < racer.length; ++i) {
            racer[i].start();
            Thread.sleep(200);
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
    
    public void testCyclicDependencyWithSimpleGuard() throws InterruptedException {
        final ObjectReference guard = new CyclicDependency.SimpleGuard();
        initTest(guard);

        assertNull(runner[0].exception);
        for(int i = 1; i < runner.length; ++i) {
            assertNotNull(runner[i].exception);
        }
    }

    
    public void testCyclicDependencyWithThreadSafeGuard() throws InterruptedException {
        final ObjectReference guard = new CyclicDependency.ThreadLocalGuard();
        initTest(guard);

        for(int i = 0; i < runner.length; ++i) {
            assertNull(runner[i].exception);
        }
    }
    
    public void testCyclicDependencyException() {
        final CyclicDependencyException cdEx = new CyclicDependencyException(getClass());
        cdEx.push(String.class);
        final Class[] classes = cdEx.getDependencies();
        assertEquals(2, classes.length);
        assertSame(getClass(), classes[0]);
        assertSame(String.class, classes[1]);
        assertTrue(cdEx.getMessage().indexOf(getClass().getName()) >= 0);
    }
}
