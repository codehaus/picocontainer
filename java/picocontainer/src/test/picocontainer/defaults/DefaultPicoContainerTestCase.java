/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import junit.framework.TestCase;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;

import java.util.ArrayList;
import java.util.List;

public class DefaultPicoContainerTestCase extends TestCase {

    public interface Peelable {
        void peel();
    }

    public interface Washable {
        void wash();
    }

    public interface Startable {
        void start();

        void stop();
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

    public static class Aa extends RecordingAware implements Washable {
        public Aa(Recorder recorder) {
            super(recorder);
        }

        public void wash() {
            recorder.record("Aa.wash()");
        }
    }

    public static class Bb extends RecordingAware implements Washable {
        public Bb(Recorder recorder, Aa a) {
            super(recorder);
            a.toString();
        }

        public void wash() {
            recorder.record("Bb.wash()");
        }
    }

    public static class Cc extends RecordingAware implements Washable {
        public Cc(Recorder recorder, Aa a, Bb b) {
            super(recorder);
            a.toString();
            b.toString();
        }

        public void wash() {
            recorder.record("Cc.wash()");
        }
    }

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.instantiateComponents();

        assertEquals(2, pico.getComponents().length);

        Peelable myPeelableContainer = (Peelable) pico.getAggregateComponentProxy();

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testWorksWithMultipleInterfaces() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getAggregateComponentProxy();

        ((Washable) myPeelableAndWashableContainer).wash();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);
        ;

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable) myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

    }

    public void testAsCallsAllComponents() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        //an unmanaged component
        PeelableComponent peelableComponent = new PeelableComponent();
        pico.registerComponent(PeelableComponent.class, peelableComponent);

        //some managed ones
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getAggregateComponentProxy();

        ((Washable) myPeelableAndWashableContainer).wash();

        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable) myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

    }

    public void testBespokeLifecycleCallsComponentsInReverseOrder() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(Aa.class, Aa.class);
        pico.registerComponent(Bb.class, Bb.class);
        pico.registerComponent(Cc.class, Cc.class);

        pico.instantiateComponents();

        assertEquals("instantiated Aa", recorder.getWhatHappened(0));
        assertEquals("instantiated Bb", recorder.getWhatHappened(1));
        assertEquals("instantiated Cc", recorder.getWhatHappened(2));

        recorder.clear();

        Object washableContainer =
                pico.getAggregateComponentProxy(false, true);

        ((Washable) washableContainer).wash();

        assertEquals("Cc.wash()", recorder.getWhatHappened(0));
        assertEquals("Bb.wash()", recorder.getWhatHappened(1));
        assertEquals("Aa.wash()", recorder.getWhatHappened(2));

    }

    public void testGetAggregateComponentProxyOnlyCallsManagedComponents() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        Aa unmanagedComponent = new Aa(recorder);

        recorder.clear();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(Aa.class, unmanagedComponent);
        pico.registerComponent(Bb.class);
        pico.registerComponent(Cc.class);

        pico.instantiateComponents();

        assertEquals("instantiated Bb", recorder.getWhatHappened(0));
        assertEquals("instantiated Cc", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.getAggregateComponentProxy(false, false);

        ((Washable) washableContainer).wash();

        assertEquals("Cc.wash()", recorder.getWhatHappened(0));
        assertEquals("Bb.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an getAggregateComponentProxy() proxy",
                !recorder.thingsThatHappened.contains("Aa.wash()"));
    }

    public void testPeelableAndWashable() throws PicoIntrospectionException, PicoRegistrationException, PicoInstantiationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object proxy = pico.getAggregateComponentProxy();

        Peelable peelable = (Peelable) proxy;
        peelable.peel();

        Washable washable = (Washable) proxy;
        washable.wash();

        PeelableComponent pComp = (PeelableComponent) pico.getComponent(PeelableComponent.class);
        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);

        assertTrue(pComp.wasPeeled);
        assertTrue(peelNWash.wasWashed);

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
    }

    public abstract static class AbstractFood extends RecordingAware implements Food {
        public AbstractFood(Recorder recorder) {
            super(recorder);
        }
    }

    public static class Apple extends AbstractFood {
        public Apple(Recorder recorder) {
            super(recorder);
        }

        public void eat() {
            recorder.record("Apple eaten");
        }
    }

    public static class Orange extends AbstractFood {
        public Orange(Recorder recorder) {
            super(recorder);
        }

        public void eat() {
            recorder.record("Orange eaten");
        }
    }


    public void testRecursiveAggregation() throws NotConcreteRegistrationException, AssignabilityRegistrationException, DuplicateComponentTypeRegistrationException, PicoIntrospectionException, PicoInstantiationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Recorder.class);
        pico.registerComponent(AppleFactory.class);
        pico.registerComponent(OrangeFactory.class);

        pico.instantiateComponents();

        // Get the proxy for AppleFactory and OrangeFactory
        FoodFactory foodFactory = (FoodFactory) pico.getAggregateComponentProxy();

// TODO uncomment this
//        int foodFactoryCode = foodFactory.hashCode();
//        assertFalse("Should get a real hashCode", Integer.MIN_VALUE == foodFactoryCode);

        // Get the proxied Food and eat it. Should eat the orange and apple in one go.
        Food food = foodFactory.makeFood();
        food.eat();

        String s = food.toString();
        assertNull("toString() should return null", s);
        // Try to call a method returning a primitive.

// TODO uncomment. Should be tested too
//        int foodCode = food.hashCode();

        // Get the recorder so we can see if the apple and orange were actually eaten
        Recorder recorder = (Recorder) pico.getComponent(Recorder.class);
        assertTrue("Apple should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Apple eaten"));
        assertTrue("Orange should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Orange eaten"));

    }

//    public static interface PeelableAndWashableContainer extends PeelableAndWashable, ClassRegistrationPicoContainer {
//
//    }

//    public void testPeelableAndWashableContainer() throws WrongNumberOfConstructorsException, PicoRegistrationException, PicoStartException {
//
//        PeelableAndWashableContainer pawContainer = (PeelableAndWashableContainer)
//                new MorphingHierarchicalPicoContainer(
//                        new NullContainer(),
//                        new NullLifecycleManager(),
//                        new DefaultComponentFactory())
//                .as(PeelableAndWashableContainer.class);
//
//        pawContainer.registerComponent(PeelableComponent.class);
//        pawContainer.registerComponent(PeelableAndWashableComponent.class);
//
//        pawContainer.instantiateComponents();
//
//        pawContainer.wash();
//        pawContainer.peel();
//
//        PeelableComponent pComp = (PeelableComponent) pawContainer.getComponent(PeelableComponent.class);
//        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pawContainer.getComponent(PeelableAndWashableComponent.class);
//
//        assertTrue(pComp.wasPeeled);
//        assertTrue(peelNWash.wasWashed);
//
//    }

    public void testBasicComponentInteraction() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        WilmaImpl wilma = (WilmaImpl) pico.getComponent(WilmaImpl.class);

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }

    public void testGetComponentSpecification() throws NotConcreteRegistrationException, DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, AmbiguousComponentResolutionException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertNull(pico.findComponentSpecification(Wilma.class));
        pico.registerComponent(WilmaImpl.class);
        assertNotNull(pico.findComponentSpecification(WilmaImpl.class));
        assertNotNull(pico.findComponentSpecification(Wilma.class));
    }

    public void testComponentSpecInstantiateComponentWithNoDependencies() throws PicoInstantiationException, PicoIntrospectionException {
        ComponentSpecification componentSpec = new ComponentSpecification(new DefaultComponentFactory(), WilmaImpl.class, WilmaImpl.class, new Parameter[0]);
        Object comp = componentSpec.instantiateComponent(null);
        assertNotNull(comp);
        assertTrue(comp instanceof WilmaImpl);
    }

//    public void testComponentSpecInstantiateComponentWithOneDependency() throws PicoInvocationTargetInitailizationException {
//        ComponentSpecification componentSpec = new ComponentSpecification(FredImpl.class, FredImpl.class);
//
//        Mock mockPicoContainer = new OrderedMock(PicoContainer.class);
//        mockPicoContainer.expectAndReturn("getComponent", C.args(C.eq(Wilma.class)), new WilmaImpl());
//
//        Object comp = componentSpec.instantiateComponent((PicoContainer) mockPicoContainer.proxy(),
//                new DefaultComponentFactory());
//
//        assertNotNull(comp);
//        assertTrue(comp instanceof FredImpl);
//        mockPicoContainer.verify();
//    }

    public void testInstantiateOneComponent() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(WilmaImpl.class);
        pico.instantiateComponents();

        WilmaImpl wilma = (WilmaImpl) pico.getComponent(WilmaImpl.class);

        assertNotNull(wilma);
    }
}
