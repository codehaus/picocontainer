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

import org.nanocontainer.nanoaop.ClassPointcut;

/**
 * @author Stephen Molitor
 */
public class DynaopClassPointcut implements dynaop.ClassPointcut, ClassPointcut {
    
    private final dynaop.ClassPointcut delegate;

    public DynaopClassPointcut(dynaop.ClassPointcut delegate) {
        this.delegate = delegate;
    }
    
    public boolean picks(Class clazz) {
        return delegate.picks(clazz);
    }
    
}
