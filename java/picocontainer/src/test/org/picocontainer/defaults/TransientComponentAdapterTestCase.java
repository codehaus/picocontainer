package org.picocontainer.defaults;

import junit.framework.TestCase;

public class TransientComponentAdapterTestCase extends TestCase {
    public void testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance() {
        TransientComponentAdapter componentAdapter = new TransientComponentAdapter(null, Object.class);
        Object o1 = componentAdapter.getComponentInstance(null);
        Object o2 = componentAdapter.getComponentInstance(null);
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
        picoContainer.registerComponent(new TransientComponentAdapter(TransientComponent.class, TransientComponent.class));
        TransientComponent c1 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        TransientComponent c2 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        assertNotSame(c1, c2);
        assertSame(c1.service, c2.service);
    }

}
