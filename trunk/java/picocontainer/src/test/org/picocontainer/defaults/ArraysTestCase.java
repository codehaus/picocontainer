package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ArraysTestCase extends TestCase {
    private MutablePicoContainer pico;
    private Shark shark;
    private Cod cod;
    private Bowl bowl;

    protected void setUp() throws Exception {
        pico = new DefaultPicoContainer();

// START SNIPPET: usage
        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class);

        bowl = (Bowl) pico.getComponentInstance(Bowl.class);
// END SNIPPET: usage

        shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
    }

    private void explanation() {
// START SNIPPET: explanation
        Shark shark = new Shark();
        Cod cod = new Cod();

        Fish[] fishes = new Fish[]{shark, cod};
        Cod[] cods = new Cod[]{cod};

        Bowl bowl = new Bowl(fishes, cods);
// START SNIPPET: explanation
    }

// START SNIPPET: classes
    public static interface Fish {
    }

    public static class Cod implements Fish{
    }

    public static class Shark implements Fish{
    }

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
// END SNIPPET: classes

    public void testShouldCreateBowlWithFishCollection() {
        Fish[] fishes = bowl.getFishes();
        assertEquals(2, fishes.length);
        assertTrue(Arrays.asList(fishes).contains(shark));
        assertTrue(Arrays.asList(fishes).contains(cod));

        Cod[] cods = bowl.getCods();
        assertEquals(1, cods.length);
        assertTrue(Arrays.asList(cods).contains(cod));
    }

    public static class UngenericCollectionBowl {
        public UngenericCollectionBowl(Collection fish) {
        }
    }

    public void testShouldNotInstantiateCollectionForUngenericCollectionParameters() {
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
        pico.registerComponentImplementation(AnotherGenericCollectionBowl.class);
        try {
            pico.getComponentInstance(AnotherGenericCollectionBowl.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
            // expected
        }
    }
}