package org.picocontainer.doc.advanced;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class MapsTestCase
        extends TestCase
        implements CollectionDemoClasses {
    private MutablePicoContainer pico;

    protected void setUp() throws Exception {
        pico = new DefaultPicoContainer();
    }

    // START SNIPPET: bowl

    public static class Bowl {
        private final TreeMap fishes;
        private final Map cods;

        public Bowl(TreeMap fishes, Map cods) {
            this.fishes = fishes;
            this.cods = cods;
        }

        public Map getFishes() {
            return fishes;
        }

        public Map getCods() {
            return cods;
        }
    }

    // END SNIPPET: bowl

    public void testShouldCreateBowlWithFishCollection() {

        //      START SNIPPET: usage

        pico.registerComponentImplementation("Shark", Shark.class);
        pico.registerComponentImplementation("Cod", Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        //      END SNIPPET: usage

        Shark shark = (Shark) pico.getComponentInstanceOfType(Shark.class);
        Cod cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
        Bowl bowl = (Bowl) pico.getComponentInstanceOfType(Bowl.class);
        
        Map fishMap = bowl.getFishes();
        assertEquals(2, fishMap.size());
        Collection fishes = fishMap.values();
        assertTrue(fishes.contains(shark));
        assertTrue(fishes.contains(cod));

        Map codMap = bowl.getCods();
        assertEquals(1, codMap.size());
        Collection cods = fishMap.values();
        assertTrue(cods.contains(cod));
    }
    
    public void testShouldCreateBowlWithNamedFishesOnly() {

        //      START SNIPPET: useKeyType

        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation("Nemo", Cod.class);
        pico.registerComponentImplementation(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(String.class, Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        
        Bowl bowl = (Bowl) pico.getComponentInstanceOfType(Bowl.class);
        //      END SNIPPET: useKeyType
        
        //      START SNIPPET: ensureKeyType

        Cod cod = (Cod) pico.getComponentInstanceOfType(Cod.class);
        Map fishMap = bowl.getFishes();
        Map codMap = bowl.getCods();
        assertEquals(1, fishMap.size());
        assertEquals(1, codMap.size());
        assertEquals(fishMap, codMap);
        assertSame(cod,fishMap.get("Nemo"));
        //      END SNIPPET: ensureKeyType
    }
}