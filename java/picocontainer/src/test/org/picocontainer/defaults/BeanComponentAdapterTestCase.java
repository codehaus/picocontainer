package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;

public class BeanComponentAdapterTestCase extends TestCase {

    public static class A {
        private B b;

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }
    }

    public static class B {
    }

    public void testDependenciesAreResolved() {
        BeanComponentAdapter aAdapter = new BeanComponentAdapter("a", A.class, null);
        BeanComponentAdapter bAdapter = new BeanComponentAdapter("b", B.class, null);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);

        A a = (A) aAdapter.getComponentInstance(pico);
        assertNotNull(a.getB());
    }

}
