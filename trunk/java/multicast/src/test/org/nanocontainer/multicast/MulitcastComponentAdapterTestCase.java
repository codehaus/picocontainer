package org.picoextras.multicast;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.Invoker;
import org.picocontainer.defaults.MulticastInvoker;
import org.picocontainer.defaults.NullInvocationInterceptor;

/**
 * @author Chris Stevenson
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulitcastComponentAdapterTestCase extends TestCase {

    public static interface Kissable {
        void kiss();
    }

    public static class TestGirl implements Kissable {
        private boolean kissed = false;

        public TestGirl() {
        }

        public boolean wasKissed() {
            return kissed;
        }

        public void kiss() {
            kissed = true;
        }

        public void wipeFace() {
            kissed = false;
        }
    }

    public static class TestBoy {
        private Kissable kissable;

        public TestBoy(Kissable kissable) {
            this.kissable = kissable;
        }

        public void doKiss() {
            kissable.kiss();
        }
    }

    private TestGirl girl1;
    private TestGirl girl2;
    private TestGirl girl3;
    private MulticastingComponentAdapter multicastingComponentAdapter;

    private void setUpGirls(Invoker invoker) {
        girl1 = new TestGirl();
        girl2 = new TestGirl();
        girl3 = new TestGirl();
        multicastingComponentAdapter = new MulticastingComponentAdapter("girls", TestGirl.class, new NullInvocationInterceptor(), invoker);
        multicastingComponentAdapter.addComponentInstance(girl1);
        multicastingComponentAdapter.addComponentInstance(girl2);
        multicastingComponentAdapter.addComponentInstance(girl3);
    }

    public void testMulticastingComponentSendsKissToAllComponents() {
        MulticastInvoker invoker = new MulticastInvoker();
        setUpGirls(invoker);

        Kissable kissable = (Kissable) multicastingComponentAdapter.getComponentInstance();
        kissable.kiss();

        assertTrue(girl1.wasKissed());
        assertTrue(girl2.wasKissed());
        assertTrue(girl3.wasKissed());
    }

    public void testRoundRobinComponentDispatchesEachCallToANewOneInARoundRobinFashion() {
        RoundRobinInvoker invoker = new RoundRobinInvoker();
        setUpGirls(invoker);

        Kissable kissable = (Kissable) multicastingComponentAdapter.getComponentInstance();

        wipeGirls();
        kissable.kiss();
        assertTrue(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertFalse(girl3.wasKissed());

        wipeGirls();
        kissable.kiss();
        assertFalse(girl1.wasKissed());
        assertTrue(girl2.wasKissed());
        assertFalse(girl3.wasKissed());

        wipeGirls();
        kissable.kiss();
        assertFalse(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertTrue(girl3.wasKissed());

        wipeGirls();
        kissable.kiss();
        assertTrue(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertFalse(girl3.wasKissed());
    }

    private void wipeGirls() {
        girl1.wipeFace();
        girl2.wipeFace();
        girl3.wipeFace();
    }

    public void testBoyKissesAllGirlsAsRoundRobin() {
        RoundRobinInvoker invoker = new RoundRobinInvoker();
        setUpGirls(invoker);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(multicastingComponentAdapter);
        pico.registerComponentImplementation(TestBoy.class);

        TestBoy boy = (TestBoy) pico.getComponentInstance(TestBoy.class);

        wipeGirls();
        boy.doKiss();
        assertTrue(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertFalse(girl3.wasKissed());

        wipeGirls();
        boy.doKiss();
        assertFalse(girl1.wasKissed());
        assertTrue(girl2.wasKissed());
        assertFalse(girl3.wasKissed());

        wipeGirls();
        boy.doKiss();
        assertFalse(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertTrue(girl3.wasKissed());

        wipeGirls();
        boy.doKiss();
        assertTrue(girl1.wasKissed());
        assertFalse(girl2.wasKissed());
        assertFalse(girl3.wasKissed());
    }

}
