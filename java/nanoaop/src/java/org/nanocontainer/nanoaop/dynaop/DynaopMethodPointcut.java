/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import java.lang.reflect.Method;

import org.nanocontainer.nanoaop.MethodPointcut;

/**
 * @author Stephen Molitor
 */
public class DynaopMethodPointcut implements MethodPointcut, dynaop.MethodPointcut {
    
    private final dynaop.MethodPointcut delegate;
    
    public DynaopMethodPointcut(dynaop.MethodPointcut delegate) {
        this.delegate = delegate;
    }

    public boolean picks(Method method) {
        return delegate.picks(method);
    }

}
