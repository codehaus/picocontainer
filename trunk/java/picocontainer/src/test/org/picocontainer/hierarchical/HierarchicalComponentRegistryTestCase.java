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
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NullContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.testmodel.FredImpl;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.WilmaImpl;

import java.util.Collection;

public class HierarchicalComponentRegistryTestCase extends TestCase {

    public void testBasicContainerAsserts() {
        try {
            new HierarchicalComponentRegistry(null, new DefaultComponentRegistry());
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            new HierarchicalComponentRegistry(new NullContainer(), null);
            fail("Should have had NPE)");
        } catch (NullPointerException npe) {
            // expected
        }

    }

    public void testBasicRegAndStart() throws PicoInitializationException, PicoRegistrationException {
        DefaultPicoContainer dpc = new DefaultPicoContainer.Default();
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(dpc);

        dpc.registerComponentByClass(FredImpl.class);
        dpc.registerComponentByClass(WilmaImpl.class);

        dpc.instantiateComponents();

        assertEquals("There should be two comps in the container", 2, hcr.getComponentInstances().size());

        assertTrue("There should have been a Fred in the container", hcr.hasComponentInstance(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", hcr.hasComponentInstance(WilmaImpl.class));
    }

    public static class DerivedWilma extends WilmaImpl {
        public DerivedWilma() {
        }
    }

    public void testRegisterComponentWithObject() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer parent = new DefaultPicoContainer.Default();
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parent);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponentByClass(FredImpl.class);
        parent.registerComponentByInstance(new WilmaImpl());

        parent.instantiateComponents();

        assertTrue("There should have been a Fred in the container", child.hasComponent(FredImpl.class));
        assertTrue("There should have been a Wilma in the container", child.hasComponent(WilmaImpl.class));
    }

    public void testGetComponentTypes() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer parent = new DefaultPicoContainer.Default();
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parent);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponentByClass(FredImpl.class);
        parent.registerComponent(Wilma.class, WilmaImpl.class);

        // You might have thought that starting the container shouldn't be necessary
        // just to get the types, but it is. The map holding the types->component instances
        // doesn't receive anything until the components are instantiated.
        parent.instantiateComponents();
        child.instantiateComponents();

        Collection types = child.getComponentKeys();
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a FredImpl type", types.contains(FredImpl.class));
        assertTrue("There should be a Wilma type", types.contains(Wilma.class));
        assertTrue("There should not be a WilmaImpl type", !types.contains(WilmaImpl.class));
    }

    public void testParentContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer parent = new DefaultPicoContainer.Default();
        HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parent);
        DefaultPicoContainer child = new DefaultPicoContainer.WithComponentRegistry(hcr);

        parent.registerComponent(Wilma.class, WilmaImpl.class);
        parent.instantiateComponents();
        child.registerComponentByClass(FredImpl.class);
        child.instantiateComponents();

        assertEquals("The parent should return 2 components (one from the parent)", 2, child.getComponents().size());
        assertTrue("Wilma should have been passed through the parent container", child.hasComponent(Wilma.class));
        assertTrue("There should have been a FredImpl in the container", child.hasComponent(FredImpl.class));

    }

}
