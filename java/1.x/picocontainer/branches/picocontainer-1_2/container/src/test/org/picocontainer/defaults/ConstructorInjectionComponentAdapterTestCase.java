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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


public class ConstructorInjectionComponentAdapterTestCase extends AbstractComponentAdapterTestCase {

    protected Class getComponentAdapterType() {
        return ConstructorInjectionComponentAdapter.class;
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
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

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentImplementation(A.class);
        return new ConstructorInjectionComponentAdapter(B.class, B.class);
    }

    protected ComponentAdapter prepDEF_visitable() {
        return new ConstructorInjectionComponentAdapter("bar", B.class, new Parameter[]{ComponentParameter.DEFAULT});
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentImplementation(SimpleTouchable.class);
        return new ConstructorInjectionComponentAdapter(
                NamedDependsOnTouchable.class, NamedDependsOnTouchable.class, new Parameter[]{
                        ComponentParameter.DEFAULT, new ConstantParameter("Name")});
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(SimpleTouchable.class, SimpleTouchable.class);
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(final MutablePicoContainer picoContainer) {
        return prepSER_isSerializable(picoContainer);
    }

    public static class NamedDependsOnTouchable extends DependsOnTouchable {
        public NamedDependsOnTouchable(Touchable t, String name) {
            super(t);
        }
    }

    protected ComponentAdapter prepVER_verificationFails(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(DependsOnTouchable.class, DependsOnTouchable.class);
    }

    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(SimpleTouchable.class, SimpleTouchable.class);
    }

    public static class Erroneous {
        public Erroneous() {
            throw new VerifyError("test");
        }
    }

    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(Erroneous.class, Erroneous.class);
    }

    public static class RuntimeThrowing {
        public RuntimeThrowing() {
            throw new RuntimeException("test");
        }
    }

    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(RuntimeThrowing.class, RuntimeThrowing.class);
    }

    public static class NormalExceptionThrowing {
        public NormalExceptionThrowing() throws Exception {
            throw new Exception("test");
        }
    }

    protected ComponentAdapter prepINS_normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(
            MutablePicoContainer picoContainer) {
        return new ConstructorInjectionComponentAdapter(NormalExceptionThrowing.class, NormalExceptionThrowing.class);
    }

    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
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

    protected ComponentAdapter prepRES_failingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        final ComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter(C1.class, C1.class);
        picoContainer.registerComponent(componentAdapter);
        picoContainer.registerComponentImplementation(C2.class, C2.class);
        return componentAdapter;
    }

    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
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

    public abstract class InstantiationExceptionThrowing {
        public InstantiationExceptionThrowing() {
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

    public class IllegalAccessExceptionThrowing {
        private IllegalAccessExceptionThrowing() {
        }
    }

    // TODO test fails currently, since non accessible ctors are filtered out, because of
    // PICO-201.
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

    public void testRegisterAbstractShouldFail() throws PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.registerComponentImplementation(Runnable.class);
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (NotConcreteRegistrationException e) {
            assertEquals(Runnable.class, e.getComponentImplementation());
            assertTrue(e.getMessage().indexOf(Runnable.class.getName()) > 0);
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

    public void testMonitoringHappensBeforeAndAfterInstantiation() throws NoSuchMethodException {
        Mock monitor = mock(ComponentMonitor.class);
        Constructor emptyHashMapCtor = HashMap.class.getConstructor(new Class[0]);
        monitor.expects(once()).method("instantiating").with(eq(emptyHashMapCtor));
        Constraint durationIsGreaterThanOrEqualToZero = new Constraint() {
            public boolean eval(Object o) {
                Long duration = (Long)o;
                return 0 <= duration.longValue();
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("The endTime wasn't after the startTime");
            }
        };
        monitor.expects(once()).method("instantiated").with(eq(emptyHashMapCtor), durationIsGreaterThanOrEqualToZero);
        ConstructorInjectionComponentAdapter cica = new ConstructorInjectionComponentAdapter(
                Map.class, HashMap.class, new Parameter[0], false, (ComponentMonitor)monitor.proxy());
        cica.getComponentInstance(null);
    }

    public void testMonitoringHappensBeforeAndOnFailOfImpossibleComponentsInstantiation() throws NoSuchMethodException {
        Mock monitor = mock(ComponentMonitor.class);
        Constructor barfingActionListenerCtor = BarfingActionListener.class.getConstructor(new Class[0]);
        monitor.expects(once()).method("instantiating").with(eq(barfingActionListenerCtor));

        Constraint isITE = new Constraint() {
            public boolean eval(Object o) {
                Exception ex = (Exception)o;
                return ex instanceof InvocationTargetException;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have been unable to instantiate");
            }
        };

        monitor.expects(once()).method("instantiationFailed").with(eq(barfingActionListenerCtor), isITE);
        ConstructorInjectionComponentAdapter cica = new ConstructorInjectionComponentAdapter(
                ActionListener.class, BarfingActionListener.class, new Parameter[0], false, (ComponentMonitor)monitor.proxy());
        try {
            cica.getComponentInstance(null);
            fail("Should barf");
        } catch (RuntimeException e) {
            assertEquals("Barf!", e.getMessage());
        }
    }

    private static class BarfingActionListener implements ActionListener {
        public BarfingActionListener() {
            throw new RuntimeException("Barf!");
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        ConstructorInjectionComponentAdapter cica = new ConstructorInjectionComponentAdapter(
                NullLifecycle.class, NullLifecycle.class, new Parameter[0], false, 
                new DelegatingComponentMonitor(), strategy);
        Touchable touchable = new SimpleTouchable();
        cica.start(touchable);
        cica.stop(touchable);
        cica.dispose(touchable);
        assertEquals("<start<stop<dispose", strategy.recording());
    }
}
