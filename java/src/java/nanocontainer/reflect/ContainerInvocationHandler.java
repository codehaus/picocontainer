/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer.reflect;

import picocontainer.PicoContainer;
import picocontainer.Container;

import java.lang.reflect.InvocationHandler;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
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
