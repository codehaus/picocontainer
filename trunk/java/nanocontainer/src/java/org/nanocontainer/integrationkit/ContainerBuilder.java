/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.integrationkit;

import org.picocontainer.defaults.ObjectReference;

/**
 * The responsability of a ContainerBuilder is to <em>instantiate</em> containers.
 * It delegates the responsability of the <em>assembly</em> to a
 * {@link ContainerAssembler}.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ContainerBuilder {

    /**
     * Create, assemble, init and start a new PicoContainer and store it
     * at a given reference.
     *
     * @param containerRef       Where to store the new container.
     * @param parentContainerRef reference to a container that may be used as a parent to the new container (may be null).
     * @param assemblyScope      Argument to be passed to ContainerAssembler.
     */
    void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, Object assemblyScope);

    /**
     * Locate a container at the given reference so it can be stopped,
     * destroyed and removed.
     *
     * @param containerRef Where the container is stored.
     * @deprecated Not needed anymore??
     */
    void killContainer(ObjectReference containerRef);

}

