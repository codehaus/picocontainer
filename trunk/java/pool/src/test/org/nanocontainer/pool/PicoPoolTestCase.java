/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.pool;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.FredImpl;
import org.nanocontainer.testmodel.WilmaImpl;

import java.util.NoSuchElementException;


/**
 *  <p><code>PooledPicoContainerTestCase</code> tests behaviour of the DefaultPicoPool
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public class PicoPoolTestCase extends TestCase {
    public void testCreatePoolWithPico() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        PicoPoolConfiguration config = new PicoPoolConfiguration(
                WilmaImpl.class, 3, DefaultPicoPool.FAIL_WHEN_EXHAUSTED,
                0, null, null);

        pico.registerComponentInstance(config);
        pico.registerComponentImplementation("myPool", DefaultPicoPool.class);
        DefaultPicoPool pool = (DefaultPicoPool) pico.getComponentInstance("myPool");
        assertNotNull(pool);
        assertEquals(pool.getImplementation(), WilmaImpl.class);
        assertEquals(0, pool.getSize());

        Object borrowed = pool.borrowComponent();
        assertNotNull(borrowed);
        ((WilmaImpl) borrowed).hello();
        assertEquals(1, pool.getSize());
        pool.returnComponent(borrowed);

        borrowed = pool.borrowComponent();
        assertNotNull(borrowed);
        assertEquals(1, pool.getSize());
        Object borrowed2 = pool.borrowComponent();
        assertNotNull(borrowed2);
        assertEquals(2, pool.getSize());
        assertTrue(((WilmaImpl) borrowed).helloCalled() ^ ((WilmaImpl) borrowed2).helloCalled());
    }

    public void testFailOnExhaust() {
        DefaultPicoPool pool = new DefaultPicoPool(WilmaImpl.class);
        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.FAIL_WHEN_EXHAUSTED);

        for (int i = 0; i < DefaultPicoPool.DEFAULT_MAX_SIZE; i++) {
            pool.borrowComponent();
            assertEquals(pool.getSize(), i + 1);
        }

        try {
            pool.borrowComponent();
            fail("Should throw an Exception");
        } catch (NoSuchElementException e) {
            //expected
        }
    }

    public void testBlockExpiryOnExhaust() {
        long poolExpiry = 1000;

        DefaultPicoPool pool =
                new DefaultPicoPool(
                        new PicoPoolConfiguration(
                                WilmaImpl.class,
                                2,
                                DefaultPicoPool.BLOCK_WHEN_EXHAUSTED,
                                poolExpiry,
                                null,
                                null));

        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.BLOCK_WHEN_EXHAUSTED);

        assertEquals(0, pool.getSize());
        pool.borrowComponent();
        pool.borrowComponent();
        assertEquals(2, pool.getSize());
        long starttime = System.currentTimeMillis();
        try {
            pool.borrowComponent();
            fail("Should throw an Exception");
        } catch (NoSuchElementException e) {
            long totalTime = System.currentTimeMillis() - starttime;
            //Need to allow for alittle variance in system time
            assertTrue(totalTime < (poolExpiry + 50) && totalTime > (poolExpiry - 50));
        }
    }

    public void testBlockOnExhaust() {
        long poolExpiry = 2000;

        DefaultPicoPool pool =
                new DefaultPicoPool(
                        new PicoPoolConfiguration(
                                WilmaImpl.class,
                                2,
                                DefaultPicoPool.BLOCK_WHEN_EXHAUSTED,
                                poolExpiry,
                                null,
                                null));

        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.BLOCK_WHEN_EXHAUSTED);

        assertEquals(0, pool.getSize());
        Object borrowed = pool.borrowComponent();
        assertEquals(1, pool.getSize());
        long starttime = System.currentTimeMillis();

        long borrowerWait = 1000;
        Borrower borrower = new Borrower(pool, borrowerWait);
        borrower.start();
        //Make sure the borrower borrows first
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        borrowed = pool.borrowComponent();
        long totalTime = System.currentTimeMillis() - starttime;
        //Need to allow for alittle variance in system time
        assertTrue(totalTime < (borrowerWait + 50) && totalTime > (borrowerWait - 50));

        assertNotNull(borrowed);
    }

    public void testGrowOnExhaust() {
        DefaultPicoPool pool =
                new DefaultPicoPool(
                        new PicoPoolConfiguration(
                                WilmaImpl.class,
                                2,
                                DefaultPicoPool.GROW_WHEN_EXHAUSTED,
                                0,
                                null,
                                null));

        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.GROW_WHEN_EXHAUSTED);

        Object borrowed = pool.borrowComponent();
        borrowed = pool.borrowComponent();
        assertEquals(2, pool.getSize());

        //Should now grow
        borrowed = pool.borrowComponent();
        assertNotNull(borrowed);
        //Ojbect not in the pool
        assertEquals(2, pool.getSize());
    }

    public void testDependantPoolObject() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(WilmaImpl.class);

        DefaultPicoPool pool =
                new DefaultPicoPool(
                        new PicoPoolConfiguration(
                                FredImpl.class,
                                2,
                                DefaultPicoPool.GROW_WHEN_EXHAUSTED,
                                0,
                                null,
                                pico));

        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.GROW_WHEN_EXHAUSTED);
        assertEquals(0, pool.getSize());
        Object borrowed = pool.borrowComponent();
        assertNotNull(borrowed);
        assertEquals(1, pool.getSize());
        //Component initialised with SimpleTouchable in the parent container
        assertNotNull(((FredImpl) borrowed).wilma());
    }

    public void testPoolObjectVisibility() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(WilmaImpl.class);

        DefaultPicoPool pool =
                new DefaultPicoPool(
                        new PicoPoolConfiguration(
                                FredImpl.class,
                                2,
                                DefaultPicoPool.FAIL_WHEN_EXHAUSTED,
                                0,
                                null,
                                pico));

        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.FAIL_WHEN_EXHAUSTED);
        assertEquals(0, pool.getSize());
        Object borrowed = pool.borrowComponent();
        assertNotNull(borrowed);
        assertEquals(1, pool.getSize());
        //Component initialised with SimpleTouchable in the parent container
        assertNotNull(((FredImpl) borrowed).wilma());

        Object poolComp = pool.getPoolContainer().getComponentInstance(FredImpl.class.getName() + 0);
        assertNotNull(poolComp);

        poolComp = pico.getComponentInstance(FredImpl.class.getName() + 0);
        assertNull(poolComp);

    }

    public void testClearPool() {
        DefaultPicoPool pool = new DefaultPicoPool(WilmaImpl.class);
        assertEquals(pool.getExhaustedAction(), DefaultPicoPool.FAIL_WHEN_EXHAUSTED);

        pool.borrowComponent();
        assertEquals(1, pool.getSize());

        pool.clearPool();
        assertEquals(0, pool.getSize());
        assertEquals(0, pool.getPoolContainer().getComponentAdapters().size());

        pool.borrowComponent();
        assertEquals(1, pool.getSize());
    }

    public void testPoolComponentAdapter() {
        MutablePicoContainer pico = new DefaultPicoContainer(new PicoPoolComponentAdapterFactory());

        pico.registerComponentImplementation(WilmaImpl.class);

        WilmaImpl component = (WilmaImpl) pico.getComponentInstance(WilmaImpl.class);
        component.hello();

        WilmaImpl component2 = (WilmaImpl) pico.getComponentInstance(WilmaImpl.class);
        assertTrue(!component2.helloCalled());

        //TODO how do we return these components back to the pool in a clean
        //way?
    }

    private class Borrower extends Thread {
        private DefaultPicoPool pool;
        private long time;

        public Borrower(DefaultPicoPool pool, long time) {
            super("Borrower");
            if (pool == null)
                throw new IllegalArgumentException("Pool cannot be null");
            this.pool = pool;
            if (time < 500)
                time = 500;
            this.time = time;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Object object = pool.borrowComponent();
            try {
                sleep(time);
            } catch (InterruptedException e) {
                // ignore
            }
            pool.returnComponent(object);
        }

    }
}
