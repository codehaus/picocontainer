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
import org.picocontainer.defaults.AbstractImplementationHidingPicoContainerTestCase;

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
        public MyThread(String s){super(s);}
    }

    public void testHidingWithoutParameter() {
        // this was a bug reported by Arnd Kors on 21st Sept on the mail list.
        ImplementationHidingPicoContainer pico = new ImplementationHidingPicoContainer();
        pico.registerComponentImplementation(String.class);
        pico.registerComponentImplementation(Runnable.class, MyThread.class);
        pico.verify();
    }

}
