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
import org.picocontainer.extras.DefaultLifecyclePicoAdapter;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @version $Revision$
 */
public class DefaultLifecycleContainerBuilder implements ContainerBuilder {

    public void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, ContainerAssembler assembler, String assemblyName) {

        MutablePicoContainer container = new DefaultPicoContainer();
        DefaultLifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(container);

        if (parentContainerRef != null) {
            MutablePicoContainer parent = (MutablePicoContainer) parentContainerRef.get();
            container.addParent(parent);
        }

        try {
            assembler.assembleContainer(container, assemblyName);
            lifecycle.start();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: What should I throw?
        }

        // hold on to it
        containerRef.set(lifecycle);

    }

    public void killContainer(ObjectReference containerRef) {
        try {
            LifecyclePicoAdapter lifecycle = (LifecyclePicoAdapter) containerRef.get();
            lifecycle.stop();
            lifecycle.dispose();

            MutablePicoContainer picoContainer = (MutablePicoContainer) lifecycle.getPicoContainer();
            List parentsToRemove = new ArrayList(picoContainer.getParentContainers());
            for (Iterator iterator = parentsToRemove.iterator(); iterator.hasNext();) {
                picoContainer.removeParent((MutablePicoContainer) iterator.next());
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot shutdown container", e); // todo: what should i throw?
        } finally {
            containerRef.set(null);
        }
    }

}
