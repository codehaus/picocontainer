/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.multicast;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.Startable;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.jmock.Mock;
import org.jmock.C;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ComponentMulticasterAdapterTestCase extends TestCase {
    public interface Peelable {
        void peel();
    }

    public interface Washable {
        void wash();
    }

    public static class PeelableComponent implements Peelable {
        boolean wasPeeled;

        public PeelableComponent() {
        }

        public void peel() {
            wasPeeled = true;
        }
    }

    public static class CoincidentallyPeelableComponent {
        boolean wasPeeled;

        public void peel() {
            wasPeeled = true;
        }
    }

    public static class PeelableAndWashableComponent extends PeelableComponent implements Washable {
        boolean wasWashed;

        public void wash() {
            wasWashed = true;
        }
    }

    public static class Recorder {
        List thingsThatHappened = new ArrayList();

        public void record(String something) {
            thingsThatHappened.add(something);
        }

        public String getWhatHappened(int i) {
            return (String) thingsThatHappened.get(i);
        }

        public void clear() {
            thingsThatHappened.clear();
        }
    }

    public static abstract class RecordingAware {
        protected Recorder recorder;

        public RecordingAware(Recorder recorder) {
            this.recorder = recorder;
            String name = getClass().getName();
            String niceName = name.substring(name.lastIndexOf("$") + 1);
            recorder.record("instantiated " + niceName);
        }
    }

    public static class RecordingAware2 extends RecordingAware implements Washable {
        public RecordingAware2(Recorder recorder) {
            super(recorder);
        }

        public void wash() {
            recorder.record("RecordingAware2.wash()");
        }
    }

    public static class RecordingAware3 extends RecordingAware implements Washable {
        public RecordingAware3(Recorder recorder, RecordingAware2 a) {
            super(recorder);
            a.toString();
        }

        public void wash() {
            recorder.record("RecordingAware3.wash()");
        }
    }

    public static class RecordingAware4 extends RecordingAware implements Washable {
        public RecordingAware4(Recorder recorder, RecordingAware2 a, RecordingAware3 b) {
            super(recorder);
            a.toString();
            b.toString();
        }

        public void wash() {
            recorder.record("RecordingAware4.wash()");
        }
    }

    public static interface FoodFactory {
        Food makeFood();

        int hashCode();
    }

    public abstract static class AbstractFoodFactory extends RecordingAware implements FoodFactory {
        public AbstractFoodFactory(Recorder recorder) {
            super(recorder);
        }
    }

    public static class AppleFactory extends AbstractFoodFactory {
        public AppleFactory(Recorder recorder) {
            super(recorder);
        }

        public Food makeFood() {
            return new Apple(recorder);
        }
    }

    public static class OrangeFactory extends AbstractFoodFactory {
        public OrangeFactory(Recorder recorder) {
            super(recorder);
        }

        public Food makeFood() {
            return new Orange(recorder);
        }
    }

    public static interface Food {
        void eat();

        int magic();
    }

    public abstract static class AbstractFood extends RecordingAware implements Food {
        public AbstractFood(Recorder recorder) {
            super(recorder);
        }
    }

    public static interface Boozable {
        String getBooze();
    }

    public static class Apple extends AbstractFood implements Boozable {
        public Apple(Recorder recorder) {
            super(recorder);
        }

        public void eat() {
            recorder.record("Apple eaten");
        }

        public int magic() {
            return 5;
        }

        public String getBooze() {
            return "Calvados";
        }
    }

    public static class Orange extends AbstractFood {
        public Orange(Recorder recorder) {
            super(recorder);
        }

        public void eat() {
            recorder.record("Orange eaten");
        }

        public int magic() {
            return 11;
        }
    }

    public void testMulticasterInvocationsCanBeInterceptedByInvocationInterceptor() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        Mock mockStartable = new Mock(Startable.class);
        mockStartable.expect("start", C.args());
        pico.registerComponentInstance(mockStartable.proxy());

        Mock mockInvocationInterceptor = new Mock(InvocationInterceptor.class);
        mockInvocationInterceptor.expect("intercept", C.args(C.isA(Method.class), C.IS_ANYTHING, C.IS_ANYTHING));

        ComponentMulticasterAdapter cma = new ComponentMulticasterAdapter(new StandardProxyMulticasterFactory(), (InvocationInterceptor)mockInvocationInterceptor.proxy());
        Startable startable = (Startable) cma.getComponentMulticaster(pico, true);

        startable.start();

        mockStartable.verify();
        mockInvocationInterceptor.verify();
    }

    public static interface Standalone{}
    public static class StandaloneImpl implements Standalone{}
    public static class NeedsOne {
        public NeedsOne(Standalone standalone) {
        }
    }
    public void testMulticasterDoesntMulticastToParent() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer(parent);

        parent.registerComponentImplementation(StandaloneImpl.class);
        child.registerComponentImplementation(NeedsOne.class);

        Object multicaster = new ComponentMulticasterAdapter().getComponentMulticaster(child, true);
        assertTrue(multicaster instanceof Serializable);
        assertFalse(multicaster instanceof Standalone);
    }

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(PeelableComponent.class);
        pico.registerComponentImplementation(CoincidentallyPeelableComponent.class);

        assertEquals(2, pico.getComponentInstances().size());

        Peelable myPeelableContainer = (Peelable) new ComponentMulticasterAdapter().getComponentMulticaster(pico, true);

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponentInstance(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponentInstance(CoincidentallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testPeelableAndWashable() throws PicoInitializationException, PicoRegistrationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(PeelableComponent.class);
        pico.registerComponentImplementation(PeelableAndWashableComponent.class);

        Object proxy = new ComponentMulticasterAdapter().getComponentMulticaster(pico, true);

        Peelable peelable = (Peelable) proxy;
        peelable.peel();

        Washable washable = (Washable) proxy;
        washable.wash();

        PeelableComponent pComp = (PeelableComponent) pico.getComponentInstance(PeelableComponent.class);
        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pico.getComponentInstance(PeelableAndWashableComponent.class);

        assertTrue(pComp.wasPeeled);
        assertTrue(peelNWash.wasWashed);

    }

    public void testRecursiveAggregation()
            throws PicoRegistrationException, AssignabilityRegistrationException,
            DuplicateComponentKeyRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Recorder.class);
        pico.registerComponentImplementation(AppleFactory.class);
        pico.registerComponentImplementation(OrangeFactory.class);

        // Get the proxy for AppleFactory and OrangeFactory
        FoodFactory foodFactory = (FoodFactory) new ComponentMulticasterAdapter().getComponentMulticaster(pico, true);

        int foodFactoryCode = foodFactory.hashCode();
        assertFalse("Should get a real hashCode", Integer.MIN_VALUE == foodFactoryCode);

        // Get the proxied Food and eat it. Should eat the orange and apple in one go.
        Food food = foodFactory.makeFood();
        food.eat();

        String s = food.toString();
        assertTrue("getOriginalFileName() should return the result from the invocation handler", s.indexOf("AggregatingInvocationHandler") != -1);

        // Try to call a hashCode on a "recursive" proxy.
        food.hashCode();

        // Get some booze. Should be ok since only one is Boozable
        Boozable boozable = (Boozable) food;
        assertEquals("Calvados", boozable.getBooze());

        // Test hashCode() and equals(Object)
        List list = new ArrayList();
        list.add(food);
        assertTrue(list.contains(food));
        assertEquals(food, food);

        // Get the recorder so we can see if the apple and orange were actually eaten
        Recorder recorder = (Recorder) pico.getComponentInstance(Recorder.class);
        assertTrue("Apple should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Apple eaten"));
        assertTrue("Orange should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Orange eaten"));

    }

    public void testMulticastingToConcreteClasses() {
        List one = new ArrayList();
        List two = new ArrayList();

        MulticasterFactory multicasterFactory = new StandardProxyMulticasterFactory();
        List multicaster = (List) multicasterFactory.createComponentMulticaster(
                getClass().getClassLoader(),
                Arrays.asList(new List[]{one, two}),
                false,
                new NullInvocationInterceptor(),
                new MulticastInvoker()
        );
        multicaster.add("hello");
        assertTrue(one.contains("hello"));
        assertTrue(two.contains("hello"));
    }


}