/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class LifecycleContainerBuilder implements ContainerBuilder {

    public final void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, Object assemblyScope) {
        PicoContainer parentContainer = parentContainerRef == null ? null : (PicoContainer) parentContainerRef.get();
        PicoContainer container = createContainer(parentContainer, assemblyScope);

        // register the child in the parent so that lifecycle can be propagated down the hierarchy
        if (parentContainer != null && parentContainer instanceof MutablePicoContainer) {
            MutablePicoContainer mutableContainer = (MutablePicoContainer) parentContainer;

            // this synchronization is necessary, because several servlet request may
            // occur at the same time for given session, and this produce race condition
            // especially in framed environments
            synchronized (mutableContainer) {
                mutableContainer.registerComponentInstance(containerRef, container);
            }
        }

        if (container instanceof MutablePicoContainer) {
            composeContainer((MutablePicoContainer) container, assemblyScope);
        }
        autoStart(container);

        // hold on to it
        containerRef.set(container);
    }

    protected void autoStart(PicoContainer container) {
        System.out.println("--> autostarted");
        container.start();
    }

    public void killContainer(ObjectReference containerRef) {
        try {
            PicoContainer pico = (PicoContainer) containerRef.get();
            pico.stop();
            pico.dispose();
            PicoContainer parent = pico.getParent();
            if (parent != null && parent instanceof MutablePicoContainer) {
                // see comment in buildContainer
                synchronized (parent) {
                    ((MutablePicoContainer) parent).unregisterComponentByInstance(pico);
                }
            }
        } finally {
            containerRef.set(null);
        }
    }

    protected abstract void composeContainer(MutablePicoContainer container, Object assemblyScope);

    protected abstract PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope);
}
