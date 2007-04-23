package org.picocontainer.doc.advanced;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CollectionComponentParameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


/**
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class CollectionsTestCase
        extends TestCase
        implements CollectionDemoClasses {
    private MutablePicoContainer pico;

    protected void setUp() throws Exception {
        pico = new DefaultPicoContainer();
    }

    // START SNIPPET: bowl

    public static class Bowl {
        private final LinkedList fishes;
        private final Collection cods;

        public Bowl(LinkedList fishes, Collection cods) {
            this.fishes = fishes;
            this.cods = cods;
        }

        public Collection getFishes() {
            return fishes;
        }

        public Collection getCods() {
            return cods;
        }
    }

    // END SNIPPET: bowl

    public void testShouldCreateBowlWithFishCollection() {

        //      START SNIPPET: usage

        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{
                new ComponentParameter(Fish.class, false), new ComponentParameter(Cod.class, false)});
        //      END SNIPPET: usage

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);
        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);

        Collection fishes = bowl.getFishes();
        assertEquals(2, fishes.size());
        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));

        Collection cods = bowl.getCods();
        assertEquals(1, cods.size());
        assertTrue(cods.contains(cod));
    }

    public void testShouldCreateBowlWithFishesOnly() {

        //      START SNIPPET: directUsage

        final Set set = new HashSet();
        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{
                new ComponentParameter(Fish.class, false), new ComponentParameter(Cod.class, false)});
        pico.registerComponent(set);

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: directUsage

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);

        //      START SNIPPET: directDemo

        Collection cods = bowl.getCods();
        assertEquals(0, cods.size());
        assertSame(set, cods);

        Collection fishes = bowl.getFishes();
        assertEquals(2, fishes.size());
        //      END SNIPPET: directDemo

        assertTrue(fishes.contains(cod));
        assertTrue(fishes.contains(shark));
    }

    public void testShouldCreateBowlWithFishCollectionAnyway() {

        //      START SNIPPET: ensureCollection

        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{
                new CollectionComponentParameter(Fish.class, false), new CollectionComponentParameter(Cod.class, false)});
        // This component will match both arguments of Bowl's constructor
        pico.registerComponent(new LinkedList());

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: ensureCollection

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);

        Collection fishes = bowl.getFishes();
        assertEquals(2, fishes.size());
        Collection cods = bowl.getCods();
        assertEquals(1, cods.size());

        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));
        assertTrue(cods.contains(cod));
    }

    public void testShouldCreateBowlWithNoFishAtAll() {

        //      START SNIPPET: emptyCollection

        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{
                new ComponentParameter(Fish.class, true), new ComponentParameter(Cod.class, true)});

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: emptyCollection

        Collection fishes = bowl.getFishes();
        assertEquals(0, fishes.size());
        Collection cods = bowl.getCods();
        assertEquals(0, cods.size());
    }
}