package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class FarquharTestCase extends TestCase {

    public static class CheeseComponentAdapter extends AbstractComponentAdapter {
        private Map bla;

        public CheeseComponentAdapter(Object componentKey, Class componentImplementation, Map cheeseMap) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
            super(componentKey, componentImplementation);
            this.bla = cheeseMap;
        }

        public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
            return bla.get("cheese");
        }

        public void verify() throws UnsatisfiableDependenciesException {

        }
    }

    public static interface Cheese {
        String getName();
    }
    public static class Gouda implements Cheese {
        public String getName() {
            return "Gouda";
        }
    }
    public static class Roquefort implements Cheese {
        public String getName() {
            return "Roquefort";
        }
    }
    public static class Omelette {
        private final Cheese cheese;

        public Omelette(Cheese cheese) {
            this.cheese = cheese;
        }

        public Cheese getCheese() {
            return cheese;
        }
    }

    public void testOmeletteCanHaveDifferentCheeseWithAFunnyComponentAdapter() {
        Map cheeseMap = new HashMap();

        MutablePicoContainer pico = new DefaultPicoContainer(new ConstructorComponentAdapterFactory());
        pico.registerComponentImplementation(Omelette.class);
        pico.registerComponent(new CheeseComponentAdapter("scott", Gouda.class, cheeseMap));

        Cheese gouda = new Gouda();
        cheeseMap.put("cheese", gouda);
        Omelette goudaOmelette = (Omelette) pico.getComponentInstance(Omelette.class);
        assertSame(gouda, goudaOmelette.getCheese());

        Cheese roquefort = new Roquefort();
        cheeseMap.put("cheese", roquefort);
        Omelette roquefortOmelette = (Omelette) pico.getComponentInstance(Omelette.class);
        assertSame(roquefort, roquefortOmelette.getCheese());
    }

    public void testOmeletteCanHaveDifferentCheeseUsingSwapping() {
        MutablePicoContainer pico = new DefaultPicoContainer(
                new ImplementationHidingComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(),false));

        pico.registerComponentImplementation(Omelette.class);
        pico.registerComponentImplementation(Cheese.class, Gouda.class);

        Omelette flexibleOmelette = (Omelette) pico.getComponentInstance(Omelette.class);
        assertEquals("Gouda", flexibleOmelette.getCheese().getName());

        // Let's swap the cheese without creating a new omelette
        Swappable swappableCheese = (Swappable) flexibleOmelette.getCheese();
        swappableCheese.__hotSwap(new Roquefort());
        assertEquals("Roquefort", flexibleOmelette.getCheese().getName());
    }

}