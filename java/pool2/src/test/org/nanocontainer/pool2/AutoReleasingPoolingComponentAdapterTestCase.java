/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                                         *
 *****************************************************************************/
package org.nanocontainer.pool2;

import junit.framework.TestCase;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

/**
 * @author J&ouml;rg Schaible
 */
public class AutoReleasingPoolingComponentAdapterTestCase extends TestCase {
    private AutoReleasingPoolingComponentAdapter componentAdapter;

    static public interface Identifiable {
        int getId();
    }

    static public class InstanceCounter implements Identifiable {
        private static int counter = 0;
        final private int id;

        public InstanceCounter() {
            id = counter++;
        }

        public int getId() {
            return id;
        }

        public boolean equals(Object arg) {
            return arg instanceof Identifiable && id == ((Identifiable) arg).getId();
        }
    }

    protected void setUp() throws Exception {
        InstanceCounter.counter = 0;
        componentAdapter = new AutoReleasingPoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", InstanceCounter.class));
    }

    public void testInstancesCanBeRecycled() {
        Object borrowed0 = componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();
        Object borrowed2 = componentAdapter.getComponentInstance();

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        borrowed1 = null;
        System.gc();

        Identifiable borrowed = (Identifiable) componentAdapter.getComponentInstance();
        assertEquals(1, borrowed.getId());

        ((PooledInstance) borrowed).returnInstanceToPool();

        Object borrowedReloaded = componentAdapter.getComponentInstance();
        assertEquals(borrowed, borrowedReloaded);
    }

    public void testBadTypeCantBeRecycled() {
        try {
            componentAdapter.returnComponentInstance(new InstanceCounter());
            fail();
        } catch (BadTypeException e) {
            assertEquals(InstanceCounter.class, e.getActual());
        }
    }

    public void testInternalGCCall() {
        componentAdapter = new AutoReleasingPoolingComponentAdapter(new ConstructorInjectionComponentAdapter("foo", InstanceCounter.class), 1);
        for (int i = 0; i < 5; i++) {
            Identifiable borrowed = (Identifiable) componentAdapter.getComponentInstance();
            assertNotNull(borrowed);
            assertEquals(0, borrowed.getId());
        }
    }
}
