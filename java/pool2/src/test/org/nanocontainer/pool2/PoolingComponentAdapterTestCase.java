/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.pool2;

import junit.framework.TestCase;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class PoolingComponentAdapterTestCase extends TestCase {
    private PoolingComponentAdapter componentAdapter;

    protected void setUp() throws Exception {
        componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", Object.class));
    }

    public void testNewIsInstantiatedOnEachRequest() {
        Object borrowed0 = componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();

        assertNotSame(borrowed0, borrowed1);
    }

    public void testInstancesCanBeRecycled() {
        Object borrowed0 = componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();
        Object borrowed2 = componentAdapter.getComponentInstance();

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        componentAdapter.returnComponentInstance(borrowed1);

        Object borrowed = componentAdapter.getComponentInstance();
        assertSame(borrowed1, borrowed);
    }

    // TODO: Why? This is an implementation detail, that does not matter for the caller.
    public void testPoolIsFifo() {
        Object borrowed0 = componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();
        componentAdapter.returnComponentInstance(borrowed0);
        componentAdapter.returnComponentInstance(borrowed1);
        Object borrowed2 = componentAdapter.getComponentInstance();
        Object borrowed3 = componentAdapter.getComponentInstance();

        assertSame(borrowed0, borrowed2);
        assertSame(borrowed1, borrowed3);
    }

    public void testBadTypeCantBeRecycled() {
        try {
            componentAdapter.returnComponentInstance("bad");
            fail();
        } catch (BadTypeException e) {
            assertEquals(Object.class, e.getExpected());
            assertEquals(String.class, e.getActual());
            assertEquals("Expected java.lang.Object, but got java.lang.String", e.getMessage());
        }
    }

    public void testUnmanagedInstanceCantBeRecycled() {
        final Object borrowed = new Object();
        try {
            componentAdapter.returnComponentInstance(borrowed);
            fail();
        } catch (UnmanagedInstanceException e) {
            assertEquals(borrowed, e.getInstance());
        }
    }

    public void testBlocksWhenExhausted() throws InterruptedException {
        final PoolingComponentAdapter componentAdapter2 = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", Object.class), 2, 5000);

        final Object[] borrowed = new Object[3];
        final Throwable[] threadException = new Throwable[2];

        final StringBuffer order = new StringBuffer();
        final Thread returner = new Thread() {
            public void run() {
                try {
                    Thread.sleep(50); // ensure, that main thread is blocked
                    order.append("returner ");
                    componentAdapter2.returnComponentInstance(borrowed[0]);
                } catch (Throwable t) {
                    t.printStackTrace();
                    synchronized (componentAdapter2) {
                        componentAdapter2.notify();
                    }
                    threadException[1] = t;
                }
            }
        };

        borrowed[0] = componentAdapter2.getComponentInstance();
        borrowed[1] = componentAdapter2.getComponentInstance();
        returner.start();

        // should block
        order.append("main ");
        borrowed[2] = componentAdapter2.getComponentInstance();
        order.append("main");

        returner.join();

        assertNull(threadException[0]);
        assertNull(threadException[1]);

        assertEquals("main returner main", order.toString());

        assertSame(borrowed[0], borrowed[2]);
        assertNotSame(borrowed[1], borrowed[2]);
    }

    public void testTimeoutWhenExhausted() throws InterruptedException {
        final PoolingComponentAdapter componentAdapter2 =
                new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", Object.class), 2, 250);
        Object borrowed0 = componentAdapter2.getComponentInstance();
        Object borrowed1 = componentAdapter2.getComponentInstance();
        long time = System.currentTimeMillis();
        try {
            Object borrowed2 = componentAdapter2.getComponentInstance();
            fail("Missing ExhaustedException, pool cannot grow further.");
        } catch (ExhaustedException e) {
            assertTrue(System.currentTimeMillis() - time >= 250);
        }
    }
    public void testGrowsAlways() {
        final PoolingComponentAdapter componentAdapter2 =
                new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", Object.class), -1);
        final Set set = new HashSet();
        try {
            final int max = 5;
            int i;
            for (i = 0; i < max; ++i) {
                assertEquals(i, componentAdapter2.getTotalSize());
                final Object object = componentAdapter2.getComponentInstance();
                assertNotNull(object);
                assertEquals(i+1, componentAdapter2.getBusy());
                assertEquals(0, componentAdapter2.getAvailable());
                set.add(object);
            }
            assertEquals(i, componentAdapter2.getTotalSize());
            assertEquals(i, componentAdapter2.getBusy());
            assertEquals(i, set.size());

            for (Iterator iter = set.iterator(); iter.hasNext();) {
              Object object = iter.next();
              componentAdapter2.returnComponentInstance(object);
                assertEquals(max, componentAdapter2.getTotalSize());
                assertEquals(--i, componentAdapter2.getBusy());
                assertEquals(max-i, componentAdapter2.getAvailable());
            }
            
            for (i = 0; i < max; ++i) {
                assertEquals(max, componentAdapter2.getTotalSize());
                final Object object = componentAdapter2.getComponentInstance();
                assertNotNull(object);
                assertEquals(i+1, componentAdapter2.getBusy());
                assertEquals(max-i-1, componentAdapter2.getAvailable());
                set.add(object);
            }

            assertEquals(max, set.size());
            
        } catch (ExhaustedException e) {
            fail("This pool should not get exhausted.");
        }
    }
    
    public void testFailsWhenExhausted() {
        final PoolingComponentAdapter componentAdapter2 =
                new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", Object.class), 2);
        assertEquals(0, componentAdapter2.getTotalSize());
        Object borrowed0 = componentAdapter2.getComponentInstance();
        assertEquals(1, componentAdapter2.getTotalSize());
        Object borrowed1 = componentAdapter2.getComponentInstance();
        assertEquals(2, componentAdapter2.getTotalSize());
        try {
            Object borrowed2 = componentAdapter2.getComponentInstance();
            fail("Missing ExhaustedException, pool cannot grow further.");
        } catch (ExhaustedException e) {
        }

        assertNotSame(borrowed0, borrowed1);
    }
}
