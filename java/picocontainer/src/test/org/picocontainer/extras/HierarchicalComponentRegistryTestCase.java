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

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HierarchicalComponentRegistryTestCase extends TestCase {

    public void testBasicContainerAsserts() {
        try {
            new HierarchicalComponentRegistry.WithChildRegistry(null, new DefaultComponentRegistry());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            new HierarchicalComponentRegistry.WithChildRegistry(new DefaultComponentRegistry(), null);
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }

    }

    public void testBasicRegAndStart() throws PicoInitializationException, PicoRegistrationException {
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer dpc = new DefaultPicoContainer.WithComponentRegistry(dcr);
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(dcr);
        DefaultPicoContainer dpc2 = new DefaultPicoContainer.WithComponentRegistry(hcr);

        dpc.registerComponentByClass(DependsOnTouchable.class);
        dpc.registerComponentByClass(SimpleTouchable.class);
        HashMap hashMap = new HashMap();
        HashSet hashSet = new HashSet();
        dpc.registerComponent(Map.class, hashMap);
        dpc2.registerComponent(Set.class, hashSet);

        dpc.getComponents();
        dpc2.getComponents();

        Collection keys = hcr.getComponentKeys();
        assertTrue(keys.contains(DependsOnTouchable.class));
        assertTrue(keys.contains(SimpleTouchable.class));
        assertTrue(keys.contains(Map.class));
        assertTrue(keys.contains(Set.class));

        List list = hcr.getOrderedComponents();
        assertTrue(list.contains(hashMap));
        assertTrue(list.contains(hashSet));

        assertEquals("There should be two comps in the internals", 4, hcr.getComponentInstances().size());

        assertTrue("There should have been a Fred in the registry", hcr.hasComponentInstance(DependsOnTouchable.class));
        assertTrue("There should have been a Touchable in the registry", hcr.hasComponentInstance(SimpleTouchable.class));
        assertTrue("There should have been a Map in the registry", hcr.hasComponentInstance(Map.class));
        assertTrue("There should have been a Set in the registry", hcr.hasComponentInstance(Set.class));

    }

    public static class DerivedTouchable extends SimpleTouchable {
        public DerivedTouchable() {
        }
    }

    public void testRegisterComponentWithObject() throws PicoRegistrationException, PicoInitializationException {
        DefaultComponentRegistry parentReg = new DefaultComponentRegistry();
        DefaultPicoContainer parent = new DefaultPicoContainer.WithComponentRegistry(parentReg);
        parent.registerComponentByInstance(new SimpleTouchable());

        DefaultComponentRegistry childReg = new DefaultComponentRegistry();
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(childReg);
        child.registerComponentByClass(DependsOnTouchable.class);

        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry(parentReg, childReg);

        assertNotNull("There should have been a DependsOnTouchable in the internals", hcr.getComponentInstance(DependsOnTouchable.class));
        assertNotNull("There should have been a Touchable in the internals", hcr.getComponentInstance(SimpleTouchable.class));
    }

    public void testGetComponentTypes() throws PicoRegistrationException, PicoInitializationException {

        DefaultComponentRegistry parentReg = new DefaultComponentRegistry();
        DefaultPicoContainer parent = new DefaultPicoContainer.WithComponentRegistry(parentReg);
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parentReg);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponentByClass(DependsOnTouchable.class);
        parent.registerComponent(Touchable.class, SimpleTouchable.class);

        // You might have thought that starting the internals shouldn't be necessary
        // just to get the types, but it is. The map holding the types->component instances
        // doesn't receive anything until the components are instantiated.
        parent.getComponents();
        child.getComponents();

        Collection types = child.getComponentKeys();
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a DependsOnTouchable type", types.contains(DependsOnTouchable.class));
        assertTrue("There should be a Touchable type", types.contains(Touchable.class));
        assertTrue("There should not be a SimpleTouchable type", !types.contains(SimpleTouchable.class));

        assertNotNull("Should have a thing implementing Touchable", hcr.getComponentInstance(Touchable.class));
        assertEquals("Should have a thing implementing Touchable", hcr.getComponentInstance(Touchable.class).getClass(), SimpleTouchable.class);
        assertNotNull("Should have a thing implementing Touchable", hcr.getComponentInstance(Touchable.class));

    }

    public void testParentContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultComponentRegistry parentReg = new DefaultComponentRegistry();
        DefaultPicoContainer parent = new DefaultPicoContainer.WithComponentRegistry(parentReg);
        parent.registerComponent(Touchable.class, SimpleTouchable.class);

        DefaultComponentRegistry childReg = new DefaultComponentRegistry();
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(childReg);
        child.registerComponentByClass(DependsOnTouchable.class);

        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry(parentReg, childReg);

        assertEquals("The parent should return 2 components (one from the parent)", 2, hcr.getComponentInstances().size());
        assertNotNull("Touchable should have been passed through the parent internals", hcr.getComponentInstance(Touchable.class));
        assertNotNull("There should have been a DependsOnTouchable in the internals", hcr.getComponentInstance(DependsOnTouchable.class));

    }

}
