/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.concurrent;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picocontainer.defaults.ObjectReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Unit test for ThreadLocalReference
 *
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalReferenceTest
        extends TestCase {

    private List m_exceptionList;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        m_exceptionList = Collections.synchronizedList(new ArrayList());
    }

    final class RunIt
            implements Runnable {

        private ObjectReference m_reference;

        /**
         * Construct an instance.
         *
         * @param reference
         */
        public RunIt(ObjectReference reference) {
            super();
            m_reference = reference;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                final Thread thread = Thread.currentThread();
                m_reference.set(thread.getName());
                synchronized (thread) {
                    thread.wait();
                }
                Assert.assertEquals(thread.getName(), m_reference.get());
            } catch (Exception e) {
                m_exceptionList.add(e);
            }
        }
    }

    /**
     * Test working ThreadLocalReference
     *
     * @throws InterruptedException
     */
    public final void testThreadLocalReference() throws InterruptedException {
        final ThreadLocalReference reference = new ThreadLocalReference();
        final Thread[] threads = new Thread[]{
            new Thread(new RunIt(reference), "junit-TLR-1"),
            new Thread(new RunIt(reference), "junit-TLR-2"),
            new Thread(new RunIt(reference), "junit-TLR-3")};
        reference.set("Hello");
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        Thread.sleep(50);
        assertEquals("Hello", reference.get());
        for (int i = 0; i < threads.length; i++) {
            synchronized (threads[i]) {
                threads[i].notify();
            }
        }
        Thread.sleep(50);
        assertEquals("Unexpected Exceptions: " + m_exceptionList, 0, m_exceptionList.size());
    }
}