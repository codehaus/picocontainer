/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;

public class ConstructorInjectionComponentAdapterTestCase extends TestCase {
    public void testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance() {
        ConstructorInjectionComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("blah", Object.class);
        Object o1 = componentAdapter.getComponentInstance();
        Object o2 = componentAdapter.getComponentInstance();
        assertNotNull(o1);
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
        picoContainer.registerComponent(new ConstructorInjectionComponentAdapter(TransientComponent.class, TransientComponent.class));
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
        InstantiatingComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", A.class);
        componentAdapter.verify();
    }

    public void testFailingVerificationWithUnsatisfiedDependencies() {
        ComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", B.class);
        componentAdapter.setContainer(new DefaultPicoContainer());
        try {
            componentAdapter.verify();
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
    }
    
    public static class C1 {
    	public C1(C2 c2) {
            fail("verification should not instantiate");
        }
    }
    
    public static class C2 {
    	public C2(C1 i1) {
            fail("verification should not instantiate");
        }
    }

    public void testFailingVerificationWithCyclicDependencyException() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentImplementation(C1.class);
        picoContainer.registerComponentImplementation(C2.class);
        try {
            picoContainer.verify();
            fail();
        } catch (CyclicDependencyException e) {
            String message = e.getMessage();
            assertTrue(message.indexOf("C1") + message.indexOf("C2") > 0);
        }
    }
    
    public static class D {
    	public D(A a) {
        }
    }

    public void testFailingVerificationWithPicoInitializationException() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentImplementation(A.class);
        picoContainer.registerComponentImplementation(B.class);
        picoContainer.registerComponentImplementation(D.class, D.class, 
                new Parameter[] { new ComponentParameter(), new ComponentParameter() });
        try {
            picoContainer.verify();
            fail();
        } catch (PicoInitializationException e) {
            String message = e.getMessage();
            assertTrue(message.indexOf(D.class.getName() + "(" + A.class.getName() + ")") > 0);
        }
    }

}
