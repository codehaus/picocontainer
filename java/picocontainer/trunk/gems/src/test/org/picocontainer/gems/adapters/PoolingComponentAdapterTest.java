/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29.08.2005 by Jörg Schaible
 */
package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.pool.Poolable;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolingComponentAdapterTest extends AbstractComponentAdapterTestCase {

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
            return arg instanceof Identifiable && id == ((Identifiable)arg).getId();
        }
    }

    protected void setUp() throws Exception {
        InstanceCounter.counter = 0;
    }

    public void testNewIsInstantiatedOnEachRequest() {
        ComponentAdapter componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Identifiable.class, InstanceCounter.class));

        Object borrowed0 = componentAdapter.getComponentInstance(null);
        Object borrowed1 = componentAdapter.getComponentInstance(null);

        assertNotSame(borrowed0, borrowed1);
    }

    public void testInstancesCanBeRecycled() {
        ComponentAdapter componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Identifiable.class, InstanceCounter.class));

        Object borrowed0 = componentAdapter.getComponentInstance(null);
        Object borrowed1 = componentAdapter.getComponentInstance(null);
        Object borrowed2 = componentAdapter.getComponentInstance(null);

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        borrowed1 = null;
        System.gc();

        Identifiable borrowed = (Identifiable)componentAdapter.getComponentInstance(null);
        assertEquals(1, borrowed.getId());

        ((Poolable)borrowed).returnInstanceToPool();

        Object borrowedReloaded = componentAdapter.getComponentInstance(null);
        assertEquals(borrowed, borrowedReloaded);
    }

    public void testBlocksWhenExhausted() throws InterruptedException {
        final ComponentAdapter componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Identifiable.class, InstanceCounter.class), new PoolingComponentAdapter.DefaultContext() {
            public int getMaxSize() {
                return 2;
            }

            public int getMaxWaitInMilliseconds() {
                return 3000;
            }
        });

        final Identifiable[] borrowed = new Identifiable[3];
        final Throwable[] threadException = new Throwable[2];

        final StringBuffer order = new StringBuffer();
        final Thread returner = new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        notifyAll();
                        wait(200); // ensure, that main thread is blocked
                    }
                    order.append("returner ");
                    ((Poolable)borrowed[0]).returnInstanceToPool();
                } catch (Throwable t) {
                    t.printStackTrace();
                    synchronized (componentAdapter) {
                        componentAdapter.notify();
                    }
                    threadException[1] = t;
                }
            }
        };

        borrowed[0] = (Identifiable)componentAdapter.getComponentInstance(null);
        borrowed[1] = (Identifiable)componentAdapter.getComponentInstance(null);
        synchronized (returner) {
            returner.start();
            returner.wait();
        }

        // should block
        order.append("main ");
        borrowed[2] = (Identifiable)componentAdapter.getComponentInstance(null);
        order.append("main");

        returner.join();

        assertNull(threadException[0]);
        assertNull(threadException[1]);

        assertEquals("main returner main", order.toString());

        assertEquals(borrowed[0].getId(), borrowed[2].getId());
        assertFalse(borrowed[1].getId() == borrowed[2].getId());
    }

    public void testTimeoutWhenExhausted() throws InterruptedException {
        final ComponentAdapter componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Identifiable.class, InstanceCounter.class), new PoolingComponentAdapter.DefaultContext() {
            public int getMaxSize() {
                return 2;
            }

            public int getMaxWaitInMilliseconds() {
                return 250;
            }
        });

        Identifiable borrowed0 = (Identifiable)componentAdapter.getComponentInstance(null);
        Identifiable borrowed1 = (Identifiable)componentAdapter.getComponentInstance(null);
        assertNotNull(borrowed0);
        assertFalse(borrowed0.getId() == borrowed1.getId());
        long time = System.currentTimeMillis();
        try {
            componentAdapter.getComponentInstance(null);
            fail("Thrown " + PoolException.class.getName() + " expected");
        } catch (final PoolException e) {
            assertTrue(e.getMessage().indexOf("Time out") >= 0);
            assertTrue(System.currentTimeMillis() - time >= 250);
        }
    }

    public void XXXtestGrowsAlways() {
        PoolingComponentAdapter componentAdapter = new PoolingComponentAdapter(
                new ConstructorInjectionComponentAdapter("foo", Object.class),
                new PoolingComponentAdapter.DefaultContext() {

                    public ProxyFactory getProxyFactory() {
                        return new CglibProxyFactory();
                    }
                });

        final Set set = new HashSet();
        try {
            final int max = 5;
            int i;
            for (i = 0; i < max; ++i) {
                assertEquals(i, componentAdapter.size());
                final Object object = componentAdapter.getComponentInstance(null);
                set.add(object);
            }
            assertEquals(i, componentAdapter.size());
            assertEquals(i, set.size());

            for (Iterator iter = set.iterator(); iter.hasNext();) {
                Poolable object = (Poolable)iter.next();
                object.returnInstanceToPool();
                assertEquals(max, componentAdapter.size());
            }

            for (i = 0; i < max; ++i) {
                assertEquals(max, componentAdapter.size());
                final Object object = componentAdapter.getComponentInstance(null);
                assertNotNull(object);
                set.add(object);
            }

            assertEquals(max, set.size());

        } catch (PoolException e) {
            fail("This pool should not get exhausted.");
        }
    }

    public void testFailsWhenExhausted() {
        final PoolingComponentAdapter componentAdapter = new PoolingComponentAdapter(
                new ConstructorInjectionComponentAdapter(Identifiable.class, InstanceCounter.class),
                new PoolingComponentAdapter.DefaultContext() {
                    public int getMaxSize() {
                        return 2;
                    }
                });

        assertEquals(0, componentAdapter.size());
        Object borrowed0 = componentAdapter.getComponentInstance(null);
        assertEquals(1, componentAdapter.size());
        Object borrowed1 = componentAdapter.getComponentInstance(null);
        assertEquals(2, componentAdapter.size());
        try {
            componentAdapter.getComponentInstance(null);
            fail("Expected ExhaustedException, pool shouldn't be able to grow further.");
        } catch (PoolException e) {
            assertTrue(e.getMessage().indexOf("exhausted") >= 0);
        }

        //assertNotSame(borrowed0, borrowed1);
    }

    public void testInternalGCCall() {
        ComponentAdapter componentAdapter = new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Identifiable.class, InstanceCounter.class), new PoolingComponentAdapter.DefaultContext() {
            public int getMaxSize() {
                return 1;
            }

            public boolean autostartGC() {
                return true;
            }
        });

        for (int i = 0; i < 5; i++) {
            final Identifiable borrowed = (Identifiable)componentAdapter.getComponentInstance(null);
            assertNotNull(borrowed);
            assertEquals(0, borrowed.getId());
        }
    }

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return PoolingComponentAdapter.class;
    }

    protected int getComponentAdapterNature() {
        // @todo: It is serializable ...
        return super.getComponentAdapterNature() & ~(INSTANTIATING | RESOLVING | VERIFYING | SERIALIZABLE);
    }

    private ComponentAdapter createPoolOfTouchables() {
        return new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Touchable.class, SimpleTouchable.class));
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_visitable() {
        return createPoolOfTouchables();
    }
}
