package org.picocontainer.doc.advanced;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CollectionComponentParameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;


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

        pico.component("Shark", Shark.class);
        pico.component("Cod", Cod.class);
        pico.component(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        //      END SNIPPET: usage

        Shark shark = (Shark) pico.getComponent(Shark.class);
        Cod cod = (Cod) pico.getComponent(Cod.class);
        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        
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

        pico.component(Shark.class);
        pico.component("Nemo", Cod.class);
        pico.component(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(String.class, Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        
        Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
        //      END SNIPPET: useKeyType
        
        //      START SNIPPET: ensureKeyType

        Cod cod = (Cod) pico.getComponent(Cod.class);
        Map fishMap = bowl.getFishes();
        Map codMap = bowl.getCods();
        assertEquals(1, fishMap.size());
        assertEquals(1, codMap.size());
        assertEquals(fishMap, codMap);
        assertSame(cod,fishMap.get("Nemo"));
        //      END SNIPPET: ensureKeyType
    }
    
    public void testShouldCreateBowlWithFishesFromParent() {

        // START SNIPPET: scope

        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.component("Tom", Cod.class);
        parent.component("Harry", Cod.class);
        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.component("Dick", Cod.class);
        child.component(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        Bowl bowl = (Bowl) child.getComponent(Bowl.class);
        assertEquals(3, bowl.fishes.size());
        assertEquals(3, bowl.cods.size());

        // END SNIPPET: scope
    }
    
    public void testShouldCreateBowlWith2CodsOnly() {

        // START SNIPPET: scopeOverlay

        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.component("Tom", Cod.class);
        parent.component("Dick", Cod.class);
        parent.component("Harry", Cod.class);
        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.component("Dick", Shark.class);
        child.component(Bowl.class, Bowl.class, new Parameter[]{
            new ComponentParameter(Fish.class, false),
            new ComponentParameter(Cod.class, false)
        });
        Bowl bowl = (Bowl) child.getComponent(Bowl.class);
        assertEquals(3, bowl.fishes.size());
        assertEquals(2, bowl.cods.size());

        // END SNIPPET: scopeOverlay
    }
    
    public void testShouldCreateBowlWithoutTom() {

        // START SNIPPET: individualSelection

        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.component("Tom", Cod.class);
        mpc.component("Dick", Cod.class);
        mpc.component("Harry", Cod.class);
        mpc.component("Sharky", Shark.class);
        mpc.component(Bowl.class, Bowl.class, new Parameter[]{
            new CollectionComponentParameter(Fish.class, false),
            new CollectionComponentParameter(Cod.class, false) {
                protected boolean evaluate(ComponentAdapter adapter) {
                    return !"Tom".equals(adapter.getComponentKey());
                }
            }
        });
        Cod tom = (Cod) mpc.getComponent("Tom");
        Bowl bowl = (Bowl) mpc.getComponent(Bowl.class);
        assertTrue(bowl.fishes.values().contains(tom));
        assertFalse(bowl.cods.values().contains(tom));

        // END SNIPPET: individualSelection
    }
}