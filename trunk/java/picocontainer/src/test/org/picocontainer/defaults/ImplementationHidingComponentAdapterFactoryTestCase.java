/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImplementationHidingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    private ImplementationHidingComponentAdapterFactory implementationHiddingComponentAdapterFactory = new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    private CachingComponentAdapterFactory cachingComponentAdapterFactory = new CachingComponentAdapterFactory(implementationHiddingComponentAdapterFactory);

    public static interface Man {
        Woman getWoman();

        void kiss();

        boolean wasKissed();
    }

    public static interface Woman {
        Man getMan();
    }

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
        wife.getMan().kiss();
        Man man = (Man) husbandAdapter.getComponentInstance();
        assertTrue(man.wasKissed());

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man) ((Swappable) man).__hotSwap(newMan);

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
        Man oldMan = (Man) ((Swappable) man).__hotSwap(newMan);

        wife.getMan().kiss();
        assertFalse(oldMan.wasKissed());
        assertTrue(newMan.wasKissed());

    }

    public void testBigamy() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(
                new ConstructorComponentAdapterFactory()));
        pico.registerComponentImplementation(Woman.class, Wife.class);
        Woman firstWife = (Woman) pico.getComponentInstance(Woman.class);
        Woman secondWife = (Woman) pico.getComponentInstance(Woman.class);
        assertNotSame(firstWife, secondWife);

    }

    //TODO we need to be able to control which interfaces are subject to dynamic proxy
    // we used to have this feature with registerComponentImplementationByType or somesuch.

    public void do_nottestProxiedWifeIsNotSuperWoman() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(
                new ConstructorComponentAdapterFactory()));
        pico.registerComponentImplementation(Woman.class, Wife.class);
        Woman wife = (Woman) pico.getComponentInstance(Woman.class);
        assertFalse("Wife should not be castable to SuperWoman, as she should be proxied to Woman only", wife instanceof SuperWoman);
    }


    public static class Bad implements Serializable {
        public Bad() {
            throw new IllegalStateException("HAHA");
        }
    }

    public void testIHCAFwithCTORandNoCaching() {
        // http://lists.codehaus.org/pipermail/picocontainer-dev/2004-January/001985.html
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new ImplementationHidingComponentAdapter(new ConstructorComponentAdapter("l", ArrayList.class)));

        List list1 = (List) pico.getComponentInstance("l");
        List list2 = (List) pico.getComponentInstance("l");

        assertNotSame(list1, list2);

        list1.add("Hello");
        assertTrue(list1.contains("Hello"));
        assertFalse(list2.contains("Hello"));
    }

    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new ImplementationHidingComponentAdapter(new ConstructorComponentAdapter("l", ArrayList.class)));
        List l = (List) pico.getComponentInstance("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();
        ArrayList oldSubject = (ArrayList) ((Swappable)l).__hotSwap(newList);
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }

    public interface OtherSwappable {
        Object __hotSwap(Object newSubject);
    }

    public static class OtherSwappableImpl implements OtherSwappable {
        public Object __hotSwap(Object newSubject) {
            return "TADA";
        }
    }

    public void testInterferingSwapMethodsInComponentMasksHotSwappingFunctionality() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(new ImplementationHidingComponentAdapter(new ConstructorComponentAdapter("os", OtherSwappableImpl.class)));
        OtherSwappable os = (OtherSwappable) pico.getComponentInstance("os");
        OtherSwappable os2 = new OtherSwappableImpl();

        assertEquals("TADA", os.__hotSwap(os2));
        Swappable os_ = (Swappable) os;
        assertEquals("TADA", os_.__hotSwap(os2));
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return cachingComponentAdapterFactory;
    }
}
