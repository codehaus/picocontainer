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
 * Aspect that applies to the set of components matched by a
 * <code>org.nanocontainer.nanoaop.ComponentPointcut</code>.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
abstract class ComponentAspect {

    private final ComponentPointcut componentPointcut;

    /**
     * Creates a new <code>ComponentAspect</code> with the given component
     * pointcut.
     * 
     * @param componentPointcut the component pointcut.
     */
    ComponentAspect(ComponentPointcut componentPointcut) {
        this.componentPointcut = componentPointcut;
    }

    /**
     * Applies this aspect to a component, if the component is picked by the
     * pointcut passed to the constructor. Template method that delegates to the
     * <code>wrap</code> method if the pointcut matches.
     * 
     * @param componentKey the component key.
     * @param component the component instance.
     * @return the aspected component.
     */
    final Object applyAspect(Object componentKey, Object component) {
        if (componentPointcut.picks(componentKey)) {
            return wrap(component);
        } else {
            return component;
        }
    }

    /**
     * Called by <code>applyAspect</code> to actually apply the advice to the component.
     * 
     * @param component the component to apply the advice to.
     * @return the aspected component.
     */
    abstract Object wrap(Object component);

}