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
 * Interface for composing a container.
 * See <a href="http://wiki.opensymphony.com/space/PicoContainer+Integration">WebWork PicoContainer Integration</a>
 * for sample usage.
 * @author Joe Walnes <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ContainerComposer {

    /**
     * Compose the container. This typically involves assembly (registration)
     * and configuration (setting primitive arguments) of components.
     *
     * @param container container to compose
     * @param compositionScope scope of the container. This variable can be used as a hint to determine
     *      exactly what components should be registered.
     */
    void composeContainer(MutablePicoContainer container, Object compositionScope);

}
