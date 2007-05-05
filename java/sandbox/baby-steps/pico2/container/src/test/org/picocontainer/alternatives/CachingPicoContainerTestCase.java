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

import java.util.ArrayList;
import java.util.List;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class CachingPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new CachingPicoContainer();
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new CachingPicoContainer(parent);
    }

    public void testUsageOfADifferentComponentAdapterFactory() {
        // Jira bug 212 - logical opposite
        MutablePicoContainer parent = new DefaultPicoContainer();
        CachingPicoContainer pico = new CachingPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parent);
        pico.component(List.class, ArrayList.class);
        List list1 = (List) pico.getComponent(List.class);
        List list2 = (List) pico.getComponent(List.class);
        assertNotNull(list1);
        assertNotNull(list2);
        assertTrue(list1 == list2);
    }


    public void testComponentAdaptersListsAreNotCached() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        CachingPicoContainer pico = new CachingPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parent);
        pico.component(List.class, ArrayList.class);
        List list1 = (List) pico.getComponentAdapters(List.class);
        List list2 = (List) pico.getComponentAdapters(List.class);
        assertNotNull(list1);
        assertNotNull(list2);
        assertTrue(list1 != list2);
    }    
        
}
