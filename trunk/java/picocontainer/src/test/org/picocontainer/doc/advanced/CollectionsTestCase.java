package org.picocontainer.doc.advanced;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CollectionComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.LinkedList;


/**
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class CollectionsTestCase
        extends TestCase {
    private MutablePicoContainer pico;
    private Shark shark;
    private Cod cod;
    private Bowl bowl;

    protected void setUp() throws Exception {
        pico = new DefaultPicoContainer();
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

    // END SNIPPET: classes

    public void XXXtestShouldCreateBowlWithFishCollection() {

        //      START SNIPPET: usage

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new CollectionComponentParameter(Fish.class, false),
            new CollectionComponentParameter(Cod.class, false)
        });
        //      END SNIPPET: usage

        shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
        
        Collection fishes = bowl.getFishes();
        assertEquals(2, fishes.size());
        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));

        Collection cods = bowl.getCods();
        assertEquals(1, cods.size());
        assertTrue(cods.contains(cod));
    }
}