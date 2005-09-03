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
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.testmodel.SimpleTouchable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingCachingPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new ImplementationHidingCachingPicoContainer();
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


    public void testMakeChildContainer(){
        MutablePicoContainer parent = new ImplementationHidingCachingPicoContainer();
        parent.registerComponentImplementation("t1", SimpleTouchable.class);
        MutablePicoContainer child = parent.makeChildContainer();
        Object t1 = child.getParent().getComponentInstance("t1");        
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);        
    }    

}
