package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.Swappable;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.adapters.CachingComponentAdapter;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class HotSwappingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    private HotSwappingComponentAdapterFactory implementationHidingComponentAdapterFactory = new HotSwappingComponentAdapterFactory(
            new AnyInjectionComponentAdapterFactory());
    private CachingComponentAdapterFactory cachingComponentAdapterFactory = new CachingComponentAdapterFactory(
            implementationHidingComponentAdapterFactory);

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingStandardProxyfactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.component(Collection.class, ArrayList.class);
        Object collection = pico.getComponent(Collection.class);
        assertTrue(collection instanceof Collection);
        assertFalse(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingStandardProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.component("list", ArrayList.class);
        Object collection = pico.getComponent("list");
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingCGLIBProxyfactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory(), new CglibProxyFactory()));
        pico.component(Collection.class, ArrayList.class);
        Object collection = pico.getComponent(Collection.class);
        assertTrue(collection instanceof Collection);
        assertFalse(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingCGLIBProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory(), new CglibProxyFactory()));
        pico.component("list", ArrayList.class);
        Object collection = pico.getComponent("list");
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

        CachingComponentAdapter wifeAdapter = (CachingComponentAdapter)caf.createComponentAdapter(ComponentCharacteristics.CDI, "wife", Wife.class, (Parameter[])null);
        CachingComponentAdapter husbandAdapter = (CachingComponentAdapter)caf
                .createComponentAdapter(ComponentCharacteristics.CDI, "husband", Husband.class, (Parameter[])null);

        pico.adapter(wifeAdapter);
        pico.adapter(husbandAdapter);

        Woman wife = (Woman)wifeAdapter.getComponentInstance(pico);
        Man wifesMan = wife.getMan();
        wifesMan.kiss();
        Man man = (Man)husbandAdapter.getComponentInstance(pico);
        assertTrue(man.wasKissed());

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man)((Swappable)man).hotswap(newMan);

        wife.getMan().kiss();
        assertTrue(newMan.wasKissed());
        assertNotSame(man, oldMan);
        assertNotSame(oldMan, newMan);
        assertNotSame(newMan, man);

        assertFalse(man.hashCode() == oldMan.hashCode());
        assertFalse(oldMan.hashCode() == newMan.hashCode());
        // TODO: Enable for proxytoys-0.2 final
        // assertTrue(newMan.hashCode() == man.hashCode());
    }


    public void testTheThingThatPaulDoesNotLikeAboutTheCurrentHotSwapper() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory());

        HotSwappingComponentAdapter ca = (HotSwappingComponentAdapter) pico.component(List.class, ArrayList.class).lastCA();

        List list = (List) pico.getComponent(List.class);

        // I do not like that I can cast the object to something that is mutable beyond the scope
        // of its design (list functions)
        List list2 = (List) ((Swappable) list).hotswap(new ArrayList());

        MutablePicoContainer child = pico.makeChildContainer();
        child.component(ExposesListMember.class, ExposesListMemberImpl.class);
        Object componentInstance = child.getComponent(ExposesListMember.class);
        ExposesListMember exposer = (ExposesListMember) componentInstance;

        // still mutable for children :-(
        List list3 = (List) ((Swappable) exposer.getList()).hotswap(new ArrayList());

        // what I really want ...
        //ca.swapInstance(...);
    }

    public static interface ExposesListMember {
        List getList();
    }

    public static class ExposesListMemberImpl implements ExposesListMember {
        private final List list;

        public ExposesListMemberImpl(List l) {
            list = l;
        }

        public List getList() {
            return list;
        }

    }


    public void testHighLevelCheating() {
        MutablePicoContainer pico = new DefaultPicoContainer(createComponentAdapterFactory());

        // Register two classes with mutual dependencies in the constructor (!!!)
        pico.component(Wife.class);
        pico.component(Husband.class);

        Woman wife = (Woman)pico.getComponent(Wife.class);
        Man man = (Man)pico.getComponent(Husband.class);

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man)((Swappable)man).hotswap(newMan);

        wife.getMan().kiss();
        assertFalse(oldMan.wasKissed());
        assertTrue(newMan.wasKissed());

    }

    public void testBigamy() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwappingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.component(Woman.class, Wife.class);
        Woman firstWife = (Woman)pico.getComponent(Woman.class);
        Woman secondWife = (Woman)pico.getComponent(Woman.class);
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
        pico.adapter(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class)));

        List list1 = (List)pico.getComponent("l");
        List list2 = (List)pico.getComponent("l");

        assertNotSame(list1, list2);
        assertFalse(list1 instanceof ArrayList);

        list1.add("Hello");
        assertTrue(list1.contains("Hello"));
        assertFalse(list2.contains("Hello"));
    }

    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.adapter(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class)));
        List l = (List)pico.getComponent("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();
        ArrayList oldSubject = (ArrayList)((Swappable)l).hotswap(newList);
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
        pico.adapter(new HotSwappingComponentAdapter(new ConstructorInjectionComponentAdapter(
                "os", OtherSwappableImpl.class)));
        OtherSwappable os = (OtherSwappable)pico.getComponent("os");
        OtherSwappable os2 = new OtherSwappableImpl();

        assertEquals("TADA", os.hotswap(os2));
        Swappable os_ = (Swappable)os;
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

    // TODO: Fails with versions of cglib >= 2.0.1
    public void testShouldBeAbleToHandleMutualDependenciesWithoutInterfaceImplSeparation() {
        MutablePicoContainer pico = new DefaultPicoContainer(new CachingComponentAdapterFactory(
                new HotSwappingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), new CglibProxyFactory())));

        pico.component(Yin.class);
        pico.component(Yang.class);

        Yin yin = (Yin)pico.getComponent(Yin.class);
        Yang yang = (Yang)pico.getComponent(Yang.class);

        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }

}
