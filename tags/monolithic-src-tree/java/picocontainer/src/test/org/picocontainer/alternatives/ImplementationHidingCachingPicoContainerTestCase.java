/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/

package org.picocontainer.alternatives;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.CachingComponentAdapterFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingCachingPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new ImplementationHidingCachingPicoContainer();
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent, LifecycleManager lifecycleManager) {
        return new ImplementationHidingCachingPicoContainer(new CachingComponentAdapterFactory(new DefaultComponentAdapterFactory()), parent, lifecycleManager);
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new ImplementationHidingCachingPicoContainer(parent);
    }

    public void testUsageOfADifferentComponentAdapterFactory() {
        // Jira bug 212 - logical opposite
        MutablePicoContainer parent = new DefaultPicoContainer();
        ImplementationHidingCachingPicoContainer pico = new ImplementationHidingCachingPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parent);
        pico.registerComponentImplementation(List.class, ArrayList.class);
        List list1 = (List) pico.getComponentInstanceOfType(List.class);
        List list2 = (List) pico.getComponentInstanceOfType(List.class);
        assertNotNull(list1);
        assertNotNull(list2);
        assertTrue(list1 == list2);
    }


}
