/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

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
import org.picocontainer.monitors.DelegatingComponentMonitor;
import org.picocontainer.adapters.ConstructorInjectionAdapter;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


public class ConstructorInjectionAdapterTestCase extends AbstractComponentAdapterTestCase {

    protected Class getComponentAdapterType() {
        return ConstructorInjectionAdapter.class;
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter("foo", A.class);
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
        picoContainer.addComponent(A.class);
        return new ConstructorInjectionAdapter(B.class, B.class);
    }

    protected ComponentAdapter prepDEF_visitable() {
        return new ConstructorInjectionAdapter("bar", B.class, new Parameter[]{ComponentParameter.DEFAULT});
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(SimpleTouchable.class);
        return new ConstructorInjectionAdapter(
                NamedDependsOnTouchable.class, NamedDependsOnTouchable.class, new Parameter[]{
                        ComponentParameter.DEFAULT, new ConstantParameter("Name")});
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter(SimpleTouchable.class, SimpleTouchable.class);
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
        return new ConstructorInjectionAdapter(DependsOnTouchable.class, DependsOnTouchable.class);
    }

    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter(SimpleTouchable.class, SimpleTouchable.class);
    }

    public static class Erroneous {
        public Erroneous() {
            throw new VerifyError("test");
        }
    }

    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter(Erroneous.class, Erroneous.class);
    }

    public static class RuntimeThrowing {
        public RuntimeThrowing() {
            throw new RuntimeException("test");
        }
    }

    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter(RuntimeThrowing.class, RuntimeThrowing.class);
    }

    public static class NormalExceptionThrowing {
        public NormalExceptionThrowing() throws Exception {
            throw new Exception("test");
        }
    }

    protected ComponentAdapter prepINS_normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(
            MutablePicoContainer picoContainer) {
        return new ConstructorInjectionAdapter(NormalExceptionThrowing.class, NormalExceptionThrowing.class);
    }

    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(SimpleTouchable.class);
        return new ConstructorInjectionAdapter(DependsOnTouchable.class, DependsOnTouchable.class);
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
        final ComponentAdapter componentAdapter = new ConstructorInjectionAdapter(C1.class, C1.class);
        picoContainer.addAdapter(componentAdapter);
        picoContainer.addComponent(C2.class, C2.class);
        return componentAdapter;
    }

    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        final ComponentAdapter componentAdapter = new ConstructorInjectionAdapter(C1.class, C1.class);
        picoContainer.addAdapter(componentAdapter);
        picoContainer.addComponent(C2.class, C2.class);
        return componentAdapter;
    }

    public void testNormalExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.addComponent(NormalExceptionThrowing.class);
        try {
            picoContainer.getComponent(NormalExceptionThrowing.class);
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
            picoContainer.addComponent(InstantiationExceptionThrowing.class);
            picoContainer.getComponent(InstantiationExceptionThrowing.class);
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
            picoContainer.addComponent(IllegalAccessExceptionThrowing.class);
            picoContainer.getComponent(IllegalAccessExceptionThrowing.class);
            fail();
        } catch (PicoInitializationException e) {
            assertTrue(e.getCause().getMessage().indexOf(IllegalAccessExceptionThrowing.class.getName()) > 0);
        }
    }

    public void testPicoInitializationExceptionThrownBecauseOfFilteredConstructors() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.addComponent(IllegalAccessExceptionThrowing.class);
            picoContainer.getComponent(IllegalAccessExceptionThrowing.class);
            fail();
        } catch (PicoInitializationException e) {
            assertTrue(e.getMessage().indexOf(IllegalAccessExceptionThrowing.class.getName()) > 0);
        }
    }

    public void testRegisterAbstractShouldFail() throws PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.addComponent(Runnable.class);
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
        pico.addComponent(Component201.class);
        pico.addComponent(new Integer(2));
        pico.addComponent(new Boolean(true));
        pico.addComponent("Hello");
        assertNotNull(pico.getComponent(Component201.class));
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
        Constraint isAHashMapThatWozCreated = new Constraint() {
            public boolean eval(Object o) {
                return o instanceof HashMap;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have been a hashmap");
            }
        };

        Constraint injectedIsEmptyArray = new Constraint() {
            public boolean eval(Object o) {
                Object[] injected = (Object[])o;
                return 0 == injected.length;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have had nothing injected into it");
            }
        };

        monitor.expects(once()).method("instantiated").with(eq(emptyHashMapCtor), isAHashMapThatWozCreated, injectedIsEmptyArray, durationIsGreaterThanOrEqualToZero);
        ConstructorInjectionAdapter cica = new ConstructorInjectionAdapter(
                Map.class, HashMap.class, new Parameter[0], (ComponentMonitor)monitor.proxy());
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
        ConstructorInjectionAdapter cica = new ConstructorInjectionAdapter(
                ActionListener.class, BarfingActionListener.class, new Parameter[0], (ComponentMonitor)monitor.proxy());
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
        ConstructorInjectionAdapter cica = new ConstructorInjectionAdapter(
                NullLifecycle.class, NullLifecycle.class, new Parameter[0],
                new DelegatingComponentMonitor(), strategy);
        Touchable touchable = new SimpleTouchable();
        cica.start(touchable);
        cica.stop(touchable);
        cica.dispose(touchable);
        assertEquals("<start<stop<dispose", strategy.recording());
    }
}
