package nanocontainer.reflect;

/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                           *
 *****************************************************************************/

import picocontainer.Container;

import java.lang.reflect.Method;

/**
 * InvocationHandler that adds multiple return type functionality.
 * The result is an array of the return values for each matching
 * method in the container's components.
 *
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class SequentialInvocationHandler extends ContainerInvocationHandler {

    public SequentialInvocationHandler(Container container) {
        super(container);
    }

    /**
     * Returns an Array of Object, containing all the return values for the components
     * in the associated PicoContainer.
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Object targetComponent = null;
        // Try to invoke the method on all components.
        Object[] components = getContainer().getComponents();
        if( components.length == 0 ) {
            throw new NoInvocationTargetException(proxy,method);
        }
        boolean success = false;
        for (int i = 0; i < components.length; i++) {
            Object component = components[i];
            try {
                result = method.invoke( component, args );
                success = true;
                if( targetComponent != null ) {
                    throw new AmbiguousInvocationException(targetComponent,component,method);
                }
                targetComponent = component;
            } catch( Exception ignore ) {
            }
        }
        if( success == false ) {
            throw new NoInvocationTargetException(proxy,method);
        }
        return result;
    }
}
