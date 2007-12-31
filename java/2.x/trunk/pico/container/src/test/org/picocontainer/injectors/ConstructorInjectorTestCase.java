/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;

import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.junit.Test;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


public class ConstructorInjectorTestCase extends AbstractComponentAdapterTestCase {

    protected Class getComponentAdapterType() {
        return ConstructorInjector.class;
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new ConstructorInjector("foo", A.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
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
        return new ConstructorInjector(B.class, B.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    protected ComponentAdapter prepDEF_visitable() {
        return new ConstructorInjector("bar", B.class, new Parameter[] {ComponentParameter.DEFAULT} , new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(SimpleTouchable.class);
        return new ConstructorInjector(
                NamedDependsOnTouchable.class, NamedDependsOnTouchable.class,
                new Parameter[] {ComponentParameter.DEFAULT, new ConstantParameter("Name")} , new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new ConstructorInjector(SimpleTouchable.class, SimpleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
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
        return new ConstructorInjector(DependsOnTouchable.class, DependsOnTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        return new ConstructorInjector(SimpleTouchable.class, SimpleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    public static class Erroneous {
        public Erroneous() {
            throw new VerifyError("test");
        }
    }

    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjector(Erroneous.class, Erroneous.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    public static class RuntimeThrowing {
        public RuntimeThrowing() {
            throw new RuntimeException("test");
        }
    }

    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        return new ConstructorInjector(RuntimeThrowing.class, RuntimeThrowing.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    public static class NormalExceptionThrowing {
        public NormalExceptionThrowing() throws Exception {
            throw new Exception("test");
        }
    }

    protected ComponentAdapter prepINS_normalExceptionIsRethrownInsidePicoInitializationException(
            MutablePicoContainer picoContainer) {
        return new ConstructorInjector(NormalExceptionThrowing.class, NormalExceptionThrowing.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
    }

    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(SimpleTouchable.class);
        return new ConstructorInjector(DependsOnTouchable.class, DependsOnTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
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
        final ComponentAdapter componentAdapter = new ConstructorInjector(C1.class, C1.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        picoContainer.addAdapter(componentAdapter);
        picoContainer.addComponent(C2.class, C2.class);
        return componentAdapter;
    }

    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        final ComponentAdapter componentAdapter = new ConstructorInjector(C1.class, C1.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        picoContainer.addAdapter(componentAdapter);
        picoContainer.addComponent(C2.class, C2.class);
        return componentAdapter;
    }

    @Test public void testNormalExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.addComponent(NormalExceptionThrowing.class);
        try {
            picoContainer.getComponent(NormalExceptionThrowing.class);
            fail();
        } catch (PicoCompositionException e) {
            assertEquals("test", e.getCause().getMessage());
        }
    }

    public static class InstantiationExceptionThrowing {
        public InstantiationExceptionThrowing() {
            throw new RuntimeException("Barf");
        }
    }

    @Test public void testInstantiationExceptionThrownInCtorIsRethrownInsideInvocationTargetExeption() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.addComponent(InstantiationExceptionThrowing.class);
            picoContainer.getComponent(InstantiationExceptionThrowing.class);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Barf", e.getMessage());
        }
    }

    public static class AllConstructorsArePrivate {
        private AllConstructorsArePrivate() {
        }
    }

    @Test public void testPicoInitializationExceptionThrownBecauseOfFilteredConstructors() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        try {
            picoContainer.addComponent(AllConstructorsArePrivate.class);
            picoContainer.getComponent(AllConstructorsArePrivate.class);
            fail();
        } catch (PicoCompositionException e) {
            String s = e.getMessage();
            assertTrue(s.indexOf("constructors were not accessible") > 0);
            assertTrue(s.indexOf(AllConstructorsArePrivate.class.getName()) > 0);
        }
    }

    @Test public void testRegisterInterfaceShouldFail() throws PicoCompositionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.addComponent(Runnable.class);
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (AbstractInjector.NotConcreteRegistrationException e) {
            assertEquals(Runnable.class, e.getComponentImplementation());
            assertTrue(e.getMessage().indexOf(Runnable.class.getName()) > 0);
        }
    }

    @Test public void testRegisterAbstractShouldFail() throws PicoCompositionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.addComponent(AbstractButton.class);
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (AbstractInjector.NotConcreteRegistrationException e) {
            assertEquals(AbstractButton.class, e.getComponentImplementation());
            assertTrue(e.getMessage().indexOf(AbstractButton.class.getName()) > 0);
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
    @Test public void testShouldNotConsiderNonPublicConstructors() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Component201.class);
        pico.addComponent(new Integer(2));
        pico.addComponent(Boolean.TRUE);
        pico.addComponent("Hello");
        assertNotNull(pico.getComponent(Component201.class));
    }

    @Test public void testMonitoringHappensBeforeAndAfterInstantiation() throws NoSuchMethodException {
        Mock monitor = mock(ComponentMonitor.class);
        Constructor emptyHashMapCtor = HashMap.class.getConstructor();
        monitor.expects(once()).method("instantiating").with(NULL, isA(ConstructorInjector.class),eq(emptyHashMapCtor)).will(returnValue(emptyHashMapCtor));
        Constraint durationIsGreaterThanOrEqualToZero = new Constraint() {
            public boolean eval(Object o) {
                Long duration = (Long)o;
                return 0 <= duration;
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

        monitor.expects(once()).method("instantiated").with(new Constraint[] {NULL, isA(ComponentAdapter.class), eq(emptyHashMapCtor), isAHashMapThatWozCreated, injectedIsEmptyArray, durationIsGreaterThanOrEqualToZero});
        ConstructorInjector cica = new ConstructorInjector(
                Map.class, HashMap.class, new Parameter[0], (ComponentMonitor)monitor.proxy(), new NullLifecycleStrategy(), false);
        cica.getComponentInstance(null);
    }

    @Test public void testMonitoringHappensBeforeAndOnFailOfImpossibleComponentsInstantiation() throws NoSuchMethodException {
        Mock monitor = mock(ComponentMonitor.class);
        Constructor barfingActionListenerCtor = BarfingActionListener.class.getConstructor();
        monitor.expects(once()).method("instantiating").with(new Constraint[] {NULL,isA(ComponentAdapter.class), eq(barfingActionListenerCtor)}).will(returnValue(barfingActionListenerCtor));

        Constraint isITE = new Constraint() {
            public boolean eval(Object o) {
                Exception ex = (Exception)o;
                return ex instanceof InvocationTargetException;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have been unable to instantiate");
            }
        };

        monitor.expects(once()).method("instantiationFailed").with(NULL,isA(ComponentAdapter.class), eq(barfingActionListenerCtor), isITE);
        ConstructorInjector cica = new ConstructorInjector(
                ActionListener.class, BarfingActionListener.class, new Parameter[0], (ComponentMonitor)monitor.proxy(), new NullLifecycleStrategy(), false);
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

    @Test public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        ConstructorInjector cica = new ConstructorInjector(
                NullLifecycle.class, NullLifecycle.class, new Parameter[0],
                new AbstractComponentMonitor(), strategy, false);
        Touchable touchable = new SimpleTouchable();
        cica.start(touchable);
        cica.stop(touchable);
        cica.dispose(touchable);
        assertEquals("<start<stop<dispose", strategy.recording());
    }
}
