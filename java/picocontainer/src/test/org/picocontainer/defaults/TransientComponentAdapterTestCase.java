package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;

public class TransientComponentAdapterTestCase extends TestCase {
    public void testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance() {
        ConstructorComponentAdapter componentAdapter = new ConstructorComponentAdapter("blah", Object.class);
        MutablePicoContainer pico = new DefaultPicoContainer();
        Object o1 = componentAdapter.getComponentInstance(pico);
        Object o2 = componentAdapter.getComponentInstance(pico);
        assertNotSame(o1, o2);
    }

    public static class Service {
    }

    public static class TransientComponent {
        private Service service;

        public TransientComponent(Service service) {
            this.service = service;
        }
    }

    public void testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientComponentAdapter() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentImplementation(Service.class);
        picoContainer.registerComponent(new ConstructorComponentAdapter(TransientComponent.class, TransientComponent.class));
        TransientComponent c1 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        TransientComponent c2 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        assertNotSame(c1, c2);
        assertSame(c1.service, c2.service);
    }


    public static class A {
        public A() {
            fail("verification should not instantiate");
        }
    }

    public static class B {
        public B(A a) {
            fail("verification should not instantiate");
        }
    }

    public void testSuccessfulVerificationWithNoDependencies() {
        InstantiatingComponentAdapter componentAdapter = new ConstructorComponentAdapter("foo", A.class);
        componentAdapter.verify(null);
    }

    public void testFailingVerificationWithUnsatisfiedDependencies() {
        ComponentAdapter componentAdapter = new ConstructorComponentAdapter("foo", B.class);
        try {
            componentAdapter.verify(new DefaultPicoContainer());
            fail("Expected NoSatisfiableConstructorsException");
        } catch (NoSatisfiableConstructorsException e) {
            // ok
        }
    }

}
