/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.hierarchical;

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

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

        dpc.instantiateComponents();
        dpc2.instantiateComponents();

        Set set = hcr.getComponentInstanceKeys();
        assertTrue(set.contains(DependsOnTouchable.class));
        assertTrue(set.contains(SimpleTouchable.class));
        assertTrue(set.contains(Map.class));
        assertTrue(set.contains(Set.class));

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
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parentReg);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponentByClass(DependsOnTouchable.class);
        parent.registerComponentByInstance(new SimpleTouchable());

        parent.instantiateComponents();

        assertTrue("There should have been a Fred in the internals", child.hasComponent(DependsOnTouchable.class));
        assertTrue("There should have been a Touchable in the internals", child.hasComponent(SimpleTouchable.class));
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
        parent.instantiateComponents();
        child.instantiateComponents();

        Collection types = child.getComponentKeys();
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a DependsOnTouchable type", types.contains(DependsOnTouchable.class));
        assertTrue("There should be a Touchable type", types.contains(Touchable.class));
        assertTrue("There should not be a SimpleTouchable type", !types.contains(SimpleTouchable.class));

        assertNotNull("Should have a thing implementing Touchable", hcr.findImplementingComponent(Touchable.class));
        assertEquals("Should have a thing implementing Touchable", hcr.findImplementingComponent(Touchable.class).getClass(), SimpleTouchable.class);
        assertNotNull("Should have a thing implementing Touchable", hcr.findImplementingComponentSpecification(Touchable.class));

    }

    public void testParentContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultComponentRegistry parentReg = new DefaultComponentRegistry();
        DefaultPicoContainer parent = new DefaultPicoContainer.WithComponentRegistry(parentReg);
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parentReg);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponent(Touchable.class, SimpleTouchable.class);
        parent.instantiateComponents();
        child.registerComponentByClass(DependsOnTouchable.class);
        child.instantiateComponents();

        assertEquals("The parent should return 2 components (one from the parent)", 2, child.getComponents().size());
        assertTrue("Touchable should have been passed through the parent internals", child.hasComponent(Touchable.class));
        assertTrue("There should have been a DependsOnTouchable in the internals", child.hasComponent(DependsOnTouchable.class));

    }

}
