package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;
import java.util.HashMap;

/**
 * This class can be used to test out various things asked on the mailing list.
 * Or to answer questions.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class UserQuestionTestCase extends TestCase {

    // From Scott Farquahsr
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

        MutablePicoContainer pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
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
        MutablePicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory(), false));

        pico.registerComponentImplementation(Omelette.class);
        pico.registerComponentImplementation(Cheese.class, Gouda.class);

        Omelette flexibleOmelette = (Omelette) pico.getComponentInstance(Omelette.class);
        assertEquals("Gouda", flexibleOmelette.getCheese().getName());

        // Let's swap the cheese without creating a new omelette
        Swappable swappableCheese = (Swappable) flexibleOmelette.getCheese();
        swappableCheese.hotswap(new Roquefort());
        assertEquals("Roquefort", flexibleOmelette.getCheese().getName());
    }

    // From Alex Shneyderman 23/03/2004
    public void testMultipleOmelettesCanHaveDifferentCheeseUsingSingleSwapping() {
        MutablePicoContainer pico = new DefaultPicoContainer(
                new CachingComponentAdapterFactory(
                        new ImplementationHidingComponentAdapterFactory(
                                new ConstructorInjectionComponentAdapterFactory(), false)));

        pico.registerComponentImplementation("Omelette1", Omelette.class);
        pico.registerComponentImplementation("Omelette2", Omelette.class);
        pico.registerComponentImplementation(Cheese.class, Gouda.class);

        Omelette flexibleOmelette1 = (Omelette) pico.getComponentInstance("Omelette1");
        Omelette flexibleOmelette2 = (Omelette) pico.getComponentInstance("Omelette2");
        assertNotSame(flexibleOmelette1, flexibleOmelette2);
        assertSame(flexibleOmelette1.getCheese(), flexibleOmelette2.getCheese());

        // Let's swap the cheese for all omelettes :)
        Cheese cheese = (Cheese) pico.getComponentInstance(Cheese.class);
        Swappable swappableCheese = (Swappable) cheese;
        swappableCheese.hotswap(new Roquefort());
        
        assertEquals("Roquefort", flexibleOmelette1.getCheese().getName());
        assertEquals("Roquefort", flexibleOmelette2.getCheese().getName());
    }

    public static interface InterfaceX {
        String getIt();
    }

    public static class Enabled implements InterfaceX {
        public String getIt() {
            return "Enabled";
        }
    }

    public static class Disabled implements InterfaceX {
        public String getIt() {
            return "Disabled";
        }
    }

    public static class Something implements InterfaceX {
        private final Disabled disabled;
        private final Enabled enabled;
        private final Map map;

        public Something(Disabled disabled, Enabled enabled, Map map) {
            this.disabled = disabled;
            this.enabled = enabled;
            this.map = map;
        }

        public String getIt() {
            if (map.get("enabled") == null) {
                return disabled.getIt();
            } else {
                return enabled.getIt();
            }
        }
    }

    public static class NeedsInterfaceX {
        private final InterfaceX interfaceX;

        public NeedsInterfaceX(InterfaceX interfaceX) {
            this.interfaceX = interfaceX;
        }

        public String getIt() {
            return interfaceX.getIt();
        }
    }

    public void testMoreWeirdness() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        Map map = new HashMap();
        pico.registerComponentInstance(map);
        // See class level javadoc in DefaultPicoContainer - about precedence. 
        pico.registerComponentImplementation(InterfaceX.class, Something.class);
        pico.registerComponentImplementation(Disabled.class);
        pico.registerComponentImplementation(Enabled.class);
        pico.registerComponentImplementation(NeedsInterfaceX.class);

        NeedsInterfaceX needsInterfaceX = (NeedsInterfaceX) pico.getComponentInstance(NeedsInterfaceX.class);
        assertEquals("Disabled", needsInterfaceX.getIt());
        map.put("enabled", "blah");
        assertEquals("Enabled", needsInterfaceX.getIt());
    }

    // From John Tal 23/03/2004
    public static interface ABC {
    }

    public static interface DEF {
    }

    public static class ABCImpl implements ABC {
        public ABCImpl(DEF def) {
        }
    }

    public static class DEFImpl implements DEF {
        public DEFImpl() {
        }
    }

    public void testJohnTalOne() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();

        picoContainer.registerComponentImplementation("ABC",ABCImpl.class);
        picoContainer.registerComponentImplementation("DEF",DEFImpl.class);

        assertEquals(ABCImpl.class, picoContainer.getComponentInstance("ABC").getClass());
    }

}