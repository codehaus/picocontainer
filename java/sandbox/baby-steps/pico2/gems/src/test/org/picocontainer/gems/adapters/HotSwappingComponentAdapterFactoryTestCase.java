package org.picocontainer.gems.adapters;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.adapters.ConstructorInjectionFactory;
import org.picocontainer.adapters.ConstructorInjectionAdapter;
import org.picocontainer.adapters.AnyInjectionFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public final class HotSwappingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    private final ComponentFactory implementationHidingComponentAdapterFactory = new HotSwappingComponentAdapterFactory().forThis(new AnyInjectionFactory());

    // START SNIPPET: man
    public static interface Man {
        Woman getWoman();

        void kiss();

        boolean wasKissed();
    }

    // END SNIPPET: man

    // START SNIPPET: woman
    public static interface Woman {
        Man getMan();
    }

    // END SNIPPET: woman

    public static class Wife implements Woman {
        public final Man partner;

        public Wife(Man partner) {
            this.partner = partner;
        }

        public Man getMan() {
            return partner;
        }
    }


    public void testHotSwappingNaturaelyCaches() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory().forThis(new ConstructorInjectionFactory()));
        pico.addComponent(Map.class, HashMap.class);
        Map firstMap = (Map)pico.getComponent(Map.class);
        Map secondMap = (Map)pico.getComponent(Map.class);
        assertSame(firstMap, secondMap);

    }


    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        HotSwappingComponentAdapter hsca = (HotSwappingComponentAdapter) pico.addAdapter(new HotSwappingComponentAdapter(new ConstructorInjectionAdapter("l", ArrayList.class))).lastCA();
        List l = (List)pico.getComponent("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();

        ArrayList oldSubject = (ArrayList) hsca.swapRealInstance(newList);;
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }


    protected ComponentFactory createComponentAdapterFactory() {
        return implementationHidingComponentAdapterFactory;
    }

}
