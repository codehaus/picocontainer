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

import org.nanocontainer.nanoaop.AbstractPointcutFactory;
import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.MethodPointcut;

import dynaop.Pointcuts;

/**
 * @author Stephen Molitor
 */
public class DynaopPointcutFactory extends AbstractPointcutFactory {

    public ClassPointcut instancesOf(Class type) {
        return new DynaopClassPointcut(Pointcuts.instancesOf(type));
    }

    public MethodPointcut allMethods() {
        return new DynaopMethodPointcut(Pointcuts.ALL_METHODS);
    }

}