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

import dynaop.Aspects;
import dynaop.MixinFactory;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * Mixin aspect that is applied to the components that match a component
 * pointcut.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
class MixinComponentAspect extends ComponentAspect {

    private Class[] mixinInterfaces;
    private Class mixinClass;
    private MixinFactory mixinFactory;

    /**
     * Creates a new <code>MixinComponentAspect</code> from the given
     * component pointcut and mixin class.
     * 
     * @param componentPointcut the components to introduce the mixin to.
     * @param mixinClass the mixin class.
     */
    MixinComponentAspect(ComponentPointcut componentPointcut, Class mixinClass) {
        super(componentPointcut);
        this.mixinClass = mixinClass;
    }

    /**
     * Creates a new <code>MixinComponentAspect</code> from the given
     * component pointcut and mixin class. The aspected component will implement
     * the provided set of mixin interfaces.
     * 
     * @param componentPointcut the components to introduce the mixin to.
     * @param mixinInterfaces the mixin interfaces the aspected component will
     *        implement.
     * @param mixinClass the mixin class.
     */
    MixinComponentAspect(ComponentPointcut componentPointcut, Class[] mixinInterfaces, Class mixinClass) {
        this(componentPointcut, mixinClass);
        this.mixinInterfaces = mixinInterfaces;
    }

    /**
     * Creates a new <code>MixinComponentAspect</code> from the given
     * component pointcut and mixin class. The aspected component will implement
     * the provided set of mixin interfaces.
     * 
     * @param componentPointcut the components to introduce the mixin to.
     * @param mixinInterfaces the mixin interfaces the aspected component will
     *        implement.
     * @param mixinFactory the mixin factory.
     */
    MixinComponentAspect(ComponentPointcut componentPointcut, Class[] mixinInterfaces, MixinFactory mixinFactory) {
        super(componentPointcut);
        this.mixinInterfaces = mixinInterfaces;
        this.mixinFactory = mixinFactory;
    }

    Object wrap(Object component) {
        Aspects aspects = new Aspects();
        if (mixinClass != null) {
            mixinClass(aspects);
        } else {
            aspects.mixin(Pointcuts.ALL_CLASSES, mixinInterfaces, mixinFactory);
        }
        return ProxyFactory.getInstance(aspects).wrap(component);
    }

    private void mixinClass(Aspects aspects) {
        if (mixinInterfaces != null) {
            aspects.mixin(Pointcuts.ALL_CLASSES, mixinInterfaces, mixinClass, null);
        } else {
            aspects.mixin(Pointcuts.ALL_CLASSES, mixinClass, null);
        }
    }

}