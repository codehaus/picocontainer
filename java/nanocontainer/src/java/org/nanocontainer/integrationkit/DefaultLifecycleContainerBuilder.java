/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

package org.nanocontainer.integrationkit;

import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.picocontainer.PicoContainer;

public class DefaultLifecycleContainerBuilder extends LifecycleContainerBuilder {
    private final ContainerComposer composer;

    public DefaultLifecycleContainerBuilder(ContainerComposer composer) {
        this.composer = composer;
    }

    protected void composeContainer(SoftCompositionPicoContainer container, Object assemblyScope) {
        composer.composeContainer(container, assemblyScope);
    }

    protected PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
        return new DefaultSoftCompositionPicoContainer(parentContainer);
    }
}