package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.adapters.InstanceComponentAdapter;
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
        containerMock.expects(once()).method("getComponentAdapters").with(eq(String.class)).will(
                returnValue(Arrays.asList(new ComponentAdapter[]{
                        new InstanceComponentAdapter("y", "Hello"), new InstanceComponentAdapter("z", "World")})));
        containerMock.expects(once()).method("getComponent").with(eq("z")).will(returnValue("World"));
        containerMock.expects(once()).method("getComponent").with(eq("y")).will(returnValue("Hello"));
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
        mpc.component(Bowl.class);
        mpc.component(Cod.class);
        mpc.component(Shark.class);
        return mpc;
    }

    public void testNativeArrays() {
        MutablePicoContainer mpc = getDefaultPicoContainer();
        Cod cod = (Cod) mpc.getComponent(Cod.class);
        Bowl bowl = (Bowl) mpc.getComponent(Bowl.class);
        assertEquals(1, bowl.cods.length);
        assertEquals(2, bowl.fishes.length);
        assertSame(cod, bowl.cods[0]);
        assertNotSame(bowl.fishes[0], bowl.fishes[1]);
    }

    public void testCollectionsAreGeneratedOnTheFly() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.adapter(new ConstructorInjectionComponentAdapter(Bowl.class, Bowl.class));
        mpc.component(Cod.class);
        Bowl bowl = (Bowl) mpc.getComponent(Bowl.class);
        assertEquals(1, bowl.cods.length);
        mpc.component("Nemo", new Cod());
        bowl = (Bowl) mpc.getComponent(Bowl.class);
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
        mpc.component(CollectedBowl.class, CollectedBowl.class, new Parameter[]{
                new ComponentParameter(Cod.class, false), new ComponentParameter(Fish.class, false)});
        mpc.component(Cod.class);
        mpc.component(Shark.class);
        Cod cod = (Cod) mpc.getComponent(Cod.class);
        CollectedBowl bowl = (CollectedBowl) mpc.getComponent(CollectedBowl.class);
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
        mpc.component(MappedBowl.class, MappedBowl.class, new Parameter[]{new ComponentParameter(
                Fish.class, false)});
        mpc.component(Cod.class);
        mpc.component(Shark.class);
        MappedBowl bowl = (MappedBowl) mpc.getComponent(MappedBowl.class);
        assertEquals(2, bowl.fishes.length);
        assertNotSame(bowl.fishes[0], bowl.fishes[1]);
    }

    public static class UngenericCollectionBowl {
        public UngenericCollectionBowl(Collection fish) {
        }
    }

    public void testShouldNotInstantiateCollectionForUngenericCollectionParameters() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.component(UngenericCollectionBowl.class);
        try {
            pico.getComponent(UngenericCollectionBowl.class);
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
        pico.component(AnotherGenericCollectionBowl.class);
        try {
            pico.getComponent(AnotherGenericCollectionBowl.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
            // expected
        }
    }

    public void testAllowsEmptyArraysIfEspeciallySet() {
        MutablePicoContainer pico = getDefaultPicoContainer();
        pico.component(
                AnotherGenericCollectionBowl.class, AnotherGenericCollectionBowl.class,
                new Parameter[]{ComponentParameter.ARRAY_ALLOW_EMPTY});
        AnotherGenericCollectionBowl bowl = (AnotherGenericCollectionBowl) pico
                .getComponent(AnotherGenericCollectionBowl.class);
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
        pico.component(SimpleTouchable.class);
        pico.component(TouchableObserver.class);
        Touchable observer = (Touchable) pico.getComponent(TouchableObserver.class);
        assertNotNull(observer);
        observer.touch();
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponent(SimpleTouchable.class);
        assertTrue(touchable.wasTouched);
    }

    public void testWillRemoveComponentsWithMatchingKeyFromParent() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.component("Tom", Cod.class);
        parent.component("Dick", Cod.class);
        parent.component("Harry", Cod.class);
        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.component("Dick", Shark.class);
        child.component(Bowl.class);
        Bowl bowl = (Bowl) child.getComponent(Bowl.class);
        assertEquals(3, bowl.fishes.length);
        assertEquals(2, bowl.cods.length);
    }

    public void testBowlWithoutTom() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.component("Tom", Cod.class);
        mpc.component("Dick", Cod.class);
        mpc.component("Harry", Cod.class);
        mpc.component(Shark.class);
        mpc.component(CollectedBowl.class, CollectedBowl.class, new Parameter[]{
            new CollectionComponentParameter(Cod.class, false) {
                protected boolean evaluate(ComponentAdapter adapter) {
                    return !"Tom".equals(adapter.getComponentKey());
                }
            },
            new CollectionComponentParameter(Fish.class, false)
        });
        CollectedBowl bowl = (CollectedBowl) mpc.getComponent(CollectedBowl.class);
        Cod tom = (Cod) mpc.getComponent("Tom");
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
        pico.component(DependsOnAll.class, DependsOnAll.class, new Parameter[]{
                parameter, parameter, parameter, parameter, parameter, parameter,
        // parameter, parameter, parameter,
                });
        assertNotNull(pico.getComponent(DependsOnAll.class));
    }

    public void testVerify() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        CollectionComponentParameter parameterNonEmpty = CollectionComponentParameter.ARRAY;
        pico.component(Shark.class);
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

    // PICO-243 : this test will fail if executed on jdk1.3 without commons-collections
    public void testOrderOfElementsOfAnArrayDependencyIsPreserved() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.component("first", "first");
        pico.component("second", "second");
        pico.component("third", "third");
        pico.component("fourth", "fourth");
        pico.component("fifth", "fifth");
        pico.component(Truc.class);

        final List strings = pico.getComponents(String.class);
        assertEquals("first", strings.get(0));
        assertEquals("second", strings.get(1));
        assertEquals("third", strings.get(2));
        assertEquals("fourth", strings.get(3));
        assertEquals("fifth", strings.get(4));

        pico.getComponent(Truc.class);
    }

    public static final class Truc {
        public Truc(String[] s) {
            assertEquals("first", s[0]);
            assertEquals("second", s[1]);
            assertEquals("third", s[2]);
            assertEquals("fourth", s[3]);
            assertEquals("fifth", s[4]);
        }
    }

}