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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class ConstructorInjectionComponentAdapterTestCase extends AbstractComponentAdapterTestCase {

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterType()
     */
    protected Class getComponentAdapterType() {
        return ConstructorInjectionComponentAdapter.class;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter("foo", A.class);
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

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentImplementation(A.class);
        return new ConstructorInjectionComponentAdapter(B.class, B.class);
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVisitable()
     */
    protected ComponentAdapter prepareTestVisitable() {
        return new ConstructorInjectionComponentAdapter("foo", A.class);
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestSerializable(final MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(SimpleTouchable.class, SimpleTouchable.class);
    }
    
    public static class NamedDependsOnTouchable extends DependsOnTouchable {
        public NamedDependsOnTouchable(Touchable t, String name) {
            super(t);
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestShouldBeAbleToTakeParameters(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestShouldBeAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentImplementation(SimpleTouchable.class);
        return new ConstructorInjectionComponentAdapter(NamedDependsOnTouchable.class, NamedDependsOnTouchable.class, new Parameter[] {
                new ComponentParameter(),
                new ConstantParameter("Name")
        });
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithUnsatisfiedDependency(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithUnsatisfiedDependency(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(DependsOnTouchable.class, DependsOnTouchable.class);
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestComponentAdapterCreatesNewInstances(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestComponentAdapterCreatesNewInstances(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(SimpleTouchable.class, SimpleTouchable.class);
    }

    public static class Erroneous {
        public Erroneous() {
            throw new VerifyError("test");
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestErrorIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestErrorIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(Erroneous.class, Erroneous.class);
    }

    public static class RuntimeThrowing {
        public RuntimeThrowing() {
            throw new RuntimeException("test");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestRuntimeExceptionIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestRuntimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(RuntimeThrowing.class, RuntimeThrowing.class);
    }
    
    public static class NormalExceptionThrowing {
        public NormalExceptionThrowing() throws Exception {
            throw new Exception("test");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(NormalExceptionThrowing.class, NormalExceptionThrowing.class);
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestDependenciesAreResolved(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestDependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentImplementation(SimpleTouchable.class);
        return new ConstructorInjectionComponentAdapter(DependsOnTouchable.class, DependsOnTouchable.class);
    }
    
    public static class C1 {
        public C1(C2 c2) {
            fail("verification should not instantiate");
        }
    }

    public static class C2 {
        public C2(C1 c1) {
            fail("verification should not instantiate");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        final ComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter(C1.class, C1.class);
        picoContainer.registerComponent(componentAdapter);
        picoContainer.registerComponentImplementation(C2.class, C2.class);
        return componentAdapter;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingInstantiationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        final ComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter(C1.class, C1.class);
        picoContainer.registerComponent(componentAdapter);
        picoContainer.registerComponentImplementation(C2.class, C2.class);
        return componentAdapter;
    }

    public void testNormalExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentImplementation(NormalExceptionThrowing.class);
        try {
            picoContainer.getComponentInstance(NormalExceptionThrowing.class);
            fail();
        } catch (PicoInvocationTargetInitializationException e) {
            assertEquals("test", e.getCause().getMessage());
        }
    }

    public void testInstantiationExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.registerComponentImplementation(InstantiationExceptionThrowing.class);
            picoContainer.getComponentInstance(InstantiationExceptionThrowing.class);
            fail();
        } catch (NotConcreteRegistrationException e) {
        }
    }

    // TODO test fails currently, since non accessible ctors are filtered out, because of PICO-201.
    // Maybe we can activate it again with some kind of SecurityManager & Policy combination? 
    public void XXXtestIllegalAccessExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.registerComponentImplementation(IllegalAccessExceptionThrowing.class);
            picoContainer.getComponentInstance(IllegalAccessExceptionThrowing.class);
            fail();
        } catch (PicoInitializationException e) {
            assertTrue(e.getCause().getMessage().indexOf(IllegalAccessExceptionThrowing.class.getName()) > 0);
        }
    }
    
    public void testPicoInitializationExceptionThrownBecauseOfFilteredConstructors() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.registerComponentImplementation(IllegalAccessExceptionThrowing.class);
            picoContainer.getComponentInstance(IllegalAccessExceptionThrowing.class);
            fail();
        } catch (PicoInitializationException e) {
            assertTrue(e.getMessage().indexOf(IllegalAccessExceptionThrowing.class.getName()) > 0);
        }
    }

    private static class Private {
        private Private() {
        }
    }

    private static class NotYourBusiness {
        private NotYourBusiness(Private aPrivate) {
            assertNotNull(aPrivate);
        }
    }

    // http://jira.codehaus.org/browse/PICO-189
    public void testShouldBeAbleToInstantiateNonPublicClassesWithNonPublicConstructors() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory(true));
        pico.registerComponentImplementation(Private.class);
        pico.registerComponentImplementation(NotYourBusiness.class);
        assertNotNull(pico.getComponentInstance(NotYourBusiness.class));
    }

    static public class Component201 {
        public Component201(final String s) {
        }
        protected Component201(final Integer i, final Boolean b) {
            fail("Wrong constructor taken.");
        }
    }
    
    // http://jira.codehaus.org/browse/PICO-201
    public void testShouldNotConsiderNonPublicConstructors() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Component201.class);
        pico.registerComponentInstance(new Integer(2));
        pico.registerComponentInstance(new Boolean(true));
        pico.registerComponentInstance("Hello");
        assertNotNull(pico.getComponentInstance(Component201.class));
    }
}
