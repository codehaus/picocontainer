/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.integrationkit;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ContainerBuilder {

    /**
     * Create, assemble, init and start a new PicoContainer and store it
     * at a given reference.
     *
     * @param containerRef        Where to store the new container.
     * @param parentContainerRef  Parent container (may be null)
     * @param assembler           Strategy for assembling components in container.
     * @param assemblyScope       Argument to be passed to ContainerAssembler.
     */
    void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, ContainerAssembler assembler, Object assemblyScope);

    /**
     * Locate a container at the given reference so it can stopped,
     * destroyed and removed.
     *
     * @param containerRef        Where the container is stored.
     */
    void killContainer(ObjectReference containerRef);

}
