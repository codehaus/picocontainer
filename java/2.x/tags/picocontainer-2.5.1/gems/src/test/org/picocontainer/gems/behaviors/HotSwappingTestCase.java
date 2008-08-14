/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.gems.GemsCharacteristics;
import org.picocontainer.gems.PicoGemsBuilder;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.tck.AbstractComponentFactoryTest;


public final class HotSwappingTestCase extends AbstractComponentFactoryTest {
    private final ComponentFactory implementationHidingComponentFactory = new HotSwapping().wrap(new AdaptingInjection());

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

        public Wife(final Man partner) {
            this.partner = partner;
        }

        public Man getMan() {
            return partner;
        }
    }


    @Test
    public void testHotSwappingNaturaelyCaches() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwapping().wrap(new ConstructorInjection()));
        pico.addComponent(Map.class, HashMap.class);
        Map firstMap = pico.getComponent(Map.class);
        Map secondMap = pico.getComponent(Map.class);
        assertSame(firstMap, secondMap);
    }

    @Test
    public void testHotSwappingNaturaelyCaches2() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwapping());
        pico.addComponent(Map.class, HashMap.class);
        Map firstMap = pico.getComponent(Map.class);
        firstMap.put("foo", "bar");
        HotSwappable hsca = (HotSwappable) pico.getComponentAdapter(Map.class);
        hsca.getSwappable().swap(new HashMap());
        Map secondMap = pico.getComponent(Map.class);
        secondMap.put("apple", "orange");
        assertSame(firstMap, secondMap);
        assertNull(firstMap.get("foo"));
        assertNotNull(firstMap.get("apple"));
    }


    @Test
    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ConstructorInjector constructorInjector = new ConstructorInjector("l", ArrayList.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        HotSwappable hsca = (HotSwappable) pico.addAdapter(new HotSwappable(constructorInjector)).getComponentAdapter(constructorInjector.getComponentKey());
        List l = (List)pico.getComponent("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();

        ArrayList oldSubject = (ArrayList) hsca.swapRealInstance(newList);
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }
    
    @Test
    public void testHotswapTurnOnTurnOfWithPicoContainer() {
    	MutablePicoContainer pico = new PicoBuilder().withBehaviors(PicoGemsBuilder.HOT_SWAPPING()).build();
    	pico.as(GemsCharacteristics.NO_HOT_SWAP).addComponent("firstMap", HashMap.class);
    	pico.as(GemsCharacteristics.HOT_SWAP).addComponent("hotswapMap", HashMap.class);
    	
    	assertNull(pico.getComponentAdapter("firstMap").findAdapterOfType(HotSwappable.class));
    	assertNotNull(pico.getComponentAdapter("hotswapMap").findAdapterOfType(HotSwappable.class));
    }


    @Override
	protected ComponentFactory createComponentFactory() {
        return implementationHidingComponentFactory;
    }

}
