package org.picocontainer.doc.advanced;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CollectionComponentParameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Arrays;
import java.util.List;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ArraysTestCase
        extends TestCase
        implements CollectionDemoClasses {
    private MutablePicoContainer pico;

    protected void setUp() throws Exception {
        pico = new DefaultPicoContainer();
    }

    private void explanation() {
        // START SNIPPET: explanation

        Shark shark = new Shark();
        Cod cod = new Cod();

        Fish[] fishes = new Fish[]{shark, cod};
        Cod[] cods = new Cod[]{cod};

        Bowl bowl = new Bowl(fishes, cods);
        // END SNIPPET: explanation
    }

    // START SNIPPET: bowl

    public static class Bowl {
        private final Fish[] fishes;
        private final Cod[] cods;

        public Bowl(Fish[] fishes, Cod[] cods) {
            this.fishes = fishes;
            this.cods = cods;
        }

        public Fish[] getFishes() {
            return fishes;
        }

        public Cod[] getCods() {
            return cods;
        }
    }

    // END SNIPPET: bowl

    public void testShouldCreateBowlWithFishCollection() {

        //      START SNIPPET: usage

        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        pico.registerComponent(Bowl.class);

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: usage

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);

        List fishes = Arrays.asList(bowl.getFishes());
        assertEquals(2, fishes.size());
        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));

        List cods = Arrays.asList(bowl.getCods());
        assertEquals(1, cods.size());
        assertTrue(cods.contains(cod));
    }

    public void testShouldCreateBowlWithCodsOnly() {

        //      START SNIPPET: directUsage

        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        pico.registerComponent(Bowl.class);
        pico.registerComponent(new Fish[]{});

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: directUsage

        Cod cod = (Cod) pico.getComponent(Cod.class);

        //      START SNIPPET: directDemo

        List cods = Arrays.asList(bowl.getCods());
        assertEquals(1, cods.size());

        List fishes = Arrays.asList(bowl.getFishes());
        assertEquals(0, fishes.size());
        //      END SNIPPET: directDemo

        assertTrue(cods.contains(cod));
    }

    public void testShouldCreateBowlWithFishCollectionAnyway() {

        //      START SNIPPET: ensureArray

        pico.registerComponent(Shark.class);
        pico.registerComponent(Cod.class);
        Parameter parameter = new CollectionComponentParameter();
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{parameter, parameter});
        pico.registerComponent(new Fish[]{});
        pico.registerComponent(new Cod[]{});

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: ensureArray

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);

        //      START SNIPPET: ensureDemo

        List fishes = Arrays.asList(bowl.getFishes());
        assertEquals(2, fishes.size());

        List cods = Arrays.asList(bowl.getCods());
        assertEquals(1, cods.size());
        //      END SNIPPET: ensureDemo

        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));
        assertTrue(cods.contains(cod));
    }

    public void testShouldCreateBowlWithNoFishAtAll() {

        //      START SNIPPET: emptyArray

        Parameter parameter = CollectionComponentParameter.ARRAY_ALLOW_EMPTY;
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{parameter, parameter});

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: emptyArray

        List fishes = Arrays.asList(bowl.getFishes());
        assertEquals(0, fishes.size());
        List cods = Arrays.asList(bowl.getCods());
        assertEquals(0, cods.size());
    }

    public void testShouldCreateBowlWithNamedFishesOnly() {

        //      START SNIPPET: useKeyType

        pico.registerComponent(Shark.class);
        pico.registerComponent("Nemo", Cod.class);
        pico.registerComponent(Bowl.class, Bowl.class, new Parameter[]{
                new ComponentParameter(String.class, Fish.class, false), new ComponentParameter(Cod.class, false)});

        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: useKeyType

        //      START SNIPPET: ensureKeyType

        List fishes = Arrays.asList(bowl.getFishes());
        List cods = Arrays.asList(bowl.getCods());
        assertEquals(1, fishes.size());
        assertEquals(1, cods.size());
        assertEquals(fishes, cods);
        //      END SNIPPET: ensureKeyType
    }
}