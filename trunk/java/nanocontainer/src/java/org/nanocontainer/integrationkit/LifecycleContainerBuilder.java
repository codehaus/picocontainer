/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class LifecycleContainerBuilder implements ContainerBuilder {

    public void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, Object assemblyScope) {
        PicoContainer parentContainer = parentContainerRef == null ? null : (PicoContainer) parentContainerRef.get();
        MutablePicoContainer container = createContainer(parentContainer, assemblyScope);

        composeContainer(container, assemblyScope);
        container.start();

        // hold on to it
        containerRef.set(container);
    }

    protected abstract void composeContainer(MutablePicoContainer container, Object assemblyScope);

    public void killContainer(ObjectReference containerRef){
        try {
            MutablePicoContainer pico = (MutablePicoContainer) containerRef.get();
            pico.stop();
            pico.dispose();
            pico.setParent(null);
        } finally {
            containerRef.set(null);
        }
    }

    protected MutablePicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
		return new DefaultPicoContainer(parentContainer);
	}
}
