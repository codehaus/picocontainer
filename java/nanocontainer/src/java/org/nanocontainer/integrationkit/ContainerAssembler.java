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

/**
 * Interface for assembling a container.
 * See <a href="http://wiki.opensymphony.com/space/PicoContainer+Integration">WebWork PicoContainer Integration</a>
 * for sample usage.
 * @author Joe Walnes <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ContainerAssembler {

    /**
     * Assemble the container. This typically involves registering
     * components.
     *
     * @param container container to assemble
     * @param assemblyScope scope of the container. This variable can be used as a hint to determine
     *      exactly what components should be registered.
     */
    void assembleContainer(MutablePicoContainer container, Object assemblyScope);

}
