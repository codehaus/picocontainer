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
        extends TestCase {
    private MutablePicoContainer pico;

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

    public void testShouldCreateBowlWithFishCollection() {

        //      START SNIPPET: usage

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        //      END SNIPPET: usage

        Shark shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        Cod cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
        Bowl bowl = (Bowl) pico.getComponentInstanceOfType(Bowl.class);
        
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
        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        pico.registerComponentInstance(set);

        Bowl bowl = (Bowl) pico.getComponentInstance(Bowl.class);
        //      END SNIPPET: directUsage

        Shark shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        Cod cod = (Cod) pico.getComponentInstanceOfType(Cod.class);

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

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new CollectionComponentParameter(Fish.class, false),
            new CollectionComponentParameter(Cod.class, false)
        });
        // This component will match both arguments of Bowl's constructor
        pico.registerComponentInstance(new LinkedList());

        Bowl bowl = (Bowl) pico.getComponentInstance(Bowl.class);
        //      END SNIPPET: ensureCollection

        Shark shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        Cod cod = (Cod) pico.getComponentInstanceOfType(Cod.class);

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

        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(true),
            new ComponentParameter(true)
        });

        Bowl bowl = (Bowl) pico.getComponentInstance(Bowl.class);
        //      END SNIPPET: emptyCollection

        Collection fishes = bowl.getFishes();
        assertEquals(0, fishes.size());
        Collection cods = bowl.getCods();
        assertEquals(0, cods.size());
    }
}