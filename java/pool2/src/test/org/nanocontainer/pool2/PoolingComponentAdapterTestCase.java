package org.picoextras.pool2;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PoolingComponentAdapterTestCase extends TestCase {
    private PoolingComponentAdapter componentAdapter;
    private MutablePicoContainer pico;

    protected void setUp() throws Exception {
        componentAdapter = new PoolingComponentAdapter(new ConstructorComponentAdapter("foo", Object.class));
        pico = new DefaultPicoContainer();
    }

    public void testNewIsInstantiatedOnEachRequest() {
        Object borrowed0 = componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();

        assertNotSame(borrowed0, borrowed1);
    }

    public void testInstancesCanBeRecycled() {
        Object borrowed0 =  componentAdapter.getComponentInstance();
        Object borrowed1 = componentAdapter.getComponentInstance();
        Object borrowed2 = componentAdapter.getComponentInstance();

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        componentAdapter.returnComponentInstance(borrowed1);

        Object borrowed = componentAdapter.getComponentInstance();
        assertSame(borrowed1, borrowed);
    }

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
        final PoolingComponentAdapter componentAdapter2 = new PoolingComponentAdapter(new ConstructorComponentAdapter("foo", Object.class), 2, 5000);

        final Object[] borrowed = new Object[3];
        final Throwable[] threadException = new Throwable[2];

        final StringBuffer order = new StringBuffer();
        final Thread returner = new Thread() {
            public void run() {
                try {
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

    public void testFailsWhenExhausted() {
        final PoolingComponentAdapter componentAdapter2 =
                new PoolingComponentAdapter(new ConstructorComponentAdapter("foo", Object.class), 2, 0);
        Object borrowed0 = componentAdapter2.getComponentInstance();
        Object borrowed1 = componentAdapter2.getComponentInstance();
        try {
            Object borrowed2 = componentAdapter2.getComponentInstance();
        } catch (ExhaustedException e) {
        }
    }

    public void testGrowsWhenExhausted() {
        final PoolingComponentAdapter componentAdapter2 =
                new PoolingComponentAdapter(new ConstructorComponentAdapter("foo", Object.class), 5);
        assertEquals(0, componentAdapter2.getTotalSize());
        Object borrowed0 = componentAdapter2.getComponentInstance();
        assertEquals(1, componentAdapter2.getTotalSize());
        Object borrowed1 = componentAdapter2.getComponentInstance();
        assertEquals(2, componentAdapter2.getTotalSize());
        Object borrowed2 = componentAdapter2.getComponentInstance();
        assertEquals(3, componentAdapter2.getTotalSize());

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);
    }
}
