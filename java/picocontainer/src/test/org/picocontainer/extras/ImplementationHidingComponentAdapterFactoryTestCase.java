/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.defaults.*;

public class ImplementationHidingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    public static interface Man {
        Woman getWoman();

        void kiss();
        boolean wasKissed();
    }

    public static interface Woman {
        Man getMan();
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

    public static class Wife implements Woman {
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

        ImplementationHidingComponentAdapter wifeAdapter = (ImplementationHidingComponentAdapter) caf.createComponentAdapter("wife", Wife.class, null);
        ImplementationHidingComponentAdapter husbandAdapter = (ImplementationHidingComponentAdapter) caf.createComponentAdapter("husband", Husband.class, null);

        pico.registerComponent(wifeAdapter);
        pico.registerComponent(husbandAdapter);

        Woman wife = (Woman) wifeAdapter.getComponentInstance(pico);
        wife.getMan().kiss();
        Man man = (Man) husbandAdapter.getComponentInstance(pico);
        assertTrue(man.wasKissed());

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        Man oldMan = (Man) husbandAdapter.hotSwap(newMan);

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
        DefaultPicoContainer pico = new DefaultPicoContainer(createComponentAdapterFactory());

        // Register two classes with mutual dependencies in the constructor (!!!)
        pico.registerComponentImplementation(Wife.class);
        pico.registerComponentImplementation(Husband.class);

        Woman wife = (Woman) pico.getComponentInstance(Wife.class);
        Man man = (Man) pico.getComponentInstance(Husband.class);

        assertSame(man, wife.getMan());
        assertSame(wife, man.getWoman());

        // Let the wife use another (single) man
        Man newMan = new Husband(null);
        ImplementationHidingComponentAdapter husbandAdapter = (ImplementationHidingComponentAdapter) pico.findComponentAdapter(Husband.class);
        Man oldMan = (Man) husbandAdapter.hotSwap(newMan);

        wife.getMan().kiss();
        assertFalse(oldMan.wasKissed());
        assertTrue(newMan.wasKissed());

    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

}
