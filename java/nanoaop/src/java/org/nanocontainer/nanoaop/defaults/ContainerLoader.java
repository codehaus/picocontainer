/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;

/**
 * @author Stephen Molitor
 */
public class ContainerLoader {

    private PicoContainer container;

    public ContainerLoader() {
    }

    public PicoContainer getContainer() {
        if (container == null) {
            throw new PicoInitializationException("Container has not been set");
        }
        return container;
    }

    public void setContainer(PicoContainer container) {
        this.container = container;
    }

}