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
import org.picocontainer.ComponentFactory;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.testmodel.FlintstonesImpl;
import org.picocontainer.testmodel.FredImpl;
import org.picocontainer.testmodel.Webster;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.WilmaImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(PeelableComponent.class);
        pico.registerComponentByClass(CoincidentallyPeelableComponent.class);
        pico.instantiateComponents();

        assertEquals(2, pico.getComponents().size());

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
        pico.registerComponent(RecordingAware2.class, RecordingAware2.class);
        pico.registerComponent(RecordingAware3.class, RecordingAware3.class);
        pico.registerComponent(RecordingAware4.class, RecordingAware4.class);

        pico.instantiateComponents();

        assertEquals("instantiated RecordingAware2", recorder.getWhatHappened(0));
        assertEquals("instantiated RecordingAware3", recorder.getWhatHappened(1));
        assertEquals("instantiated RecordingAware4", recorder.getWhatHappened(2));

        recorder.clear();

        Object washableContainer =
                pico.getCompositeComponent(false, true);

        ((Washable) washableContainer).wash();

        assertEquals("RecordingAware4.wash()", recorder.getWhatHappened(0));
        assertEquals("RecordingAware3.wash()", recorder.getWhatHappened(1));
        assertEquals("RecordingAware2.wash()", recorder.getWhatHappened(2));

    }

    public void testGetAggregateComponentProxyOnlyCallsManagedComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        RecordingAware2 unmanagedComponent = new RecordingAware2(recorder);

        recorder.clear();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(RecordingAware2.class, unmanagedComponent);
        pico.registerComponentByClass(RecordingAware3.class);
        pico.registerComponentByClass(RecordingAware4.class);

        pico.instantiateComponents();

        assertEquals("instantiated RecordingAware3", recorder.getWhatHappened(0));
        assertEquals("instantiated RecordingAware4", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.getCompositeComponent(false, false);

        ((Washable) washableContainer).wash();

        assertEquals("RecordingAware4.wash()", recorder.getWhatHappened(0));
        assertEquals("RecordingAware3.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an getCompositeComponent() proxy",
                !recorder.thingsThatHappened.contains("RecordingAware2.wash()"));
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
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(dcr);

        assertNull(dcr.findImplementingComponentSpecification(Wilma.class));
        pico.registerComponentByClass(WilmaImpl.class);
        assertNotNull(dcr.findImplementingComponentSpecification(WilmaImpl.class));
        assertNotNull(dcr.findImplementingComponentSpecification(Wilma.class));
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

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().size());

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

        assertEquals("Wrong number of comps in the container", 3, pico.getComponents().size());

        assertTrue("Object one the same", pico.getComponent("one") != null);
        assertTrue("Object two the same", pico.getComponent("two") != null);

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegistrationByInterfaceAndName() throws Exception {
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(dcr);

        Webster one = new Webster(new ArrayList());
        Webster two = new Webster(new ArrayList());

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent("one", one);
        pico.registerComponent("two", two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 4, pico.getComponents().size());

        assertTrue("There should have been a Fred in the container", pico.hasComponent(FredImpl.class));
        assertTrue(
                "There should have been a WilmaImpl in the container",
                dcr.findImplementingComponent(WilmaImpl.class) != null);

        assertEquals("Looking up one Wilma", one, pico.getComponent("one"));
        assertEquals("Looking up two Wilma", two, pico.getComponent("two"));

        assertTrue("Object one the same", one == pico.getComponent("one"));
        assertTrue("Object two the same", two == pico.getComponent("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegisterByNameResolvesToInterfaceRegisteredComponents() throws Exception {
        // TODO we should add some kind of findImplementatingComponents() method to PicoContainer!
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(dcr);

        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponent("fred", FredImpl.class);
        pico.registerComponent("fred2", FredImpl.class);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 3, pico.getComponents().size());

        assertTrue("There should have been a Wilma in the container", pico.hasComponent(Wilma.class));
        assertTrue(
                "There should have been a WilmaImpl in the container",
                dcr.findImplementingComponent(WilmaImpl.class) != null);

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

    public void testBasicContainerAsserts() {
        try {
            new DefaultPicoContainer(new DefaultComponentFactory(), null);
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().indexOf("childRegistry") >= 0);
        }
        try {
            new DefaultPicoContainer(null, new DefaultComponentRegistry());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().indexOf("componentFactory") >= 0);
        }

    }

    public void testSerializabilityOfContainer() throws NotConcreteRegistrationException,
        AssignabilityRegistrationException, PicoInitializationException,
        DuplicateComponentKeyRegistrationException, PicoInvocationTargetInitializationException,
        IOException, ClassNotFoundException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponentByClass(WilmaImpl.class);

        pico.instantiateComponents();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.of.serialization"));

        oos.writeObject(pico);

        pico = null;

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.of.serialization"));

        PicoContainer deserializedPico = (PicoContainer) ois.readObject();

        WilmaImpl wilma = (WilmaImpl) deserializedPico.getComponent(WilmaImpl.class);

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }

    public void testTooFewComponents() throws PicoInitializationException, PicoRegistrationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(FredImpl.class);

        try {
            pico.instantiateComponents();
            fail("should need a wilma");
        } catch (UnsatisfiedDependencyInstantiationException e) {
            // expected
            assertTrue(e.getClassThatNeedsDeps() == FredImpl.class);
            assertTrue(e.getMessage().indexOf(FredImpl.class.getName()) > 0);

        }
    }

    public void testDuplicateRegistrationWithTypeAndObject() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(WilmaImpl.class);
        try {
            pico.registerComponent(WilmaImpl.class, new WilmaImpl());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == WilmaImpl.class);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }
    }

    public static class DerivedWilma extends WilmaImpl {
        public DerivedWilma() {
        }
    }

    public void testAmbiguousHierarchy() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        // Register two Wilmas that Fred will be confused about
        pico.registerComponentByClass(WilmaImpl.class);
        pico.registerComponentByClass(DerivedWilma.class);

        // Register a confused Fred
        pico.registerComponentByClass(FredImpl.class);

        try {
            pico.instantiateComponents();
            fail("Fred should have been confused about the two Wilmas");
        } catch (AmbiguousComponentResolutionException e) {
            // expected

            List ambiguous = Arrays.asList(e.getResultingKeys());
            assertTrue(ambiguous.contains(DerivedWilma.class));
            assertTrue(ambiguous.contains(WilmaImpl.class));
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(DerivedWilma.class.getName()) > 0);
        }
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        RegistrationPicoContainer pico = new DefaultPicoContainer.Default();

        try {
            pico.registerComponent(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object as Serializable");
            //TODO why?
        } catch (PicoRegistrationException e) {
            //TODO contains ?

        }

    }

    public void testComponentRegistrationMismatch() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new DefaultPicoContainer.Default();

        try {
            pico.registerComponent(List.class, WilmaImpl.class);
        } catch (AssignabilityRegistrationException e) {
            // not worded in message
            assertTrue(e.getMessage().indexOf(List.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(WilmaImpl.class.getName()) > 0);
        }

    }

    public void testMultipleArgumentConstructor() throws Throwable /* fixme */ {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(FredImpl.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.registerComponentByClass(FlintstonesImpl.class);

        pico.instantiateComponents();

        assertTrue("There should have been a FlintstonesImpl in the container", pico.hasComponent(FlintstonesImpl.class));
    }

    public void testTooManyContructors() throws PicoRegistrationException, PicoInitializationException {

        RegistrationPicoContainer pico = new DefaultPicoContainer.Default();

        try {
            pico.registerComponentByClass(Vector.class);
            fail("Should fail because there are more than one constructors");
        } catch (NoPicoSuitableConstructorException e) {
            // expected;
            assertEquals("Should be right class", Vector.class,  e.getForImplementationClass());
        }

    }

    public void testRegisterAbstractShouldFail() throws PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = new DefaultPicoContainer.Default();

        try {
            pico.registerComponentByClass(Runnable.class);
            fail("Shouldn't be allowed to register abstract classes or interfaces.");
        } catch (NotConcreteRegistrationException e) {
            assertEquals(Runnable.class, e.getComponentImplementation());
            assertTrue(e.getMessage().indexOf(Runnable.class.getName()) > 0);
        }
    }

    public void testWithComponentFactory() throws PicoRegistrationException, PicoInitializationException {
        final WilmaImpl wilma = new WilmaImpl();
        DefaultPicoContainer pc = new DefaultPicoContainer.WithComponentFactory(new ComponentFactory() {
            public Object createComponent(ComponentSpecification componentSpec, Object[] args) {
                return wilma;
            }

            public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
                return new Class[0];
            }
        });
        pc.registerComponentByClass(WilmaImpl.class);
        pc.instantiateComponents();
        assertEquals(pc.getComponent(WilmaImpl.class), wilma);
    }

    public static class Barney {
        public Barney() {
            throw new RuntimeException("Whoa!");
        }
    }

    public void testInvocationTargetException() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(Barney.class);
        try {
            pico.instantiateComponents();
        } catch (PicoInvocationTargetInitializationException e) {
            assertEquals("Whoa!", e.getCause().getMessage());
            assertTrue(e.getMessage().indexOf("Whoa!") > 0);
        }
    }

    interface Animal {

        String getFood();
    }

    public static class Dino implements Animal {
        String food;

        public Dino(String food) {
            this.food = food;
        }

        public String getFood() {
            return food;
        }
    }

    public static class Dino2 extends Dino {
        public Dino2(int number) {
            super(String.valueOf(number));
        }
    }

    public static class Dino3 extends Dino {
        public Dino3(String a, String b) {
            super(a + b);
        }
    }

    public static class Dino4 extends Dino {
        public Dino4(String a, int n, String b, Wilma wilma) {
            super(a + n + b + " " + wilma.getClass().getName());
        }
    }

    public void testParameterCanBePassedToConstructor() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino.class);
        pico.addParameterToComponent(Dino.class, String.class, "bones");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public void testParameterCanBePrimitive() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino2.class);
        pico.addParameterToComponent(Dino2.class, Integer.class, new Integer(22));
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino3.class);
        pico.addParameterToComponent(Dino3.class, String.class, "a");
        pico.addParameterToComponent(Dino3.class, String.class, "b");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(Animal.class, Dino4.class);
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        pico.addParameterToComponent(Dino4.class, String.class, "a");
        pico.addParameterToComponent(Dino4.class, Integer.class, new Integer(3));
        pico.addParameterToComponent(Dino4.class, String.class, "b");
        pico.instantiateComponents();

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b org.picocontainer.testmodel.WilmaImpl", animal.getFood());
    }

    public static interface I {
    }

    public static class AA implements I {
        public AA(I i) {
        }
    }

    public static class BB implements I {
        public BB(I i) {
        }
    }

    public void testExtendAndDependOnSameType() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponentByClass(AA.class);
        pico.registerComponentByClass(BB.class);

        try {
            pico.instantiateComponents();
            fail("Should have barfed");
        } catch (AmbiguousComponentResolutionException e) {
            // Neither can be instantiated without the other.
        }
    }

}
