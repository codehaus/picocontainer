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
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleContainerBuilder implements ContainerBuilder {

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

    public void killContainer(ObjectReference containerRef) {
        try {
            MutablePicoContainer pico = (MutablePicoContainer) containerRef.get();
            pico.stop();
            pico.dispose();
            pico.setParent(null);
        } finally {
            containerRef.set(null);
        }
    }
}
