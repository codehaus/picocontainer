/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.integrationkit;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * This ContainerBuilder uses PicoContainer's standard lifecycle API.
 *
 * TODO rename to LifecycleContainerBuilder when back on MAIN branch
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleContainerBuilder implements ContainerBuilder {

    /**
     * {@inheritDoc}
     * Also calls start() on the container.
     */
    public void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, ContainerAssembler assembler, Object assemblyScope) {

        MutablePicoContainer container = new DefaultPicoContainer();

        if (parentContainerRef != null) {
            MutablePicoContainer parent = (MutablePicoContainer) parentContainerRef.get();
            container.setParent(parent);
        }

        assembler.assembleContainer(container, assemblyScope);
        container.start();

        // hold on to it
        containerRef.set(container);
    }

    /**
     * {@inheritDoc}
     * Also calls stop() and dispose() on the container.
     */
    public void killContainer(ObjectReference containerRef) {
        try {
            MutablePicoContainer container = (MutablePicoContainer) containerRef.get();
            container.stop();
            container.dispose();
            container.setParent(null);
        } finally {
            containerRef.set(null);
        }
    }
}
