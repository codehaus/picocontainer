package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class CollectionComponentParameterTestCase
        extends MockObjectTestCase {

    public void testShouldInstantiateArrayOfStrings() {
        CollectionComponentParameter ccp = new CollectionComponentParameter();

        Mock componentAdapterMock = mock(ComponentAdapter.class);
        componentAdapterMock.expects(atLeastOnce()).method("getComponentKey").will(returnValue("x"));
        Mock containerMock = mock(PicoContainer.class);
        containerMock.expects(once()).method("getComponentAdapters").withNoArguments().will(returnValue(new HashSet()));
        containerMock.expects(once()).method("getComponentAdaptersOfType").with(eq(String.class)).will(
                returnValue(Arrays.asList(new ComponentAdapter[]{
                        new InstanceComponentAdapter("y", "Hello"), new InstanceComponentAdapter("z", "World")})));
        containerMock.expects(once()).method("getComponentInstance").with(eq("z")).will(returnValue("World"));
        containerMock.expects(once()).method("getComponentInstance").with(eq("y")).will(returnValue("Hello"));
        containerMock.expects(once()).method("getParent").withNoArguments().will(returnValue(null));

        List expected = Arrays.asList(new String[]{"Hello", "World"});
        Collections.sort(expected);
        List actual = Arrays.asList((Object[]) ccp.resolveInstance(
                (PicoContainer) containerMock.proxy(), (ComponentAdapter) componentAdapterMock.proxy(), String[].class));
        Collections.sort(actual);
        assertEquals(expected, actual);
    }

    static public interface Fish {
    }

    static public class Cod
            implements Fish {
        public String toString() {
            return "Cod";
        }
    }

    static public class Shark
            implements Fish {
        public String toString() {
            return "Shark";
        }
    }

    static public class Bowl {
        private final Cod[] cods;
        private final Fish[] fishes;

        public Bowl(Cod cods[], Fish fishes[]) {
            this.cods = cods;
            this.fishes = fishes;
        }
    }

    private MutablePicoContainer getDefaultPicoContainer() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Bowl.class);
        mpc.registerComponentImplementation(Cod.class);
        mpc.registerComponentImplementation(Shark.class);
        return mpc;
    }

    public void testNativeArrays() {
        MutablePicoContainer mpc = getDefaultPicoContainer();
        Cod cod = (Cod) mpc.getComponentInstanceOfType(Cod.class);
        Bowl bowl = (Bowl) mpc.getComponentInstance(Bowl.class);
        assertEquals(1, bowl.cods.length);
        assertEquals(2, bowl.fishes.length);
        assertSame(cod, bowl.cods[0]);
        assertNotSame(bowl.fishes[0], bowl.fishes[1]);
    }

    public void testCollectionsAreGeneratedOnTheFly() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new ConstructorInjectionComponentAdapter(Bowl.class, Bowl.class));
        mpc.registerComponentImplementation(Cod.class);
        Bowl bowl = (Bowl) mpc.getComponentInstance(Bowl.class);
        assertEquals(1, bowl.cods.length);
        mpc.registerComponentInstance("Nemo", new Cod());
        bowl = (Bowl) mpc.getComponentInstance(Bowl.class);
        assertEquals(2, bowl.cods.length);
        assertNotSame(bowl.cods[0], bowl.cods[1]);
    }

    static public class CollectedBowl {
        private final Cod[] cods;
        private final Fish[] fishes;

        public CollectedBowl(Collection cods, Collection fishes) {
            this.cods = (Cod[]) cods.toArray(new Cod[cods.size()]);
            this.fishes = (Fish[]) fishes.toArray(new Fish[fishes.size()]);
        }
    }

    public void testCollections() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(CollectedBowl.class, CollectedBowl.class, new Parameter[]{
                new ComponentParameter(Cod.class, false), new ComponentParameter(Fish.class, false)});
        mpc.registerComponentImplementation(Cod.class);
        mpc.registerComponentImplementation(Shark.class);
        Cod cod = (Cod) mpc.getComponentInstanceOfType(Cod.class);
        CollectedBowl bowl = (CollectedBowl) mpc.getComponentInstance(CollectedBowl.class);
        assertEquals(1, bowl.cods.length);
        assertEquals(2, bowl.fishes.length);
        assertSame(cod, bowl.cods[0]);
        assertNotSame(bowl.fishes[0], bowl.fishes[1]);
    }

    static public class MappedBowl {
        private final Fish[] fishes;

        public MappedBowl(Map map) {
            Collection collection = map.values();
            this.fishes = (Fish[]) collection.toArray(new Fish[collection.size()]);
        }
    }

    public void testMaps() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(MappedBowl.class, MappedBowl.class, new Parameter[]{new ComponentParameter(
                Fish.class, false)});
        mpc.registerComponentImplementation(Cod.class);
        mpc.registerComponentImplementation(Shark.class);
        MappedBowl bowl = (MappedBowl) mpc.getComponentInstance(MappedBowl.class);
        assertEquals(2, bowl.fishes.length);
        assertNotSame(bowl.fishes[0], bowl.fishes[1]);
    }

    public static class UngenericCollectionBowl {
        public UngenericCollectionBowl(Collection fish) {
        }
    }

    public void testShouldNotInstantiateCollectionForUngenericCollectionParameters() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.registerComponentImplementation(UngenericCollectionBowl.class);
        try {
            pico.getComponentInstance(UngenericCollectionBowl.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
            // expected
        }
    }

    public static class AnotherGenericCollectionBowl {
        private final String[] strings;

        public AnotherGenericCollectionBowl(String[] strings) {
            this.strings = strings;
        }

        public String[] getStrings() {
            return strings;
        }
    }

    public void testShouldFailWhenThereAreNoComponentsToPutInTheArray() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.registerComponentImplementation(AnotherGenericCollectionBowl.class);
        try {
            pico.getComponentInstance(AnotherGenericCollectionBowl.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
            // expected
        }
    }

    public void testAllowsEmptyArraysIfEspeciallySet() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.registerComponentImplementation(
                AnotherGenericCollectionBowl.class, AnotherGenericCollectionBowl.class,
                new Parameter[]{ComponentParameter.ARRAY_ALLOW_EMPTY});
        AnotherGenericCollectionBowl bowl = (AnotherGenericCollectionBowl) pico
                .getComponentInstance(AnotherGenericCollectionBowl.class);
        assertNotNull(bowl);
        assertEquals(0, bowl.strings.length);
    }

    public static class TouchableObserver
            implements Touchable {
        private final Touchable[] touchables;

        public TouchableObserver(Touchable[] touchables) {
            this.touchables = touchables;

        }

        public void touch() {
            for (int i = 0; i < touchables.length; i++) {
                touchables[i].touch();
            }
        }
    }

    public void testWillOmitSelfFromCollection() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(TouchableObserver.class);
        Touchable observer = (Touchable) pico.getComponentInstanceOfType(TouchableObserver.class);
        assertNotNull(observer);
        observer.touch();
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstanceOfType(SimpleTouchable.class);
        assertTrue(touchable.wasTouched);
    }

    public void testWillRemoveComponentsWithMatchingKeyFromParent() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentImplementation("Tom", Cod.class);
        parent.registerComponentImplementation("Dick", Cod.class);
        parent.registerComponentImplementation("Harry", Cod.class);
        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.registerComponentImplementation("Dick", Shark.class);
        child.registerComponentImplementation(Bowl.class);
        Bowl bowl = (Bowl) child.getComponentInstance(Bowl.class);
        assertEquals(3, bowl.fishes.length);
        assertEquals(2, bowl.cods.length);
    }

    public void testBowlWithoutTom() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation("Tom", Cod.class);
        mpc.registerComponentImplementation("Dick", Cod.class);
        mpc.registerComponentImplementation("Harry", Cod.class);
        mpc.registerComponentImplementation(Shark.class);
        mpc.registerComponentImplementation(CollectedBowl.class, CollectedBowl.class, new Parameter[]{
            new CollectionComponentParameter(Cod.class, false) {
                protected boolean evaluate(ComponentAdapter adapter) {
                    return !"Tom".equals(adapter.getComponentKey());
                }
            },
            new CollectionComponentParameter(Fish.class, false)
        });
        CollectedBowl bowl = (CollectedBowl) mpc.getComponentInstance(CollectedBowl.class);
        Cod tom = (Cod) mpc.getComponentInstance("Tom");
        assertEquals(4, bowl.fishes.length);
        assertEquals(2, bowl.cods.length);
        assertFalse(Arrays.asList(bowl.cods).contains(tom));
    }

    public static class DependsOnAll {
        public DependsOnAll(Set set, SortedSet sortedSet, Collection collection, List list, SortedMap sortedMap, Map map
        // , ConcurrentMap concurrentMap, Queue queue, BlockingQueue blockingQueue
        ) {
            assertNotNull(set);
            assertNotNull(sortedSet);
            assertNotNull(collection);
            assertNotNull(list);
            assertNotNull(sortedMap);
            assertNotNull(map);
            //            assertNotNull(concurrentMap);
            //            assertNotNull(queue);
            //            assertNotNull(blockingQueue);
        }
    }

    public void testDifferentCollectiveTypesAreResolved() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        CollectionComponentParameter parameter = new CollectionComponentParameter(Fish.class, true);
        pico.registerComponentImplementation(DependsOnAll.class, DependsOnAll.class, new Parameter[]{
                parameter, parameter, parameter, parameter, parameter, parameter,
        // parameter, parameter, parameter,
                });
        assertNotNull(pico.getComponentInstance(DependsOnAll.class));
    }

    public void testVerify() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        CollectionComponentParameter parameterNonEmpty = CollectionComponentParameter.ARRAY;
        pico.registerComponentImplementation(Shark.class);
        parameterNonEmpty.verify(pico, null, Fish[].class);
        try {
            parameterNonEmpty.verify(pico, null, Cod[].class);
            fail("(PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getMessage().indexOf(Cod.class.getName())>0);
        }
        CollectionComponentParameter parameterEmpty = CollectionComponentParameter.ARRAY_ALLOW_EMPTY;
        parameterEmpty.verify(pico, null, Fish[].class);
        parameterEmpty.verify(pico, null, Cod[].class);
    }
}