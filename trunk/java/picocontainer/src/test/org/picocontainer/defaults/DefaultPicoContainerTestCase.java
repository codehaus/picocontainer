/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.FredImpl;
import org.picocontainer.testmodel.Webster;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.WilmaImpl;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(PeelableComponent.class);
        pico.registerComponentByClass(CoincidentallyPeelableComponent.class);
        pico.instantiateComponents();

        assertEquals(2, pico.getComponents().length);

        Peelable myPeelableContainer = (Peelable) pico.getCompositeComponent();

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testWorksWithMultipleInterfaces() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(PeelableComponent.class);
        pico.registerComponentByClass(CoincidentallyPeelableComponent.class);
        pico.registerComponentByClass(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getCompositeComponent();

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

    public void testAsCallsAllComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        //an unmanaged component
        PeelableComponent peelableComponent = new PeelableComponent();
        pico.registerComponent(PeelableComponent.class, peelableComponent);

        //some managed ones
        pico.registerComponentByClass(CoincidentallyPeelableComponent.class);
        pico.registerComponentByClass(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getCompositeComponent(true, true);

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

    public void testBespokeLifecycleCallsComponentsInReverseOrder() throws PicoRegistrationException, PicoInitializationException {

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
                pico.getCompositeComponent(false, true);

        ((Washable) washableContainer).wash();

        assertEquals("Cc.wash()", recorder.getWhatHappened(0));
        assertEquals("Bb.wash()", recorder.getWhatHappened(1));
        assertEquals("Aa.wash()", recorder.getWhatHappened(2));

    }

    public void testGetAggregateComponentProxyOnlyCallsManagedComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        Aa unmanagedComponent = new Aa(recorder);

        recorder.clear();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(Aa.class, unmanagedComponent);
        pico.registerComponentByClass(Bb.class);
        pico.registerComponentByClass(Cc.class);

        pico.instantiateComponents();

        assertEquals("instantiated Bb", recorder.getWhatHappened(0));
        assertEquals("instantiated Cc", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.getCompositeComponent(false, false);

        ((Washable) washableContainer).wash();

        assertEquals("Cc.wash()", recorder.getWhatHappened(0));
        assertEquals("Bb.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an getCompositeComponent() proxy",
                !recorder.thingsThatHappened.contains("Aa.wash()"));
    }

    public void testPeelableAndWashable() throws PicoInitializationException, PicoRegistrationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(PeelableComponent.class);
        pico.registerComponentByClass(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object proxy = pico.getCompositeComponent();

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


    public void testRecursiveAggregation()
            throws NotConcreteRegistrationException, AssignabilityRegistrationException,
            DuplicateComponentKeyRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(Recorder.class);
        pico.registerComponentByClass(AppleFactory.class);
        pico.registerComponentByClass(OrangeFactory.class);

        pico.instantiateComponents();

        // Get the proxy for AppleFactory and OrangeFactory
        FoodFactory foodFactory = (FoodFactory) pico.getCompositeComponent();

        int foodFactoryCode = foodFactory.hashCode();
        assertFalse("Should get a real hashCode", Integer.MIN_VALUE == foodFactoryCode);

        // Get the proxied Food and eat it. Should eat the orange and apple in one go.
        Food food = foodFactory.makeFood();
        food.eat();

        String s = food.toString();
        assertTrue("toString() should return the result from the invocation handler", s.indexOf("AggregatingInvocationHandler") != -1);

        // Try to call a hashCode on a "recursive" proxy.
        food.hashCode();

        // Try to call a method returning a primitive.
        try {
            food.magic();
            fail("Should fail because there is no sensible return value");
        } catch (UndeclaredThrowableException e) {
            // That's expected, and ok.
        }

        // Get some booze. Should be ok since only one is Boozable
        Boozable boozable = (Boozable) food;
        assertEquals("Calvados", boozable.getBooze());

        // Test hashCode() and equals(Object)
        List list = new ArrayList();
        list.add(food);
        assertTrue(list.contains(food));
        assertEquals(food, food);

        // Get the recorder so we can see if the apple and orange were actually eaten
        Recorder recorder = (Recorder) pico.getComponent(Recorder.class);
        assertTrue("Apple should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Apple eaten"));
        assertTrue("Orange should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Orange eaten"));

    }

//    public static interface PeelableAndWashableContainer extends PeelableAndWashable, RegistrationPicoContainer {
//
//    }

//    public void testPeelableAndWashableContainer() throws NoPicoSuitableConstructorException, PicoRegistrationException, PicoStartException {
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

    public void testBasicComponentInteraction() throws PicoInitializationException, PicoRegistrationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponentByClass(WilmaImpl.class);

        pico.instantiateComponents();

        WilmaImpl wilma = (WilmaImpl) pico.getComponent(WilmaImpl.class);

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }

    public void testGetComponentSpecification() throws NotConcreteRegistrationException, DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, AmbiguousComponentResolutionException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertNull(pico.findImplementingComponentSpecification(Wilma.class));
        pico.registerComponentByClass(WilmaImpl.class);
        assertNotNull(pico.findImplementingComponentSpecification(WilmaImpl.class));
        assertNotNull(pico.findImplementingComponentSpecification(Wilma.class));
    }

    public void testComponentSpecInstantiateComponentWithNoDependencies() throws PicoInitializationException {
        ComponentSpecification componentSpec = new ComponentSpecification(new DefaultComponentFactory(), WilmaImpl.class, WilmaImpl.class, new Parameter[0]);
        Object comp = componentSpec.instantiateComponent(null);
        assertNotNull(comp);
        assertTrue(comp instanceof WilmaImpl);
    }

    public void testDoubleInstantiation() throws PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.instantiateComponents();
        try {
            pico.instantiateComponents();
            fail("should have barfed");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testInstantiateOneComponent() throws PicoInitializationException, PicoRegistrationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(WilmaImpl.class);
        pico.instantiateComponents();

        WilmaImpl wilma = (WilmaImpl) pico.getComponent(WilmaImpl.class);

        assertNotNull(wilma);
    }

    public void testMultipleImplementationsAccessedThroughKey()
            throws PicoInitializationException, PicoRegistrationException, PicoInvocationTargetInitializationException {
        WilmaImpl wilma1 = new WilmaImpl();
        WilmaImpl wilma2 = new WilmaImpl();
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent("wilma1", wilma1);
        pico.registerComponent("wilma2", wilma2);
        pico.registerComponent("fred1", FredImpl.class, new Parameter[]{new ComponentParameter("wilma1")});
        pico.registerComponent("fred2", FredImpl.class, new Parameter[]{new ComponentParameter("wilma2")});

        pico.instantiateComponents();

        FredImpl fred1 = (FredImpl) pico.getComponent("fred1");
        FredImpl fred2 = (FredImpl) pico.getComponent("fred2");

        assertFalse(fred1 == fred2);
        assertSame(wilma1, fred1.getWilma());
        assertSame(wilma2, fred2.getWilma());
    }

    public void testRegistrationByName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Webster one = new Webster(new ArrayList());
        Wilma two = new WilmaImpl();

        pico.registerComponent("one", one);
        pico.registerComponent("two", two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().length);

        assertEquals("Looking up one Wilma", one, pico.getComponent("one"));
        assertEquals("Looking up two Wilma", two, pico.getComponent("two"));

        assertTrue("Object one the same", one == pico.getComponent("one"));
        assertTrue("Object two the same", two == pico.getComponent("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegistrationByNameAndClassWithResolving() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(List.class, new ArrayList());
        pico.registerComponent("one", Webster.class);
        pico.registerComponent("two", WilmaImpl.class);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 3, pico.getComponents().length);

        assertTrue("Object one the same", pico.getComponent("one") != null);
        assertTrue("Object two the same", pico.getComponent("two") != null);

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegistrationByInterfaceAndName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Webster one = new Webster(new ArrayList());
        Webster two = new Webster(new ArrayList());

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent("one", one);
        pico.registerComponent("two", two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 4, pico.getComponents().length);

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue(
                "There should have been a WilmaImpl in the container",
                pico.findImplementingComponent(WilmaImpl.class) != null);

        assertEquals("Looking up one Wilma", one, pico.getComponent("one"));
        assertEquals("Looking up two Wilma", two, pico.getComponent("two"));

        assertTrue("Object one the same", one == pico.getComponent("one"));
        assertTrue("Object two the same", two == pico.getComponent("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegisterByNameResolvesToInterfaceRegisteredComponents() throws Exception {
        // TODO we should add some kind of findImplementatingComponents() method to PicoContainer!
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent("fred", FredImpl.class);
        pico.registerComponent("fred2", FredImpl.class);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 3, pico.getComponents().length);

        assertTrue("There should have been a Wilma in the container", pico.hasComponent(Wilma.class));
        assertTrue(
                "There should have been a WilmaImpl in the container",
                pico.findImplementingComponent(WilmaImpl.class) != null);

        FredImpl fred = (FredImpl) pico.getComponent("fred");
        FredImpl fred2 = (FredImpl) pico.getComponent("fred2");

        assertTrue("Found fred", fred != null);
        assertTrue("Found fred2", fred2 != null);

        // lets check that the wilma's have been resolved
        assertTrue("fred should have a wilma", fred.getWilma() != null);
        assertTrue("fred2 should have a wilma", fred2.getWilma() != null);

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testDuplicateRegistration() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent("one", new WilmaImpl());
        try {
            pico.registerComponent("one", new WilmaImpl());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue("Wrong key", e.getDuplicateKey() == "one");
//            assertTrue("Wrong component", e.getComponent() instanceof WilmaImpl);
//            assertTrue("Wrong message: " + e.getMessage(), e.getMessage().startsWith("Key: one duplicated, cannot register:"));
        }
    }

    public void testHasComponentByKeyForObjectRegistration() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertTrue("should not have non existent component", !pico.hasComponent("doesNotExist"));

        pico.registerComponent("foo", new WilmaImpl());

        assertTrue("has component", pico.hasComponent("foo"));
    }

    public void testHasComponentByKeyForClassRegistration() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertTrue("should not have non existent component", !pico.hasComponent("doesNotExist"));

        pico.registerComponent("foo", WilmaImpl.class);

        // TODO should this really need to be called to check whether container has component or not
        pico.instantiateComponents();

        assertTrue("has component", pico.hasComponent("foo"));
    }

    public void testGetComponentByKeyForObjectRegistration() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertTrue("should not have non existent component", pico.getComponent("doesNotExist") == null);

        pico.registerComponent("foo", new WilmaImpl());

        pico.instantiateComponents();

        assertTrue("has component", pico.getComponent("foo") != null);
    }

    public void testGetComponentByKeyForClassRegistration() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        assertTrue("should not have non existent component", pico.getComponent("doesNotExist") == null);

        pico.registerComponent("foo", WilmaImpl.class);

        pico.instantiateComponents();

        assertTrue("has component", pico.getComponent("foo") != null);
    }

    public void testUnmanagedCompsAreNotEligibleForComposite() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Map.class, new HashMap());
        pico.instantiateComponents();

        try {
            Map map = (Map) pico.getCompositeComponent();
            fail("Unmanaged comps should nopt make it into the composite");
        } catch (ClassCastException e) {
            // expected
        }
    }

}
