/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import java.util.ArrayList;
import java.util.List;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.VerifyingVisitor;
import org.picocontainer.tck.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.testmodel.SimpleTouchable;

public class ImplementationHidingPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new ImplementationHidingPicoContainer();
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new ImplementationHidingPicoContainer(parent);
    }

    public void testStartStopAndDisposeNotCascadedtoRemovedChildren() {
        super.testStartStopAndDisposeNotCascadedtoRemovedChildren();
    }

    public static class MyThread extends Thread {
        public MyThread(String s) {
            super(s);
        }
    }

    public void testHidingWithoutParameter() {
        // this was a bug reported by Arnd Kors on 21st Sept on the mail list.
        ImplementationHidingPicoContainer pico = new ImplementationHidingPicoContainer();
        pico.registerComponent(String.class);
        pico.registerComponent(Runnable.class, MyThread.class);
        new VerifyingVisitor().traverse(pico);
    }

    public void testUsageOfADifferentComponentAdapterFactory() {
        // Jira bug 212
        MutablePicoContainer parent = new DefaultPicoContainer();
        ImplementationHidingPicoContainer pico = new ImplementationHidingPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parent);
        pico.registerComponent(List.class, ArrayList.class);
        List list1 = (List) pico.getComponent(List.class);
        List list2 = (List) pico.getComponent(List.class);
        assertNotNull(list1);
        assertNotNull(list2);
        assertFalse(list1 instanceof ArrayList);
        assertFalse(list2 instanceof ArrayList);
        assertFalse(list1 == list2);
    }
   
    public void testMakeChildContainer(){
        MutablePicoContainer parent = new ImplementationHidingPicoContainer();
        parent.registerComponent("t1", SimpleTouchable.class);
        MutablePicoContainer child = parent.makeChildContainer();
        Object t1 = child.getParent().getComponent("t1");
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);        
    }

    public void testSameInstanceCanBeUsedAsDifferentTypeWhenCaching() {
        // IHPC does not cache by default.
    }


}
