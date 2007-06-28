/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.VerifyingVisitor;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;

import java.util.ArrayList;
import java.util.List;

public class ImplementationHidingPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new ImplementationHidingPicoContainer();
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent, LifecycleManager lifecycleManager) {
        return new ImplementationHidingPicoContainer(new DefaultComponentAdapterFactory(), parent, lifecycleManager);
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
        pico.registerComponentImplementation(String.class);
        pico.registerComponentImplementation(Runnable.class, MyThread.class);
        new VerifyingVisitor().traverse(pico);
    }

    public void testUsageOfADifferentComponentAdapterFactory() {
        // Jira bug 212
        MutablePicoContainer parent = new DefaultPicoContainer();
        ImplementationHidingPicoContainer pico = new ImplementationHidingPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parent);
        pico.registerComponentImplementation(List.class, ArrayList.class);
        List list1 = (List) pico.getComponentInstanceOfType(List.class);
        List list2 = (List) pico.getComponentInstanceOfType(List.class);
        assertNotNull(list1);
        assertNotNull(list2);
        assertFalse(list1 == list2);
    }
}
