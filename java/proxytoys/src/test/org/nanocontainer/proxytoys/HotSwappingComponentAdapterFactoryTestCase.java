package org.nanocontainer.proxytoys;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.Swappable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotSwappingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    private HotSwappingComponentAdapterFactory implementationHidingComponentAdapterFactory = new HotSwappingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    private CachingComponentAdapterFactory cachingComponentAdapterFactory = new CachingComponentAdapterFactory(implementationHidingComponentAdapterFactory);

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingStandardProxyfactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
        pico.registerComponentImplementation(Collection.class, ArrayList.class);
        Object collection = pico.getComponentInstance(Collection.class);
        assertTrue(collection instanceof Collection);
        assertFalse(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingStandardProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
        pico.registerComponentImplementation("list", ArrayList.class);
        Object collection = pico.getComponentInstance("list");
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingCGLIBProxyfactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), new CglibProxyFactory()));
        pico.registerComponentImplementation(Collection.class, ArrayList.class);
        Object collection = pico.getComponentInstance(Collection.class);
        assertTrue(collection instanceof Collection);
        assertFalse(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingCGLIBProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), new CglibProxyFactory()));
        pico.registerComponentImplementation("list", ArrayList.class);
        Object collection = pico.getComponentInstance("list");
        assertTrue(collection instanceof Collection);
        assertTrue(collection instanceof List);
        assertTrue(collection instanceof ArrayList);
        assertTrue(collection.getClass().getSuperclass().equals(ArrayList.class));
    }


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

    public static interface SuperWoman extends Woman {
    }

    public static class Husband implements Man {
        public final Woman partner;
        private boolean wasKissed;

        public Husband(Woman partner) {
            this.partner = partner;
        }

        public Woman getWoman() {
            return partner;
        }

        public void kiss() {
            wasKissed = true;
        }

        public boolean wasKissed() {
            return wasKissed;
        }
    }

    public static class Wife implements SuperWoman {
        public final Man partner;

        public Wife(Man partner) {
            this.partner = partner;
        }

        public Man getMan() {
            return partner;
        }
    }

    public void testLowLevelCheating() {
        ComponentAdapterFactory caf = createComponentAdapterFactory();
        DefaultPicoContainer pico = new DefaultPicoContainer(caf);

        CachingComponentAdapter wifeAdapter = (CachingComponentAdapter) caf.createComponentAdapter("wife", Wife.class, null);
        CachingComponentAdapter husbandAdapter = (CachingComponentAdapter) caf.createComponentAdapter("husband", Husband.class, null);

        pico.registerComponent(wifeAdapter);
        pico.registerComponent(husbandAdapter);

        Woman wife = (Woman) wifeAdapter.getComponentInstance();
        Man wifesMan = wife.getMan();
        wifesMan.kiss();
        Man man = (Man) husbandAdapter.getComponentInstance();
        assertTrue(man.wasKissed());

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man) ((Swappable) man).hotswap(newMan);

        wife.getMan().kiss();
        assertTrue(newMan.wasKissed());
        assertNotSame(man, oldMan);
        assertNotSame(oldMan, newMan);
        assertNotSame(newMan, man);

        assertFalse(man.hashCode() == oldMan.hashCode());
        assertFalse(oldMan.hashCode() == newMan.hashCode());
        assertFalse(newMan.hashCode() == man.hashCode());
    }

    public void testHighLevelCheating() {
        MutablePicoContainer pico = new DefaultPicoContainer(createComponentAdapterFactory());

        // Register two classes with mutual dependencies in the constructor (!!!)
        pico.registerComponentImplementation(Wife.class);
        pico.registerComponentImplementation(Husband.class);

        Woman wife = (Woman) pico.getComponentInstance(Wife.class);
        Man man = (Man) pico.getComponentInstance(Husband.class);

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man) ((Swappable) man).hotswap(newMan);

        wife.getMan().kiss();
        assertFalse(oldMan.wasKissed());
        assertTrue(newMan.wasKissed());

    }

    public void testBigamy() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
        pico.registerComponentImplementation(Woman.class, Wife.class);
        Woman firstWife = (Woman) pico.getComponentInstance(Woman.class);
        Woman secondWife = (Woman) pico.getComponentInstance(Woman.class);
        assertNotSame(firstWife, secondWife);

    }

    public static class Bad implements Serializable {
        public Bad() {
            throw new IllegalStateException("HAHA");
        }
    }

    public void testIHCAFwithCTORandNoCaching() {
        // http://lists.codehaus.org/pipermail/picocontainer-dev/2004-January/001985.html
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class)));

        List list1 = (List) pico.getComponentInstance("l");
        List list2 = (List) pico.getComponentInstance("l");

        assertNotSame(list1, list2);
        assertFalse(list1 instanceof ArrayList);

        list1.add("Hello");
        assertTrue(list1.contains("Hello"));
        assertFalse(list2.contains("Hello"));
    }

    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class)));
        List l = (List) pico.getComponentInstance("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();
        ArrayList oldSubject = (ArrayList) ((Swappable) l).hotswap(newList);
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }

    public interface OtherSwappable {
        Object hotswap(Object newSubject);
    }

    public static class OtherSwappableImpl implements OtherSwappable {
        public Object hotswap(Object newSubject) {
            return "TADA";
        }
    }

    public void testInterferingSwapMethodsInComponentMasksHotSwappingFunctionality() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("os", OtherSwappableImpl.class)));
        OtherSwappable os = (OtherSwappable) pico.getComponentInstance("os");
        OtherSwappable os2 = new OtherSwappableImpl();

        assertEquals("TADA", os.hotswap(os2));
        Swappable os_ = (Swappable) os;
        assertEquals("TADA", os_.hotswap(os2));
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return cachingComponentAdapterFactory;
    }

    public static class Yin {
        private final Yang yang;

        public Yin(Yang yang) {
            this.yang = yang;
        }

        public Yang getYang() {
            return yang;
        }
    }

    public static class Yang {
        private final Yin yin;

        public Yang(Yin yin) {
            this.yin = yin;
        }

        public Yin getYin() {
            return yin;
        }
    }

    public void testShouldBeAbleToHandleMutualDependenciesWithoutInterfaceImplSeparation() {
        MutablePicoContainer pico = new DefaultPicoContainer(
                new CachingComponentAdapterFactory(
                        new HotSwappingComponentAdapterFactory(
                            new ConstructorInjectionComponentAdapterFactory(),
                            new CglibProxyFactory())));

        pico.registerComponentImplementation(Yin.class);
        pico.registerComponentImplementation(Yang.class);

        Yin yin = (Yin) pico.getComponentInstance(Yin.class);
        Yang yang = (Yang) pico.getComponentInstance(Yang.class);

        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }

}
