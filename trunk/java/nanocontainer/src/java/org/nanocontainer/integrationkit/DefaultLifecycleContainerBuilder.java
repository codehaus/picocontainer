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
import org.picocontainer.extras.DefaultLifecyclePicoContainer;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class DefaultLifecycleContainerBuilder implements ContainerBuilder {

    public void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, ContainerAssembler assembler, String assemblyName) {

        DefaultLifecyclePicoContainer container = new DefaultLifecyclePicoContainer();

        if (parentContainerRef != null) {
            MutablePicoContainer parent = (MutablePicoContainer) parentContainerRef.get();
            container.addParent(parent);
        }

        try {
            assembler.assembleContainer(container, assemblyName);
            container.start();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: What should I throw?
        }

        // hold on to it
        containerRef.set(container);

    }

    public void killContainer(ObjectReference containerRef) {
        try {
            DefaultLifecyclePicoContainer container = (DefaultLifecyclePicoContainer) containerRef.get();
            container.stop();
            container.dispose();
            List parentsToRemove = new LinkedList(); // eww
            for (Iterator iterator = container.getParentContainers().iterator(); iterator.hasNext();) {
                parentsToRemove.add(iterator.next());
            }
            for (Iterator iterator = parentsToRemove.iterator(); iterator.hasNext();) {
                container.removeParent((MutablePicoContainer) iterator.next());
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot shutdown container", e); // todo: what should i throw?
        } finally {
            containerRef.set(null);
        }
    }

}
