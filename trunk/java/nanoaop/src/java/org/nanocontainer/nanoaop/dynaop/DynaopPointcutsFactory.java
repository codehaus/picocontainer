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
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.defaults.AbstractPointcutsFactory;

import dynaop.Pointcuts;

/**
 * @author Stephen Molitor
 */
public class DynaopPointcutsFactory extends AbstractPointcutsFactory {

    public MethodPointcut allMethods() {
        return new DynaopMethodPointcut(Pointcuts.ALL_METHODS);
    }

    public ClassPointcut instancesOf(Class type) {
        return new DynaopClassPointcut(Pointcuts.instancesOf(type));
    }

}