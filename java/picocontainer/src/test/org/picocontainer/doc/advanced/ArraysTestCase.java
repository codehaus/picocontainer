package org.picocontainer.doc.advanced;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Arrays;
import java.util.List;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ArraysTestCase
        extends TestCase {
    private MutablePicoContainer pico;
    private Shark shark;
    private Cod cod;
    private Bowl bowl;

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

    // START SNIPPET: classes

    public static interface Fish {
    }

    public static class Cod
            implements Fish {
    }

    public static class Shark
            implements Fish {
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

        //      START SNIPPET: usage

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class);

        bowl = (Bowl) pico.getComponentInstance(Bowl.class);
        //      END SNIPPET: usage

        shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
        
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

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class);
        pico.registerComponentInstance(new Fish[]{});

        bowl = (Bowl) pico.getComponentInstance(Bowl.class);
        //      END SNIPPET: directUsage

        shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        cod = (Cod) pico.getComponentInstanceOfType(Cod.class);

        //      START SNIPPET: directDemo
        
        List cods = Arrays.asList(bowl.getCods());
        assertEquals(1, cods.size());
        
        List fishes = Arrays.asList(bowl.getFishes());
        assertEquals(0, fishes.size());
        //      END SNIPPET: directDemo
        
        assertTrue(cods.contains(cod));
    }
}