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
import org.picocontainer.*;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.DependsOnTwoComponents;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.Webster;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;

public class OldDefaultPicoContainerTestCase extends TestCase {

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

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(PeelableComponent.class);
        pico.registerComponentImplementation(CoincidentallyPeelableComponent.class);

        assertEquals(2, pico.getComponentInstances().size());

        Peelable myPeelableContainer = (Peelable) pico.getComponentMulticaster();

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponentInstance(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponentInstance(CoincidentallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testWorksWithMultipleInterfaces() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(PeelableComponent.class);
        pico.registerComponentImplementation(CoincidentallyPeelableComponent.class);
        pico.registerComponentImplementation(PeelableAndWashableComponent.class);

        Object myPeelableAndWashableContainer = pico.getComponentMulticaster();

        ((Washable) myPeelableAndWashableContainer).wash();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponentInstance(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponentInstance(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponentInstance(PeelableAndWashableComponent.class);
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

        DefaultPicoContainer pico = new DefaultPicoContainer();

        //an unmanaged component
        PeelableComponent peelableComponent = new PeelableComponent();
        pico.registerComponentInstance(PeelableComponent.class, peelableComponent);

        //some managed ones
        pico.registerComponentImplementation(CoincidentallyPeelableComponent.class);
        pico.registerComponentImplementation(PeelableAndWashableComponent.class);

        Object myPeelableAndWashableContainer = pico.getComponentMulticaster(true, true);

        ((Washable) myPeelableAndWashableContainer).wash();

        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponentInstance(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponentInstance(PeelableAndWashableComponent.class);

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable) myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

    }

    public void testBespokeLifecycleCallsComponentsInReverseOrder() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        Recorder recorder = new Recorder();

        pico.registerComponentInstance(Recorder.class, recorder);

        pico.registerComponentImplementation(RecordingAware2.class, RecordingAware2.class);
        pico.registerComponentImplementation(RecordingAware3.class, RecordingAware3.class);
        pico.registerComponentImplementation(RecordingAware4.class, RecordingAware4.class);

        pico.getComponentInstances();

        assertEquals("instantiated RecordingAware2", recorder.getWhatHappened(0));
        assertEquals("instantiated RecordingAware3", recorder.getWhatHappened(1));
        assertEquals("instantiated RecordingAware4", recorder.getWhatHappened(2));

        recorder.clear();

        Object washableContainer =
                pico.getComponentMulticaster(false, true);

        ((Washable) washableContainer).wash();

        assertEquals("RecordingAware4.wash()", recorder.getWhatHappened(0));
        assertEquals("RecordingAware3.wash()", recorder.getWhatHappened(1));
        assertEquals("RecordingAware2.wash()", recorder.getWhatHappened(2));

    }

    public void testGetAggregateComponentProxyOnlyCallsManagedComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        Recorder recorder = new Recorder();

        RecordingAware2 unmanagedComponent = new RecordingAware2(recorder);

        recorder.clear();

        pico.registerComponentInstance(Recorder.class, recorder);
        pico.registerComponentInstance(RecordingAware2.class, unmanagedComponent);
        pico.registerComponentImplementation(RecordingAware3.class);
        pico.registerComponentImplementation(RecordingAware4.class);

        pico.getComponentInstances();

        assertEquals("instantiated RecordingAware3", recorder.getWhatHappened(0));
        assertEquals("instantiated RecordingAware4", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.getComponentMulticaster(false, false);

        ((Washable) washableContainer).wash();

        assertEquals("RecordingAware4.wash()", recorder.getWhatHappened(0));
        assertEquals("RecordingAware3.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an getComponentMulticaster() proxy",
                !recorder.thingsThatHappened.contains("RecordingAware2.wash()"));
    }

    public void testPeelableAndWashable() throws PicoInitializationException, PicoRegistrationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(PeelableComponent.class);
        pico.registerComponentImplementation(PeelableAndWashableComponent.class);

        Object proxy = pico.getComponentMulticaster();

        Peelable peelable = (Peelable) proxy;
        peelable.peel();

        Washable washable = (Washable) proxy;
        washable.wash();

        PeelableComponent pComp = (PeelableComponent) pico.getComponentInstance(PeelableComponent.class);
        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pico.getComponentInstance(PeelableAndWashableComponent.class);

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
            throws PicoRegistrationException, AssignabilityRegistrationException,
            DuplicateComponentKeyRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Recorder.class);
        pico.registerComponentImplementation(AppleFactory.class);
        pico.registerComponentImplementation(OrangeFactory.class);

        // Get the proxy for AppleFactory and OrangeFactory
        FoodFactory foodFactory = (FoodFactory) pico.getComponentMulticaster();

        int foodFactoryCode = foodFactory.hashCode();
        assertFalse("Should get a real hashCode", Integer.MIN_VALUE == foodFactoryCode);

        // Get the proxied Food and eat it. Should eat the orange and apple in one go.
        Food food = foodFactory.makeFood();
        food.eat();

        String s = food.toString();
        assertTrue("getOriginalFileName() should return the result from the invocation handler", s.indexOf("AggregatingInvocationHandler") != -1);

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
        Recorder recorder = (Recorder) pico.getComponentInstance(Recorder.class);
        assertTrue("Apple should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Apple eaten"));
        assertTrue("Orange should have been eaten now. Recorded: " + recorder.thingsThatHappened, recorder.thingsThatHappened.contains("Orange eaten"));

    }

//    public static interface PeelableAndWashableContainer extends PeelableAndWashable, OldRegistrationPicoContainer {
//
//    }

//    public void testPeelableAndWashableContainer() throws TooManySatisfiableConstructorsException, PicoRegistrationException, PicoStartException {
//
//        PeelableAndWashableContainer pawContainer = (PeelableAndWashableContainer)
//                new MorphingHierarchicalPicoContainer(
//                        new NullContainer(),
//                        new NullLifecycleManager(),
//                        new DefaultComponentFactory())
//                .as(PeelableAndWashableContainer.class);
//
//        pawContainer.registerComponentInstance(PeelableComponent.class);
//        pawContainer.registerComponentInstance(PeelableAndWashableComponent.class);
//
//        pawContainer.instantiateComponents();
//
//        pawContainer.wash();
//        pawContainer.peel();
//
//        PeelableComponent pComp = (PeelableComponent) pawContainer.findComponentInstance(PeelableComponent.class);
//        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pawContainer.findComponentInstance(PeelableAndWashableComponent.class);
//
//        assertTrue(pComp.wasPeeled);
//        assertTrue(peelNWash.wasWashed);
//
//    }


    //TODO - move to AbstractComponentRegistryTestCase
    public void testGetComponentSpecification() throws PicoRegistrationException, DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, AmbiguousComponentResolutionException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        assertNull(pico.findImplementingComponentAdapter(Touchable.class));
        pico.registerComponentImplementation(SimpleTouchable.class);
        assertNotNull(pico.findImplementingComponentAdapter(SimpleTouchable.class));
        assertNotNull(pico.findImplementingComponentAdapter(Touchable.class));
    }


    //TODO move
    public void testMultipleImplementationsAccessedThroughKey()
            throws PicoInitializationException, PicoRegistrationException, PicoInvocationTargetInitializationException {
        SimpleTouchable Touchable1 = new SimpleTouchable();
        SimpleTouchable Touchable2 = new SimpleTouchable();
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentInstance("Touchable1", Touchable1);
        pico.registerComponentInstance("Touchable2", Touchable2);
        pico.registerComponentImplementation("fred1", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable1")});
        pico.registerComponentImplementation("fred2", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable2")});

        DependsOnTouchable fred1 = (DependsOnTouchable) pico.getComponentInstance("fred1");
        DependsOnTouchable fred2 = (DependsOnTouchable) pico.getComponentInstance("fred2");

        assertFalse(fred1 == fred2);
        assertSame(Touchable1, fred1.getTouchable());
        assertSame(Touchable2, fred2.getTouchable());
    }

    //TODO - move
    public void testRegistrationByName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        Webster one = new Webster(new ArrayList());
        Touchable two = new SimpleTouchable();

        pico.registerComponentInstance("one", one);
        pico.registerComponentInstance("two", two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());

        assertEquals("Looking up one Touchable", one, pico.getComponentInstance("one"));
        assertEquals("Looking up two Touchable", two, pico.getComponentInstance("two"));

        assertTrue("Object one the same", one == pico.getComponentInstance("one"));
        assertTrue("Object two the same", two == pico.getComponentInstance("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance("unknown"));
    }

    public void testRegistrationByNameAndClassWithResolving() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentInstance(List.class, new ArrayList());
        pico.registerComponentImplementation("one", Webster.class);
        pico.registerComponentImplementation("two", SimpleTouchable.class);

        assertEquals("Wrong number of comps in the internals", 3, pico.getComponentInstances().size());

        assertNotNull("Object one the same", pico.getComponentInstance("one"));
        assertNotNull("Object two the same", pico.getComponentInstance("two"));

        assertNull("Lookup of unknown key should return null", pico.getComponentInstance("unknown"));
    }

    public void testRegistrationByInterfaceAndName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        Webster one = new Webster(new ArrayList());
        Webster two = new Webster(new ArrayList());

        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentInstance("one", one);
        pico.registerComponentInstance("two", two);

        assertEquals("Wrong number of comps in the internals", 4, pico.getComponentInstances().size());

        assertTrue("There should have been a Fred in the internals", pico.hasComponent(DependsOnTouchable.class));
        assertTrue(
                "There should have been a SimpleTouchable in the internals",
                pico.getComponentInstance(SimpleTouchable.class) != null);

        assertEquals("Looking up one Touchable", one, pico.getComponentInstance("one"));
        assertEquals("Looking up two Touchable", two, pico.getComponentInstance("two"));

        assertTrue("Object one the same", one == pico.getComponentInstance("one"));
        assertTrue("Object two the same", two == pico.getComponentInstance("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance("unknown"));
    }

    public void testRegisterByNameResolvesToInterfaceRegisteredComponents() throws Exception {
        // TODO we should add some kind of findImplementatingComponents() method to PicoContainer!
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation("fred", DependsOnTouchable.class);
        pico.registerComponentImplementation("fred2", DependsOnTouchable.class);
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);


        assertEquals("Wrong number of comps in the internals", 3, pico.getComponentInstances().size());

        assertTrue("There should have been a Touchable in the internals", pico.hasComponent(Touchable.class));
        assertTrue(
                "There should have been a SimpleTouchable in the internals",
                pico.getComponentInstance(SimpleTouchable.class) != null);

        DependsOnTouchable fred = (DependsOnTouchable) pico.getComponentInstance("fred");
        DependsOnTouchable fred2 = (DependsOnTouchable) pico.getComponentInstance("fred2");

        assertTrue("Found fred", fred != null);
        assertTrue("Found fred2", fred2 != null);

        // let's check that the Touchable's have been resolved
        assertTrue("fred should have a Touchable", fred.getTouchable() != null);
        assertTrue("fred2 should have a Touchable", fred2.getTouchable() != null);

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance("unknown"));
    }

    public void testUnmanagedCompsAreNotEligibleForComposite() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentInstance(Map.class, new HashMap());

        try {
            Map map = (Map) pico.getComponentMulticaster();
            fail("Unmanaged components should not make it into the multicaster: " + map);
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testDuplicateRegistrationWithTypeAndObject() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(SimpleTouchable.class);
        try {
            pico.registerComponentInstance(SimpleTouchable.class, new SimpleTouchable());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == SimpleTouchable.class);
            assertTrue(e.getMessage().indexOf(SimpleTouchable.class.getName()) > 0);
        }
    }

    public static class DerivedTouchable extends SimpleTouchable {
        public DerivedTouchable() {
        }
    }

    public void testAmbiguousDependencies() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        // Register two Touchables that Fred will be confused about
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(DerivedTouchable.class);

        // Register a confused DependsOnTouchable
        pico.registerComponentImplementation(DependsOnTouchable.class);

        try {
            pico.getComponentInstance(DependsOnTouchable.class);
            fail("DependsOnTouchable should have been confused about the two Touchables");
        } catch (AmbiguousComponentResolutionException e) {
            List componentImplementations = Arrays.asList(e.getAmbiguousComponentKeys());
            assertTrue(componentImplementations.contains(DerivedTouchable.class));
            assertTrue(componentImplementations.contains(SimpleTouchable.class));

            assertTrue(e.getMessage().indexOf(DerivedTouchable.class.getName()) != -1);
        }
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.registerComponentInstance(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object as Serializable");
            //TODO why?
        } catch (PicoRegistrationException e) {
            //TODO contains ?

        }

    }

    public void testComponentRegistrationMismatch() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.registerComponentImplementation(List.class, SimpleTouchable.class);
        } catch (AssignabilityRegistrationException e) {
            // not worded in message
            assertTrue(e.getMessage().indexOf(List.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(SimpleTouchable.class.getName()) > 0);
        }

    }

    public void testMultipleArgumentConstructor() throws Throwable /* fixme */ {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentImplementation(DependsOnTwoComponents.class);

        assertTrue("There should have been a DependsOnTwoComponents in the internals", pico.hasComponent(DependsOnTwoComponents.class));
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

    public void testWithComponentFactory() throws PicoRegistrationException, PicoInitializationException {
        final SimpleTouchable touchable = new SimpleTouchable();
        DefaultPicoContainer pc = new DefaultPicoContainer(new ComponentAdapterFactory() {
            public ComponentAdapter createComponentAdapter(final Object componentKey,
                                                           final Class componentImplementation,
                                                           Parameter[] parameters)
                    throws PicoIntrospectionException {
                return new ComponentAdapter() {
                    public Object getComponentKey() {
                        return componentKey;
                    }

                    public Class getComponentImplementation() {
                        return componentImplementation;
                    }

                    public Object getComponentInstance(MutablePicoContainer componentRegistry)
                            throws PicoInitializationException {
                        return touchable;
                    }
                };
            }
        });
        pc.registerComponentImplementation(SimpleTouchable.class);
        assertSame(pc.getComponentInstance(SimpleTouchable.class), touchable);
    }

    public static class Barney {
        public Barney() {
            throw new RuntimeException("Whoa!");
        }
    }

    public void testInvocationTargetException() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Barney.class);
        try {
            pico.getComponentInstance(Barney.class);
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
        public Dino4(String a, int n, String b, Touchable Touchable) {
            super(a + n + b + " " + Touchable.getClass().getName());
        }
    }

    public void testParameterCanBePassedToConstructor() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(
                Animal.class,
                Dino.class,
                new Parameter[] {
                    new ConstantParameter("bones")
                });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public void testParameterCanBePrimitive() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Animal.class, Dino2.class, new Parameter[]{new ConstantParameter(new Integer(22))});

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Animal.class, Dino3.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter("b")
        });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentImplementation(Animal.class, Dino4.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter(new Integer(3)),
            new ConstantParameter("b"),
            new ComponentParameter(Touchable.class)
        });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b org.picocontainer.testmodel.SimpleTouchable", animal.getFood());
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
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(AA.class);
        pico.registerComponentImplementation(BB.class);

        try {
            pico.getComponentInstance(BB.class);
            fail("Should have barfed");
        } catch (AmbiguousComponentResolutionException e) {
            // Neither can be instantiated without the other.
        }
    }

}
