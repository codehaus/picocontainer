/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                           *
 *****************************************************************************/

package nanocontainer.aggregating.reflect;

import picocontainer.PicoContainer;
import picocontainer.Container;

import java.lang.reflect.InvocationHandler;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.4 $
 */
public abstract class ContainerInvocationHandler implements InvocationHandler {
    private Container container;

    protected ContainerInvocationHandler( Container container ) {
        this.container = container;
    }

    protected Container getContainer() {
        return container;
    }
}
