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

import org.nanocontainer.nanoaop.ComponentPointcut;

/**
 * @author Stephen Molitor
 */
abstract class ComponentAspect {

    private final ComponentPointcut componentPointcut;

    ComponentAspect(ComponentPointcut componentPointcut) {
        this.componentPointcut = componentPointcut;
    }

    final Object applyAspect(Object componentKey, Object component) {
        if (componentPointcut.picks(componentKey)) {
            return wrap(component);
        } else {
            return component;
        }
    }

    abstract Object wrap(Object component);

}