/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/

package org.nanocontainer.servlet;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentRegistry;

public interface ContainerFactory {
    PicoContainer buildContainer(String configName, ComponentRegistry componentRegistry);

    PicoContainer buildContainerWithParent(PicoContainer parentContainer, ComponentRegistry parentRegistry, String configName);

    ObjectInstantiator buildInstantiator(PicoContainer parentContainer, ComponentRegistry parentRegistry);

    void destroyContainer(PicoContainer container);
}

