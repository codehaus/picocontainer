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
 * @author Stephen Molitor
 */
public class MixinComponentAspect extends ComponentAspect {
    
    private Class[] mixinInterfaces;
    private Class mixinClass;
    private MixinFactory mixinFactory;
    
    MixinComponentAspect(ComponentPointcut componentPointcut, Class mixinClass) {
        super(componentPointcut);
        this.mixinClass = mixinClass;
    }

    MixinComponentAspect(ComponentPointcut componentPointcut, Class[] mixinInterfaces, Class mixinClass) {
        this(componentPointcut, mixinClass);
        this.mixinInterfaces = mixinInterfaces;
    }
    
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
